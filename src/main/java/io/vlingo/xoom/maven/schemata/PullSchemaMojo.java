// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata;

import com.google.gson.Gson;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.HTTP_OK;


@Mojo(name = "pull-schema", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class PullSchemaMojo extends AbstractMojo {

    public static final String SCHEMATA_CODE_RESOURCE_PATH = "/api/code/%s/%s";
    public static final String SCHEMATA_SCHEMA_VERSION_RESOURCE_PATH = "/api/versions/%s/status";
    public static final String SCHEMATA_REFERENCE_SEPARATOR = ":";
    public static final String SCHEMATA_DEPENDENCIES_RESOURCE_PATH = "/api/schemas/%s/dependencies";
    private Pattern PACKAGE_NAME_PATTERN = Pattern.compile("package (.+);.*");
    private final SchemataServiceDNSResolver schemataServiceDNSResolver;

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(name = "schemataService")
    private SchemataService schemataService;

    @Parameter(name = "outputDirectory", defaultValue = "target/generated-sources/xoom/schemata", required = true)
    private File outputDirectory;

    @Parameter(property = "schemata")
    private List<Schema> schemata;

    private final io.vlingo.xoom.actors.Logger logger;

    public PullSchemaMojo() {
        this.schemataServiceDNSResolver = new SchemataServiceDNSResolver();
        this.logger = io.vlingo.xoom.actors.Logger.basicLogger();
        logger.info("XOOM: Pulling code generated from VLINGO XOOM Schemata registry.");
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (schemataService != null) {
            pullSources();
        } else {
            logger.warn("There's no schema configured to pull.");
        }
    }

    private void pullSources() throws MojoExecutionException, MojoFailureException {
        resolveSchemataURL();
        logger.info(schemataService.toString());
        this.project.addCompileSourceRoot(this.outputDirectory.toString());

        try {
            for (final String reference : resolveSchemaReferences()) {
                final String source = this.pullSource(reference);
                this.writeSourceFile(reference, source);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Pulling schemata failed", e);
        }
    }

    private String pullSource(final String schemaReference) throws IOException, MojoFailureException {
        URL codeResourceUrl = codeResourceUrl(this.schemataService.getUrl(), schemaReference, "java");
        validateSchemaStatus(schemaReference);

        logger.info("Pulling {} from {}", schemaReference, codeResourceUrl);
        URLConnection connection = codeResourceUrl.openConnection();
        connection.setRequestProperty("Accept", "text/plain, text/x-java-source");

        StringBuilder sources = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                sources.append(line);
                sources.append("\n");
            }
        }
        logger.info("Pulled {}", schemaReference);
        return sources.toString();
    }

    @SuppressWarnings("unchecked")
    private void loadDependenciesReferences(final String parentSchemaReference,
                                            final Set<String> declaredReferences,
                                            final Set<String> loadedDependenciesReferences) throws IOException, MojoExecutionException {
        final URL schemaDependenciesUrl = schemaDependenciesUrl(this.schemataService.getUrl(), parentSchemaReference);

        logger.info("Searching {} dependencies at {}", parentSchemaReference, schemaDependenciesUrl);
        HttpURLConnection connection = (HttpURLConnection) schemaDependenciesUrl.openConnection();
        connection.setRequestProperty("Accept", "text/plain, text/x-java-source");
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HTTP_OK) {
            final List<String> dependencies =
                    new Gson().fromJson(response(connection), List.class);

            final Set<String> newReferences =
                    dependencies.stream().filter(ref -> !declaredReferences.contains(ref) &&
                            !loadedDependenciesReferences.contains(ref)).collect(Collectors.toSet());

            for(final String reference : newReferences) {
                loadedDependenciesReferences.add(reference);
                loadDependenciesReferences(reference, declaredReferences, loadedDependenciesReferences);
            }
        } else {
            logError(connection, "Searching dependencies");
            throw new MojoExecutionException("Could not get dependencies from " + schemaDependenciesUrl + ": "
                    + connection.getResponseMessage() + " - " + connection.getResponseCode() +
                    "-" + response(connection));
        }
    }

    private Set<String> resolveSchemaReferences() throws MojoExecutionException, IOException {
        final Set<String> dependenciesReferences = new HashSet<>();

        final Set<String> declaredReferences =
                this.schemata.stream().map(Schema::getRef).collect(Collectors.toSet());

        for(final String reference : declaredReferences) {
            loadDependenciesReferences(reference, declaredReferences, dependenciesReferences);
        }
        return Stream.of(declaredReferences, dependenciesReferences).flatMap(Set::stream)
                .collect(Collectors.toSet());
    }


    private void validateSchemaStatus(String schemaReference) throws IOException, MojoFailureException {
        URL versionResourceUrl = versionDataUrl(this.schemataService.getUrl(), schemaReference);
        logger.info("Retrieving version data for {} from {}", schemaReference, versionResourceUrl);

        URLConnection connection = versionResourceUrl.openConnection();
        connection.setRequestProperty("Accept", "application/json, text/plain");
        String status = readString(connection);

        switch(status) {
            case "Published":
                // do nothing, this is the happy case
                break;
            case "Draft":
            case "Deprecated":
                logger.warn( "{} status is '{}': don't use in production builds", schemaReference, status);
                break;
            case "Removed":
                logger.error( "{} status is '{}' and may no longer be used", schemaReference, status);
                throw new MojoFailureException(schemaReference + " has reached the end of its life cycle");
            default:
                logger.error("Unknown status " + status +". Are you using matching versions of xoom-schemata and the build plugins?");

        }

    }

    private void writeSourceFile(String schemaReference, String source) throws IOException {

        // we need to parse the sources as we can't access event type meta data here
        Path sourceDirPath = packagePathFromSource(this.outputDirectory, source);
        Files.createDirectories(sourceDirPath);
        Path sourceFilePath = sourceDirPath.resolve(fileNameFromReference(schemaReference) + ".java");

        logger.info("Writing {} to {}", schemaReference, sourceFilePath);
        Files.write(
                sourceFilePath,
                source.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        logger.info("Wrote {}", sourceFilePath);

    }

    Path packagePathFromSource(File srcRoot, String source) {
        Matcher matcher = PACKAGE_NAME_PATTERN.matcher(source);
        boolean isInDefaultPackage = !matcher.find();
        if (isInDefaultPackage) {
            return srcRoot.toPath();
        }

        String packageName = matcher.group(1);
        String[] directories = packageName.split("\\.");
        String relativePackagePath = String.join(File.separator, directories);
        return srcRoot.toPath().resolve(relativePackagePath);
    }

    private String fileNameFromReference(String reference) {
        String[] parts = reference.split(SCHEMATA_REFERENCE_SEPARATOR);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Pass a reference in the form of <organization>:<unit>:<context>:<schema>[:<version>]");
        }
        return parts[3];
    }

    private static String readString(URLConnection connection) throws IOException {
        String data = "";
        try (
          BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
        ) {
            data = reader.lines().collect(Collectors.joining("\n"));
        }
        return data;
    }

    private void logError(final HttpURLConnection connection, final String pattern) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            logger.error(pattern, response.toString());
        }
    }

    private String response(final HttpURLConnection connection) throws IOException {
        return new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
    }

    private URL codeResourceUrl(URL baseUrl, String schemaReference, String language) throws MalformedURLException {
        return new URL(baseUrl, String.format(SCHEMATA_CODE_RESOURCE_PATH, schemaReference, language));
    }

    private URL schemaDependenciesUrl(URL baseUrl, String schemaReference) throws MalformedURLException {
        return new URL(baseUrl, String.format(SCHEMATA_DEPENDENCIES_RESOURCE_PATH, schemaReference));
    }

    private URL versionDataUrl(URL baseUrl, String schemaReference) throws MalformedURLException {
        return new URL(baseUrl, String.format(SCHEMATA_SCHEMA_VERSION_RESOURCE_PATH, schemaReference));
    }

    private void resolveSchemataURL() throws MojoExecutionException {
        if(schemataServiceDNSResolver.useDNS(project)) {
            try {
                final URL actualURL = schemataService.getUrl();
                final URL surrogateURL = schemataServiceDNSResolver.resolve(actualURL, project);
                schemataService.changeURL(surrogateURL);
                logger.info("Using surrogate Schemata URL: " + surrogateURL);
            } catch (MalformedURLException e) {
                throw new MojoExecutionException("Pulling schemata failed", e);
            }
        }
    }

}

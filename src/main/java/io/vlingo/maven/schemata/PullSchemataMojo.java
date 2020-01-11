// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.maven.schemata;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Mojo(name = "pull-schemata", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class PullSchemataMojo extends AbstractMojo {

    public static final String SCHEMATA_CODE_RESOURCE_PATH = "/code/%s/%s";
    public static final String SCHEMATA_REFERENCE_SEPARATOR = ":";
    Pattern PACKAGE_NAME_PATTERN = Pattern.compile("package (.+);.*");


    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(name = "schemataService")
    private SchemataService schemataService;

    @Parameter(name = "outputDirectory", defaultValue = "target/generated-sources/vlingo", required = true)
    private File outputDirectory;

    @Parameter(property = "schemata")
    private List<Schema> schemata;

    private final io.vlingo.actors.Logger logger;

    public PullSchemataMojo() {
        this.logger = io.vlingo.actors.Logger.basicLogger();
        logger.info("vlingo/maven: Pulling code generated from vlingo/schemata registry.");
    }

    @Override
    public void execute() throws MojoExecutionException {
        logger.info(schemataService.toString());
        this.project.addCompileSourceRoot(this.outputDirectory.toString());
        try {
            for (Schema schema : this.schemata) {
                String reference = schema.getRef();
                String source = this.pullSource(reference);
                this.writeSourceFile(reference, source);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Pulling schemata failed", e);
        }
    }

    private String pullSource(String schemaReference) throws IOException {
        URL codeResourceUrl = codeResourceUrl(this.schemataService.getUrl(), schemaReference, "java");

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

    private URL codeResourceUrl(URL baseUrl, String schemaReference, String language) throws MalformedURLException {
        return new URL(baseUrl, String.format(SCHEMATA_CODE_RESOURCE_PATH, schemaReference, language));
    }
}

// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.maven.schemata;

import com.google.gson.Gson;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;


@Mojo(name = "push-schemata", defaultPhase = LifecyclePhase.INSTALL)
public class PushSchemataMojo extends AbstractMojo {
    public static final String SCHEMATA_VERSION_RESOURCE_PATH = "/versions/%s";
    private final Gson gson;


    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(name = "srcDirectory", defaultValue = "src/main/vlingo/schemata", required = true)
    private File srcDirectory;

    @Parameter(name = "schemataService")
    private SchemataService schemataService;


    @Parameter(property = "schemata")
    private List<Schema> schemata;

    private final io.vlingo.actors.Logger logger;

    public PushSchemataMojo() {
        this.logger = io.vlingo.actors.Logger.basicLogger();
        logger.info("vlingo/maven: Pushing project schemata to vlingo-schemata registry.");
        gson = new Gson();
    }

    @Override
    public void execute() throws MojoExecutionException {

        for (Schema schema : this.schemata) {
            String reference = schema.getRef();

            Path sourceFile =
                    srcDirectory.toPath()
                            .resolve(schema.getSrc());

            String description = this.generateDescription(reference, this.project.getArtifact().toString());

            try {
                String specification = new String(Files.readAllBytes(sourceFile));
                String previousVersion = schema.getPreviousVersion() == null ? "0.0.0" : schema.getPreviousVersion();

                String payload = this.payloadFrom(specification, previousVersion, description);
                this.push(reference, payload);
            } catch (IOException e) {
                throw new MojoExecutionException(
                        "Schema specification " + sourceFile.toAbsolutePath() +
                                " could not be pushed to " + schemataService.getUrl()
                        , e);
            }
        }

    }

    private String generateDescription(String reference, String project) {
        StringBuilder description = new StringBuilder();
        description.append("# ");
        description.append(reference);
        description.append("\n\n");
        description.append("Schema `");
        description.append(reference);
        description.append("` pushed from `");
        description.append(project);
        description.append("`.\n\n");
        description.append("Publication date: ");
        description.append(LocalDateTime.now().toString());

        return description.toString();
    }

    private String payloadFrom(String specification, String previousVersion, String description) {
        SchemaVersion payload = new SchemaVersion(description, specification, previousVersion);
        return gson.toJson(payload);
    }

    private void push(String reference, String payload) throws IOException, MojoExecutionException {
        URL schemaVersionUrl = schemaVersionUrl(this.schemataService.getUrl(), reference);

        logger.info("Pushing {} to {}.", reference, schemaVersionUrl);

        HttpURLConnection connection = (HttpURLConnection) schemaVersionUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream os = connection.getOutputStream();
        os.write(payload.getBytes(StandardCharsets.UTF_8));
        os.close();

        StringBuilder sb = new StringBuilder();
        int HttpResult = connection.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_CREATED) {
            logger.info("Successfully pushed {}", schemaVersionUrl);
        } else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                logger.error("Pushing the schema version failed: {}", response.toString());
            }
            throw new MojoExecutionException(
                    "Could not push " + reference
                            + " to " + schemaVersionUrl
                            + ": " + connection.getResponseMessage()
                            + " - " + connection.getResponseCode());
        }
    }

    private URL schemaVersionUrl(URL baseUrl, String schemaVersionReference) throws MalformedURLException {
        return new URL(baseUrl, String.format(SCHEMATA_VERSION_RESOURCE_PATH, schemaVersionReference));
    }


    private class SchemaVersion {
        final String description;
        final String specification;
        final String previousVersion;

        private SchemaVersion(String description, String specification, String previousVersion) {
            this.description = description;
            this.specification = specification;
            this.previousVersion = previousVersion;
        }
    }
}

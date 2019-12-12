// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


@Mojo(name = "pull-schemata", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class PullSchemataMojo extends AbstractMojo {

    public static final String SCHEMATA_CODE_RESOURCE_PATH = "/code";

    @Parameter(name = "schemataService")
    private SchemataService schemataService;

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
        try {
            for (Schema schema : this.schemata) {
                System.out.println(this.pullSource(schema.getRef()));
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

    private URL codeResourceUrl(URL baseUrl, String schemaReference, String language) throws MalformedURLException {
        return new URL(baseUrl, String.format("/code/%s/%s", schemaReference, language));
    }
}

// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata;

import io.vlingo.xoom.maven.schemata.api.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;


@Mojo(name = "push-schema", defaultPhase = LifecyclePhase.INSTALL)
public class PushSchemaMojo extends AbstractMojo {
    public static final String SCHEMATA_VERSION_RESOURCE_PATH = "versions/%s";

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(name = "srcDirectory", defaultValue = "src/main/xoom/schemata", required = true)
    private File srcDirectory;

    @Parameter(name = "schemataService")
    private SchemataService schemataService;

    @Parameter(property = "schemata")
    private List<Schema> schemata;

    private final io.vlingo.xoom.actors.Logger logger;
    private OrganizationAPI organizationAPI;
    private UnitAPI unitAPI;
    private ContextAPI contextAPI;
    private SchemaAPI schemaAPI;
    private SchemaVersionAPI schemaVersionAPI;

    public PushSchemaMojo() {
        this.logger = io.vlingo.xoom.actors.Logger.basicLogger();
        logger.info("XOOM: Pushing project schemata to VLINGO XOOM Schemata registry.");
    }

    @Override
    public void execute() throws MojoExecutionException {
        initializeAPI();
        createSchemaParents();

        for (final Schema schema : this.schemata) {
            final String reference = schema.getRef();

            final Path sourceFile =
                    srcDirectory.toPath()
                            .resolve(schema.getSrc());

            final String description =
                    this.generateDescription(reference, this.project.getArtifact().toString());

            try {
                final String specification = new String(Files.readAllBytes(sourceFile));
                final String route = String.format(SCHEMATA_VERSION_RESOURCE_PATH, reference);
                final String previousVersion = schema.getPreviousVersion() == null ? "0.0.0" : schema.getPreviousVersion();
                final SchemaVersion schemaVersion = new SchemaVersion(description, specification, previousVersion);
                this.createSchema(schema, sourceFile);
                this.schemaVersionAPI.create(schemataService.getUrl(), route, schemaVersion);
            } catch (IOException e) {
                throw new MojoExecutionException(
                        "Schema specification " + sourceFile.toAbsolutePath() +
                                " could not be pushed to " + schemataService.getUrl()
                        , e);
            }
        }
    }

    private void initializeAPI() {
        final int serviceReadinessInterval = resolveServiceReadinessInterval();
        this.organizationAPI = new OrganizationAPI(serviceReadinessInterval);
        this.unitAPI = new UnitAPI(organizationAPI, serviceReadinessInterval);
        this.contextAPI = new ContextAPI(unitAPI, organizationAPI, serviceReadinessInterval);
        this.schemaAPI = new SchemaAPI(unitAPI, contextAPI, organizationAPI, serviceReadinessInterval);
        this.schemaVersionAPI = new SchemaVersionAPI();
    }

    private void createSchemaParents() throws MojoExecutionException {
        if(schemataService.getHierarchicalCascade()) {
            try {
                this.organizationAPI.create(schemataService.getUrl(), schemataService.getClientOrganization());
                this.unitAPI.create(schemataService.getUrl(), schemataService.getClientOrganization(), schemataService.getClientUnit());
            } catch (final IOException e) {
                throw new MojoExecutionException("Unable to create organization/unit");
            }
        }
    }

    private void createSchema(final Schema schema, final Path specificationSourceFile) throws MojoExecutionException {
        if(schemataService.getHierarchicalCascade()) {
            try {
                final String schemaName = schema.getSchemaName();
                final String namespace = schema.getContextNamespace();
                this.contextAPI.create(schemataService.getUrl(), schemataService.getClientOrganization(), schemataService.getClientUnit(), namespace);
                this.schemaAPI.create(schemataService.getUrl(), schemataService.getClientOrganization(), schemataService.getClientUnit(), namespace, schemaName, specificationSourceFile);
            } catch (final IOException e) {
                throw new MojoExecutionException("Unable to create context/schema");
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

    private int resolveServiceReadinessInterval() {
        return schemataService != null && schemataService.getHierarchicalCascade() ? 1500 : 0;
    }

}

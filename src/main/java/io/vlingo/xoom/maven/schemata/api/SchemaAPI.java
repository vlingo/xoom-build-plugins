// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata.api;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class SchemaAPI extends API {

  private static final String ROUTE_PATTERN = "organizations/%s/units/%s/contexts/%s/schemas";

  private final UnitAPI unitAPI;
  private final ContextAPI contextAPI;
  private final OrganizationAPI organizationAPI;

  public SchemaAPI(final UnitAPI unitAPI,
                   final ContextAPI contextAPI,
                   final OrganizationAPI organizationAPI,
                   final int schemataServiceReadinessInterval) {
    super(schemataServiceReadinessInterval);
    this.unitAPI = unitAPI;
    this.contextAPI = contextAPI;
    this.organizationAPI = organizationAPI;
  }

  public void create(final URL baseURL,
                     final String organizationName,
                     final String unitName,
                     final String namespace,
                     final String schemaName,
                     final Path specificationSourceFile) throws IOException, MojoExecutionException {
    final OrganizationData organization =
            organizationAPI.find(baseURL, organizationName);

    final UnitData unit =
            unitAPI.find(baseURL, organization.organizationId, unitName);

    final ContextData context =
            contextAPI.find(baseURL, organization.organizationId, unit.unitId, namespace);

    if(!alreadyExist(baseURL, organization.organizationId, unit.unitId, context.contextId, schemaName)) {
      final String route = resolveRoute(organization.organizationId, unit.unitId, context.contextId);
      final String category = retrieveCategory(specificationSourceFile);
      final SchemaData schemaData = new SchemaData(schemaName, category);
      post(SchemaData.class.getSimpleName(), baseURL, route, schemaData);
    }
  }

  private boolean alreadyExist(final URL baseURL,
                               final String organizationId,
                               final String unitId,
                               final String contextId,
                               final String schemaName) throws IOException, MojoExecutionException {
    return getAll(SchemaData[].class, baseURL, resolveRoute(organizationId, unitId, contextId)).stream()
            .anyMatch(schema -> schema.name.equals(schemaName));
  }

  private String resolveRoute(final String organizationId,
                              final String unitId,
                              final String contextId) {
    return String.format(ROUTE_PATTERN, organizationId, unitId, contextId);
  }

  private String retrieveCategory(final Path specificationSourceFile) throws IOException {
    final String specification = new String(Files.readAllBytes(specificationSourceFile)).trim();
    return specification.substring(0, specification.indexOf(" "));
  }

}

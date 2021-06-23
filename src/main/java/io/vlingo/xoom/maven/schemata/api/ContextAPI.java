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

public class ContextAPI extends API {

  private static final String ROUTE_PATTERN = "organizations/%s/units/%s/contexts";

  private final UnitAPI unitAPI;
  private final OrganizationAPI organizationAPI;

  public ContextAPI(final UnitAPI unitAPI,
                    final OrganizationAPI organizationAPI,
                    final int schemataServiceReadinessInterval,
                    final boolean skipPrompt) {
    super(schemataServiceReadinessInterval, skipPrompt);
    this.unitAPI = unitAPI;
    this.organizationAPI = organizationAPI;
  }

  public void create(final URL baseURL,
                     final String organizationName,
                     final String unitName,
                     final String namespace) throws IOException, MojoExecutionException {
    final OrganizationData organization = organizationAPI.find(baseURL, organizationName);
    final UnitData unit = unitAPI.find(baseURL, organization.organizationId, unitName);
    if(!alreadyExist(baseURL, organization.organizationId, unit.unitId, namespace)) {
      promptForHierarchyCreation("context", String.format("%s:%s:%s", organizationName, unitName, namespace));
      final String route = String.format(ROUTE_PATTERN, organization.organizationId, unit.unitId);
      post(ContextData.class.getSimpleName(), baseURL, route, new ContextData(namespace));
    }
  }

  public ContextData find(final URL baseURL,
                          final String organizationId,
                          final String unitId,
                          final String namespace) throws IOException, MojoExecutionException {
    return getAll(ContextData[].class, baseURL, resolveRoute(organizationId, unitId))
            .stream().filter(context -> context.namespace.equals(namespace))
            .findFirst().get();
  }

  private boolean alreadyExist(final URL baseURL,
                               final String organizationId,
                               final String unitId,
                               final String namespace) throws IOException, MojoExecutionException {
    return getAll(ContextData[].class, baseURL, resolveRoute(organizationId, unitId)).stream()
            .anyMatch(context -> context.namespace.equals(namespace));
  }

  private String resolveRoute(String organizationId, String unitId) {
    return String.format(ROUTE_PATTERN, organizationId, unitId);
  }

}

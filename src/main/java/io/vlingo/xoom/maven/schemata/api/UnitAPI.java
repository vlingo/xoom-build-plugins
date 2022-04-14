// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata.api;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.net.URL;

public class UnitAPI extends API {

  private static final String ROUTE_PATTERN = "organizations/%s/units";

  private final OrganizationAPI organizationAPI;

  public UnitAPI(final OrganizationAPI organizationAPI,
                 final int schemataServiceReadinessInterval,
                 final boolean skipPrompt) {
    super(schemataServiceReadinessInterval, skipPrompt);
    this.organizationAPI = organizationAPI;
  }

  public void create(final URL baseURL, final String organizationName, final String unitName) throws IOException, MojoExecutionException {
    final OrganizationData data = organizationAPI.find(baseURL, organizationName);
    if(!alreadyExist(baseURL, data.organizationId, unitName)) {
      promptForHierarchyCreation("unit", organizationName + ":" + unitName);
      final String route = String.format(ROUTE_PATTERN, data.organizationId);
      post(UnitData.class.getSimpleName(), baseURL, route, new UnitData(unitName));
    }
  }

  public UnitData find(final URL baseURL, final String organizationId, final String unitName) throws IOException, MojoExecutionException {
    return getAll(UnitData[].class, baseURL, resolveRoute(organizationId))
            .stream().filter(unit -> unit.name.equals(unitName))
            .findFirst().get();
  }

  private boolean alreadyExist(final URL baseURL, final String organizationId, final String unitName) throws IOException, MojoExecutionException {
    return getAll(UnitData[].class, baseURL, resolveRoute(organizationId)).stream()
            .anyMatch(unit -> unit.name.equals(unitName));
  }

  private String resolveRoute(final String organizationId) {
    return String.format(ROUTE_PATTERN, organizationId);
  }

}

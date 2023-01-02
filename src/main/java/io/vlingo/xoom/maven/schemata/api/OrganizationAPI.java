// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata.api;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.net.URL;

public class OrganizationAPI extends API {

  public OrganizationAPI(final int serviceReadinessInterval, boolean skipPrompt) {
    super(serviceReadinessInterval, skipPrompt);
  }

  public void create(final URL baseURL, final String organizationName) throws IOException, MojoExecutionException {
    if(!alreadyExist(baseURL, organizationName)) {
      promptForHierarchyCreation("organization", organizationName);
      post(OrganizationData.class.getSimpleName(), baseURL, "organizations", new OrganizationData(organizationName));
    }
  }

  public OrganizationData find(final URL baseURL, final String organizationName) throws IOException, MojoExecutionException {
    return getAll(OrganizationData[].class, baseURL, "organizations")
            .stream().filter(org -> org.name.equals(organizationName))
            .findFirst().get();
  }

  private boolean alreadyExist(final URL baseURL, final String organizationName) throws IOException, MojoExecutionException {
    return getAll(OrganizationData[].class, baseURL, "organizations")
            .stream().anyMatch(org -> org.name.equals(organizationName));
  }
}

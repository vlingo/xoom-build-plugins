// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.maven.schemata;

import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class SchemataServiceDNSResolver {

  private static final String PROFILE_ID = "schemata-service";
  private static final String SERVICE_NAME = "name";

  public URL resolve(final URL actualURL,
                     final MavenProject mavenProject) throws MalformedURLException {
    final Optional<String> serviceName =
            findServiceName(mavenProject);

    return serviceName.isPresent() ? new URL("http", serviceName.get(), "") : actualURL;
  }

  public boolean useDNS(final MavenProject mavenProject) {
    return findServiceName(mavenProject).isPresent();
  }

  private Optional<String> findServiceName(final MavenProject mavenProject) {
    final Optional<Profile> optionalProfile =
            mavenProject.getActiveProfiles().stream()
                    .filter(profile -> profile.getId().equals(PROFILE_ID))
                    .findFirst();

    if(optionalProfile.isPresent() && optionalProfile.get().getProperties().containsKey(SERVICE_NAME)) {
      return Optional.of(optionalProfile.get().getProperties().getProperty(SERVICE_NAME));
    }

    return Optional.empty();
  }

}

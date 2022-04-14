// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.maven.schemata;

import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class SchemataServiceDNSResolver {

  private static final String PROFILE_ID = "schemata-service";
  private static final String SERVICE_NAME = "name";
  private static final String PORT = "port";

  public URL resolve(final URL actualURL,
                     final MavenProject mavenProject) throws MalformedURLException {
    final Optional<String> optionalServiceName =
            findServiceName(mavenProject);

    if(optionalServiceName.isPresent()) {
      final String serviceName = optionalServiceName.get();
      final Optional<Integer> optionalPort = findPort(mavenProject);
      if (optionalPort.isPresent()) {
        return new URL("http", serviceName, optionalPort.get(), "");
      }
      return new URL("http", optionalServiceName.get(), "");
    } else {
      return actualURL;
    }

  }

  public boolean useDNS(final MavenProject mavenProject) {
    return findServiceName(mavenProject).isPresent();
  }

  private Optional<String> findServiceName(final MavenProject mavenProject) {
    final Optional<Profile> optionalProfile = findProfile(mavenProject);

    if(optionalProfile.isPresent() && optionalProfile.get().getProperties().containsKey(SERVICE_NAME)) {
      return Optional.of(optionalProfile.get().getProperties().getProperty(SERVICE_NAME));
    }

    return Optional.empty();
  }

  private Optional<Integer> findPort(final MavenProject mavenProject) {
    final Optional<Profile> optionalProfile = findProfile(mavenProject);

    if(optionalProfile.isPresent() && optionalProfile.get().getProperties().containsKey(PORT)) {
      final String port =
              optionalProfile.get().getProperties().getProperty(PORT);

      if(!StringUtils.isNumeric(port)) {
        throw new IllegalArgumentException("The schemata service port is not a number.");
      }
      return Optional.of(Integer.parseInt(port));
    }

    return Optional.empty();
  }

  private Optional<Profile> findProfile(final MavenProject mavenProject) {
    return mavenProject.getActiveProfiles().stream()
            .filter(profile -> profile.getId().equals(PROFILE_ID))
            .findFirst();
  }

}

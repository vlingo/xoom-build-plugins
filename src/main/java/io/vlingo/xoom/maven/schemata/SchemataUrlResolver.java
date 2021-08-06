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

public class SchemataUrlResolver {

  private static final String PROFILE_ID = "schemata-integration";
  private static final String SCHEMATA_URL = "schemata-url";

  public URL resolve(final URL actualURL,
                     final MavenProject mavenProject) throws MalformedURLException {
    final Optional<String> surrogateURL =
            findSurrogateSchemataURL(mavenProject);

    return surrogateURL.isPresent() ? new URL(surrogateURL.get()) : actualURL;
  }

  public boolean useSurrogateURl(final MavenProject mavenProject) {
    return findSurrogateSchemataURL(mavenProject).isPresent();
  }

  private Optional<String> findSurrogateSchemataURL(final MavenProject mavenProject) {
    final Optional<Profile> optionalProfile =
            mavenProject.getActiveProfiles().stream()
                    .filter(profile -> profile.getId().equals(PROFILE_ID))
                    .findFirst();

    if(optionalProfile.isPresent() && optionalProfile.get().getProperties().containsKey(SCHEMATA_URL)) {
      return Optional.of(optionalProfile.get().getProperties().getProperty(SCHEMATA_URL));
    }

    return Optional.empty();
  }

}

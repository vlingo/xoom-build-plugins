// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.maven.schemata;

@SuppressWarnings("unused")
public class SchemaVersion {
    final String description;
    final String specification;
    final String previousVersion;

    SchemaVersion(String description, String specification, String previousVersion) {
        this.description = description;
        this.specification = specification;
        this.previousVersion = previousVersion;
    }


}

# vlingo-build-plugins

[![Javadocs](http://javadoc.io/badge/io.vlingo/vlingo-build-plugins.svg?color=brightgreen)](http://javadoc.io/doc/io.vlingo/vlingo-build-plugins) [![Build](https://github.com/vlingo/vlingo-build-plugins/workflows/Build/badge.svg)](https://github.com/vlingo/vlingo-build-plugins/actions?query=workflow%3ABuild) [ ![Download](https://api.bintray.com/packages/vlingo/vlingo-platform-java/vlingo-build-plugins/images/download.svg) ](https://bintray.com/vlingo/vlingo-platform-java/vlingo-build-plugins/_latestVersion) [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vlingo-platform-java/community/)

The VLINGO/PLATFORM build tooling using Maven plugins.

Docs: https://docs.vlingo.io/vlingo-build-plugins

See vlingo-build-plugins-test for examples.

### Bintray

```xml
  <repositories>
    <repository>
      <id>jcenter</id>
      <url>https://jcenter.bintray.com/</url>
    </repository>
  </repositories>
  <dependencies>
    <dependency>
      <groupId>io.vlingo</groupId>
      <artifactId>vlingo-build-plugins</artifactId>
      <version>1.3.0</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
```

```gradle
dependencies {
    compile 'io.vlingo:vlingo-build-plugins:1.3.0'
}

repositories {
    jcenter()
}
```

License (See LICENSE file for full license)
-------------------------------------------
Copyright © 2012-2020 VLINGO LABS. All rights reserved.

This Source Code Form is subject to the terms of the
Mozilla Public License, v. 2.0. If a copy of the MPL
was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/.

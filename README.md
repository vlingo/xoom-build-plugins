# vlingo-maven-plugin


[![Javadocs](http://javadoc.io/badge/io.vlingo/vlingo-maven-plugin.svg?color=brightgreen)](http://javadoc.io/doc/io.vlingo/vlingo-maven-plugin) [![Build Status](https://travis-ci.org/vlingo/vlingo-maven-plugin-test.svg?branch=master)](https://travis-ci.org/vlingo/vlingo-maven-plugin-test) [ ![Download](https://api.bintray.com/packages/vlingo/vlingo-platform-java/vlingo-maven-plugin/images/download.svg) ](https://bintray.com/vlingo/vlingo-platform-java/vlingo-maven-plugin/_latestVersion) [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vlingo-platform-java/community)

The vlingo/PLATFORM build tooling using Maven plugins.

See vlingo-maven-plugin-test for examples.

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
      <artifactId>vlingo-maven-plugin</artifactId>
      <version>0.9.0-RC1</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
```

```gradle
dependencies {
    compile 'io.vlingo:vlingo-maven-plugin:0.9.0-RC1'
}

repositories {
    jcenter()
}
```

License (See LICENSE file for full license)
-------------------------------------------
Copyright © 2012-2018 Vaughn Vernon. All rights reserved.

This Source Code Form is subject to the terms of the
Mozilla Public License, v. 2.0. If a copy of the MPL
was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/.

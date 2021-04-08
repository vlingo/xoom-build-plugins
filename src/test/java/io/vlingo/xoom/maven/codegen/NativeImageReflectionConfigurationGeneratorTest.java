// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.maven.codegen;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class NativeImageReflectionConfigurationGeneratorTest {
    @Test
    public void shouldGenerateTheExpectedReflectionJson() throws Exception {
        String expected = new String(Files.readAllBytes(Paths.get(getClass().getResource("/reflection-test.json").toURI()))).replaceAll(System.lineSeparator(), "\n");
        NativeImageReflectionConfigurationGenerator generator = new NativeImageReflectionConfigurationGenerator(Data.FAKE_CLASSES);

        String result = generator.generate();
        assertEquals(expected, result);
    }
}
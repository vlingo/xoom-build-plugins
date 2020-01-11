package io.vlingo.maven.codegen;

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
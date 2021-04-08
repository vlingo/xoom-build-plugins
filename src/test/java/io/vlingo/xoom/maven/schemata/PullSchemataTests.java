package io.vlingo.xoom.maven.schemata;

import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class PullSchemataTests {

    @Test
    public void packageCanBeExtractedFromSource() {
        String source = "package this.is.my.package;\n" +
                "\n" +
                "import io.vlingo.xoom.lattice.model.DomainEvent;\n" +
                "\n" +
                "public final class SchemaDefined extends DomainEvent {\n" +
                "\n" +
                "  public SchemaDefined() {\n" +
                "}";

        PullSchemataMojo psmut = new PullSchemataMojo();
        Path result = psmut.packagePathFromSource(new File("."), source);
        assertEquals(Paths.get(".", "this", "is", "my", "package"), result);
    }

    @Test
    public void defaultPackageCanBeExtractedFromSource() {
        String source = "import io.vlingo.xoom.lattice.model.DomainEvent;\n" +
                "\n" +
                "public final class SchemaDefined extends DomainEvent {\n" +
                "\n" +
                "  public SchemaDefined() {\n" +
                "}";

        PullSchemataMojo psmut = new PullSchemataMojo();
        Path result = psmut.packagePathFromSource(new File("."), source);
        assertEquals(Paths.get("."), result);
    }
}

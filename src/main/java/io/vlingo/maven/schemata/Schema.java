package io.vlingo.maven.schemata;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Schema {
    private final io.vlingo.actors.Logger logger = io.vlingo.actors.Logger.basicLogger();

    @Parameter(property = "src")
    private String src;

    @Parameter(property = "ref", required = true)
    private String ref;

    @Parameter(property = "previousVersion")
    private String previousVersion;

    public Path getSrc() throws MojoExecutionException {
        if (src != null) {
            return Paths.get(src);
        }

        // default to schema name from reference
        logger.debug("No explicit source file given, defaulting to <schemata srcDirectory/schema name from ref>.vss");
        String[] refParts = ref.split(":");
        if (refParts.length != 5) {
            throw new MojoExecutionException(
                    "Invalid schema reference. Should be <org>:<unit>:<context namespace>:<schema name>:<version>");
        }
        String fileName = refParts[3] + ".vss";
        logger.info("Setting source to {} for {}", fileName, ref);
        return Paths.get(fileName);
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    @Override
    public String toString() {
        return "Schema{" +
                "src=" + src +
                ", ref='" + ref + '\'' +
                ", previousVersion='" + previousVersion + '\'' +
                '}';
    }
}

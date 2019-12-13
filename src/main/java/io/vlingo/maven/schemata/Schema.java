package io.vlingo.maven.schemata;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public class Schema {
    @Parameter(property = "src")
    private File src;

    @Parameter(property = "ref", required = true)
    private String ref;

    @Parameter(property = "previousVersion")
    private String previousVersion;

    public File getSrc() {
        return src;
    }

    public void setSrc(File src) {
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

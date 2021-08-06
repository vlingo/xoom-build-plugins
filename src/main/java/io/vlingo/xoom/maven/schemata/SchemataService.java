package io.vlingo.xoom.maven.schemata;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

public class SchemataService {

    private URL url;

    private String clientOrganization;

    private String clientUnit;

    private boolean hierarchicalCascade = false;

    private boolean skipPrompt = false;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getClientOrganization() {
        return clientOrganization;
    }

    public void setClientOrganization(String clientOrganization) {
        this.clientOrganization = clientOrganization;
    }

    public String getClientUnit() {
        return clientUnit;
    }

    public void setClientUnit(String clientUnit) {
        this.clientUnit = clientUnit;
    }

    public void setHierarchicalCascade(boolean createSchemaParents) {
        this.hierarchicalCascade = createSchemaParents;
    }

    public boolean getHierarchicalCascade() {
        return hierarchicalCascade;
    }

    public boolean isSkipPrompt() {
        return skipPrompt;
    }

    public void setSkipPrompt(boolean skipPrompt) {
        this.skipPrompt = skipPrompt;
    }

    public void changeURL(final URL url) throws MalformedURLException {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SchemataService{" +
                "url=" + url +
                ", clientOrganization='" + clientOrganization + '\'' +
                ", clientUnit='" + clientUnit + '\'' +
                ", hierarchicalCascade='" + hierarchicalCascade + '\'' +
                ", skipPrompt='" + skipPrompt + '\'' +
                '}';
    }
}

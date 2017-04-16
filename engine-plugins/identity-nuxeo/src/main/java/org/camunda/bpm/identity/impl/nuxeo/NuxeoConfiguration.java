package org.camunda.bpm.identity.impl.nuxeo;

public class NuxeoConfiguration {

    protected String nuxeoUrl = "";
    protected String adminUsername = "";
    protected String adminPassword = "";

    public String getNuxeoUrl() {
        return nuxeoUrl;
    }

    public void setNuxeoUrl(String nuxeoUrl) {
        this.nuxeoUrl = nuxeoUrl;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}

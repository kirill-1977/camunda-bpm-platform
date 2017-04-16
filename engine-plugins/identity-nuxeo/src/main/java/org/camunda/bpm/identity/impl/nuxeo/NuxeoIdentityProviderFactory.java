package org.camunda.bpm.identity.impl.nuxeo;

import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

public class NuxeoIdentityProviderFactory implements SessionFactory {

    protected NuxeoConfiguration nuxeoConfiguration;

    public Class<?> getSessionType() {
        return ReadOnlyIdentityProvider.class;
    }

    public Session openSession() {
        return new NuxeoIdentityProviderSession(nuxeoConfiguration);
    }

    public NuxeoConfiguration getNuxeoConfiguration() {
        return nuxeoConfiguration;
    }

    public void setNuxeoConfiguration(NuxeoConfiguration nuxeoConfiguration) {
        this.nuxeoConfiguration = nuxeoConfiguration;
    }
}

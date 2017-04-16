package org.camunda.bpm.identity.impl.nuxeo.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.identity.impl.nuxeo.NuxeoIdentityProviderFactory;
import org.camunda.bpm.identity.impl.nuxeo.NuxeoConfiguration;

public class NixedIdentityProviderPlugin extends NuxeoConfiguration implements ProcessEnginePlugin {

    protected Logger LOG = Logger.getLogger(NixedIdentityProviderPlugin.class.getName());

    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

        LOG.log(Level.INFO, "PLUGIN {0} activated on process engine {1}", new String[]{getClass().getSimpleName(), processEngineConfiguration.getProcessEngineName()});

        NuxeoIdentityProviderFactory nuxeoIdentityProviderFactory = new NuxeoIdentityProviderFactory();
        nuxeoIdentityProviderFactory.setNuxeoConfiguration(this);

        processEngineConfiguration.setIdentityProviderSessionFactory(nuxeoIdentityProviderFactory);
        processEngineConfiguration.setDbIdentityUsed(false);
        processEngineConfiguration.setAuthorizationEnabled(false);

    }

    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        // TODO nothing to do ??
    }

    public void postProcessEngineBuild(ProcessEngine processEngine) {
        // nothing to do
    }
}
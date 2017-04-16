package org.camunda.bpm.identity.impl.nuxeo;

import java.util.List;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

public class NuxeoGroupQuery extends GroupQueryImpl {

    private static final long serialVersionUID = 1L;

    public NuxeoGroupQuery() {
        super();
    }

    public NuxeoGroupQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public long executeCount(CommandContext commandContext) {
        final NuxeoIdentityProviderSession identityProvider = getNuxeoIdentityProvider(commandContext);
        return identityProvider.findGroupCountByQueryCriteria(this);
    }

    public List<Group> executeList(CommandContext commandContext, Page page) {
        final NuxeoIdentityProviderSession identityProvider = getNuxeoIdentityProvider(commandContext);
        return identityProvider.findGroupByQueryCriteria(this);
    }

    protected NuxeoIdentityProviderSession getNuxeoIdentityProvider(CommandContext commandContext) {
        return (NuxeoIdentityProviderSession) commandContext.getReadOnlyIdentityProvider();
    }
}
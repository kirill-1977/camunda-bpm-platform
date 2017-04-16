package org.camunda.bpm.identity.impl.nuxeo;

import java.util.List;

import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

public class NuxeoUserQueryImpl extends UserQueryImpl {

    private static final long serialVersionUID = 1L;

    public NuxeoUserQueryImpl() {
        super();
    }

    public NuxeoUserQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public long executeCount(CommandContext commandContext) {
        final NuxeoIdentityProviderSession provider = getNuxeoIdentityProvider(commandContext);
        return provider.findUserCountByQueryCriteria(this);
    }

    public List<User> executeList(CommandContext commandContext, Page page) {
        final NuxeoIdentityProviderSession provider = getNuxeoIdentityProvider(commandContext);
        return provider.findUserByQueryCriteria(this);
    }

    protected NuxeoIdentityProviderSession getNuxeoIdentityProvider(CommandContext commandContext) {
        return (NuxeoIdentityProviderSession) commandContext.getReadOnlyIdentityProvider();
    }

    public UserQuery desc() {
        //TODO
        throw new UnsupportedOperationException("The NUXEO identity provider does not support descending search order.");
    }

}

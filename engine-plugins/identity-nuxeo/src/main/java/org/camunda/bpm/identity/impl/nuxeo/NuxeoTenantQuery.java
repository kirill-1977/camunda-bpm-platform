package org.camunda.bpm.identity.impl.nuxeo;

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.TenantQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

public class NuxeoTenantQuery extends TenantQueryImpl {

    private static final long serialVersionUID = 1L;

    public NuxeoTenantQuery() {
        super();
    }

    public NuxeoTenantQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public long executeCount(CommandContext commandContext) {
        return 0;
    }

    @Override
    public List<Tenant> executeList(CommandContext commandContext, Page page) {
        return Collections.emptyList();
    }

}

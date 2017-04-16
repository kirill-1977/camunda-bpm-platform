package org.camunda.bpm.identity.impl.nuxeo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.NativeUserQuery;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.TenantQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.objects.user.Groups;
import org.nuxeo.client.api.objects.user.Users;

public class NuxeoIdentityProviderSession implements ReadOnlyIdentityProvider {

    private final static Logger LOG = Logger.getLogger(NuxeoIdentityProviderSession.class.getName());

    protected NuxeoConfiguration nuxeoConfiguration;
    protected NuxeoClient nuxeoClient;

    public NuxeoIdentityProviderSession(NuxeoConfiguration nuxeoConfiguration) {
        this.nuxeoConfiguration = nuxeoConfiguration;
    }

    public void flush() {
        // nothing to do
    }

    public void close() {
        if (nuxeoClient != null) {
            try {
                nuxeoClient.logout();
            } catch (Exception e) {
                LOG.log(Level.FINE, "exception while closing NUXEO", e);
            }
        }
    }

    protected NuxeoClient openContext(String username, String password) {
        return new NuxeoClient(nuxeoConfiguration.getNuxeoUrl(), username, password);
    }

    protected void ensureContextInitialized() {
        if (nuxeoClient == null) {
            nuxeoClient = openContext(nuxeoConfiguration.getAdminUsername(), nuxeoConfiguration.getAdminPassword());
        }
    }

    // Users /////////////////////////////////////////////////
    public User findUserById(String userId) {
        ensureContextInitialized();
        org.nuxeo.client.api.objects.user.User nuxeoUser =
                nuxeoClient.getUserManager().fetchUser(userId);
        return nuxeo2camunda(nuxeoUser);
    }

    public UserQuery createUserQuery() {
        return new NuxeoUserQueryImpl(org.camunda.bpm.engine.impl.context.Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    public UserQueryImpl createUserQuery(CommandContext commandContext) {
        return new NuxeoUserQueryImpl();
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        throw new BadUserRequestException("Native user queries are not supported for NUXEO identity service provider.");
    }

    public long findUserCountByQueryCriteria(NuxeoUserQueryImpl query) {
        ensureContextInitialized();
        return findUserByQueryCriteria(query).size();
    }

    public List<User> findUserByQueryCriteria(NuxeoUserQueryImpl query) {
        ensureContextInitialized();
        if (query.getGroupId() != null) {
            return findUsersByGroupId(query);
        } else {
            return findUsersWithoutGroupId(query);
        }
    }

    protected List<User> findUsersByGroupId(NuxeoUserQueryImpl query) {
        String groupId = query.getGroupId(); //TODO ???
        List<User> userList = new ArrayList<User>();
        Users users = nuxeoClient.getUserManager().searchUser("*");
        for (org.nuxeo.client.api.objects.user.User nuxeoUser : users.getUsers()) {
            userList.add(nuxeo2camunda(nuxeoUser));
        }
        return userList;
    }

    public List<User> findUsersWithoutGroupId(NuxeoUserQueryImpl query) {
        List<User> userList = new ArrayList<User>();
        Users users = nuxeoClient.getUserManager().searchUser("*");
        for (org.nuxeo.client.api.objects.user.User nuxeoUser : users.getUsers()) {
            userList.add(nuxeo2camunda(nuxeoUser));
        }
        return userList;
    }

    public boolean checkPassword(String userId, String password) {
        if (password == null) {
            return false;
        }

        if (userId == null || userId.isEmpty()) {
            return false;
        }

        NuxeoUserEntity user = (NuxeoUserEntity) findUserById(userId);
        close();

        if (user == null) {
            return false;
        } else {
            try {
                openContext(user.getId(), password);
                return true;
            } catch (NuxeoAuthenticationException e) {
                return false;
            }
        }
    }

    // Groups ///////////////////////////////////////////////

    public Group findGroupById(String groupId) {
        return createGroupQuery(org.camunda.bpm.engine.impl.context.Context.getCommandContext())
                .groupId(groupId)
                .singleResult();
    }

    public GroupQuery createGroupQuery() {
        return new NuxeoGroupQuery(org.camunda.bpm.engine.impl.context.Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    public GroupQuery createGroupQuery(CommandContext commandContext) {
        return new NuxeoGroupQuery();
    }

    public long findGroupCountByQueryCriteria(NuxeoGroupQuery nuxeoGroupQuery) {
        ensureContextInitialized();
        return findGroupByQueryCriteria(nuxeoGroupQuery).size();
    }

    public List<Group> findGroupByQueryCriteria(NuxeoGroupQuery query) {
        ensureContextInitialized();
        List<Group> groupList = new ArrayList<Group>();

        Group management = new NuxeoGroupEntity();
        management.setId("management");
        management.setName("management");
        management.setType(org.camunda.bpm.engine.authorization.Groups.CAMUNDA_ADMIN);
        groupList.add(management);

        Group sales = new NuxeoGroupEntity();
        sales.setId("sales");
        sales.setName("sales");
        sales.setType(org.camunda.bpm.engine.authorization.Groups.CAMUNDA_ADMIN);
        groupList.add(sales);

        Group accounting = new NuxeoGroupEntity();
        accounting.setId("accounting");
        accounting.setName("accounting");
        accounting.setType(org.camunda.bpm.engine.authorization.Groups.CAMUNDA_ADMIN);
        groupList.add(accounting);

/*
        Groups groups = nuxeoClient.getUserManager().searchGroup("*");
        if (groups.getGroups() != null) {
            for (org.nuxeo.client.api.objects.user.Group nuxeoGroup : groups.getGroups()) {
                groupList.add(nuxeo2camunda(nuxeoGroup));
            }
        }
*/
        return groupList;
    }

    /**
     * @return true if the passed-in user is currently authenticated
     */
    protected boolean isAuthenticatedUser(UserEntity user) {
        if (user.getId() == null) {
            return false;
        }
        return user.getId().equals(org.camunda.bpm.engine.impl.context.Context.getCommandContext().getAuthenticatedUserId());
    }

    protected boolean isAuthorized(Permission permission, Resource resource, String resourceId) {
        return true;
/*

        return org.camunda.bpm.engine.impl.context.Context.getCommandContext()
                .getAuthorizationManager()
                .isAuthorized(permission, resource, resourceId);
*/
    }

    @Override
    public TenantQuery createTenantQuery() {
        return new NuxeoTenantQuery(org.camunda.bpm.engine.impl.context.Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public TenantQuery createTenantQuery(CommandContext commandContext) {
        return new NuxeoTenantQuery();
    }

    @Override
    public Tenant findTenantById(String id) {
        //TODO
        return null;
    }


    private User nuxeo2camunda(org.nuxeo.client.api.objects.user.User nuxeoUser) {
        User result = new NuxeoUserEntity();
        result.setEmail(nuxeoUser.getEmail());
        result.setLastName(nuxeoUser.getLastName());
        result.setFirstName(nuxeoUser.getFirstName());
        result.setPassword(nuxeoUser.getPassword());
        result.setId(nuxeoUser.getUserName());
        return result;

    }

    private Group nuxeo2camunda(org.nuxeo.client.api.objects.user.Group nuxeoGroup) {
        Group result = new NuxeoGroupEntity();
        result.setId(nuxeoGroup.getGroupLabel());
        result.setName(nuxeoGroup.getGroupLabel());
        return result;

    }
}
package org.camunda.bpm.identity.impl.nuxeo;

import org.camunda.bpm.engine.impl.identity.IdentityProviderException;

public class NuxeoAuthenticationException extends IdentityProviderException {

    private static final long serialVersionUID = 1L;

    public NuxeoAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NuxeoAuthenticationException(String message) {
        super(message);
    }

}

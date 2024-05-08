package com.abc.kotak.security;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of a not activated user trying to authenticate.
 */
public class ApplicationNotActiveException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public ApplicationNotActiveException(String message) {
        super(message);
    }

    public ApplicationNotActiveException(String message, Throwable t) {
        super(message, t);
    }
}

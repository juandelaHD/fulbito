package ar.uba.fi.ingsoft1.football5.common.exception;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;

public class UnauthorizedException extends AuthorizationDeniedException {

    public UnauthorizedException(String msg, AuthorizationResult authorizationResult) {
        super(msg, authorizationResult);
    }
}

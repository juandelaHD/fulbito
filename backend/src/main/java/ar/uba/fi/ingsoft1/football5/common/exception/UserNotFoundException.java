package ar.uba.fi.ingsoft1.football5.common.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String entity, String username) {
        super(String.format("Failed to find %s with username %s", entity, username));
    }
}

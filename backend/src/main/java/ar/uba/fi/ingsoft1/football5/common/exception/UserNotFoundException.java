package ar.uba.fi.ingsoft1.football5.common.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String entity, String username) {
        super(String.format("Failed to find %s with username %s", entity, username));
    }
    public UserNotFoundException(String entity, Long id) {
        super(String.format("Failed to find %s with id %d", entity, id));
    }

}

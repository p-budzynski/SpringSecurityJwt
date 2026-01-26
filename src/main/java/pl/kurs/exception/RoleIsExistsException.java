package pl.kurs.exception;

public class RoleIsExistsException extends RuntimeException {
    public RoleIsExistsException(String message) {
        super(message);
    }
}

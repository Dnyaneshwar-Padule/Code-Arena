package exception;

/**
 * Exception for authentication and credential validation failures.
 */
public class AuthenticationException extends ServiceException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

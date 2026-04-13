package exception;

/**
 * Exception representing service-layer and use-case failures.
 */
public class ServiceException extends AppException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

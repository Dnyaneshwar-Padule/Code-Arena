package exception;

/**
 * Exception representing persistence and data-access failures.
 */
public class DaoException extends AppException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}

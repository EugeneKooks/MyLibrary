package by.epam.kooks.dao.exception;

/**
 * @author Eugene Kooks
 */
public class DaoException extends Exception {

    public DaoException(String message, Exception cause) {
        super(message, cause);
    }
}

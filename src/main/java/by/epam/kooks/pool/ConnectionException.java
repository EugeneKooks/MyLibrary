package by.epam.kooks.pool;

/**
 * @author Eugene Kooks
 */
public class ConnectionException extends Exception {
    public ConnectionException(String message, Exception cause) {
        super(message, cause);
    }
}

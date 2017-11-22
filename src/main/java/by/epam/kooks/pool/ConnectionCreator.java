package by.epam.kooks.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


class ConnectionCreator {
    /*Property name of database url*/
    private static final String DB_URL = "db.url";
    /*Property name of database user*/
    private static final String DB_USER = "db.user";
    /*Property name of database password*/
    private static final String DB_PASSWORD = "db.password";
    /*Name of property file*/
    private static final String DB_CONFIG = "database";
    /* Url of the database*/
    private static String url;
    /*Login of the user of database*/
    private static String login;
    /*Password of the user of database*/
    private static String password;

    static {
        try {
            ResourceBundle resourceBundle = PropertyResourceBundle.getBundle(DB_CONFIG);
            url = resourceBundle.getString(DB_URL);
            login = resourceBundle.getString(DB_USER);
            password = resourceBundle.getString(DB_PASSWORD);
        } catch (MissingResourceException e) {
            throw new RuntimeException(String.format("Can not find resource bundle. Reason : %s", e.getMessage()));
        }
    }

    /*
    * Creates new connection and returns ProxyConnection object
    * */
    static ProxyConnection getConnection() throws ConnectionException{
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, login, password);

        } catch (SQLException e) {
            throw new ConnectionException(String.format("Connection was not created. Reason : %s", e.getMessage()), e);
        }
        return new ProxyConnection(connection);
    }
}
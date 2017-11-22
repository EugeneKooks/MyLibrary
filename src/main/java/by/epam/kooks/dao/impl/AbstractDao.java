package by.epam.kooks.dao.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractDao {
    /*Logger*/
    private static final Logger LOGGER = LogManager.getLogger();

    public AbstractDao() {
    }

    /*Closes statement*/
    void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.ERROR, String.format("Can not close statement. Reason : %s", e.getMessage()));
            }
        }
    }

    /*Gets connection back to connection pool*/
    void closeConnection(Connection connection) {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.ERROR, String.format("Can not close connection. Reason : %s", e.getMessage()));
            }
        }
    }
}
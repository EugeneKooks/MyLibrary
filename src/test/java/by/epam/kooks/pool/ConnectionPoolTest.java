package by.epam.kooks.pool;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectionPoolTest {
    private static ConnectionPool connectionPool;
    private static int numberOfConnections ;


    @BeforeClass
    public static void initPool() {
        connectionPool = ConnectionPool.getInstance();
        numberOfConnections = connectionPool.size();
    }

    @Test
    public void connectionPoolInitialized() throws Exception {
        assertNotNull(connectionPool);
    }

    @Test
    public void getConnectionTest() throws Exception {
        Connection connection = connectionPool.getConnection();
        assertNotNull(connection);
        connection.close();
    }

    @Test
    public void releaseConnectionTest() throws Exception {
        Connection connection = connectionPool.getConnection();
        connection.close();
        int actual = connectionPool.size();
        assertEquals(numberOfConnections, actual);
    }
}

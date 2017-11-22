package by.epam.kooks.pool;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ConnectionPool {
    /*Logger*/
    private static final Logger LOGGER = LogManager.getLogger();
    /*Size of the connection pool*/
    private static final int POOL_SIZE = 5;
    /*Lock object for thread safe instantiating of connection pool*/
    private static Lock lock = new ReentrantLock();
    /*Blocking queue to store connections to database*/
    private BlockingQueue<ProxyConnection> connectionQueue;
    /*Instance of connection pool*/
    private static ConnectionPool instance;
    /*AtomicBoolean object for indicating if connection pool created*/
    private static AtomicBoolean isConnectionPoolCreated = new AtomicBoolean(false);

    /*Constructor of connection pool. Private to make it singleton*/
    private ConnectionPool() {
        connectionQueue = new ArrayBlockingQueue<>(POOL_SIZE);
        try {
            initConnectionPool();
        } catch (SQLException e) {
            LOGGER.log(Level.FATAL, String.format("Connection pool was not created. Reason : %s", e.getMessage()));
            throw new RuntimeException(String.format("Connection pool was not created. Reason : %s", e.getMessage()), e);
        }
    }

    /*Gets instance of connection pool*/
    public static ConnectionPool getInstance() {
        if (!isConnectionPoolCreated.get()) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ConnectionPool();
                    isConnectionPoolCreated.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /* Initializes connection pool. If connection pool size less then half of required number of connections
    * RuntimeException is thrown*/
    private void initConnectionPool() throws SQLException {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        for (int i = 0; i < POOL_SIZE; i++) {
            createConnectionAndAddToPool();
        }
        if (!isAllConnectionsCreated()) {
            tryToRecreateConnections();
            if (connectionQueue.size() > POOL_SIZE / 2) {
                LOGGER.log(Level.INFO, "Available connections: " + connectionQueue.size());
            } else {
                throw new RuntimeException("Connection pool has not enough connections to proceed working");
            }
        } else {
            LOGGER.log(Level.INFO, String.format("All connections were created successfully. Connection pool size: %s connections", connectionQueue.size()));
        }
    }

    /*Checks if all connections were created*/
    private boolean isAllConnectionsCreated() {
        return connectionQueue.size() == POOL_SIZE;
    }

    /* Gets connection from connection pool*/
    public ProxyConnection getConnection() {
        ProxyConnection connection = null;
        try {
            connection = connectionQueue.take();
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, String.format("Can not get connection from pool. Reason : %s", e.getMessage()));
        }
        return connection;
    }

    /*Gets size of connection pool*/
    public int size() {
        return connectionQueue.size();
    }

    /*Returns connection to connection pool*/
    void releaseConnection(ProxyConnection connection) {
        connectionQueue.offer(connection);
    }

    /*Destroys all connections and deregisters drivers*/
    public void destroyConnections() {
        int size = connectionQueue.size();
        for (int i = 0; i < size; i++) {
            try {
                ProxyConnection connection = connectionQueue.take();
                connection.closeConnection();
            } catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, String.format("Can not take connection from pool. Reason : %s", e.getMessage()));
            } catch (SQLException e) {
                LOGGER.log(Level.ERROR, String.format("Can not close connection. Reason : %s", e.getMessage()));
            }
        }
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, String.format("Can not deregister driver. Reason : %s", e.getMessage()));
        }
    }

    /*Creates connection and adds to connection pool*/
    private void createConnectionAndAddToPool() {
        try {
            ProxyConnection connection = ConnectionCreator.getConnection();
            connectionQueue.put(connection);
        } catch (ConnectionException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, String.format("Connection was not added to pool. Reason : %s", e.getMessage()));
        }
    }
    /*Tries to recreate connections, if not all were created */
    private void tryToRecreateConnections() {
        int difference = POOL_SIZE - connectionQueue.size();
        for (int i = 0; i < difference; i++) {
            createConnectionAndAddToPool();
        }
    }
}
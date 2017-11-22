package by.epam.kooks.dao.impl;

import by.epam.kooks.entity.Transaction;
import by.epam.kooks.dao.TransactionDao;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.entity.Management;
import by.epam.kooks.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Kooks
 */
public class MySqlTransactionDao extends AbstractDao implements TransactionDao {
    private static final Logger log = LogManager.getLogger(MySqlTransactionDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM transaction WHERE id_transaction = ?";
    private static final String INSERT = "INSERT INTO transaction (id_book_info,id_customer) VALUES(?,?)";
    private static final String UPDATE = "UPDATE transaction SET end_date = ?,id_book_info = ?,id_customer = ? WHERE id_transaction = ? ";
    private static final String DELETE = "DELETE FROM transaction WHERE id_transaction = ?";
    private static final String FIND_BY_CUSTOMER = "SELECT * FROM transaction WHERE id_customer = ?";
    private static final String FIND_BY_MANAGEMENT = "SELECT transaction.id_transaction ,transaction.start_date ,transaction.end_date FROM transaction JOIN management ON management.id_transaction = transaction.id_transaction WHERE management.id_management = ?";
    private static final String ACTIVE_CUSTOMER = "SELECT transaction.id_transaction ,transaction.start_date ,transaction.end_date ,transaction.id_book_info ,transaction.id_customer FROM transaction JOIN management ON transaction.id_transaction  = management.id_transaction WHERE management.return_date IS NOT NULL AND transaction.id_customer = ? limit ?,?";
    private static final String INACTIVE_CUSTOMER = "SELECT transaction.id_transaction ,transaction.start_date ,transaction.end_date ,transaction.id_book_info ,transaction.id_customer FROM transaction JOIN management ON transaction.id_transaction  = management.id_transaction WHERE management.return_date IS NULL AND transaction.id_customer = ? limit ?,?";
    private static final String TRANSACTION_ACTIVE_COUNT = "SELECT COUNT(*) FROM management JOIN transaction ON transaction.id_transaction  = management.id_transaction WHERE management.return_date IS NOT NULL AND transaction.id_customer = ?";
    private static final String TRANSACTION_INACTIVE_COUNT = "SELECT COUNT(*) FROM management JOIN transaction ON transaction.id_transaction  = management.id_transaction WHERE management.return_date IS NULL AND transaction.id_customer = ?";

    @Override
    public Transaction insert(Transaction item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try { connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, item.getBookInfo().getId());
            preparedStatement.setInt(2, item.getCustomer().getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getInt(1));
            log.debug("Creating the transaction entity with id = {}", item.getId());
            return item;
        } catch (SQLException e) {
            log.warn("Can't insert {} where value equals : {}", this.getClass().getSimpleName(), item);
            throw new DaoException("can't insert " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public Transaction findById(int id) throws DaoException {
        Transaction transaction = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    transaction = itemTransaction(resultSet);
                }
            return transaction;
        } catch (SQLException e) {
            log.warn("Can't find the transaction entity where id equals : {} ", id, e);
            throw new DaoException("can't find by id ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public void update(Transaction item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(UPDATE);
                statementTransaction(preparedStatement, item);
                preparedStatement.setInt(4, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Update the transaction entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't update the transaction entity where id equals : {} ", item.getId(), e);
            throw new DaoException("can't update" + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public void delete(Transaction item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(DELETE);
                preparedStatement.setInt(1, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Delete the transaction entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't delete the transaction entity where id equals : {} ", item.getId(), e);
            throw new DaoException("can't delete " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public List<Transaction> findByCustomer(Transaction transaction) throws DaoException {
        List<Transaction> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_CUSTOMER);
            preparedStatement.setInt(1, transaction.getCustomer().getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    transaction = itemTransaction(resultSet);
                    list.add(transaction);
                }
            return  list;
        } catch (SQLException e) {
            log.warn("Can't find transaction entity by customer where customer id equals : {} ", transaction.getCustomer().getId(), e);
            throw new DaoException("can't find by customer", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public List<Transaction> getListTransactionByCustomer(Transaction transaction, int start, int count, boolean isActive) throws DaoException {
        List<Transaction> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(isActive ? ACTIVE_CUSTOMER : INACTIVE_CUSTOMER);
            preparedStatement.setInt(1, transaction.getCustomer().getId());
            preparedStatement.setInt(2, ((start - 1) * count));
            preparedStatement.setInt(3, count);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    transaction = itemTransaction(resultSet);
                    list.add(transaction);
                }
            return list;
        } catch (SQLException e)
        {
            log.warn("Can't get transaction list by range {} to {} where book transaction id equals : {} ", start, count, transaction.getId(), e);
            throw new DaoException("can't get list of transaction by customer ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public Transaction findByManagement(Management management) throws DaoException {
        Transaction transaction = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_MANAGEMENT);
            preparedStatement.setInt(1, management.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transaction = itemTransaction(resultSet);
            }
                return transaction;}
        catch (SQLException e) {
            log.warn("Can't find transaction entity by management where id equals : {} ", management.getId(), e);
            throw new DaoException("can't find by management ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }}

    @Override
    public int getTransactionCountByCustomer(Transaction transaction, boolean isActive) throws DaoException {
        int count = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(isActive ? TRANSACTION_ACTIVE_COUNT : TRANSACTION_INACTIVE_COUNT);
            preparedStatement.setInt(1, transaction.getCustomer().getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            return count;
        } catch (SQLException e) {
            log.warn("Can't get transaction entity count by customer where id equals : {} ", transaction.getCustomer().getId(), e);
            throw new DaoException("can't count by active/inactive ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    private Transaction itemTransaction(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt(1));
        transaction.setStartDate(resultSet.getDate(2));
        transaction.setEndDate(resultSet.getTimestamp(3));
        return transaction;
    }
    private PreparedStatement statementTransaction(PreparedStatement statement, Transaction item) throws SQLException {
        if (item.getEndDate() == null) {
            statement.setNull(1, Types.DATE);
        } else {
            statement.setTimestamp(1, item.getEndDate());
        }
        statement.setInt(2, item.getBookInfo().getId());
        statement.setInt(3, item.getCustomer().getId());
        return statement;
    }
}

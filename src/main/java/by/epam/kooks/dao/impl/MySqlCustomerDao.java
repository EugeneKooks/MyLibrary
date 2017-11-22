package by.epam.kooks.dao.impl;

import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.Management;
import by.epam.kooks.dao.CustomerDao;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Kooks
 */
public class MySqlCustomerDao extends AbstractDao implements CustomerDao {
    private static final Logger log = LogManager.getLogger(MySqlCustomerDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM customer WHERE id_customer = ?";
    private static final String INSERT = "INSERT INTO customer VALUES (id_customer,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE customer SET register_date = ?,password = ?,email = ?,id_person = ?,id_role = ? WHERE id_customer = ?";
    private static final String DELETE = "DELETE FROM customer  WHERE id_customer = ?";
    private static final String COUNT_CUSTOMER = "SELECT COUNT(*) FROM customer";
    private static final String FIND_BY_LOGIN = "SELECT * FROM customer  WHERE email = ?";
    private static final String FIND_BY_LOGIN_PASSWORD = "SELECT * FROM customer  WHERE email = ?  AND password = ?";
    private static final String FIND_BY_MANAGEMENT = "SELECT customer.id_customer ,customer.email FROM customer JOIN transaction ON customer.id_customer  = transaction.id_customer JOIN management ON management.id_transaction  = transaction.id_transaction WHERE management.id_management = ?";
    private static final String LIMIT_CUSTOMER = "SELECT * FROM customer limit ?,?";
    @Override
    public Customer insert(Customer item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            statementCustomer(preparedStatement, item);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getInt(1));
            log.debug("Create the customer entity where id = {} ", item.getId());
            return item;
        } catch (SQLException e) {
            log.warn("Can't insert {} where value equals : {}", item, e);
            throw new DaoException("can't insert customer = {}" + item, e);
        }
        finally {
                closeStatement(preparedStatement);
                closeConnection(connection);
            }
        }
    @Override
    public Customer findById(int id) throws DaoException {
        Customer customer = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    customer = itemCustomer(resultSet);
                }
            return customer;
        } catch (SQLException e) {
            log.warn("Can't find the customer entity where id equals : {} ", id, e);
            throw new DaoException("can't find by id ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void update(Customer item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(UPDATE);
            statementCustomer(preparedStatement, item);
            preparedStatement.setInt(6, item.getId());
            preparedStatement.executeUpdate();
            log.debug("Update the customer entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't update the customer entity where id equals : {}", item.getId(), e);
            throw new DaoException("can't update customer = {} " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public void delete(Customer item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(DELETE);
                preparedStatement.setInt(1, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Delete the customer entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't delete the customer entity where id equals : {}", item.getId(), e);
            throw new DaoException("can't delete customer = {}" + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }


    @Override
    public int getCustomerCount() throws DaoException {
        int count = 0;
        Connection connection = null;
        Statement statement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(COUNT_CUSTOMER);
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            return count;
        } catch (SQLException e) {
            log.warn("Can't get customer count {} " + this.getClass().getSimpleName(), e);
            throw new DaoException("can't get count customer ", e);
        }
        finally {
            closeStatement(statement);
            closeConnection(connection);
        }
    }
    @Override
    public Customer getCustomer(String login) throws DaoException {
        Customer customer = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_LOGIN);
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        customer = itemCustomer(resultSet);
                    }
            return  customer;
        } catch (SQLException e) {
            log.warn("Can't find the customer entity by login {} ", login, e);
            throw new DaoException("can't get by login ", e);
        }
      finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public Customer getCustomer(String login, String password) throws DaoException {
        Customer customer = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_LOGIN_PASSWORD);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    customer = itemCustomer(resultSet);
                }
            return customer;
        } catch (SQLException e) {
            log.warn("Can't find the customer entity by login{} and password **** ", login, e);
            throw new DaoException("can't get by login and password ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public Customer findByManagement(Management management) throws DaoException {
        Customer customer = new Customer();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_MANAGEMENT);
            preparedStatement.setInt(1, management.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    customer.setId(resultSet.getInt(1));
                    customer.setEmail(resultSet.getString(2));
                }
                return customer;
        } catch (SQLException e) {
            log.warn("Can't find the customer entity by management where management id equals : {} ", management.getId(), e);
            throw new DaoException("can't find by management ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public List<Customer> getLimitCustomers(int start, int count) throws DaoException {
        List<Customer> list = new ArrayList<>();
        Customer customer = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(LIMIT_CUSTOMER);
            preparedStatement.setInt(1, ((start - 1) * count));
            preparedStatement.setInt(2, count);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    customer = itemCustomer(resultSet);
                    list.add(customer);
                }
            return list;
        } catch (SQLException e) {
            log.warn("Can't get customer list by range {} to {} ", start, count, e);
            throw new DaoException("can't get list of customer ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private PreparedStatement statementCustomer(PreparedStatement statement, Customer item) throws SQLException {
        statement.setDate(1, item.getRegisterDate());
        statement.setString(2, item.getPassword());
        statement.setString(3, item.getEmail());
        statement.setInt(4, item.getPerson().getId());
        statement.setInt(5, item.getCustomerRole().getId());
        return statement;
    }

    private Customer itemCustomer(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setId(resultSet.getInt(1));
        customer.setRegisterDate(resultSet.getDate(2));
        customer.setPassword(resultSet.getString(3));
        customer.setEmail(resultSet.getString(4));
        return customer;
    }
}

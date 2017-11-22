package by.epam.kooks.dao.impl;

import by.epam.kooks.dao.CustomerRoleDao;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.CustomerRole;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eugene Kooks
 */
public class MySqlCustomerRoleDao extends AbstractDao implements CustomerRoleDao {
    private static final Logger log = LogManager.getLogger(MySqlCustomerRoleDao.class);

    private static final String FIND_BY_CUSTOMER = "SELECT role.id_role,role.name FROM role JOIN customer ON customer.id_role  = role.id_role WHERE customer.id_customer = ?";
    private static final String FIND_BY_NAME_ROLE = "SELECT * FROM role  WHERE name = ?";


    @Override
    public CustomerRole findByCustomer(Customer customer) throws DaoException {
        CustomerRole customerRole = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_CUSTOMER);
            preparedStatement.setInt(1, customer.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    customerRole = itemRole(resultSet);
                }
                return customerRole;
        } catch (SQLException e) {
            log.warn("Can't find customerRole entity by customer where customer id equals : {}", customer.getId(), e);
            throw new DaoException("can't find by customer ", e);
        }
             finally {
                closeStatement(preparedStatement);
                closeConnection(connection);
            }
        }

    @Override
    public CustomerRole findRoleByName(String nameRole) throws DaoException {
        CustomerRole customerRole = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_NAME_ROLE);
            preparedStatement.setString(1, nameRole);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    customerRole = itemRole(resultSet);
                }
                return customerRole;
        } catch (SQLException e) {
            log.warn("Can't find customerRole entity by name where name equals : {} ", nameRole, e);
            throw new DaoException("can't find by role ", e);
        }
       finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private CustomerRole itemRole(ResultSet resultSet) throws SQLException {
        CustomerRole customerRole = new CustomerRole();
        customerRole.setId(resultSet.getInt(1));
        customerRole.setName(resultSet.getString(2));
        return customerRole;
    }


    @Override
    public CustomerRole insert(CustomerRole item) throws DaoException {
        return null;
    }

    @Override
    public CustomerRole findById(int id) throws DaoException {
        return null;
    }

    @Override
    public void update(CustomerRole item) throws DaoException {

    }

    @Override
    public void delete(CustomerRole item) throws DaoException {

    }
}

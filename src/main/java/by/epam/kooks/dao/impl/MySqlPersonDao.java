package by.epam.kooks.dao.impl;

import by.epam.kooks.dao.PersonDao;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.Person;
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
public class MySqlPersonDao extends AbstractDao implements PersonDao {
    private static final Logger log = LogManager.getLogger(MySqlPersonDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM person WHERE id_person = ?";
    private static final String INSERT = "INSERT INTO person VALUES (id_person,?,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE person SET first_name = ?,last_name = ?,middle_name = ?,phone = ?,birthday = ?,address = ? WHERE id_person = ?";
    private static final String DELETE = "DELETE FROM person WHERE id_person = ?";
    private static final String FIND_BY_CUSTOMER = "SELECT person.id_person,person.first_name,person.last_name,person.middle_name,person.phone,person.birthday,person.address FROM person JOIN customer ON customer.id_person  = person.id_person WHERE customer.id_customer = ?";

    @Override
    public Person insert(Person item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
                try {connection = ConnectionPool.getInstance().getConnection();
                     preparedStatement = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            statementPerson(preparedStatement, item).executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getInt(1));
            log.debug("Create the person entity where id = {}", item.getId());
            return item;
        } catch (SQLException e) {
            log.warn("Can't insert {} where value equals : {} ", this.getClass().getSimpleName(), item, e);
            throw new DaoException("can't insert " + item, e);
        }
        finally {
                    closeStatement(preparedStatement);
                    closeConnection(connection);
                }
    }

    @Override
    public Person findById(int id) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Person person = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_ID);
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        person = itemPerson(resultSet);
                    }
                    return person;
        } catch (SQLException e) {
            log.warn("Can't find the person entity where id equals : {} ", id, e);
            throw new DaoException("can't find by id ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public void update(Person item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(UPDATE);
                statementPerson(preparedStatement, item);
                preparedStatement.setInt(7, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Update the person entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't update the person entity where id equals : {}", item.getId());
            throw new DaoException("can't update " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void delete(Person item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(DELETE);
                preparedStatement.setInt(1, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Delete the person entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't delete the person entity where id equals : {} ", item.getId(), e);
            throw new DaoException("can't delete  " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public Person findByCustomer(Customer customer) throws DaoException {
        Person person = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_CUSTOMER);
                preparedStatement.setInt(1, customer.getId());
                ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        person = itemPerson(resultSet);
                                    }
                                    return person;
        } catch (SQLException e) {
            log.warn("Can't find person entity by customer where id equals : {} ", customer.getId(), e);
            throw new DaoException("can't find by customer ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private PreparedStatement statementPerson(PreparedStatement statement, Person item) throws SQLException {
        statement.setString(1, item.getFirstName());
        statement.setString(2, item.getLastName());
        statement.setString(3, item.getMiddleName());
        statement.setString(4, item.getPhone());
        statement.setDate(5, item.getBirthday());
        statement.setString(6, item.getAddress());
        return statement;
    }

    private Person itemPerson(ResultSet resultSet) throws SQLException {
        Person person = new Person();
        person.setId(resultSet.getInt(1));
        person.setFirstName(resultSet.getString(2));
        person.setLastName(resultSet.getString(3));
        person.setMiddleName(resultSet.getString(4));
        person.setPhone(resultSet.getString(5));
        person.setBirthday(resultSet.getDate(6));
        person.setAddress(resultSet.getString(7));
        return person;
    }
}

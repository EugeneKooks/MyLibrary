package by.epam.kooks.dao.impl;

import by.epam.kooks.dao.ManagementDao;
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
public class MySqlManagementDao extends AbstractDao implements ManagementDao {
    private static final Logger log = LogManager.getLogger(MySqlManagementDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM management WHERE id_management = ?";
    private static final String INSERT = "INSERT INTO management VALUES (id_management,?,?)";
    private static final String UPDATE = "UPDATE management SET return_date = ?,id_transaction = ? WHERE id_management = ?";
    private static final String ACTIVE_MANAGEMENT = "SELECT * FROM management WHERE return_date IS NULL limit ?,?";
    private static final String INACTIVE_MANAGEMENT = "SELECT * FROM management WHERE return_date IS NOT NULL limit ?,?";
    private static final String MANAGEMENT_ACTIVE_COUNT = "SELECT COUNT(*) FROM management WHERE return_date IS NULL";
    private static final String MANAGEMENT_INACTIVE_COUNT = "SELECT COUNT(*) FROM management WHERE return_date IS NOT NULL";

    @Override
    public Management insert(Management item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            statementManagement(preparedStatement, item);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getInt(1));
            log.debug("Create the management entity where id = {}", item.getId());
            return item;
        } catch (SQLException e) {
            log.warn("Can't insert {} where value equals : {}", item);
            throw new DaoException("can't insert " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public Management findById(int id) throws DaoException {
        Management management = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    management = itemManagement(resultSet);
            }
            return management;
        } catch (SQLException e) {
            log.warn("Can't find the management entity where id equals : {} ", id, e);
            throw new DaoException("can find by id ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void update(Management item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(UPDATE);
                statementManagement(preparedStatement, item);
                preparedStatement.setInt(3, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Update the management entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't update the management entity where id equals : {} ", item.getId(), e);
            throw new DaoException("can't update  " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void delete(Management item) throws DaoException {
    }

    @Override
    public List<Management> getListManagement(int start, int count, boolean isActive) throws DaoException {
        List<Management> list = new ArrayList<>();
        Management management = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(isActive ? INACTIVE_MANAGEMENT : ACTIVE_MANAGEMENT);
            preparedStatement.setInt(1, ((start - 1) * count));
            preparedStatement.setInt(2, count);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    management = itemManagement(resultSet);
                    list.add(management);
                }
            return list;
        } catch (SQLException e) {
            log.warn("Can't get management list by isActivity{} where page range {} to {} ", isActive, start, count, e);
            throw new DaoException("Can't get list of management ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }


    @Override
    public int getManagementCount(boolean isActive) throws DaoException {
        int count = 0;
Connection connection = null;
PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(isActive ? MANAGEMENT_INACTIVE_COUNT : MANAGEMENT_ACTIVE_COUNT);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            return count;
        } catch (SQLException e) {
            log.warn("Can't get management count", e);
            throw new DaoException("Can't count by active/inactive ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private Management itemManagement(ResultSet resultSet) throws SQLException {
        Management management = new Management();
        management.setId(resultSet.getInt(1));
        management.setReturnDate(resultSet.getTimestamp(2));
        return management;
    }

    private PreparedStatement statementManagement(PreparedStatement statement, Management item) throws SQLException {
        if (item.getReturnDate() == null) {
            statement.setNull(1, Types.DATE);
        } else {
            statement.setTimestamp(1, item.getReturnDate());
        }
        statement.setInt(2, item.getTransaction().getId());
        return statement;
    }
}

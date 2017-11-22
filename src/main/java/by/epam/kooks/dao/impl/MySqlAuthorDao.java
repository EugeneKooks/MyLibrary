package by.epam.kooks.dao.impl;

import by.epam.kooks.dao.AuthorDao;
import by.epam.kooks.entity.Author;
import by.epam.kooks.entity.Book;
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
public class MySqlAuthorDao extends AbstractDao implements AuthorDao {
    private static final Logger log = LogManager.getLogger(MySqlAuthorDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM author WHERE id_author = ?";
    private static final String INSERT = "INSERT INTO author VALUES(id_author,?,?)";
    private static final String UPDATE = "UPDATE author SET first_name = ?,last_name =? WHERE id_author = ?";
    private static final String DELETE = "DELETE FROM author WHERE id_author = ?";
    private static final String FIND_BY_BOOK = "SELECT author.id_author,author.first_name,author.last_name FROM author JOIN book ON book.id_author  = author.id_author WHERE book.id_book = ?";

    @Override
    public Author insert(Author item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(INSERT,PreparedStatement.RETURN_GENERATED_KEYS);
            statement(preparedStatement, item).executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getInt(1));
            log.debug("Create the author entity where id = {}", item.getId());
            return item;
        } catch (SQLException e) {
            log.warn("Can't insert where value equals : {} ", item, e);
            throw new DaoException("Can't insert " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public Author findById(int id) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Author author = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    author = itemAuthor(resultSet);
                }
                return author;
        } catch (SQLException e) {
            log.warn("Can't find the author entity where id equals : {} ", id, e);
            throw new DaoException("Can't find by id  ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
            }
    @Override
    public void update(Author item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(UPDATE);
                statement(preparedStatement, item);
                preparedStatement.setInt(3, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Update the author entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't update the author entity where id equals : {} ", item.getId(), e);
            throw new DaoException("can't update " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public void delete(Author item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setInt(1, item.getId());
            preparedStatement.executeUpdate();
            log.debug("Delete the author entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't delete the author entity where id equals : {} ", item.getId(), e);
            throw new DaoException("can't delete author " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public Author findByBook(Book book) throws DaoException {
        Author author = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_BOOK);
            preparedStatement.setInt(1, book.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    author = itemAuthor(resultSet);
            }
            return author;
        } catch (SQLException e) {
            log.warn("Can't find the author entity by book where book id equals : {}", book.getId());
            throw new DaoException("can't find by book ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    private PreparedStatement statement(PreparedStatement statement, Author item) throws SQLException {
        statement.setString(1, item.getFirstName());
        statement.setString(2, item.getLastName());
        return statement;
    }

    private Author itemAuthor(ResultSet resultSet) throws SQLException {
        Author author = new Author();
        author.setId(resultSet.getInt(1));
        author.setFirstName(resultSet.getString(2));
        author.setLastName(resultSet.getString(3));

        return author;
    }
}

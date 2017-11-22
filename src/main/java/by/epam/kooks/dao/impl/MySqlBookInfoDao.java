package by.epam.kooks.dao.impl;

import by.epam.kooks.dao.BookInfoDao;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.entity.Transaction;
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
public class MySqlBookInfoDao extends AbstractDao implements BookInfoDao {
    private static final Logger log = LogManager.getLogger(MySqlBookInfoDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM book_info WHERE id_book_info = ?";
    private static final String INSERT = "INSERT INTO book_info VALUES (id_book_info,?,?,?)";
    private static final String UPDATE = "UPDATE book_info SET amount = ?,price = ?,id_book = ? WHERE id_book_info = ?";
    private static final String DELETE = "DELETE FROM book_info WHERE id_book_info = ?";
    private static final String FIND_BY_BOOK = "SELECT book_info.id_book_info,book_info.amount,book_info.price FROM book_info JOIN book ON book.id_book  = book_info.id_book WHERE book.id_book = ?";
    private static final String FIND_BY_TRANSACTION = "SELECT book_info.id_book_info,book_info.amount,book_info.price FROM book_info JOIN transaction ON transaction.id_book_info  = book_info.id_book_info WHERE transaction.id_transaction = ?";

    @Override
    public BookInfo insert(BookInfo item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
                statementBookInfo(preparedStatement, item);
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    resultSet.next();
                    item.setId(resultSet.getInt(1));
                    log.debug("Create the bookInfo entity where id = {}", item.getId());
                    return item;
        } catch (SQLException e) {
            log.warn("Can't insert where value equals : {}", item, e);
            throw new DaoException("can't insert " + item, e);
        }
      finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public BookInfo findById(int id) throws DaoException {
        return findBookInfoByValue(FIND_BY_ID, id);
    }

    @Override
    public void update(BookInfo item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try { connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(UPDATE);
                statementBookInfo(preparedStatement, item);
                preparedStatement.setInt(4, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Update the bookInfo entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't update the bookInfo entity where id equals : {}", item.getId(), e);
            throw new DaoException("can't update bookInfo" + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public void delete(BookInfo item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setInt(1, item.getId());
            preparedStatement.executeUpdate();
            log.debug("Delete the bookInfo entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't delete the bookInfo entity where id equals : {}", item.getId(), e);
            throw new DaoException("can't delete bookInfo" + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public BookInfo findByBook(int id) throws DaoException {
        return findBookInfoByValue(FIND_BY_BOOK, id);
    }


    @Override
    public BookInfo findByTransaction(Transaction transaction) throws DaoException {
        BookInfo bookInfo = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_TRANSACTION);
            preparedStatement.setInt(1, transaction.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    bookInfo = itemBookInfo(resultSet);
                }
                return bookInfo;
        } catch (SQLException e) {
            log.warn("Can't find the bookInfo entity by transaction where transaction id equals : {}", transaction.getId(), e);
            throw new DaoException("can't find by transaction ", e);
        }
       finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private BookInfo itemBookInfo(ResultSet resultSet) throws SQLException {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setId(resultSet.getInt(1));
        bookInfo.setAmount(resultSet.getInt(2));
        bookInfo.setPrice(resultSet.getInt(3));
        return bookInfo;
    }

    private PreparedStatement statementBookInfo(PreparedStatement statement, BookInfo item) throws SQLException {
        statement.setInt(1, item.getAmount());
        statement.setInt(2, item.getPrice());
        statement.setInt(3, item.getBook().getId());
        return statement;
    }

    private BookInfo findBookInfoByValue(String sqlQuery, int id) throws DaoException {
        BookInfo bookInfo = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    bookInfo = itemBookInfo(resultSet);
                }
                return bookInfo;
        } catch (SQLException e) {
            log.warn("Can't find the bookInfo entity by book where book id equals : {}", id, e);
            throw new DaoException("can't find by book ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
}

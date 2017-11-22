package by.epam.kooks.dao.impl;

import by.epam.kooks.dao.BookDao;
import by.epam.kooks.entity.Book;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.entity.Genre;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.entity.Person;
import by.epam.kooks.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.security.provider.PolicyParser;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Kooks
 */
public class MySqlBookDao extends AbstractDao implements BookDao {
    private static final Logger log = LogManager.getLogger(MySqlBookDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM book WHERE id_book = ?";
    private static final String INSERT = "INSERT INTO book VALUES (id_book,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE book SET name = ?,isbn = ?,description = ?,id_genre = ?,id_author = ? WHERE id_book = ?";
    private static final String DELETE = "DELETE FROM book WHERE id_book = ?";
    private static final String COUNT_BOOK_BY_GENRE = "SELECT COUNT(*) FROM book WHERE id_genre = ?";
    private static final String LIMIT_BOOK_BY_GENRE = "select * from book  where id_genre = ? limit ?,? ";
    private static final String FIND_BY_NAME = "SELECT * FROM book  WHERE name = ?";
    private static final String FIND_BY_BOOK = "SELECT book.id_book,book.name,book.isbn,book.description,book.id_genre ,book.id_author FROM book JOIN book_info ON book_info.id_book  = book.id_book WHERE book_info.id_book_info = ?";
    private static final String FIND_BY_ISBN = "SELECT * FROM book WHERE isbn = ?";

    @Override
    public Book insert(Book item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            statementBook(preparedStatement, item);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getInt(1));
            log.debug("Create the book entity where id = {}", item.getId());
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
    public Book findById(int id) throws DaoException {
        Book book = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                book = itemBook(resultSet);
            }
            return book;
        } catch (SQLException e) {
            log.warn("Can't find the book entity where id equals : {}", id, e);
            throw new DaoException("can't find by id", e);
        } finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void update(Book item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
              preparedStatement = connection.prepareStatement(UPDATE);
                statementBook(preparedStatement, item);
                preparedStatement.setInt(6, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Update the book entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't update the book entity where id equals : {}", item.getId(), e);
            throw new DaoException("can't update " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void delete(Book item) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(DELETE);
                preparedStatement.setInt(1, item.getId());
                preparedStatement.executeUpdate();
            log.debug("Delete the book entity where id = {} : ", item.getId());
        } catch (SQLException e) {
            log.warn("Can't delete the book entity where id equals : {}", item.getId(), e);
            throw new DaoException("can't delete book " + item, e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public int getBookCountByGenre(Genre genre) throws DaoException {
        int count = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(COUNT_BOOK_BY_GENRE);
            preparedStatement.setInt(1, genre.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
        return  count;
        } catch (SQLException e) {
            log.warn("Can't get book count by genre where genre id equals : {} ", genre.getId(), e);
            throw new DaoException("can't count by genre ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public List<Book> getLimitBookByGenre(Genre genre, int start, int count) throws DaoException {
        List<Book> list = new ArrayList<>();
        Book book = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
                 preparedStatement = connection.prepareStatement(LIMIT_BOOK_BY_GENRE);
                preparedStatement.setInt(1, genre.getId());
                preparedStatement.setInt(2, ((start - 1) * count));
                preparedStatement.setInt(3, count);
                ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        book = itemBook(resultSet);
                        list.add(book);
                    }
                return list;

        } catch (SQLException e) {
            log.warn("Can't get book list by range {} to {} where book genre equals : {} ", start, count, genre, e);
            throw new DaoException("can't get list of book by genre ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public List<Book> findByName(String name) throws DaoException {
        List<Book> list = new ArrayList<>();
        Book book = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_NAME);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    book = itemBook(resultSet);
                    list.add(book);
                }
            return list;
        } catch (SQLException e) {
            log.warn("Can't find the book entity by book where book name equals : {} ", name, e);
            throw new DaoException("Can't find by name ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public Book findByBookInfo(BookInfo bookInfo) throws DaoException {
        Book book = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_BOOK);
            preparedStatement.setInt(1, bookInfo.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    book = itemBook(resultSet);
                }
                return book;
        } catch (SQLException e) {
            log.warn("Can't find the book entity by bookInfo where bookInfo id equals : {} ", bookInfo.getId());
            throw new DaoException("can't find by bookInfo ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public Book findByIsbn(String isbn) throws DaoException {
        Book book = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_ISBN);
            preparedStatement.setString(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    book = itemBook(resultSet);
            }
            return book;
        } catch (SQLException e) {
            log.warn("Can't find the book entity by book where book isbn equals : {} ", isbn, e);
            throw new DaoException("can't find by isbn ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private Book itemBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getInt(1));
        book.setName(resultSet.getString(2));
        book.setIsbn(resultSet.getString(3));
        book.setDescription(resultSet.getString(4));
        return book;
    }

    private PreparedStatement statementBook(PreparedStatement statement, Book item) throws SQLException {
        statement.setString(1, item.getName());
        statement.setString(2, item.getIsbn());
        statement.setString(3, item.getDescription());
        statement.setInt(4, item.getGenre().getId());
        statement.setInt(5, item.getAuthor().getId());
        return statement;
    }}


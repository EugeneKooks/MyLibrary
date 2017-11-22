package by.epam.kooks.dao.impl;

import by.epam.kooks.dao.GenreDao;
import by.epam.kooks.entity.Book;
import by.epam.kooks.entity.Genre;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Kooks
 */
public class MySqlGenreDao extends AbstractDao implements GenreDao {
    private static final Logger log = LogManager.getLogger(MySqlGenreDao.class);

    private static final String FIND_BY_ID = "SELECT * FROM genre WHERE id_genre = ?";
    private static final String SELECT_ALL = "SELECT * FROM genre";
    private static final String FIND_BY_BOOK = "SELECT genre.id_genre,genre.name FROM genre JOIN book ON book.id_genre  = genre.id_genre WHERE book.id_book = ?";

    @Override
    public Genre insert(Genre item) throws DaoException {
        return null;
    }

    @Override
    public Genre findById(int id) throws DaoException {
        Genre genre = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    genre = itemGenre(resultSet);
                }
            return genre;
        } catch (SQLException e) {
            log.warn("Can't find the genre entity where id equals : {} ", id, e);
            throw new DaoException("can't find by id ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public void update(Genre item) throws DaoException {

    }

    @Override
    public void delete(Genre item) throws DaoException {

    }


    @Override
    public List<Genre> getAllGenre() throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Genre> list = new ArrayList<>();
        Genre genre = null;
        try {connection = ConnectionPool.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SELECT_ALL);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    genre = itemGenre(resultSet);
                    list.add(genre);
                }
            return list;
        } catch (SQLException e) {
            log.warn("Can't get all genre list ", e);
            throw new DaoException("can't get all list ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }
    @Override
    public Genre findByBook(Book book) throws DaoException {
        Genre genre = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_BOOK);
            statement.setInt(1, book.getId());
            ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    genre = itemGenre(resultSet);
                }
            return genre;
        } catch (SQLException e) {
            throw new DaoException("can't find by book ", e);
        }
        finally {
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private Genre itemGenre(ResultSet resultSet) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt(1));
        genre.setName(resultSet.getString(2));
        return genre;
    }
}

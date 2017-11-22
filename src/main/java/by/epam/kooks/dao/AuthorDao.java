package by.epam.kooks.dao;

import by.epam.kooks.entity.Author;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.entity.Book;

/**
 * Interface, describes additional queries for the author table in the database.
 *
 * @author Eugene Kooks
 */
public interface AuthorDao extends Dao<Author>{

    /**
     * The method searches with the Book entity and returns the Author entity.
     *
     * @param book - entity.
     * @return Returns a concrete entity.
     */
    Author findByBook(Book book) throws DaoException;


}
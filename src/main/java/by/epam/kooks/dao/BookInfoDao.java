package by.epam.kooks.dao;

import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.entity.Transaction;

/**
 * Interface, describes additional queries for the bookInfo table in the database.
 *
 * @author Eugene Kooks
 */
public interface BookInfoDao extends Dao<BookInfo> {

    /**
     * Method, to search for a book by the number in the table.
     *
     * @param id - unique number
     * @return Returns the concise essence of BookInfo.
     */
    BookInfo findByBook(int id) throws DaoException;

    /**
     * Метод, для поиск книги с учетом сущности Transaction.
     *
     * @param transaction - entity
     * @return Returns a specific BookInfo entity.
     */
    BookInfo findByTransaction(Transaction transaction) throws DaoException;
}

package by.epam.kooks.service;

import by.epam.kooks.dao.impl.MySqlAuthorDao;
import by.epam.kooks.dao.impl.MySqlBookDao;
import by.epam.kooks.dao.impl.MySqlBookInfoDao;
import by.epam.kooks.dao.impl.MySqlGenreDao;
import by.epam.kooks.entity.Author;
import by.epam.kooks.entity.Book;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.entity.Genre;
import by.epam.kooks.dao.AuthorDao;
import by.epam.kooks.dao.BookDao;
import by.epam.kooks.dao.exception.DaoException;
import by.epam.kooks.dao.BookInfoDao;
import by.epam.kooks.dao.GenreDao;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

/**
 * Class - service, perform all manipulations and transactions associated with the book.
 *
 * @author Eugene Kooks
 */
public class BookService {
    private static final Logger log = LogManager.getLogger(BookService.class);
    /**
     * Method, performs book registration.
     *
     * @param bookInfo - book , witch need to register.
     */
    public void registerBook(BookInfo bookInfo) throws ServiceException {
                  try {
                AuthorDao authorDao = new MySqlAuthorDao();
                BookDao bookDao = new MySqlBookDao();
                BookInfoDao bookInfoDao = new MySqlBookInfoDao();
                authorDao.insert(bookInfo.getBook().getAuthor());
                bookDao.insert(bookInfo.getBook());
                bookInfoDao.insert(bookInfo);
            } catch (DaoException e) {
                log.info("Can't register book where bookInfo id equals: {} ", bookInfo.getId(), e);
                throw new ServiceException("Can't register book", e);
            }
        }
    /**
     * Method, performs search bookInfo by bookInfo id.
     *
     * @param id - bookInfo id
     * @return Specific entity bookInfo
     */
    public BookInfo findBookInfoById(int id) throws ServiceException {

            try {
                log.debug("Find bookInfo by id: {}", id);
                BookInfoDao bookInfoDao = new MySqlBookInfoDao();
                return bookInfoDao.findById(id);
            } catch (DaoException e) {
                log.warn("can't find by id book{}", id, e);
                throw new ServiceException("can't find by id book", e);
            }
        }
    /**
     * Method, performs search bookInfo by book id.
     *
     * @param id - book id
     * @return Specific entity bookInfo
     */
    public BookInfo findBookInfoByBookId(int id) throws ServiceException {
            try {
                log.debug("Find bookInfo by book id: {}", id);
                BookInfoDao bookInfoDao = new MySqlBookInfoDao();
                BookDao bookDao = new MySqlBookDao();
                BookInfo bookInfo = bookInfoDao.findByBook(id);
                Book book = null;
                if(bookInfo!=null) {
                     book = bookDao.findByBookInfo(bookInfo);
                }else{
                    log.warn("Can't find by id book{}", id);
                    throw new ServiceException("Can't find by id book");
                }
                fillBook(book);
                bookInfo.setBook(book);
                return bookInfo;
            } catch (DaoException e) {
                log.warn("Can't find by id book{}", id, e);
                throw new ServiceException("Can't find by id book", e);
            }
        }
    /**
     * Method, performs delete bookInfo by bookInfo id.
     *
     * @param bookInfo - entity must contain an bookInfo id
     */
    public void deleteBook(BookInfo bookInfo) throws ServiceException{
            try {
                log.debug("Start delete a book where bookInfo id equals :{}", bookInfo.getId());
                BookInfoDao bookInfoDao = new MySqlBookInfoDao();
                BookDao bookDao = new MySqlBookDao();
                AuthorDao authorDao = new MySqlAuthorDao();
                Book book = bookDao.findByBookInfo(bookInfo);
                Author author = authorDao.findByBook(book);
                bookInfoDao.delete(bookInfo);
                bookDao.delete(book);
                authorDao.delete(author);
                log.info("Delete a book where bookInfo id equals :{}", bookInfo.getId());
            } catch (DaoException e) {
                log.warn("Can't delete book where bookInfo id equals: {} ", bookInfo.getId(), e);
                throw new ServiceException("can't delete book", e);
            }
        }
    /**
     * Method, performs update book by book id
     *
     * @param bookInfo - entity must contain an book id
     */
    public void updateBook(BookInfo bookInfo) throws ServiceException {

            try {
                log.debug("Start update a book where bookInfo id equals :{}", bookInfo.getId());
                BookInfoDao bookInfoDao = new MySqlBookInfoDao();
                BookDao bookDao = new MySqlBookDao();
                AuthorDao authorDao = new MySqlAuthorDao();
                Author author = bookInfo.getBook().getAuthor();
                Book book = bookInfo.getBook();
                authorDao.update(author);
                bookDao.update(book);
                bookInfoDao.update(bookInfo);
                log.info("Update a book where bookInfo id equals :{}", bookInfo.getId());
            } catch (DaoException e) {
                log.warn("Can't update book where bookInfo id equals: {} ", bookInfo.getId(), e);
                throw new ServiceException("Can't update book", e);
            }
        }
    /**
     * Method, performs update bookInfo by bookInfo id
     *
     * @param bookInfo - entity must contain an bookInfo id
     */
    public void updateBookInfo(BookInfo bookInfo) throws ServiceException {
            try {
                log.debug("Start update a bookInfo where bookInfo id equals :{}", bookInfo.getId());
                BookInfoDao bookInfoDao = new MySqlBookInfoDao();
                BookDao bookDao = new MySqlBookDao();
                Book book = bookDao.findByBookInfo(bookInfo);
                bookInfo.setBook(book);
                bookInfoDao.update(bookInfo);
                log.info("Update a bookInfo where bookInfo id equals :{}", bookInfo.getId());
            } catch (DaoException e) {
                log.warn("Can't update bookInfo  where bookInfo id equals: {} ", bookInfo.getId(), e);
                throw new ServiceException("can't update book", e);
            }
        }
    /**
     * Method, provides the full list of genres
     *
     * @return Return a list of genres
     */
    public List<Genre> getAllGenre() throws ServiceException {
        List<Genre> list;
        try {
            log.debug("Get all genre list transaction from BookService");
                GenreDao genreDao = new MySqlGenreDao();
                list = genreDao.getAllGenre();
                return list;
        } catch (DaoException e) {
            log.warn("Can't get all genre list transaction from BookService", e);
            throw new ServiceException("can't get all genre", e);
        }
    }
    /**
     * Method, provides the number of books found for a specific genre
     *
     * @param genre - specific genre
     * @return Return book count
     */
    public int getBookCountByGenre(Genre genre) throws ServiceException {
            try {
                log.debug("get book count by genre where genre id equals {} ", genre.getId());

                BookDao bookDao = new MySqlBookDao();
                return bookDao.getBookCountByGenre(genre);
            } catch (DaoException e) {
                log.warn("Can't get book count by genre where genre id equals {} ", genre.getId(), e);
                throw new ServiceException("can't get count book", e);
            }
        }
    /**
     * Method, provides a list of books found by a specific name
     *
     * @param name - specific name
     * @return Return a list of book
     */
    public List<Book> getBookByName(String name) throws ServiceException {
        List<Book> books;
            try {
                log.debug("Get book entity by name where book name equals {} ", name);
                BookDao bookDao = new MySqlBookDao();
                books = bookDao.findByName(name);
                return books;
            } catch (DaoException e) {
                log.warn("Can't get book entity by name where book name equals {} ", name, e);
                throw new ServiceException("Can't find book by name", e);
            }
        }
        /**
     * Method, provide a list of books for a specific genre that are in a certain range of rows in the table     *
     *
     * @param genre - specific genre
     * @param start - the row from which you must begin
     * @param end   - the row from which you must finish
     * @return Return a list of book
     */
    public List<Book> getListBook(Genre genre, int start, int end) throws ServiceException {
            try {
                BookDao bookDao = new MySqlBookDao();
                List<Book> list = bookDao.getLimitBookByGenre(genre, start, end);
                for (Book book : list) {
                    fillBook(book);
                }
                return list;
            } catch (DaoException e) {
                log.warn("Can't get list book by genre id {} where range page {} to {} ", genre.getId(), start, end, e);
                throw new ServiceException("can't get list by genre book", e);
            }
        }
    /**
     * Method, provides a c book found by a specific isbn
     *
     * @param isbn - specific isbn for book
     * @return Specific entity book
     */
    private Book getBookByIsbn(String isbn) throws ServiceException {
        Book book;
            try {
                log.debug("Get book entity by isbn where isbn equals {} ", isbn);

                BookDao bookDao = new MySqlBookDao();
                book = bookDao.findByIsbn(isbn);
                return book;
            } catch (DaoException e) {
                log.warn("Can't get book entity by isbn where isbn equals {} ", isbn, e);
                throw new ServiceException("can't find book by name", e);
            }
        }
    /**
     * Method, checks for code availability
     *
     * @param isbn - books isbn
     * @return Return accessibility or inaccessibility of the book
     */
    public boolean isBookIsbnAvailable(String isbn) throws ServiceException {
        return getBookByIsbn(isbn) != null;
    }
    /**
     * Method, fill the book entity of the data associated with book
     *
     * @param book - entity
     */
    private void fillBook(Book book) throws ServiceException {
        try {
            if (book != null) {
                log.debug("Fill book with information where book id equals {} ", book.getId());

                    AuthorDao authorDao = new MySqlAuthorDao();
                    GenreDao genreDao = new MySqlGenreDao();
                    book.setAuthor(authorDao.findByBook(book));
                    book.setGenre(genreDao.findByBook(book));
                }
        } catch (DaoException e) {
            log.warn("Can't fill book with information where book id equals {} ", book.getId(), e);
            throw new ServiceException("can't fill book", e);
        }
    }
}

package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Author;
import by.epam.kooks.entity.Book;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Genre;
import by.epam.kooks.service.BookService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Action class , allows you to edit data about a book
 *
 * @author Eugene Kooks
 */
public class BookEditAction implements Action {
    private static final Logger log = LogManager.getLogger(BookEditAction.class);

    private boolean wrong = false;

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter(Constants.BOOK_ID);

        BookService bookService = new BookService();
        Properties properties = new Properties();

        BookInfo bookInfo = new BookInfo();
        Author author = new Author();
        Genre genre = new Genre();
        Book book = new Book();

        try {
            properties.load(RegisterAction.class.getClassLoader().getResourceAsStream(Constants.VALIDATION_PROPERTIES));
        } catch (IOException e) {
            log.error("Can't load validation properties.", e);
        }

        try {
            req.setAttribute(Constants.GENRE_LIST, bookService.getAllGenre());
            bookInfo = bookService.findBookInfoByBookId(Integer.parseInt(id));
            book = bookInfo.getBook();
            author = book.getAuthor();

        } catch (ServiceException e) {
            log.warn("Can't find bookInfo by id in {}", this.getClass().getSimpleName(), e);
            e.printStackTrace();
        }

        setBookValue(req, properties, bookInfo, book, author, genre);

        if (wrong) {
            wrong = false;
            log.debug("Wrong! Referring back again {} page | book id = {}", Constants.REFERER, book.getId());
            return new ActionResult(req.getHeader(Constants.REFERER), true);
        } else {
            try {
                book.setGenre(genre);
                book.setAuthor(author);
                bookService.updateBook(bookInfo);
            } catch (ServiceException e) {
                log.info("Can't update book {}", this.getClass().getSimpleName());
                return new ActionResult(Constants.BOOKS, true);
            }
        }
        return new ActionResult(req.getHeader(Constants.REFERER), true);
    }

    private void checkParamValid(String paramValue, String validator) {
        Pattern pattern = Pattern.compile(validator);
        Matcher matcher = pattern.matcher(paramValue);
        if (!matcher.matches()) {
            wrong = true;
        }
    }

    private boolean availableParam(String param, HttpServletRequest request) {
        return request.getParameter(param) != null && !request.getParameter(param).isEmpty();
    }

    private void setBookValue(HttpServletRequest req, Properties properties, BookInfo bookInfo, Book book, Author author, Genre genre) {
        String genreName = req.getParameter(Constants.GENRE_NAME);
        genre.setId(Integer.parseInt(genreName));

        if (availableParam(Constants.FIRST_NAME, req)) {
            String firstName = req.getParameter(Constants.FIRST_NAME);
            checkParamValid(firstName, properties.getProperty(Constants.NAME_VALID));
            author.setFirstName(firstName);
        }
        if (availableParam(Constants.LAST_NAME, req)) {
            String lastName = req.getParameter(Constants.LAST_NAME);
            checkParamValid(lastName, properties.getProperty(Constants.NAME_VALID));
            author.setLastName(lastName);
        }
        if (availableParam(Constants.DESCRIPTION, req)) {
            String descript = req.getParameter(Constants.DESCRIPTION);
            checkParamValid(descript, properties.getProperty(Constants.DESCRIPTION_VALID));
            book.setDescription(descript);
        }
        if (availableParam(Constants.BOOK_NAME, req)) {
            String name = req.getParameter(Constants.BOOK_NAME);
            checkParamValid(name, properties.getProperty(Constants.BOOK_NAME_VALID));
            book.setName(name);
        }
        if (availableParam(Constants.ISBN, req)) {
            String name = req.getParameter(Constants.ISBN);
            checkParamValid(name, properties.getProperty(Constants.ISBN_VALID));
            book.setIsbn(name);

        }
        if (availableParam(Constants.BOOK_AMOUNT, req)) {
            String amount = req.getParameter(Constants.BOOK_AMOUNT);
            checkParamValid(amount, properties.getProperty(Constants.BOOK_COUNT_VALID));
            bookInfo.setAmount(Integer.parseInt(amount));
        }
        if (availableParam(Constants.BOOK_PRICE, req)) {
            String price = req.getParameter(Constants.BOOK_PRICE);
            checkParamValid(price, properties.getProperty(Constants.BOOK_PRICE_VALID));
            bookInfo.setPrice(Integer.parseInt(price));
        }
    }
}

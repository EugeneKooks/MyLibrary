package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Author;
import by.epam.kooks.entity.Book;
import by.epam.kooks.entity.BookInfo;
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
 * Action class , allows you to edit data about a bookAllows you to register a book
 *
 * @author Eugene Kooks
 */
public class BookRegisterAction implements Action {
    private static final Logger log = LogManager.getLogger(BookRegisterAction.class);
    private String firstName;
    private String lastName;
    private String isbn;
    private String description;
    private String name;
    private String genreName;
    private String amount;
    private String price;

    private boolean wrong = false;

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
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
        } catch (ServiceException e) {
            new ActionResult(Constants.WELCOME);
            log.error("Can't transfer genre list by attribute ", e);
        }
        initValues(req);
        paramValidation(properties, req);
        genre.setId(Integer.parseInt(genreName));
        author.setFirstName(firstName);
        author.setLastName(lastName);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setIsbn(isbn);
        book.setDescription(description);
        book.setName(name);
        bookInfo.setBook(book);
        if (!amount.isEmpty() && !price.isEmpty()) {
            try {
                bookInfo.setAmount(Integer.valueOf(amount));
                bookInfo.setPrice(Integer.valueOf(price));
            } catch (Exception e) {
                log.debug("Parse error from String to Int/ value strings = {} ,{}", amount, price, e);
                return new ActionResult(Constants.REGISTER_BOOK);
            }
        }
        if (wrong) {
            wrong = false;
            log.info("Book with name {} is not registered in system", book.getName());
            return new ActionResult(Constants.REGISTER_BOOK);
        } else {
            try {
                log.info("Book with name {} is registered in system", book.getName());
                bookService.registerBook(bookInfo);
            } catch (ServiceException e) {
                log.warn("Book with name {} can't be registered in system", book.getName());
            }
        }
        return new ActionResult(Constants.BOOKS, true);
    }
    private void initValues(HttpServletRequest req) {
        initAuthorValue(req);
        isbn = req.getParameter(Constants.ISBN);
        description = req.getParameter(Constants.DESCRIPTION);
        name = req.getParameter(Constants.BOOK_NAME);
        genreName = req.getParameter(Constants.GENRE_NAME);
        amount = req.getParameter(Constants.BOOK_AMOUNT);
        price = req.getParameter(Constants.BOOK_PRICE);
    }
    private void initAuthorValue(HttpServletRequest req) {
        firstName = req.getParameter(Constants.FIRST_NAME);
        lastName = req.getParameter(Constants.LAST_NAME);
    }
    private void checkParamValid(String paramName, String paramValue, String validator, HttpServletRequest request) {
        Pattern pattern = Pattern.compile(validator);
        Matcher matcher = pattern.matcher(paramValue);
        if (!matcher.matches()) {
            request.setAttribute(paramName + Constants.ERROR, true);
            wrong = true;
        }
    }
    private void paramValidation(Properties properties, HttpServletRequest req) {
        checkParamValid(Constants.FIRST_NAME, firstName, properties.getProperty(Constants.NAME_VALID), req);
        checkParamValid(Constants.LAST_NAME, lastName, properties.getProperty(Constants.NAME_VALID), req);
        checkParamValid(Constants.DESCRIPTION, description, properties.getProperty(Constants.DESCRIPTION_VALID), req);
        checkParamValid(Constants.BOOK_NAME, name, properties.getProperty(Constants.BOOK_NAME_VALID), req);
        checkParamValid(Constants.BOOK_AMOUNT, amount, properties.getProperty(Constants.BOOK_COUNT_VALID), req);
        checkParamValid(Constants.BOOK_PRICE, price, properties.getProperty(Constants.BOOK_PRICE_VALID), req);
    }
}

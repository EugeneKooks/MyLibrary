package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Book;
import by.epam.kooks.entity.Genre;
import by.epam.kooks.service.BookService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Action class, provides data about books
 *
 * @author Eugene Kooks
 */
public class PageBookAction implements Action {
    private static final Logger log = LogManager.getLogger(PageBookAction.class);

    private int genreId = 0;
    private int genreState = 1;

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse response){
        BookService bookService = new BookService();
        Genre genre = new Genre();

        String name = null;
        int page = 1;
        int recordsPerPage = 2;

        try {
            if (request.getParameter(Constants.SEARCH) != null) {
                name = request.getParameter(Constants.SEARCH);
                log.debug("Searching book with name {}" ,name);
            }
            if (request.getParameter(Constants.PAGE) != null) {
                page = Integer.parseInt(request.getParameter(Constants.PAGE));
            }
            if (request.getParameter(Constants.GENRE_ID) != null) {
                genreId = Integer.parseInt(request.getParameter(Constants.GENRE_ID));
                genre.setId(genreId);
                genreState = genreId;
            } else {
                genre.setId(genreState);
            }

            List<Book> books = bookService.getListBook(genre, page, recordsPerPage);
            List<Genre> genres = bookService.getAllGenre();
            List<Book> book = bookService.getBookByName(name);

            int numberOfRecords = bookService.getBookCountByGenre(genre);
            int numberOfPages = (int) Math.ceil((double) numberOfRecords/recordsPerPage);

            if (book.size() > Constants.MIN_COUNT_BOOKS) {
                request.setAttribute(Constants.ATT_BOOKS, book);
                request.setAttribute(Constants.ATT_GENRES, genres);
            } else {
                request.setAttribute(Constants.ATT_BOOKS, books);
                request.setAttribute(Constants.ATT_GENRES, genres);
                request.setAttribute(Constants.ATT_NO_PAGES, numberOfPages);
                request.setAttribute(Constants.ATT_CURRENT_PAGE, page);
            }

            log.debug("Transfer book list page {} to {}" , numberOfPages , numberOfRecords);

        } catch (ServiceException e) {
            log.warn("Can't show the list of books " , e);
        }
        return new ActionResult(Constants.BOOKS);
    }


}

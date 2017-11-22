package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.service.BookService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action class , allows admin delete a book.
 *
 * @author Eugene Kooks
 */
public class DeleteBookAction implements Action {
    private static final Logger log = LogManager.getLogger(DeleteBookAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){
        BookService bookService = new BookService();
        BookInfo bookInfo = new BookInfo();
        String id = req.getParameter(Constants.DELETE_ID);
        bookInfo.setId(Integer.parseInt(id));

        try {
            bookService.deleteBook(bookInfo);
            log.info("Delete book by id = {}", id);
        } catch (ServiceException e) {
            log.warn("Can't delete book by id = {}", id ,e);
            return new ActionResult(Constants.DELETE_BOOK_ERROR, true);
        }

        return new ActionResult(Constants.BOOKS, true);
    }
}

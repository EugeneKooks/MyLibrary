package by.epam.kooks.action.get;

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
 * Action class, provide data about book ,with the loaded fields
 *
 * @author Eugene Kooks
 */
public class PageBookEditAction implements Action {
    private static final Logger log = LogManager.getLogger(PageBookEditAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter(Constants.BOOK_ID);
        BookService bookService = new BookService();
        try {
            BookInfo bookInfo = bookService.findBookInfoByBookId(Integer.parseInt(id));
            req.setAttribute(Constants.BOOK_INFO, bookInfo);
            req.setAttribute(Constants.GENRE_LIST, bookService.getAllGenre());

        } catch (ServiceException e) {
            log.warn("Can't edit book where id = {}",id , e);
        }
        return new ActionResult(Constants.BOOK_EDIT);
    }
}

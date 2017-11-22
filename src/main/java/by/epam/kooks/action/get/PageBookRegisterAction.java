package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.service.BookService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action class, provide data for register new book customer
 *
 * @author Eugene Kooks
 */
public class PageBookRegisterAction implements Action {
    private static final Logger log = LogManager.getLogger(PageBookRegisterAction.class);

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse response) {
             BookService bookService = new BookService();
        try {
            request.setAttribute(Constants.GENRE_LIST, bookService.getAllGenre());
            log.debug("Transfer book registration information");
        } catch (ServiceException e) {
            log.warn("Can't transfer book registration information", e);
        }

        return new ActionResult(Constants.REGISTER_BOOK);
    }
}

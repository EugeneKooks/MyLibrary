package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.get.AbstractBasket;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Basket;
import by.epam.kooks.action.manager.Action;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class , allows admin delete a book from basket.
 *
 * @author Eugene Kooks
 */

public class DeleteBookFromBasketAction extends AbstractBasket implements Action {
    private static final Logger log = LogManager.getLogger(DeleteBookFromBasketAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){
        HttpSession session = req.getSession();
        Basket basket = getBasket(session);
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);

        if (req.getParameter(Constants.ATT_DELETE_BOOK_ID) != null) {
            int bookId = Integer.parseInt(req.getParameter(Constants.ATT_DELETE_BOOK_ID));
            log.info("Customer by id = {} deleted  book from basket", id);
            basket.delete(bookId);
        }
        if (req.getParameter(Constants.ATT_ALL_DELETE_BOOK_ID) != null) {
            log.info("Customer by id = {} deleted all book from basket", id);
            basket.deleteAll();
        }

        return new ActionResult(Constants.BASKET, true);
    }
}

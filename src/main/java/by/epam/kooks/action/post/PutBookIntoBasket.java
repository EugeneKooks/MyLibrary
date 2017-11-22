package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.action.get.AbstractBasket;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Basket;
import by.epam.kooks.service.BookService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class , allows  customers to put books into basket.
 *
 * @author Eugene Kooks
 */
public class PutBookIntoBasket extends AbstractBasket implements Action {
    private static final Logger log = LogManager.getLogger(PutBookIntoBasket.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){
        BookService bookService = new BookService();
        HttpSession session = req.getSession();
        Basket basket = getBasket(session);
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);

        try {
            int bookId = Integer.parseInt(req.getParameter(Constants.BOOK_ID));
            BookInfo book = (bookService.findBookInfoByBookId(bookId));

            for(BookInfo bookInfo : basket.getBooks()){
                if(bookInfo.getId() == book.getId()) {
                    return new ActionResult(Constants.BOOKS, true);
                }
            }
            basket.add(book);
            session.setAttribute(Constants.BASKET, basket);
            log.debug("Customer by id = {} put book into basket" , id);

        } catch (ServiceException e) {
            log.warn("Customer by id = {} can't put book into basket" , id);

        }

        return new ActionResult(Constants.BOOKS, true);
    }
}

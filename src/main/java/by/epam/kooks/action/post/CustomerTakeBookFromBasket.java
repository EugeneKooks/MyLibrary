package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.action.get.AbstractBasket;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Basket;
import by.epam.kooks.entity.Transaction;
import by.epam.kooks.service.TransactionService;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.service.BookService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class , allows customers take a book from basket
 *
 * @author Eugene Kooks
 */
public class CustomerTakeBookFromBasket extends AbstractBasket implements Action {
    private static final Logger log = LogManager.getLogger(CustomerTakeBookFromBasket.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        TransactionService transactionService = new TransactionService();
        BookService bookService = new BookService();
        Transaction transaction = new Transaction();
        Customer customer = new Customer();
        BookInfo bookInfo;

        HttpSession session = req.getSession();
        Basket basket = getBasket(session);
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);
        int bookInfoId = Integer.parseInt(req.getParameter(Constants.ATT_BOOK_INFO_ID));
        int bookId = Integer.parseInt(req.getParameter(Constants.BOOK_ID));
        try {
            bookInfo = bookService.findBookInfoByBookId(bookId);
            customer.setId(id);
            transaction.setCustomer(customer);
            transaction.setBookInfo(bookInfo);

            if (transactionService.isBookInTransactionAlreadyTaken(transaction) || transactionService.countActiveTransaction(transaction) > Constants.MAX_COUNT_BOOKS) {
                req.setAttribute(Constants.ATT_TAKE_ERROR, true);
                log.debug("Can't past checking for isAlready taken {} and max count book{}", transactionService.isBookInTransactionAlreadyTaken(transaction), transactionService.countActiveTransaction(transaction) > Constants.MAX_COUNT_BOOKS);
                return new ActionResult(Constants.BASKET);
            } else {
                basket.delete(bookInfoId);
                transactionService.customerTakeBook(transaction);
                log.debug("Book deleted from basket and take book where customer id = {}", customer.getId());
            }
        } catch (ServiceException e) {
            log.warn("Can't book delete from basket and take book where customer id = {}", customer.getId(), e);
        }
        return new ActionResult(Constants.BASKET, true);

    }
}

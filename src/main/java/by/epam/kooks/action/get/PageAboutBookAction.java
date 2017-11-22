package by.epam.kooks.action.get;
import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Transaction;
import by.epam.kooks.service.TransactionService;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.service.BookService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class, provide data about book
 *
 * @author Eugene Kooks
 */
public class PageAboutBookAction implements Action {
    private static final Logger log = LogManager.getLogger(PageAboutBookAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {

        TransactionService transactionService = new TransactionService();
        BookService bookService = new BookService();
        Transaction transaction = new Transaction();
        Customer customer = new Customer();
        BookInfo bookInfo;

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);
        String id = req.getParameter(Constants.BOOK_ID);

        customer.setId(customerId);
        transaction.setCustomer(customer);

        try {
            bookInfo = bookService.findBookInfoByBookId(Integer.parseInt(id));
            transaction.setBookInfo(bookInfo);
            log.info("Book by id = {} opened with customer id ={}", id, customerId);
        } catch (ServiceException e) {
            log.info("Book by id = {} can't open with customer id ={}", id, customerId, e);
            return new ActionResult(Constants.BOOKS , true);
        }


        checkBookStatus(req, transactionService, transaction, bookInfo);
        req.setAttribute(Constants.BOOK_INFO, bookInfo);
        return new ActionResult(Constants.ABOUT_BOOK);
    }

    /**
     * Checks the status of the book for valid values
     */
    private void checkBookStatus(HttpServletRequest req, TransactionService transactionService, Transaction transaction, BookInfo bookInfo) {
        if (transactionService.countActiveTransaction(transaction) > Constants.MAX_COUNT_BOOKS) {
            req.setAttribute(Constants.ATT_COUNT_ERROR, true);
        }
        if (bookInfo.getAmount() <= Constants.MIN_COUNT_BOOKS) {
            req.setAttribute(Constants.ATT_OVER_ERROR, true);
        }
        if (transactionService.isBookInTransactionAlreadyTaken(transaction)) {
            req.setAttribute(Constants.ATT_TAKE_ERROR, true);
        }
    }
}

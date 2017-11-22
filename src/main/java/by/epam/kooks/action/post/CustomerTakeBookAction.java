package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Transaction;
import by.epam.kooks.service.TransactionService;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.BookInfo;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class , allows customers to take a book
 *
 * @author Eugene Kooks
 */
public class CustomerTakeBookAction implements Action {
    private static final Logger log = LogManager.getLogger(CustomerTakeBookAction.class);
    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {

        TransactionService transactionService = new TransactionService();
        Transaction transaction = new Transaction();
        BookInfo bookInfo = new BookInfo();
        Customer customer = new Customer();

        HttpSession session = req.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);
        String bookId = req.getParameter(Constants.BOOK_ID);

        customer.setId(id);
        bookInfo.setId(Integer.parseInt(bookId));
        transaction.setCustomer(customer);
        transaction.setBookInfo(bookInfo);

        try {
            transactionService.customerTakeBook(transaction);
            log.debug("Transaction(take) book where customer id ={} , transaction id = {}",customer.getId() , transaction.getId() );
        } catch (ServiceException e) {
            log.warn("Can't transaction(take) book where customer id ={} , transaction id = {} ",customer.getId() , transaction.getId(),e);
        }
        return new ActionResult(Constants.BOOKS, true);
    }
}

package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.Transaction;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.service.TransactionService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class , allows you to edit data about a bookAllows you to register a book
 *
 * @author Eugene Kooks
 */
public class CustomerReturnBookAction implements Action {
    private static final Logger log = LogManager.getLogger(CustomerReturnBookAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){

        TransactionService transactionService = new TransactionService();
        Transaction transaction = new Transaction();
        Customer customer = new Customer();

        HttpSession session = req.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);
        String transactionId = req.getParameter(Constants.RETURN_BOOK);

        transaction.setId(Integer.parseInt(transactionId));
        customer.setId(id);

        try {
            transactionService.customerReturnBook(transaction, customer);
            log.debug("Transaction(return) book where customer id ={} , transaction id = {}",customer.getId() , transaction.getId() );
        } catch (ServiceException e) {
            log.warn("Can't transaction(return) book where customer id ={} , transaction id = {} ",customer.getId() , transaction.getId(),e);
            return new ActionResult(Constants.BOOKS, true);
        }

        return new ActionResult(Constants.DEPT_CUSTOMER_BOOK, true);
    }
}

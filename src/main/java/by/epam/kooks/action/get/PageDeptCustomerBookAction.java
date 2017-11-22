package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.Transaction;
import by.epam.kooks.service.TransactionService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Action class, provide data about dept books for specific customer
 *
 * @author Eugene Kooks
 */
public class PageDeptCustomerBookAction implements Action {
    private static final Logger log = LogManager.getLogger(PageDeptCustomerBookAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){
        HttpSession session = req.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);

        TransactionService transactionService = new TransactionService();
        Transaction transaction = new Transaction();
        Customer customer = new Customer();
        customer.setId(id);
        transaction.setCustomer(customer);

        try {
            List<Transaction> transactions = transactionService.getActiveCustomerTransaction(transaction);

            req.setAttribute(Constants.ATT_CUSTOMER_BOOK, transactions);
            log.debug("Transfer information about customer where customer id = {} ", customer.getId());
        } catch (ServiceException e) {
            log.warn("Can't transfer information about customer where customer id = {} ", customer.getId());
        }
        return new ActionResult(Constants.DEPT_CUSTOMER_BOOK);
    }

}
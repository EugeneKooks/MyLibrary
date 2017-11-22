package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Transaction;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.service.TransactionService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Action class, provide data about specific reader
 *
 * @author Eugene Kooks
 */
public class PageAboutReaderAction implements Action {
    private static final Logger log = LogManager.getLogger(PageAboutReaderAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){
        CustomerService customerService = new CustomerService();
        TransactionService transactionService = new TransactionService();
        List<Transaction> addTransaction = new ArrayList<>();
        Transaction transaction = new Transaction();
        Customer customer = null;
        String id = req.getParameter(Constants.READER_ID);

        try {
            customer = customerService.findCustomerById(Integer.parseInt(id));
            transaction.setCustomer(customer);
            List<Transaction> transactions = transactionService.findTransactionsByCustomer(transaction);
            for (Transaction tran : transactions) {
                if (tran.getEndDate() == null) {
                    addTransaction.add(tran);
                }
            }
            log.debug("Take information about customer by id = {}", id);
        } catch (ServiceException e) {
            log.warn("Can't take information about customer by id = {}", id);
            return new ActionResult(Constants.READERS,true);
        }
        req.setAttribute(Constants.TRANSACTIONS, addTransaction);
        req.setAttribute(Constants.ATT_CUSTOMER_INFO, customer);
        return new ActionResult(Constants.ABOUT_READER);
    }
}


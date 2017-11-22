package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Action class, provide data for admin about readers
 *
 * @author Eugene Kooks
 */
public class PageReadersAction implements Action {
    private static final Logger log = LogManager.getLogger(PageReadersAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        CustomerService customerService = new CustomerService();
        int page = 1;
        int recordPerPage = 10;

        if (req.getParameter(Constants.PAGE) != null) {
            page = Integer.parseInt(req.getParameter(Constants.PAGE));
        }
        try {
            List<Customer> readers = customerService.getListCustomers(page, recordPerPage);

            int noOfRecords = customerService.getCustomerCount();
            int noOfPages = (int) Math.ceil((double) noOfRecords / recordPerPage);

            req.setAttribute(Constants.ATT_READERS, readers);
            req.setAttribute(Constants.ATT_NO_PAGES, noOfPages);
            req.setAttribute(Constants.ATT_CURRENT_PAGE, page);
            log.debug("Transfer customer list page {} to {}", noOfPages, noOfRecords);
        } catch (ServiceException e) {
            log.warn("Can't transfer customer list page");
        }

        return new ActionResult(Constants.READERS);
    }
}

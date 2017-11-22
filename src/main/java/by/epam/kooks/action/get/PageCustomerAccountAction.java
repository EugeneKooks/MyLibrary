package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class, provide data about specific customer
 *
 * @author Eugene Kooks
 */
public class PageCustomerAccountAction implements Action {
    private static final Logger log = LogManager.getLogger(PageCustomerAccountAction.class);

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse response){
        CustomerService customerService = new CustomerService();
        Customer customer = null;
        HttpSession session = request.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);
        try {
            customer = customerService.findCustomerById(id);
            log.debug("Open customer account by id = {}", id );
        } catch (ServiceException e) {
            log.warn("Can't open customer account by id = {}", id );
        }
        request.setAttribute(Constants.ATT_CUSTOMER_INFO, customer);
        return new ActionResult(Constants.ACCOUNT);
    }
}

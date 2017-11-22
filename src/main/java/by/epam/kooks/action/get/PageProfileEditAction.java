package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.service.exception.ServiceException;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class, provide data for profile editing
 *
 * @author Eugene Kooks
 */
public class PageProfileEditAction implements Action {
    private static final Logger log = LogManager.getLogger(PagePersonalDataEditAction.class);

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse response){
        CustomerService customerService = new CustomerService();
        HttpSession session = request.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);
        try {
            Customer customer = customerService.findCustomerById(id);
            request.setAttribute(Constants.EMAIL , customer.getEmail());
            log.debug("Transfer information about customer for edit profile data where customer id = {} ", id);
        } catch (ServiceException e) {
            log.warn("Can't transfer information about customer for edit profile data where customer id = {} ", id);
        }
        return new ActionResult(Constants.PROFILE_EDIT);
    }
}

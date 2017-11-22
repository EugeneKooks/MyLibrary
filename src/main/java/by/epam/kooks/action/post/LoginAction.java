package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.service.exception.ServiceException;
import by.epam.kooks.util.Encoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class , allows a guest to execute login.
 *
 * @author Eugene Kooks
 */
public class LoginAction implements Action {
    private static final Logger log = LogManager.getLogger(LoginAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        CustomerService customerService = new CustomerService();
        String login = req.getParameter(Constants.LOGIN);
        String password = req.getParameter(Constants.PASSWORD);
        try {
            Customer customer = customerService.findCustomerByLoginAndPassword(login, Encoder.toEncode(password));

            if (customer != null) {
                HttpSession session = req.getSession();
                session.setAttribute(Constants.ATT_CUSTOMER_ID, customer.getId());
                session.setAttribute(Constants.ATT_ROLE, customer.getCustomerRole().getName());
                session.setAttribute(Constants.ATT_ROLE_ID, customer.getCustomerRole().getId());
                session.setAttribute(Constants.ATT_NAME, customer.getPerson().getFirstName());
                log.info("Customer with id = {} and role = {} is logged in system ", customer.getId(), customer.getCustomerRole().getName());
                return new ActionResult(Constants.BOOKS, true);
            } else {
                req.setAttribute(Constants.LOGIN_ERROR, true);
                return new ActionResult(Constants.WELCOME);
            }
        } catch (ServiceException e) {
            log.warn("No customer with such a password and login" ,e);
        }
        return null;
    }
}

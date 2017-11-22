package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action class , allows customer and admin edit email.
 *
 * @author Eugene Kooks
 */
public class EmailEditAction implements Action {
    private static final Logger log = LogManager.getLogger(EmailEditAction.class);

    private boolean wrong = false;

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse response) {

        CustomerService customerService = new CustomerService();
        Customer customer = new Customer();
        Properties properties = new Properties();

        HttpSession session = request.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);

        try {
            properties.load(RegisterAction.class.getClassLoader().getResourceAsStream(Constants.VALIDATION_PROPERTIES));
        } catch (IOException e) {
            log.error("Can't load validation properties.", e);
        }

        String email = request.getParameter(Constants.EMAIL);
        try {
            if (availableParam(Constants.EMAIL, request)) {
                customer = customerService.findCustomerById(id);
            }
            if (!customerService.isCustomerLoginAvailable(email)) {
                log.debug("Parameters value {} in {} doest'n past validation/customer id = {}", this.getClass().getSimpleName(), email, id);
                wrong = true;
                request.setAttribute(Constants.EMAIL_ERROR, true);
            } else {
                checkParamValid(Constants.EMAIL, email, properties.getProperty(Constants.EMAIL_VALID), request);
            }
        } catch (ServiceException e) {
            log.warn("Can't find customer by id = {}", id);
        }

        customer.setEmail(email);

        if (wrong) {
            wrong = false;
            log.debug("Wrong! Referring back again {} page | customer id = {}", Constants.PROFILE_EDIT, customer.getId());
            return new ActionResult(Constants.PROFILE_EDIT);
        } else {
            try {
                customerService.updateCustomer(customer);
            } catch (ServiceException e) {
                log.info("Can't update customer email {}", e);
            }
        }
        return new ActionResult(Constants.ACCOUNT, true);
    }


    private void checkParamValid(String paramName, String paramValue, String validator, HttpServletRequest request) {
        Pattern pattern = Pattern.compile(validator);
        Matcher matcher = pattern.matcher(paramValue);
        if (!matcher.matches()) {
            request.setAttribute(paramName + Constants.ERROR, true);
            wrong = true;
        }
    }

    private boolean availableParam(String param, HttpServletRequest request) {
        return request.getParameter(param) != null && !request.getParameter(param).isEmpty();
    }


}

package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.util.Encoder;
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
 * Action class , allows customer and admin edit password.
 *
 * @author Eugene Kooks
 */
public class PasswordEditAction implements Action {
    private static final Logger log = LogManager.getLogger(PasswordEditAction.class);

    private boolean wrong = false;

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {

        CustomerService customerService = new CustomerService();
        Customer customer = new Customer();
        Properties properties = new Properties();

        HttpSession session = req.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);

        try {
            properties.load(RegisterAction.class.getClassLoader().getResourceAsStream(Constants.VALIDATION_PROPERTIES));
        } catch (IOException e) {
            log.error("Can't load validation properties.", e);
        }

        String oldPassword = req.getParameter(Constants.OLD_PASSWORD);
        String password = req.getParameter(Constants.PASSWORD);
        String passwordConfirm = req.getParameter(Constants.PASSWORD_CONFIRM);

        try {
            customer = customerService.findCustomerById(id);

            if (!customer.getPassword().equals(Encoder.toEncode(oldPassword))) {
                wrong = true;
                log.debug("Customer by id = {} can't change oldPassword", customer.getId());
                req.setAttribute(Constants.OLD_PASSWORD_ERROR, true);
            } else {
                if (customer.getPassword().equals(Encoder.toEncode(password))) {
                    wrong = true;
                    log.debug("Customer by id = {} can't confirm new password", customer.getId());
                    req.setAttribute(Constants.MATCH_PASSWORD_ERROR, true);
                }
            }

            if (!password.equals(passwordConfirm)) {
                log.debug("Customer by id = {} can't confirm password", customer.getId());
                wrong = true;
                req.setAttribute(Constants.PASSWORD_ERROR, true);
            } else {
                checkParamValid(Constants.PASSWORD, password, properties.getProperty(Constants.PASSWORD_VALID), req);
            }

            customer.setPassword(Encoder.toEncode(password));
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        if (wrong) {
            wrong = false;
            log.debug("Wrong! Referring back again {} page | customer id = {}", Constants.PROFILE_EDIT, customer.getId());
            return new ActionResult(Constants.PROFILE_EDIT);
        } else {
            try {
                customerService.updateCustomer(customer);
            } catch (ServiceException e) {
                log.info("Can't update customer password {}", e);
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
}

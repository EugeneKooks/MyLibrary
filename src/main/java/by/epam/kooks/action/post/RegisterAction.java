package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Person;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.service.exception.ServiceException;
import by.epam.kooks.util.Encoder;
import by.epam.kooks.util.SqlDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action class , allows a customer to register in system.
 *
 * @author Eugene Kooks
 */
public class RegisterAction implements Action {
    private static final Logger log = LogManager.getLogger(RegisterAction.class);
    private String email;
    private String password;
    private String passwordConfirm;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String birthday;
    private String address;
    private boolean wrong = false;
    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        CustomerService customerService = new CustomerService();
        Properties properties = new Properties();
        Customer customer = new Customer();
        Person person = new Person();
        try {
            properties.load(RegisterAction.class.getClassLoader().getResourceAsStream(Constants.VALIDATION_PROPERTIES));
        } catch (IOException e) {
            log.error("Can't load validation properties.", e);
        }
        initValues(req);
        try {
            if (!customerService.isCustomerLoginAvailable(email)) {
                req.setAttribute(Constants.EMAIL_ERROR, true);
                wrong = true;
            } else {
                checkParamValid(Constants.EMAIL, email, properties.getProperty(Constants.EMAIL_VALID), req);
            }
        } catch (ServiceException e) {
            new ActionResult(Constants.WELCOME);
            log.info("Can't check login available", e);
        }
        if (!password.equals(passwordConfirm)) {
            wrong = true;
            req.setAttribute(Constants.PASSWORD_ERROR, true);
        } else {
            checkParamValid(Constants.PASSWORD, password, properties.getProperty(Constants.PASSWORD_VALID), req);
        }
        paramValidation(properties, req);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setMiddleName(middleName);
        person.setBirthday(SqlDate.stringToDate(birthday));
        person.setPhone(phone);
        person.setAddress(address);
        customer.setPerson(person);
        customer.setEmail(email);
        customer.setPassword(Encoder.toEncode(password));
        if (wrong) {
            wrong = false;
            log.debug("Can't register new customer and referred {} page", Constants.REGISTER);
            return new ActionResult(Constants.REGISTER);
        } else {
            try {
                log.info("Customer by login {} registered in system", customer.getEmail());
                customerService.registerCustomer(customer);
            } catch (ServiceException e) {
                log.warn("Customer by login {} can't registered in system", customer.getEmail());
            }
        }
        return new ActionResult(Constants.WELCOME, true);
    }
    private void initValues(HttpServletRequest req) {
        initPersonValue(req);
        email = req.getParameter(Constants.EMAIL);
        password = req.getParameter(Constants.PASSWORD);
        passwordConfirm = req.getParameter(Constants.PASSWORD_CONFIRM);
        phone = req.getParameter(Constants.PHONE);
        birthday = req.getParameter(Constants.BIRTHDAY);
        address = req.getParameter(Constants.ADDRESS);
    }
    private void initPersonValue(HttpServletRequest req){
        firstName = req.getParameter(Constants.FIRST_NAME);
        lastName = req.getParameter(Constants.LAST_NAME);
        middleName = req.getParameter(Constants.MIDDLE_NAME);
    }
    private void checkParamValid(String paramName, String paramValue, String validator, HttpServletRequest request) {
        Pattern pattern = Pattern.compile(validator);
        Matcher matcher = pattern.matcher(paramValue);
        if (!matcher.matches()) {
            request.setAttribute(paramName + Constants.ERROR, true);
            wrong = true;
        }
    }
    private void paramValidation(Properties properties, HttpServletRequest req) {
        checkParamValid(Constants.FIRST_NAME, firstName, properties.getProperty(Constants.NAME_VALID), req);
        checkParamValid(Constants.LAST_NAME, lastName, properties.getProperty(Constants.NAME_VALID), req);
        checkParamValid(Constants.MIDDLE_NAME, middleName, properties.getProperty(Constants.MIDDLE_NAME_VALID), req);
        checkParamValid(Constants.PHONE, phone, properties.getProperty(Constants.LIMIT_NUMBER_VALID), req);
        checkParamValid(Constants.BIRTHDAY, birthday, properties.getProperty(Constants.DATE_VALID), req);
        checkParamValid(Constants.ADDRESS, address, properties.getProperty(Constants.ADDRESS_VALID), req);
    }
}

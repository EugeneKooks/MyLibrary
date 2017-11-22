package by.epam.kooks.action.post;

import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Person;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.util.SqlDate;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Customer;
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

import static by.epam.kooks.action.constants.Constants.*;

/**
 * Action class , allows  admin edit personal date.
 *
 * @author Eugene Kooks
 */
public class PersonalDataEditAction implements Action {
    private static final Logger log = LogManager.getLogger(PersonalDataEditAction.class);

    private boolean wrong = false;

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {

        CustomerService customerService = new CustomerService();
        Customer customer = new Customer();
        Person person = new Person();

        Properties properties = new Properties();

        HttpSession session = req.getSession();
        int id = (int) session.getAttribute(ATT_CUSTOMER_ID);

        try {
            properties.load(RegisterAction.class.getClassLoader().getResourceAsStream(VALIDATION_PROPERTIES));
        } catch (IOException e) {
            log.error("Can't load validation properties.", e);

        }
        try {
            customer = customerService.findCustomerById(id);
            person = customer.getPerson();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        setPersonValue(req, properties, person);

        if (wrong) {
            wrong = false;
            return new ActionResult(PERSONAL_DATA_EDIT, true);
        } else {
            try {
                customer.setPerson(person);
                customerService.updatePerson(customer);
            } catch (ServiceException e) {
                log.warn("Can't update person date", e);
            }
        }
        return new ActionResult(ACCOUNT, true);
    }

    private void checkParamValid(String paramValue, String validator) {
        Pattern pattern = Pattern.compile(validator);
        Matcher matcher = pattern.matcher(paramValue);
        if (!matcher.matches()) {
            wrong = true;
        }
    }

    private boolean availableParam(String param, HttpServletRequest request) {
        return request.getParameter(param) != null && !request.getParameter(param).isEmpty();
    }

    private void setPersonValue(HttpServletRequest req, Properties properties, Person person) {
        if (availableParam(FIRST_NAME, req)) {
            String firstName = req.getParameter(FIRST_NAME);
            checkParamValid(firstName, properties.getProperty(NAME_VALID));
            person.setFirstName(firstName);
        }
        if (availableParam(LAST_NAME, req)) {
            String lastName = req.getParameter(LAST_NAME);
            checkParamValid(lastName, properties.getProperty(NAME_VALID));
            person.setLastName(lastName);
        }
        if (availableParam(MIDDLE_NAME, req)) {
            String middleName = req.getParameter(MIDDLE_NAME);
            checkParamValid(middleName, properties.getProperty(NAME_VALID));
            person.setMiddleName(middleName);
        }
        if (availableParam(PHONE, req)) {
            String phone = req.getParameter(PHONE);
            checkParamValid(phone, properties.getProperty(LIMIT_NUMBER_VALID));
            person.setPhone(phone);
        }
        if (availableParam(BIRTHDAY, req)) {
            String birthday = req.getParameter(BIRTHDAY);
            checkParamValid(birthday, properties.getProperty(DATE_VALID));
            person.setBirthday(SqlDate.stringToDate(birthday));
        }
        if (availableParam(ADDRESS, req)) {
            String address = req.getParameter(ADDRESS);
            checkParamValid(address, properties.getProperty(ADDRESS_VALID));
            person.setAddress(address);
        }


    }
}

package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Customer;
import by.epam.kooks.entity.Person;
import by.epam.kooks.service.CustomerService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Action class, provide data for personal date editin
 *
 * @author Eugene Kooks
 */
public class PagePersonalDataEditAction implements Action {
    private static final Logger log = LogManager.getLogger(PagePersonalDataEditAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        CustomerService customerService = new CustomerService();

        HttpSession session = req.getSession();
        int id = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);

        try {
            Customer customer = customerService.findCustomerById(id);
            Person person = customer.getPerson();

            setPersonAttribute(req, person);


            log.debug("Transfer information about customer for edit personal data where customer id = {} ", id);
        } catch (ServiceException e) {
            log.warn("Can't transfer information about customer for edit personal data where customer id = {} ", id);
        }
        return new ActionResult(Constants.PERSONAL_DATA_EDIT);
    }

    /**
     * Inserting a value into an attribute
     * */
    private void setPersonAttribute(HttpServletRequest req, Person person) {
        req.setAttribute(Constants.FIRST_NAME, person.getFirstName());
        req.setAttribute(Constants.LAST_NAME, person.getLastName());
        req.setAttribute(Constants.MIDDLE_NAME, person.getMiddleName());
        req.setAttribute(Constants.PHONE, person.getPhone());
        req.setAttribute(Constants.BIRTHDAY, person.getBirthday());
        req.setAttribute(Constants.ADDRESS, person.getAddress());
    }
}

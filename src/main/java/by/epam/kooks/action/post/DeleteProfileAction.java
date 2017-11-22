package by.epam.kooks.action.post;

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

/**
 * Action class , allows admin delete a profile.
 *
 * @author Eugene Kooks
 */
public class DeleteProfileAction implements Action {
    private static final Logger log = LogManager.getLogger(DeleteProfileAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        CustomerService customerService = new CustomerService();
        Customer customer;
        Integer id = Integer.valueOf(req.getParameter(Constants.DELETE_ID));

        try {
            customer = customerService.findCustomerById(id);
            customerService.deleteCustomer(customer);
            log.debug("Delete customer by id = {}", id);
        } catch (ServiceException e) {
            log.warn("Can't delete customer by id = {}", id, e);
            return new ActionResult(Constants.DELETE_PROFILE_ERROR, true);
        }

        if (customer.getCustomerRole().getName().equals(Constants.ADMIN)) {
            return new ActionResult(Constants.WELCOME);
        }

        return new ActionResult(Constants.READERS , true);

    }
}

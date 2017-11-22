package by.epam.kooks.action.post;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Management;
import by.epam.kooks.service.ManagementService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action class , responsible for returning the book back
 *
 * @author Eugene Kooks
 */

public class AdminReturnBookAction implements Action {
    private static final Logger log = LogManager.getLogger(AdminReturnBookAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        ManagementService managementService = new ManagementService();
        Management management = new Management();

        int id = Integer.parseInt(req.getParameter(Constants.MANAGEMENT_ID));
        management.setId(id);
        try {
            log.debug("Admin returned book");
            managementService.adminReturnBook(management);
        } catch (ServiceException e) {
            log.warn("Can't admin return book", e);
            return new ActionResult(Constants.BOOKS, true);
        }
        return new ActionResult(Constants.MANAGEMENT, true);
    }
}

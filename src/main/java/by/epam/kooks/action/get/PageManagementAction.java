package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.entity.Management;
import by.epam.kooks.service.ManagementService;
import by.epam.kooks.service.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Action class, provide data for management
 *
 * @author Eugene Kooks
 */
public class PageManagementAction implements Action {
    private static final Logger log = LogManager.getLogger(PageDeptCustomerBookAction.class);

    private boolean isActive = false;
    private boolean isActiveState = false;

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        ManagementService managementService = new ManagementService();
        int page = 1;
        int recordPerPage = 3;

        if (req.getParameter(Constants.PAGE) != null) {
            page = Integer.parseInt(req.getParameter(Constants.PAGE));
        }
        if (req.getParameter(Constants.ACTIVE) != null) {
            isActive = Boolean.valueOf(req.getParameter(Constants.ACTIVE));
            isActiveState = isActive;
        }

        try {
            List<Management> managements = managementService.getListManagement(page, recordPerPage, isActiveState);

            int noOfRecords = managementService.getManagementCount(isActiveState);
            int noOfPages = (int) Math.ceil((double) noOfRecords / recordPerPage);

            req.setAttribute(Constants.ATT_MANAGEMENTS, managements);
            req.setAttribute(Constants.ATT_NO_PAGES, noOfPages);
            req.setAttribute(Constants.ATT_CURRENT_PAGE, page);
            req.setAttribute(Constants.ATT_IS_ACTIVE_STATE, isActiveState);
            log.debug("Transfer information about management to {} page ", Constants.MANAGEMENT);
        } catch (ServiceException e) {
            log.warn("Can't transfer information about management to {} page ", Constants.MANAGEMENT);
        }
        return new ActionResult(Constants.MANAGEMENT);
    }
}

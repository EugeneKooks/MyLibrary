package by.epam.kooks.action.post;


import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Provides logout
 *
 * @author Eugene Kooks
 */
public class LogoutAction implements Action {
    private static final Logger log = LogManager.getLogger(LogoutAction.class);

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse resp) {
        HttpSession session = request.getSession();
        int id  = (int) session.getAttribute(Constants.ATT_CUSTOMER_ID);
        String roleName = (String) session.getAttribute(Constants.ATT_ROLE);
        log.info("Customer with id = {} and with role = {} logged out.", id , roleName);
        session.invalidate();
        return new ActionResult(Constants.WELCOME, true);
    }
}

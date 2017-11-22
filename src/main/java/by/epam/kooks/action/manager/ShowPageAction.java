package by.epam.kooks.action.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class , provides to show jsp page without any dependieses form action classes.
 *
 * @author Eugene Kooks
 */
public class ShowPageAction implements Action {

    private ActionResult result;

    public ShowPageAction(String page) {
        result = new ActionResult(page);
    }

    @Override
    public ActionResult execute(HttpServletRequest request, HttpServletResponse resp) {
        return result;
    }
}

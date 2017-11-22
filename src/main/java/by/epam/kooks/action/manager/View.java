package by.epam.kooks.action.manager;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static by.epam.kooks.action.constants.Constants.*;

/**
 * Calls the appropriate page depending on the request
 *
 * @author Eugene Kooks
 */
public class View {

    private static final Logger log = LogManager.getLogger(View.class);

    private HttpServletRequest request;
    private HttpServletResponse response;

    public View(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void navigate(ActionResult result) {
        try {
            if (result.isRedirect()) {
                response.sendRedirect(result.getView());
            } else {
                String path = PATH_TO_JSP + result.getView() + JSP_FORMAT;
                request.getRequestDispatcher(path).forward(request, response);
            }
        } catch (ServletException | IOException e) {
            log.error("Redirect dispatcher doesn't work" ,e);
        }
    }
}

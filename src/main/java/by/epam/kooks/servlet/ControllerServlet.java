package by.epam.kooks.servlet;

import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionFactory;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.action.manager.View;
import by.epam.kooks.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet class, the main and only servlet, a single entry point for all queries
 *
 * @author Eugene Kooks
 */

public class ControllerServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(ControllerServlet.class);

    private ActionFactory actionFactory;

    @Override
    public void init() throws ServletException {
        log.info("The servlet/app start working");
        actionFactory = new ActionFactory();
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        Action action = actionFactory.getAction(req);
        ActionResult result = action.execute(req, resp);
        View view = new View(req, resp);
        view.navigate(result);
    }

    @Override
    public void destroy() {
        log.info("The servlet/app stopped working");
        ConnectionPool.getInstance().destroyConnections();
    }
}
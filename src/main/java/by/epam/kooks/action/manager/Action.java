package by.epam.kooks.action.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface Action {
       ActionResult execute(HttpServletRequest req, HttpServletResponse resp);
}

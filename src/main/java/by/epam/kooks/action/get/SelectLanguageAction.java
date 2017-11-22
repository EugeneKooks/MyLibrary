package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.Action;
import by.epam.kooks.action.manager.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Action class, responsible for changing languages
 *
 * @author Eugene Kooks
 */
public class SelectLanguageAction implements Action {
    private static final Logger log = LogManager.getLogger(SelectLanguageAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){
        String language = req.getParameter(Constants.LANG);
        Config.set(req.getSession(), Config.FMT_LOCALE, new Locale(language));
        Cookie cookie = new Cookie(Constants.LANG, language);
        cookie.setMaxAge(Constants.HOUR * Constants.MINUTE * Constants.SEC);
        resp.addCookie(cookie);
        try {
            req.setCharacterEncoding(Constants.CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("Impossible to set character encoding" ,e);
        }
        return new ActionResult(req.getHeader(Constants.REFERER), true);

    }
}

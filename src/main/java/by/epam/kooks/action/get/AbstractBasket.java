package by.epam.kooks.action.get;
import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.entity.Basket;

import javax.servlet.http.HttpSession;

/**
 * Action class, intended to take data about the basket from sessions
 *
 * @author Eugene Kooks
 */
public class AbstractBasket {
    protected Basket getBasket(HttpSession session) {
        Basket basket;

        if (session.getAttribute(Constants.ATT_BASKET) == null) {
            basket = new Basket();
        } else {
            basket = (Basket) session.getAttribute(Constants.ATT_BASKET);
        }
        return basket;
    }

}

package by.epam.kooks.action.get;

import by.epam.kooks.action.constants.Constants;
import by.epam.kooks.action.manager.ActionResult;
import by.epam.kooks.entity.Basket;
import by.epam.kooks.action.manager.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action class, provide data about basket
 *
 * @author Eugene Kooks
 */
public class PageBasketAction extends AbstractBasket implements Action {
    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp){
        Basket basket = getBasket(req.getSession());
        req.setAttribute(Constants.ATT_BOOKS, basket);
        return new ActionResult(Constants.BASKET);
    }
}

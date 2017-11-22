package by.epam.kooks.action.manager;

/**
 * @author Eugene Kooks
 */
public class ActionResult {
    private final String view;
    private final boolean redirect;

    public ActionResult(String page, boolean redirect) {
        this.view = page;
        this.redirect = redirect;
    }

    public ActionResult(String page) {
        this.view = page;
        this.redirect = false;
    }

    public String getView() {
        return view;
    }

    public boolean isRedirect() {
        return redirect;
    }
}

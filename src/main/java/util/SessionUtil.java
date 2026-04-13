package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 * Utility for session lifecycle and user retrieval.
 */
public final class SessionUtil {

    private static final String LOGGED_IN_USER_ATTR = "loggedInUser";

    private SessionUtil() {
        // Utility class
    }

    public static void createSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(LOGGED_IN_USER_ATTR, user);
    }

    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object userObj = session.getAttribute(LOGGED_IN_USER_ATTR);
        if (userObj instanceof User) {
            return (User) userObj;
        }
        return null;
    }

    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInUser(request) != null;
    }
}

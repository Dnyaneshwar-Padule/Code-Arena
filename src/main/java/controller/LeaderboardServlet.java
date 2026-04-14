package controller;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;

import exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import util.ErrorHandlerUtil;
import util.HibernateUtil;

@WebServlet(name = "LeaderboardServlet", urlPatterns = {"/leaderboard"})
public class LeaderboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<User> leaderboardUsers = fetchLeaderboardUsers();
            request.setAttribute("leaderboardUsers", leaderboardUsers);
            request.setAttribute("currentUserId", getLoggedInUserId(request));
            request.getRequestDispatcher("/jsp/leaderboard.jsp").forward(request, response);
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(
                    request,
                    response,
                    ex,
                    "Unable to load leaderboard right now.",
                    "/jsp/leaderboard.jsp"
            );
        }
    }

    private List<User> fetchLeaderboardUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from User u where u.status = 'ACTIVE' order by coalesce(u.rating, 0) desc, u.username asc",
                            User.class
                    )
                    .setMaxResults(20)
                    .list();
        } catch (Exception ex) {
            throw new ServiceException("Unable to load leaderboard right now.", ex);
        }
    }

    private Long getLoggedInUserId(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return null;
        }
        Object value = request.getSession(false).getAttribute("loggedInUser");
        if (!(value instanceof User)) {
            return null;
        }
        return ((User) value).getId();
    }
}

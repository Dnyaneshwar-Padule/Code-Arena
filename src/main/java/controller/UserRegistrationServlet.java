package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.UserService;
import service.impl.UserServiceImpl;

import java.io.IOException;

/**
 * Controller layer servlet for handling registration endpoints.
 * It receives HTTP requests and delegates business operations to the service layer.
 */
@WebServlet(name = "UserRegistrationServlet", urlPatterns = "/register")
public class UserRegistrationServlet extends HttpServlet {

    private transient UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Placeholder flow: only demonstrate layer interaction
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setEmail(request.getParameter("email"));
        user.setPassword(request.getParameter("password"));

        userService.registerUser(user);
        response.sendRedirect(request.getContextPath() + "/jsp/index.jsp");
    }
}

package controller;

import exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.UserService;
import service.impl.UserServiceImpl;
import util.ErrorHandlerUtil;
import util.SessionUtil;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    private transient UserService userService;
    private static final String REGISTER_ERROR_FALLBACK = "Unable to process registration right now. Please try again.";

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
        try {
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            User registeredUser = userService.registerUser(username, email, password);
            SessionUtil.createSession(request, registeredUser);
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(request, response, ex, REGISTER_ERROR_FALLBACK, "/jsp/register.jsp");
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(request, response, ex, REGISTER_ERROR_FALLBACK, "/jsp/register.jsp");
        }
    }
}

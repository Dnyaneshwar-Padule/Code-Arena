package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.EmailVerificationPurpose;
import service.UserService;
import service.EmailVerificationService;
import service.impl.EmailVerificationServiceImpl;
import service.impl.UserServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    private transient UserService userService;
    private transient EmailVerificationService emailVerificationService;
    private static final String REGISTER_ERROR_FALLBACK = "Unable to process registration right now. Please try again.";

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.emailVerificationService = new EmailVerificationServiceImpl();
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
            String username = getTrimmedParameter(request, "username");
            String email = getTrimmedParameter(request, "email");
            String password = getTrimmedParameter(request, "password");

            validateRegistrationRequest(username, email, password);

            Long verificationId = emailVerificationService.createVerification(email, EmailVerificationPurpose.REGISTER);
            HttpSession session = request.getSession(true);
            clearPendingRegistrationSession(session);
            session.setAttribute(RegisterOtpServlet.REGISTER_VERIFICATION_ID_SESSION_KEY, verificationId);
            session.setAttribute(RegisterOtpServlet.PENDING_REGISTER_USERNAME_SESSION_KEY, username.trim());
            session.setAttribute(RegisterOtpServlet.PENDING_REGISTER_EMAIL_SESSION_KEY, email.trim().toLowerCase());
            session.setAttribute(RegisterOtpServlet.PENDING_REGISTER_PASSWORD_SESSION_KEY, password);

            response.sendRedirect(request.getContextPath() + "/register/verify");
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(request, response, ex, REGISTER_ERROR_FALLBACK, "/jsp/register.jsp");
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(request, response, ex, REGISTER_ERROR_FALLBACK, "/jsp/register.jsp");
        }
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }

    private void validateRegistrationRequest(String username, String email, String password) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String normalizedPassword = password == null ? "" : password.trim();

        if (normalizedUsername.isEmpty() || normalizedEmail.isEmpty() || normalizedPassword.isEmpty()) {
            throw new ValidationException("Please fill all required fields.");
        }
        if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (normalizedPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters.");
        }
        if (userService.getUserByEmail(normalizedEmail) != null || userService.getUserByUsername(normalizedUsername) != null) {
            throw new ValidationException("User already exists.");
        }
    }

    private void clearPendingRegistrationSession(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(RegisterOtpServlet.REGISTER_VERIFICATION_ID_SESSION_KEY);
        session.removeAttribute(RegisterOtpServlet.PENDING_REGISTER_USERNAME_SESSION_KEY);
        session.removeAttribute(RegisterOtpServlet.PENDING_REGISTER_EMAIL_SESSION_KEY);
        session.removeAttribute(RegisterOtpServlet.PENDING_REGISTER_PASSWORD_SESSION_KEY);
    }
}

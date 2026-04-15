package controller;

import exception.ServiceException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserService;
import service.impl.UserServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;

@WebServlet(name = "ForgotPasswordResetServlet", urlPatterns = "/forgot-password/reset")
public class ForgotPasswordResetServlet extends HttpServlet {

    private static final String FORGOT_PASSWORD_RESET_ERROR_FALLBACK = "Unable to reset password right now. Please try again.";

    private transient UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isForgotPasswordVerified(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        request.getRequestDispatcher("/jsp/forgot-password-reset.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isForgotPasswordVerified(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        try {
            String newPassword = getTrimmedParameter(request, "newPassword");
            String confirmPassword = getTrimmedParameter(request, "confirmPassword");

            if (newPassword == null || newPassword.isBlank() || confirmPassword == null || confirmPassword.isBlank()) {
                throw new ValidationException("Please fill all required fields.");
            }
            if (!newPassword.equals(confirmPassword)) {
                throw new ValidationException("Passwords do not match.");
            }

            String email = (String) session.getAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_EMAIL_SESSION_KEY);
            userService.resetPassword(email, newPassword);

            clearForgotPasswordSession(session);
            response.sendRedirect(request.getContextPath() + "/login?reset=success");
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(request, response, ex, FORGOT_PASSWORD_RESET_ERROR_FALLBACK, "/jsp/forgot-password-reset.jsp");
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(request, response, ex, FORGOT_PASSWORD_RESET_ERROR_FALLBACK, "/jsp/forgot-password-reset.jsp");
        }
    }

    private boolean isForgotPasswordVerified(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object email = session.getAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_EMAIL_SESSION_KEY);
        Object verified = session.getAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_OTP_VERIFIED_SESSION_KEY);
        return email instanceof String && Boolean.TRUE.equals(verified);
    }

    private void clearForgotPasswordSession(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_VERIFICATION_ID_SESSION_KEY);
        session.removeAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_EMAIL_SESSION_KEY);
        session.removeAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_OTP_VERIFIED_SESSION_KEY);
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }
}

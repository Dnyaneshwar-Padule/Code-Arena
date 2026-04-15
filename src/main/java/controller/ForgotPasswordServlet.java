package controller;

import exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.EmailVerificationPurpose;
import model.User;
import service.EmailVerificationService;
import service.UserService;
import service.impl.EmailVerificationServiceImpl;
import service.impl.UserServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = "/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    public static final String FORGOT_PASSWORD_VERIFICATION_ID_SESSION_KEY = "forgotPasswordVerificationId";
    public static final String FORGOT_PASSWORD_EMAIL_SESSION_KEY = "forgotPasswordEmail";
    public static final String FORGOT_PASSWORD_OTP_VERIFIED_SESSION_KEY = "forgotPasswordOtpVerified";

    private static final String FORGOT_PASSWORD_ERROR_FALLBACK = "Unable to process request right now. Please try again.";

    private transient EmailVerificationService emailVerificationService;
    private transient UserService userService;

    @Override
    public void init() throws ServletException {
        this.emailVerificationService = new EmailVerificationServiceImpl();
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(true);
            clearForgotPasswordSession(session);

            String email = getTrimmedParameter(request, "email");
            User existingUser = userService.getUserByEmail(email);

            if (existingUser == null) {
                request.setAttribute("success", "If an account exists for this email, an OTP has been sent.");
                request.getRequestDispatcher("/jsp/forgot-password.jsp").forward(request, response);
                return;
            }

            Long verificationId = emailVerificationService.createVerification(email, EmailVerificationPurpose.FORGOT_PASSWORD);
            session.setAttribute(FORGOT_PASSWORD_VERIFICATION_ID_SESSION_KEY, verificationId);
            session.setAttribute(FORGOT_PASSWORD_EMAIL_SESSION_KEY, existingUser.getEmail());
            session.setAttribute(FORGOT_PASSWORD_OTP_VERIFIED_SESSION_KEY, Boolean.FALSE);

            response.sendRedirect(request.getContextPath() + "/forgot-password/verify");
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(request, response, ex, FORGOT_PASSWORD_ERROR_FALLBACK, "/jsp/forgot-password.jsp");
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(request, response, ex, FORGOT_PASSWORD_ERROR_FALLBACK, "/jsp/forgot-password.jsp");
        }
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }

    private void clearForgotPasswordSession(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(FORGOT_PASSWORD_VERIFICATION_ID_SESSION_KEY);
        session.removeAttribute(FORGOT_PASSWORD_EMAIL_SESSION_KEY);
        session.removeAttribute(FORGOT_PASSWORD_OTP_VERIFIED_SESSION_KEY);
    }
}

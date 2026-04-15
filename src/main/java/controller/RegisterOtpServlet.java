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
import util.SessionUtil;

import java.io.IOException;

@WebServlet(name = "RegisterOtpServlet", urlPatterns = "/register/verify")
public class RegisterOtpServlet extends HttpServlet {

    public static final String REGISTER_VERIFICATION_ID_SESSION_KEY = "registerVerificationId";
    public static final String PENDING_REGISTER_USERNAME_SESSION_KEY = "pendingRegisterUsername";
    public static final String PENDING_REGISTER_EMAIL_SESSION_KEY = "pendingRegisterEmail";
    public static final String PENDING_REGISTER_PASSWORD_SESSION_KEY = "pendingRegisterPassword";

    private static final String REGISTER_OTP_ERROR_FALLBACK = "Unable to verify OTP right now. Please try again.";

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
        HttpSession session = request.getSession(false);
        if (!isPendingRegistrationPresent(session)) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        request.getRequestDispatcher("/jsp/register-otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isPendingRegistrationPresent(session)) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        String action = getTrimmedParameter(request, "action");

        try {
            if ("resend".equalsIgnoreCase(action)) {
                String email = (String) session.getAttribute(PENDING_REGISTER_EMAIL_SESSION_KEY);
                Long newVerificationId = emailVerificationService.resendVerification(email, EmailVerificationPurpose.REGISTER);
                session.setAttribute(REGISTER_VERIFICATION_ID_SESSION_KEY, newVerificationId);
                request.setAttribute("success", "A new OTP has been sent to your email.");
                request.getRequestDispatcher("/jsp/register-otp.jsp").forward(request, response);
                return;
            }

            Long verificationId = (Long) session.getAttribute(REGISTER_VERIFICATION_ID_SESSION_KEY);
            String otp = getTrimmedParameter(request, "otp");
            emailVerificationService.verifyOtp(verificationId, otp, EmailVerificationPurpose.REGISTER);

            String username = (String) session.getAttribute(PENDING_REGISTER_USERNAME_SESSION_KEY);
            String email = (String) session.getAttribute(PENDING_REGISTER_EMAIL_SESSION_KEY);
            String password = (String) session.getAttribute(PENDING_REGISTER_PASSWORD_SESSION_KEY);

            User registeredUser = userService.registerUser(username, email, password);
            clearRegistrationSession(session);
            SessionUtil.createSession(request, registeredUser);
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(request, response, ex, REGISTER_OTP_ERROR_FALLBACK, "/jsp/register-otp.jsp");
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(request, response, ex, REGISTER_OTP_ERROR_FALLBACK, "/jsp/register-otp.jsp");
        }
    }

    private boolean isPendingRegistrationPresent(HttpSession session) {
        if (session == null) {
            return false;
        }
        return session.getAttribute(REGISTER_VERIFICATION_ID_SESSION_KEY) instanceof Long
                && session.getAttribute(PENDING_REGISTER_USERNAME_SESSION_KEY) instanceof String
                && session.getAttribute(PENDING_REGISTER_EMAIL_SESSION_KEY) instanceof String
                && session.getAttribute(PENDING_REGISTER_PASSWORD_SESSION_KEY) instanceof String;
    }

    private void clearRegistrationSession(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(REGISTER_VERIFICATION_ID_SESSION_KEY);
        session.removeAttribute(PENDING_REGISTER_USERNAME_SESSION_KEY);
        session.removeAttribute(PENDING_REGISTER_EMAIL_SESSION_KEY);
        session.removeAttribute(PENDING_REGISTER_PASSWORD_SESSION_KEY);
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }
}

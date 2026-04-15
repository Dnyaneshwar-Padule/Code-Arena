package controller;

import exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.EmailVerificationPurpose;
import service.EmailVerificationService;
import service.impl.EmailVerificationServiceImpl;
import util.ErrorHandlerUtil;

import java.io.IOException;

@WebServlet(name = "ForgotPasswordOtpServlet", urlPatterns = "/forgot-password/verify")
public class ForgotPasswordOtpServlet extends HttpServlet {

    private static final String FORGOT_PASSWORD_VERIFY_ERROR_FALLBACK = "Unable to verify OTP right now. Please try again.";

    private transient EmailVerificationService emailVerificationService;

    @Override
    public void init() throws ServletException {
        this.emailVerificationService = new EmailVerificationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isForgotPasswordOtpSessionPresent(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        request.getRequestDispatcher("/jsp/forgot-password-otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isForgotPasswordOtpSessionPresent(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String action = getTrimmedParameter(request, "action");

        try {
            String email = (String) session.getAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_EMAIL_SESSION_KEY);

            if ("resend".equalsIgnoreCase(action)) {
                Long newVerificationId = emailVerificationService.resendVerification(email, EmailVerificationPurpose.FORGOT_PASSWORD);
                session.setAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_VERIFICATION_ID_SESSION_KEY, newVerificationId);
                request.setAttribute("success", "A new OTP has been sent to your email.");
                request.getRequestDispatcher("/jsp/forgot-password-otp.jsp").forward(request, response);
                return;
            }

            Long verificationId = (Long) session.getAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_VERIFICATION_ID_SESSION_KEY);
            String otp = getTrimmedParameter(request, "otp");
            emailVerificationService.verifyOtp(verificationId, otp, EmailVerificationPurpose.FORGOT_PASSWORD);

            session.setAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_OTP_VERIFIED_SESSION_KEY, Boolean.TRUE);
            response.sendRedirect(request.getContextPath() + "/forgot-password/reset");
        } catch (ServiceException ex) {
            ErrorHandlerUtil.handleException(request, response, ex, FORGOT_PASSWORD_VERIFY_ERROR_FALLBACK, "/jsp/forgot-password-otp.jsp");
        } catch (Exception ex) {
            ErrorHandlerUtil.handleException(request, response, ex, FORGOT_PASSWORD_VERIFY_ERROR_FALLBACK, "/jsp/forgot-password-otp.jsp");
        }
    }

    private boolean isForgotPasswordOtpSessionPresent(HttpSession session) {
        if (session == null) {
            return false;
        }
        return session.getAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_EMAIL_SESSION_KEY) instanceof String
                && session.getAttribute(ForgotPasswordServlet.FORGOT_PASSWORD_VERIFICATION_ID_SESSION_KEY) instanceof Long;
    }

    private String getTrimmedParameter(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        return value == null ? null : value.trim();
    }
}

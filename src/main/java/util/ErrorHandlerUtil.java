package util;

import exception.AppException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized utility for safe servlet exception handling.
 */
public final class ErrorHandlerUtil {

    private static final Logger LOGGER = Logger.getLogger(ErrorHandlerUtil.class.getName());

    private ErrorHandlerUtil() {
        // Utility class
    }

    public static void handleException(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception,
            String fallbackMessage,
            String viewPath
    ) throws ServletException, IOException {
        String safeMessage = fallbackMessage;
        if (exception instanceof AppException && exception.getMessage() != null && !exception.getMessage().isBlank()) {
            safeMessage = exception.getMessage();
        }

        LOGGER.log(Level.SEVERE, "Request processing failed: " + request.getRequestURI(), exception);
        request.setAttribute("error", safeMessage);

        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}

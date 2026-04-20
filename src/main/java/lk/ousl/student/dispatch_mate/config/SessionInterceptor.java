package lk.ousl.student.dispatch_mate.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// HandlerInterceptor runs BEFORE every controller method.
// We use it as an "auth guard" — if the user is not logged in,
// they get bounced back to the login page instead of seeing private pages.

@Component
public class SessionInterceptor implements HandlerInterceptor {

    // These session attribute keys must match what AuthController puts in the session
    public static final String SESSION_STUDENT = "loggedStudent";
    public static final String SESSION_STAFF   = "loggedStaff";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false); // false = don't create new session
        String path = request.getRequestURI();

        // ── Guard student routes ───────────────────────────────
        if (path.startsWith("/student")) {
            if (session == null || session.getAttribute(SESSION_STUDENT) == null) {
                // Not logged in → redirect to login, show a message
                response.sendRedirect("/login?error=Please+log+in+to+access+your+portal");
                return false; // stops the controller from running
            }
        }

        // ── Guard admin routes ─────────────────────────────────
        if (path.startsWith("/admin")) {
            if (session == null || session.getAttribute(SESSION_STAFF) == null) {
                response.sendRedirect("/login?error=Admin+login+required");
                return false;
            }
        }

        return true; // allow the request to proceed to the controller
    }
}

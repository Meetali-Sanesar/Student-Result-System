package com.srms.filter;

import com.srms.security.SessionManager;
import com.srms.util.ResponseUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Filter that enforces role-based access control (RBAC).
 * Admin-only endpoints are blocked for student users.
 */
public class AuthorizationFilter implements Filter {

    // Paths that require ADMIN role for write operations (POST, PUT, DELETE)
    private static final Set<String> ADMIN_WRITE_PATHS = Set.of(
            "/api/students",
            "/api/subjects",
            "/api/marks",
            "/api/results",
            "/api/audit-logs"
    );

    // Paths that are admin-only for ANY method
    private static final Set<String> ADMIN_ONLY_PATHS = Set.of(
            "/api/audit-logs",
            "/api/dashboard"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        String method = httpRequest.getMethod();

        // Skip non-API paths and auth endpoints
        if (!path.startsWith("/api/") || path.startsWith("/api/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        // Skip OPTIONS for CORS
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        // Skip if not authenticated (AuthenticationFilter handles this)
        if (!SessionManager.isAuthenticated(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String role = SessionManager.getUserRole(httpRequest);

        // Check admin-only paths
        for (String adminPath : ADMIN_ONLY_PATHS) {
            if (path.startsWith(adminPath)) {
                if (!"ADMIN".equals(role)) {
                    ResponseUtil.sendForbidden(httpResponse, "Access denied. Admin privileges required.");
                    return;
                }
            }
        }

        // Check admin-write paths for non-GET methods
        if (!"GET".equalsIgnoreCase(method)) {
            for (String adminWritePath : ADMIN_WRITE_PATHS) {
                if (path.startsWith(adminWritePath)) {
                    if (!"ADMIN".equals(role)) {
                        ResponseUtil.sendForbidden(httpResponse, "Access denied. Only admins can modify data.");
                        return;
                    }
                }
            }
        }

        // Students can only access their own data for certain endpoints
        if ("STUDENT".equals(role)) {
            // Students accessing file uploads for their own profile is allowed
            if (path.startsWith("/api/files/upload-image")) {
                chain.doFilter(request, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}

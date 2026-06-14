package com.srms.filter;

import com.srms.security.SessionManager;
import com.srms.util.ResponseUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

/**
 * Filter that checks if the user is authenticated before allowing access to API endpoints.
 * Excludes login endpoint and static resources.
 */
public class AuthenticationFilter implements Filter {

    // Endpoints that don't require authentication
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Allow OPTIONS requests (for CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Allow public endpoints
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check authentication
        if (!SessionManager.isAuthenticated(httpRequest)) {
            ResponseUtil.sendUnauthorized(httpResponse, "Authentication required. Please login.");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}

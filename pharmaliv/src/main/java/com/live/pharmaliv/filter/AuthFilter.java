package com.live.pharmaliv.filter;

import com.live.pharmaliv.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter for authentication and authorization.
 * Protects resources that require authentication and checks user roles for access control.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {
    
    // Public resources that don't require authentication
    private static final Set<String> PUBLIC_RESOURCES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("/login", "/css/", "/js/", "/img/", "/favicon.ico")));
    
    // Role-based access control
    private static final Set<String> ADMIN_RESOURCES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("/users", "/users/")));
    
    private static final Set<String> PHARMACIST_RESOURCES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("/products", "/products/", "/batches", "/batches/", "/movements", "/movements/")));
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getServletPath();
        
        // Check if the resource is public
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if the user is authenticated
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // User is not authenticated, redirect to login page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // User is authenticated, check role-based access control
        User user = (User) session.getAttribute("user");
        String userRole = user.getRole().toString();
        
        if (isAdminResource(path) && !userRole.equals("admin")) {
            // User is not an admin, redirect to dashboard with access denied message
            session.setAttribute("errorMessage", "Accès refusé. Vous n'avez pas les droits nécessaires.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard");
            return;
        }
        
        if (isPharmacistResource(path) && !userRole.equals("admin") && !userRole.equals("pharmacist")) {
            // User is not an admin or pharmacist, redirect to dashboard with access denied message
            session.setAttribute("errorMessage", "Accès refusé. Vous n'avez pas les droits nécessaires.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard");
            return;
        }
        
        // User has access to the resource, continue the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
    
    /**
     * Check if the resource is public (doesn't require authentication).
     * @param path The resource path
     * @return true if the resource is public, false otherwise
     */
    private boolean isPublicResource(String path) {
        return PUBLIC_RESOURCES.stream().anyMatch(path::startsWith);
    }
    
    /**
     * Check if the resource is admin-only.
     * @param path The resource path
     * @return true if the resource is admin-only, false otherwise
     */
    private boolean isAdminResource(String path) {
        return ADMIN_RESOURCES.stream().anyMatch(path::startsWith);
    }
    
    /**
     * Check if the resource is pharmacist-only.
     * @param path The resource path
     * @return true if the resource is pharmacist-only, false otherwise
     */
    private boolean isPharmacistResource(String path) {
        return PHARMACIST_RESOURCES.stream().anyMatch(path::startsWith);
    }
}
package com.live.pharmaliv.servlet;

import com.live.pharmaliv.dao.*;
import com.live.pharmaliv.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling user-related operations.
 * Mapped to /users URL pattern.
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/users", "/users/*"})
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    /**
     * Initialize the servlet.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAOImpl();
    }

    /**
     * Handle GET requests - display users list, user details, or user form.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            session.setAttribute("errorMessage", "Accès refusé. Vous devez être administrateur pour accéder à cette page.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Display users list
            List<User> users = userDAO.findAll();
            request.setAttribute("users", users);
            request.setAttribute("pageTitle", "Utilisateurs");
            request.getRequestDispatcher("/jsp/users.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // Display user details
            try {
                int userId = Integer.parseInt(pathInfo.substring(6));
                User user = userDAO.findById(userId);
                
                if (user != null) {
                    request.setAttribute("user", user);
                    request.setAttribute("pageTitle", "Détails de l'utilisateur");
                    request.getRequestDispatcher("/jsp/user-details.jsp").forward(request, response);
                } else {
                    // User not found
                    session.setAttribute("errorMessage", "Utilisateur non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/users");
                }
            } catch (NumberFormatException e) {
                // Invalid user ID
                session.setAttribute("errorMessage", "ID d'utilisateur invalide.");
                response.sendRedirect(request.getContextPath() + "/users");
            }
        } else if (pathInfo.equals("/add")) {
            // Display add user form
            request.setAttribute("userRoles", User.Role.values());
            request.setAttribute("pageTitle", "Nouvel utilisateur");
            request.getRequestDispatcher("/jsp/user-form.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            // Display edit user form
            try {
                int userId = Integer.parseInt(pathInfo.substring(6));
                User user = userDAO.findById(userId);
                
                if (user != null) {
                    request.setAttribute("user", user);
                    request.setAttribute("userRoles", User.Role.values());
                    request.setAttribute("pageTitle", "Modifier l'utilisateur");
                    request.getRequestDispatcher("/jsp/user-form.jsp").forward(request, response);
                } else {
                    // User not found
                    session.setAttribute("errorMessage", "Utilisateur non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/users");
                }
            } catch (NumberFormatException e) {
                // Invalid user ID
                session.setAttribute("errorMessage", "ID d'utilisateur invalide.");
                response.sendRedirect(request.getContextPath() + "/users");
            }
        } else if (pathInfo.startsWith("/delete/")) {
            // Delete user
            try {
                int userId = Integer.parseInt(pathInfo.substring(8));
                
                // Don't allow deleting the current user
                Integer currentUserId = (Integer) session.getAttribute("userId");
                if (currentUserId != null && currentUserId == userId) {
                    session.setAttribute("errorMessage", "Vous ne pouvez pas supprimer votre propre compte.");
                    response.sendRedirect(request.getContextPath() + "/users");
                    return;
                }
                
                boolean deleted = userDAO.delete(userId);
                
                if (deleted) {
                    session.setAttribute("successMessage", "Utilisateur supprimé avec succès.");
                } else {
                    session.setAttribute("errorMessage", "Utilisateur non trouvé.");
                }
                response.sendRedirect(request.getContextPath() + "/users");
            } catch (NumberFormatException e) {
                // Invalid user ID
                session.setAttribute("errorMessage", "ID d'utilisateur invalide.");
                response.sendRedirect(request.getContextPath() + "/users");
            }
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    /**
     * Handle POST requests - process user form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            session.setAttribute("errorMessage", "Accès refusé. Vous devez être administrateur pour effectuer cette action.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action) || "edit".equals(action)) {
            // Process user form
            try {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String roleStr = request.getParameter("role");
                
                // Validate required fields
                if (username == null || username.trim().isEmpty() ||
                    fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    roleStr == null || roleStr.trim().isEmpty()) {
                    
                    session.setAttribute("errorMessage", "Tous les champs marqués d'un astérisque sont obligatoires.");
                    if ("add".equals(action)) {
                        response.sendRedirect(request.getContextPath() + "/users/add");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/users/edit/" + request.getParameter("id"));
                    }
                    return;
                }
                
                // Validate role
                User.Role role;
                try {
                    role = User.Role.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    session.setAttribute("errorMessage", "Rôle invalide.");
                    if ("add".equals(action)) {
                        response.sendRedirect(request.getContextPath() + "/users/add");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/users/edit/" + request.getParameter("id"));
                    }
                    return;
                }
                
                User user;
                if ("add".equals(action)) {
                    // For new users, password is required
                    if (password == null || password.trim().isEmpty()) {
                        session.setAttribute("errorMessage", "Le mot de passe est obligatoire pour un nouvel utilisateur.");
                        response.sendRedirect(request.getContextPath() + "/users/add");
                        return;
                    }
                    
                    // Check if username already exists
                    User existingUser = userDAO.findByUsername(username);
                    if (existingUser != null) {
                        session.setAttribute("errorMessage", "Ce nom d'utilisateur est déjà utilisé.");
                        response.sendRedirect(request.getContextPath() + "/users/add");
                        return;
                    }
                    
                    // Create new user
                    user = new User();
                } else {
                    // Edit existing user
                    int userId = Integer.parseInt(request.getParameter("id"));
                    user = userDAO.findById(userId);
                    
                    if (user == null) {
                        session.setAttribute("errorMessage", "Utilisateur non trouvé.");
                        response.sendRedirect(request.getContextPath() + "/users");
                        return;
                    }
                    
                    // Check if username already exists (for another user)
                    User existingUser = userDAO.findByUsername(username);
                    if (existingUser != null && existingUser.getId() != userId) {
                        session.setAttribute("errorMessage", "Ce nom d'utilisateur est déjà utilisé.");
                        response.sendRedirect(request.getContextPath() + "/users/edit/" + userId);
                        return;
                    }
                }
                
                // Set user properties
                user.setUsername(username);
                if (password != null && !password.trim().isEmpty()) {
                    user.setPassword(password);
                }
                user.setFullName(fullName);
                user.setEmail(email);
                user.setRole(role);
                
                // Save user
                user = userDAO.save(user);
                
                session.setAttribute("successMessage", "Utilisateur " + ("add".equals(action) ? "créé" : "modifié") + " avec succès.");
                response.sendRedirect(request.getContextPath() + "/users");
                
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID d'utilisateur invalide.");
                response.sendRedirect(request.getContextPath() + "/users");
            } catch (Exception e) {
                session.setAttribute("errorMessage", "Erreur lors de l'enregistrement de l'utilisateur: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/users");
            }
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }
}
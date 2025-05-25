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

/**
 * Servlet for handling user profile operations.
 * Mapped to /profile URL pattern.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO;
    private SaleDAO saleDAO;
    private MovementDAO movementDAO;

    /**
     * Initialize the servlet.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAOImpl();
        saleDAO = new SaleDAOImpl();
        movementDAO = new MovementDAOImpl();
    }

    /**
     * Handle GET requests - display user profile.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            session.setAttribute("errorMessage", "Session expirée. Veuillez vous reconnecter.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = userDAO.findById(userId);
        if (user == null) {
            session.setAttribute("errorMessage", "Utilisateur non trouvé.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Get recent sales and movements for the user
        request.setAttribute("userSales", saleDAO.findByUserId(userId));
        request.setAttribute("userMovements", movementDAO.findByUserId(userId));
        
        request.setAttribute("user", user);
        request.setAttribute("pageTitle", "Mon profil");
        request.getRequestDispatcher("/jsp/user-profile.jsp").forward(request, response);
    }

    /**
     * Handle POST requests - update user profile.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            session.setAttribute("errorMessage", "Session expirée. Veuillez vous reconnecter.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = userDAO.findById(userId);
        if (user == null) {
            session.setAttribute("errorMessage", "Utilisateur non trouvé.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("updateProfile".equals(action)) {
            // Update profile information
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            
            // Validate required fields
            if (fullName == null || fullName.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
                
                session.setAttribute("errorMessage", "Tous les champs marqués d'un astérisque sont obligatoires.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }
            
            user.setFullName(fullName);
            user.setEmail(email);
            
            userDAO.save(user);
            
            session.setAttribute("successMessage", "Profil mis à jour avec succès.");
            response.sendRedirect(request.getContextPath() + "/profile");
            
        } else if ("changePassword".equals(action)) {
            // Change password
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Validate required fields
            if (currentPassword == null || currentPassword.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
                
                session.setAttribute("errorMessage", "Tous les champs sont obligatoires pour changer le mot de passe.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }
            
            // Validate current password
            User authenticatedUser = userDAO.authenticate(user.getUsername(), currentPassword);
            if (authenticatedUser == null) {
                session.setAttribute("errorMessage", "Mot de passe actuel incorrect.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }
            
            // Validate new password
            if (!newPassword.equals(confirmPassword)) {
                session.setAttribute("errorMessage", "Les nouveaux mots de passe ne correspondent pas.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }
            
            // Update password
            user.setPassword(newPassword);
            userDAO.save(user);
            
            session.setAttribute("successMessage", "Mot de passe changé avec succès.");
            response.sendRedirect(request.getContextPath() + "/profile");
            
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }
}
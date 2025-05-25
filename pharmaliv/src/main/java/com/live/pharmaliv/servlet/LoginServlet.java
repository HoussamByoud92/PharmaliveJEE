package com.live.pharmaliv.servlet;

import com.live.pharmaliv.dao.UserDAO;
import com.live.pharmaliv.dao.UserDAOImpl;
import com.live.pharmaliv.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet for handling user login.
 */
@WebServlet(name = "loginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAOImpl();
    }

    /**
     * Handle GET requests - display the login form.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // User is already logged in, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    /**
     * Handle POST requests - process login form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validate input
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Veuillez saisir un nom d'utilisateur et un mot de passe.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate user
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            // Authentication successful
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userRole", user.getRole().toString());
            
            // Redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
}
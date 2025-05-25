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
 * Servlet for handling alert-related operations.
 * Mapped to /alerts URL pattern.
 */
@WebServlet(name = "AlertServlet", urlPatterns = {"/alerts", "/alerts/*"})
public class AlertServlet extends HttpServlet {
    private AlertDAO alertDAO;
    private ProductDAO productDAO;

    /**
     * Initialize the servlet.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        alertDAO = new AlertDAOImpl();
        productDAO = new ProductDAOImpl();
    }

    /**
     * Handle GET requests - display alerts list, alert details, or alert form.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Display alerts list
            List<Alert> alerts = alertDAO.findAll();
            request.setAttribute("alerts", alerts);
            request.setAttribute("pageTitle", "Alertes");
            request.getRequestDispatcher("/jsp/alerts.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // Display alert details
            try {
                int alertId = Integer.parseInt(pathInfo.substring(6));
                Alert alert = alertDAO.findById(alertId);
                
                if (alert != null) {
                    request.setAttribute("alert", alert);
                    request.setAttribute("pageTitle", "Détails de l'alerte");
                    request.getRequestDispatcher("/jsp/alert-details.jsp").forward(request, response);
                } else {
                    // Alert not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Alerte non trouvée.");
                    response.sendRedirect(request.getContextPath() + "/alerts");
                }
            } catch (NumberFormatException e) {
                // Invalid alert ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID d'alerte invalide.");
                response.sendRedirect(request.getContextPath() + "/alerts");
            }
        } else if (pathInfo.equals("/create-stock-alerts")) {
            // Create stock alerts
            int count = alertDAO.createStockAlerts();
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", count + " alertes de stock créées.");
            response.sendRedirect(request.getContextPath() + "/alerts");
        } else if (pathInfo.equals("/create-expiry-alerts")) {
            // Create expiry alerts (30 days threshold)
            int count = alertDAO.createExpiryAlerts(30);
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", count + " alertes de péremption créées.");
            response.sendRedirect(request.getContextPath() + "/alerts");
        } else if (pathInfo.startsWith("/resolve/")) {
            // Resolve alert
            try {
                int alertId = Integer.parseInt(pathInfo.substring(9));
                boolean resolved = alertDAO.resolveAlert(alertId);
                
                HttpSession session = request.getSession();
                if (resolved) {
                    session.setAttribute("successMessage", "Alerte marquée comme résolue.");
                } else {
                    session.setAttribute("errorMessage", "Alerte non trouvée.");
                }
                response.sendRedirect(request.getContextPath() + "/alerts");
            } catch (NumberFormatException e) {
                // Invalid alert ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID d'alerte invalide.");
                response.sendRedirect(request.getContextPath() + "/alerts");
            }
        } else if (pathInfo.equals("/unresolved")) {
            // Display unresolved alerts
            List<Alert> alerts = alertDAO.findUnresolved();
            request.setAttribute("alerts", alerts);
            request.setAttribute("pageTitle", "Alertes non résolues");
            request.getRequestDispatcher("/jsp/alerts.jsp").forward(request, response);
        } else if (pathInfo.equals("/stock")) {
            // Display stock alerts
            List<Alert> alerts = alertDAO.findByType(Alert.Type.stock);
            request.setAttribute("alerts", alerts);
            request.setAttribute("pageTitle", "Alertes de stock");
            request.getRequestDispatcher("/jsp/alerts.jsp").forward(request, response);
        } else if (pathInfo.equals("/expiry")) {
            // Display expiry alerts
            List<Alert> alerts = alertDAO.findByType(Alert.Type.expiry);
            request.setAttribute("alerts", alerts);
            request.setAttribute("pageTitle", "Alertes de péremption");
            request.getRequestDispatcher("/jsp/alerts.jsp").forward(request, response);
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/alerts");
        }
    }

    /**
     * Handle POST requests - process alert form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        if ("resolve".equals(action)) {
            // Resolve alert
            try {
                int alertId = Integer.parseInt(request.getParameter("id"));
                boolean resolved = alertDAO.resolveAlert(alertId);
                
                if (resolved) {
                    session.setAttribute("successMessage", "Alerte marquée comme résolue.");
                } else {
                    session.setAttribute("errorMessage", "Alerte non trouvée.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID d'alerte invalide.");
            }
            response.sendRedirect(request.getContextPath() + "/alerts");
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/alerts");
        }
    }
}
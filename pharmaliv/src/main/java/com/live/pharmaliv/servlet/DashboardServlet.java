package com.live.pharmaliv.servlet;

import com.live.pharmaliv.dao.ProductDAO;
import com.live.pharmaliv.dao.ProductDAOImpl;
import com.live.pharmaliv.dao.AlertDAO;
import com.live.pharmaliv.dao.AlertDAOImpl;
import com.live.pharmaliv.dao.SaleDAO;
import com.live.pharmaliv.dao.SaleDAOImpl;
import com.live.pharmaliv.model.Alert;
import com.live.pharmaliv.model.Product;
import com.live.pharmaliv.model.Sale;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet for handling the dashboard page.
 * Displays statistics, charts, and recent alerts.
 */
@WebServlet(name = "dashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private ProductDAO productDAO;
    private AlertDAO alertDAO;
    private SaleDAO saleDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAOImpl();
        alertDAO = new AlertDAOImpl();
        saleDAO = new SaleDAOImpl();
    }

    /**
     * Handle GET requests - display the dashboard.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get total products count
        int totalProducts = productDAO.findAll().size();
        request.setAttribute("totalProducts", totalProducts);

        // Get products below threshold count
        List<Product> productsBelowThreshold = productDAO.findProductsBelowThreshold();
        request.setAttribute("productsBelowThreshold", productsBelowThreshold.size());

        // Get recent alerts (5 most recent)
        List<Alert> recentAlerts = alertDAO.findRecentAlerts(5);
        request.setAttribute("recentAlerts", recentAlerts);

        // Get unresolved alerts count for notification badge
        List<Alert> unresolvedAlerts = alertDAO.findUnresolved();
        request.setAttribute("unresolvedAlerts", unresolvedAlerts.size());

        // Get today's sales
        LocalDate today = LocalDate.now();
        BigDecimal todaySales = saleDAO.getTotalSalesForDate(today);
        request.setAttribute("todaySales", todaySales);

        // Get best selling products
        List<Product> bestSellingProducts = productDAO.getBestSellingProducts(5);
        request.setAttribute("bestSellingProducts", bestSellingProducts);

        // Get sales data for the last 7 days for chart
        Map<String, BigDecimal> salesData = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal total = saleDAO.getTotalSalesForDate(date);
            salesData.put(date.toString(), total);
        }
        request.setAttribute("salesData", salesData);

        // Get product status data for chart (stock below threshold, expiring soon, normal)
        int belowThreshold = productsBelowThreshold.size();
        int expiringSoon = alertDAO.countAlertsByType(Alert.Type.expiry);
        int normal = totalProducts - belowThreshold - expiringSoon;

        request.setAttribute("belowThreshold", belowThreshold);
        request.setAttribute("expiringSoon", expiringSoon);
        request.setAttribute("normal", normal);

        // Set page title
        request.setAttribute("pageTitle", "Tableau de bord");

        // Forward to dashboard JSP
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }
}

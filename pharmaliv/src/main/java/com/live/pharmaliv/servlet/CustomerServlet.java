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
 * Servlet for handling customer-related operations.
 * Mapped to /customers URL pattern.
 */
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customers", "/customers/*"})
public class CustomerServlet extends HttpServlet {
    private CustomerDAO customerDAO;
    private SaleDAO saleDAO;

    /**
     * Initialize the servlet.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        customerDAO = new CustomerDAOImpl();
        saleDAO = new SaleDAOImpl();
    }

    /**
     * Handle GET requests - display customers list, customer details, or customer form.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Display customers list
            List<Customer> customers = customerDAO.findAll();
            request.setAttribute("customers", customers);
            request.setAttribute("pageTitle", "Clients");
            request.getRequestDispatcher("/jsp/customers.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // Display customer details
            try {
                int customerId = Integer.parseInt(pathInfo.substring(6));
                Customer customer = customerDAO.findById(customerId);
                
                if (customer != null) {
                    // Get sales for this customer
                    List<Sale> customerSales = saleDAO.findByCustomerId(customerId);
                    
                    request.setAttribute("customer", customer);
                    request.setAttribute("customerSales", customerSales);
                    request.setAttribute("pageTitle", "Détails du client");
                    request.getRequestDispatcher("/jsp/customer-details.jsp").forward(request, response);
                } else {
                    // Customer not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Client non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/customers");
                }
            } catch (NumberFormatException e) {
                // Invalid customer ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de client invalide.");
                response.sendRedirect(request.getContextPath() + "/customers");
            }
        } else if (pathInfo.equals("/add")) {
            // Display add customer form
            request.setAttribute("pageTitle", "Nouveau client");
            request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            // Display edit customer form
            try {
                int customerId = Integer.parseInt(pathInfo.substring(6));
                Customer customer = customerDAO.findById(customerId);
                
                if (customer != null) {
                    request.setAttribute("customer", customer);
                    request.setAttribute("pageTitle", "Modifier le client");
                    request.getRequestDispatcher("/jsp/customer-form.jsp").forward(request, response);
                } else {
                    // Customer not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Client non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/customers");
                }
            } catch (NumberFormatException e) {
                // Invalid customer ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de client invalide.");
                response.sendRedirect(request.getContextPath() + "/customers");
            }
        } else if (pathInfo.startsWith("/delete/")) {
            // Delete customer
            try {
                int customerId = Integer.parseInt(pathInfo.substring(8));
                boolean deleted = customerDAO.delete(customerId);
                
                HttpSession session = request.getSession();
                if (deleted) {
                    session.setAttribute("successMessage", "Client supprimé avec succès.");
                } else {
                    session.setAttribute("errorMessage", "Client non trouvé.");
                }
                response.sendRedirect(request.getContextPath() + "/customers");
            } catch (NumberFormatException e) {
                // Invalid customer ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de client invalide.");
                response.sendRedirect(request.getContextPath() + "/customers");
            }
        } else if (pathInfo.startsWith("/search")) {
            // Search customers
            String query = request.getParameter("q");
            if (query != null && !query.trim().isEmpty()) {
                List<Customer> customers = customerDAO.findByName(query);
                request.setAttribute("customers", customers);
                request.setAttribute("searchQuery", query);
                request.setAttribute("pageTitle", "Recherche de clients");
                request.getRequestDispatcher("/jsp/customers.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/customers");
            }
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/customers");
        }
    }

    /**
     * Handle POST requests - process customer form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        if ("add".equals(action) || "edit".equals(action)) {
            // Process customer form
            try {
                String name = request.getParameter("name");
                String phone = request.getParameter("phone");
                String email = request.getParameter("email");
                String address = request.getParameter("address");
                
                // Validate required fields
                if (name == null || name.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Le nom du client est obligatoire.");
                    if ("add".equals(action)) {
                        response.sendRedirect(request.getContextPath() + "/customers/add");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/customers/edit/" + request.getParameter("id"));
                    }
                    return;
                }
                
                Customer customer;
                if ("add".equals(action)) {
                    // Create new customer
                    customer = new Customer();
                } else {
                    // Edit existing customer
                    int customerId = Integer.parseInt(request.getParameter("id"));
                    customer = customerDAO.findById(customerId);
                    
                    if (customer == null) {
                        session.setAttribute("errorMessage", "Client non trouvé.");
                        response.sendRedirect(request.getContextPath() + "/customers");
                        return;
                    }
                }
                
                // Set customer properties
                customer.setName(name);
                customer.setPhone(phone);
                customer.setEmail(email);
                customer.setAddress(address);
                
                // Save customer
                customer = customerDAO.save(customer);
                
                session.setAttribute("successMessage", "Client " + ("add".equals(action) ? "créé" : "modifié") + " avec succès.");
                response.sendRedirect(request.getContextPath() + "/customers/view/" + customer.getId());
                
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID de client invalide.");
                response.sendRedirect(request.getContextPath() + "/customers");
            } catch (Exception e) {
                session.setAttribute("errorMessage", "Erreur lors de l'enregistrement du client: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/customers");
            }
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/customers");
        }
    }
}
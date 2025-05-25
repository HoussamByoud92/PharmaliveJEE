package com.live.pharmaliv.servlet;

import com.live.pharmaliv.dao.ProductDAO;
import com.live.pharmaliv.dao.ProductDAOImpl;
import com.live.pharmaliv.dao.BatchDAO;
import com.live.pharmaliv.dao.BatchDAOImpl;
import com.live.pharmaliv.model.Product;
import com.live.pharmaliv.model.Batch;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet for handling product management.
 */
@WebServlet(name = "productsServlet", urlPatterns = {"/products", "/products/*"})
public class ProductsServlet extends HttpServlet {
    private ProductDAO productDAO;
    private BatchDAO batchDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAOImpl();
        batchDAO = new BatchDAOImpl();
    }

    /**
     * Handle GET requests - display products list or product details.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Display products list
            List<Product> products = productDAO.findAll();
            request.setAttribute("products", products);
            request.setAttribute("pageTitle", "Produits");
            request.getRequestDispatcher("/jsp/products.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // Display product details
            try {
                int productId = Integer.parseInt(pathInfo.substring(6));
                Product product = productDAO.findById(productId);
                
                if (product != null) {
                    List<Batch> batches = batchDAO.findByProductId(productId);
                    int totalStock = productDAO.getTotalStockQuantity(productId);
                    
                    request.setAttribute("product", product);
                    request.setAttribute("batches", batches);
                    request.setAttribute("totalStock", totalStock);
                    request.setAttribute("pageTitle", "Détails du produit");
                    request.getRequestDispatcher("/jsp/product-details.jsp").forward(request, response);
                } else {
                    // Product not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Produit non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/products");
                }
            } catch (NumberFormatException e) {
                // Invalid product ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de produit invalide.");
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } else if (pathInfo.equals("/add")) {
            // Display add product form
            request.setAttribute("pageTitle", "Ajouter un produit");
            request.getRequestDispatcher("/jsp/product-form.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            // Display edit product form
            try {
                int productId = Integer.parseInt(pathInfo.substring(6));
                Product product = productDAO.findById(productId);
                
                if (product != null) {
                    request.setAttribute("product", product);
                    request.setAttribute("pageTitle", "Modifier un produit");
                    request.getRequestDispatcher("/jsp/product-form.jsp").forward(request, response);
                } else {
                    // Product not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Produit non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/products");
                }
            } catch (NumberFormatException e) {
                // Invalid product ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de produit invalide.");
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } else if (pathInfo.startsWith("/delete/")) {
            // Delete product
            try {
                int productId = Integer.parseInt(pathInfo.substring(8));
                boolean deleted = productDAO.delete(productId);
                
                HttpSession session = request.getSession();
                if (deleted) {
                    session.setAttribute("successMessage", "Produit supprimé avec succès.");
                } else {
                    session.setAttribute("errorMessage", "Erreur lors de la suppression du produit.");
                }
                response.sendRedirect(request.getContextPath() + "/products");
            } catch (NumberFormatException e) {
                // Invalid product ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de produit invalide.");
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }

    /**
     * Handle POST requests - process product form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("add".equals(action) || "edit".equals(action)) {
            // Get form data
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            String dci = request.getParameter("dci");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String thresholdQuantityStr = request.getParameter("thresholdQuantity");
            
            // Validate input
            HttpSession session = request.getSession();
            if (code == null || code.trim().isEmpty() || 
                name == null || name.trim().isEmpty() || 
                priceStr == null || priceStr.trim().isEmpty() || 
                thresholdQuantityStr == null || thresholdQuantityStr.trim().isEmpty()) {
                
                session.setAttribute("errorMessage", "Veuillez remplir tous les champs obligatoires.");
                if ("add".equals(action)) {
                    response.sendRedirect(request.getContextPath() + "/products/add");
                } else {
                    String productIdStr = request.getParameter("id");
                    response.sendRedirect(request.getContextPath() + "/products/edit/" + productIdStr);
                }
                return;
            }
            
            try {
                BigDecimal price = new BigDecimal(priceStr);
                int thresholdQuantity = Integer.parseInt(thresholdQuantityStr);
                
                Product product = new Product();
                if ("edit".equals(action)) {
                    int productId = Integer.parseInt(request.getParameter("id"));
                    product.setId(productId);
                }
                
                product.setCode(code);
                product.setName(name);
                product.setDci(dci);
                product.setDescription(description);
                product.setPrice(price);
                product.setThresholdQuantity(thresholdQuantity);
                
                // Save product
                product = productDAO.save(product);
                
                // Set success message and redirect
                if ("add".equals(action)) {
                    session.setAttribute("successMessage", "Produit ajouté avec succès.");
                } else {
                    session.setAttribute("successMessage", "Produit mis à jour avec succès.");
                }
                response.sendRedirect(request.getContextPath() + "/products");
                
            } catch (NumberFormatException e) {
                // Invalid number format
                session.setAttribute("errorMessage", "Format de nombre invalide pour le prix ou la quantité seuil.");
                if ("add".equals(action)) {
                    response.sendRedirect(request.getContextPath() + "/products/add");
                } else {
                    String productIdStr = request.getParameter("id");
                    response.sendRedirect(request.getContextPath() + "/products/edit/" + productIdStr);
                }
            }
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
}
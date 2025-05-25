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
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet for handling batch-related operations.
 * Mapped to /batches URL pattern.
 */
@WebServlet(name = "BatchServlet", urlPatterns = {"/batches", "/batches/*", "/api/batches"})
public class BatchServlet extends HttpServlet {
    private BatchDAO batchDAO;
    private ProductDAO productDAO;
    private MovementDAO movementDAO;
    private UserDAO userDAO;

    /**
     * Initialize the servlet.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        batchDAO = new BatchDAOImpl();
        productDAO = new ProductDAOImpl();
        movementDAO = new MovementDAOImpl();
        userDAO = new UserDAOImpl();
    }

    /**
     * Handle GET requests - display batches list, batch details, or batch form.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        
        // Handle API requests
        if ("/api/batches".equals(servletPath)) {
            handleApiBatches(request, response);
            return;
        }
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Display batches list
            List<Batch> batches = batchDAO.findAll();
            request.setAttribute("batches", batches);
            request.setAttribute("pageTitle", "Lots");
            request.getRequestDispatcher("/jsp/batches.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // Display batch details
            try {
                int batchId = Integer.parseInt(pathInfo.substring(6));
                Batch batch = batchDAO.findById(batchId);
                
                if (batch != null) {
                    // Load associated product
                    Product product = productDAO.findById(batch.getProductId());
                    batch.setProduct(product);
                    
                    // Load movements for this batch
                    List<Movement> movements = movementDAO.findByBatchId(batchId);
                    
                    request.setAttribute("batch", batch);
                    request.setAttribute("movements", movements);
                    request.setAttribute("pageTitle", "Détails du lot");
                    request.getRequestDispatcher("/jsp/batch-details.jsp").forward(request, response);
                } else {
                    // Batch not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Lot non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/batches");
                }
            } catch (NumberFormatException e) {
                // Invalid batch ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de lot invalide.");
                response.sendRedirect(request.getContextPath() + "/batches");
            }
        } else if (pathInfo.equals("/add")) {
            // Display add batch form
            List<Product> products = productDAO.findAll();
            
            // Check if a product ID is specified in the query string
            String productIdParam = request.getParameter("productId");
            if (productIdParam != null && !productIdParam.isEmpty()) {
                try {
                    int productId = Integer.parseInt(productIdParam);
                    Product selectedProduct = productDAO.findById(productId);
                    if (selectedProduct != null) {
                        request.setAttribute("selectedProduct", selectedProduct);
                    }
                } catch (NumberFormatException e) {
                    // Invalid product ID, ignore
                }
            }
            
            request.setAttribute("products", products);
            request.setAttribute("pageTitle", "Nouveau lot");
            request.getRequestDispatcher("/jsp/batch-form.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            // Display edit batch form
            try {
                int batchId = Integer.parseInt(pathInfo.substring(6));
                Batch batch = batchDAO.findById(batchId);
                
                if (batch != null) {
                    // Load associated product
                    Product product = productDAO.findById(batch.getProductId());
                    batch.setProduct(product);
                    
                    List<Product> products = productDAO.findAll();
                    
                    request.setAttribute("batch", batch);
                    request.setAttribute("products", products);
                    request.setAttribute("pageTitle", "Modifier le lot");
                    request.getRequestDispatcher("/jsp/batch-form.jsp").forward(request, response);
                } else {
                    // Batch not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Lot non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/batches");
                }
            } catch (NumberFormatException e) {
                // Invalid batch ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de lot invalide.");
                response.sendRedirect(request.getContextPath() + "/batches");
            }
        } else if (pathInfo.startsWith("/delete/")) {
            // Delete batch
            try {
                int batchId = Integer.parseInt(pathInfo.substring(8));
                boolean deleted = batchDAO.delete(batchId);
                
                HttpSession session = request.getSession();
                if (deleted) {
                    session.setAttribute("successMessage", "Lot supprimé avec succès.");
                } else {
                    session.setAttribute("errorMessage", "Lot non trouvé.");
                }
                response.sendRedirect(request.getContextPath() + "/batches");
            } catch (NumberFormatException e) {
                // Invalid batch ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de lot invalide.");
                response.sendRedirect(request.getContextPath() + "/batches");
            }
        } else if (pathInfo.equals("/expiring-soon")) {
            // Display batches expiring soon
            List<Batch> batches = batchDAO.findBatchesExpiringSoon();
            request.setAttribute("batches", batches);
            request.setAttribute("pageTitle", "Lots expirant bientôt");
            request.getRequestDispatcher("/jsp/batches.jsp").forward(request, response);
        } else if (pathInfo.equals("/expired")) {
            // Display expired batches
            List<Batch> batches = batchDAO.findExpiredBatches();
            request.setAttribute("batches", batches);
            request.setAttribute("pageTitle", "Lots expirés");
            request.getRequestDispatcher("/jsp/batches.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/by-product/")) {
            // Display batches for a specific product
            try {
                int productId = Integer.parseInt(pathInfo.substring(12));
                List<Batch> batches = batchDAO.findByProductId(productId);
                Product product = productDAO.findById(productId);
                
                if (product != null) {
                    request.setAttribute("batches", batches);
                    request.setAttribute("product", product);
                    request.setAttribute("pageTitle", "Lots du produit " + product.getName());
                    request.getRequestDispatcher("/jsp/batches.jsp").forward(request, response);
                } else {
                    // Product not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Produit non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/batches");
                }
            } catch (NumberFormatException e) {
                // Invalid product ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de produit invalide.");
                response.sendRedirect(request.getContextPath() + "/batches");
            }
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/batches");
        }
    }

    /**
     * Handle POST requests - process batch form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        if ("add".equals(action) || "edit".equals(action)) {
            // Process batch form
            try {
                int productId = Integer.parseInt(request.getParameter("productId"));
                String batchNumber = request.getParameter("batchNumber");
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                Date expiryDate = Date.valueOf(request.getParameter("expiryDate"));
                BigDecimal purchasePrice = new BigDecimal(request.getParameter("purchasePrice"));
                
                // Validate required fields
                if (batchNumber == null || batchNumber.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Le numéro de lot est obligatoire.");
                    if ("add".equals(action)) {
                        response.sendRedirect(request.getContextPath() + "/batches/add");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/batches/edit/" + request.getParameter("id"));
                    }
                    return;
                }
                
                // Validate expiry date
                if (expiryDate.before(Date.valueOf(LocalDate.now()))) {
                    session.setAttribute("errorMessage", "La date d'expiration doit être dans le futur.");
                    if ("add".equals(action)) {
                        response.sendRedirect(request.getContextPath() + "/batches/add");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/batches/edit/" + request.getParameter("id"));
                    }
                    return;
                }
                
                Batch batch;
                boolean isNewBatch = false;
                
                if ("add".equals(action)) {
                    // Create new batch
                    batch = new Batch();
                    isNewBatch = true;
                } else {
                    // Edit existing batch
                    int batchId = Integer.parseInt(request.getParameter("id"));
                    batch = batchDAO.findById(batchId);
                    
                    if (batch == null) {
                        session.setAttribute("errorMessage", "Lot non trouvé.");
                        response.sendRedirect(request.getContextPath() + "/batches");
                        return;
                    }
                }
                
                // Set batch properties
                batch.setProductId(productId);
                batch.setBatchNumber(batchNumber);
                batch.setExpiryDate(expiryDate);
                batch.setPurchasePrice(purchasePrice);
                
                // For new batches, create an entry movement
                if (isNewBatch) {
                    batch.setQuantity(0); // Will be updated by the movement
                    batch = batchDAO.save(batch);
                    
                    // Get user ID from session
                    Integer userId = (Integer) session.getAttribute("userId");
                    if (userId == null) {
                        session.setAttribute("errorMessage", "Session expirée. Veuillez vous reconnecter.");
                        response.sendRedirect(request.getContextPath() + "/login");
                        return;
                    }
                    
                    // Create entry movement
                    Movement movement = new Movement();
                    movement.setBatchId(batch.getId());
                    movement.setType(Movement.Type.entry);
                    movement.setQuantity(quantity);
                    movement.setReason("Création du lot");
                    movement.setUserId(userId);
                    
                    movementDAO.save(movement);
                } else {
                    // For existing batches, update quantity directly
                    int oldQuantity = batch.getQuantity();
                    batch.setQuantity(quantity);
                    batchDAO.save(batch);
                    
                    // If quantity changed, create an adjustment movement
                    if (quantity != oldQuantity) {
                        // Get user ID from session
                        Integer userId = (Integer) session.getAttribute("userId");
                        if (userId == null) {
                            session.setAttribute("errorMessage", "Session expirée. Veuillez vous reconnecter.");
                            response.sendRedirect(request.getContextPath() + "/login");
                            return;
                        }
                        
                        // Create adjustment movement
                        Movement movement = new Movement();
                        movement.setBatchId(batch.getId());
                        movement.setType(Movement.Type.adjustment);
                        movement.setQuantity(quantity - oldQuantity);
                        movement.setReason("Ajustement de quantité");
                        movement.setUserId(userId);
                        
                        movementDAO.save(movement);
                    }
                }
                
                session.setAttribute("successMessage", "Lot " + (isNewBatch ? "créé" : "modifié") + " avec succès.");
                response.sendRedirect(request.getContextPath() + "/batches/view/" + batch.getId());
                
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "Format de nombre invalide.");
                if ("add".equals(action)) {
                    response.sendRedirect(request.getContextPath() + "/batches/add");
                } else {
                    response.sendRedirect(request.getContextPath() + "/batches/edit/" + request.getParameter("id"));
                }
            } catch (IllegalArgumentException e) {
                session.setAttribute("errorMessage", "Format de date invalide.");
                if ("add".equals(action)) {
                    response.sendRedirect(request.getContextPath() + "/batches/add");
                } else {
                    response.sendRedirect(request.getContextPath() + "/batches/edit/" + request.getParameter("id"));
                }
            } catch (Exception e) {
                session.setAttribute("errorMessage", "Erreur lors de l'enregistrement du lot: " + e.getMessage());
                if ("add".equals(action)) {
                    response.sendRedirect(request.getContextPath() + "/batches/add");
                } else {
                    response.sendRedirect(request.getContextPath() + "/batches/edit/" + request.getParameter("id"));
                }
            }
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/batches");
        }
    }
    
    /**
     * Handle API requests for batches.
     */
    private void handleApiBatches(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        String productIdParam = request.getParameter("productId");
        if (productIdParam != null && !productIdParam.isEmpty()) {
            try {
                int productId = Integer.parseInt(productIdParam);
                List<Batch> batches = batchDAO.findByProductId(productId);
                
                // Convert batches to JSON
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < batches.size(); i++) {
                    Batch batch = batches.get(i);
                    json.append("{");
                    json.append("\"id\":").append(batch.getId()).append(",");
                    json.append("\"batchNumber\":\"").append(batch.getBatchNumber()).append("\",");
                    json.append("\"quantity\":").append(batch.getQuantity()).append(",");
                    json.append("\"expiryDate\":\"").append(batch.getExpiryDate()).append("\"");
                    json.append("}");
                    
                    if (i < batches.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");
                
                response.getWriter().write(json.toString());
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid product ID\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Product ID is required\"}");
        }
    }
}
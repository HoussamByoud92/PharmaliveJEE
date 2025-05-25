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
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet for handling movement-related operations.
 * Mapped to /movements URL pattern.
 */
@WebServlet(name = "MovementServlet", urlPatterns = {"/movements", "/movements/*"})
public class MovementServlet extends HttpServlet {
    private MovementDAO movementDAO;
    private BatchDAO batchDAO;
    private UserDAO userDAO;
    private ProductDAO productDAO;

    /**
     * Initialize the servlet.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        movementDAO = new MovementDAOImpl();
        batchDAO = new BatchDAOImpl();
        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();
    }

    /**
     * Handle GET requests - display movements list, movement details, or movement form.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Display movements list
            List<Movement> movements = movementDAO.findAll();
            request.setAttribute("movements", movements);
            request.setAttribute("pageTitle", "Mouvements");
            request.getRequestDispatcher("/jsp/movements.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // Display movement details
            try {
                int movementId = Integer.parseInt(pathInfo.substring(6));
                Movement movement = movementDAO.findById(movementId);
                
                if (movement != null) {
                    // Load associated batch and user
                    Batch batch = batchDAO.findById(movement.getBatchId());
                    User user = userDAO.findById(movement.getUserId());
                    
                    movement.setBatch(batch);
                    movement.setUser(user);
                    
                    request.setAttribute("movement", movement);
                    request.setAttribute("pageTitle", "Détails du mouvement");
                    request.getRequestDispatcher("/jsp/movement-details.jsp").forward(request, response);
                } else {
                    // Movement not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Mouvement non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/movements");
                }
            } catch (NumberFormatException e) {
                // Invalid movement ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de mouvement invalide.");
                response.sendRedirect(request.getContextPath() + "/movements");
            }
        } else if (pathInfo.equals("/add")) {
            // Display add movement form
            List<Batch> batches = batchDAO.findAll();
            
            request.setAttribute("batches", batches);
            request.setAttribute("movementTypes", Movement.Type.values());
            request.setAttribute("pageTitle", "Nouveau mouvement");
            request.getRequestDispatcher("/jsp/movement-form.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/add/")) {
            // Display add movement form with pre-selected batch
            try {
                int batchId = Integer.parseInt(pathInfo.substring(5));
                Batch batch = batchDAO.findById(batchId);
                
                if (batch != null) {
                    List<Batch> batches = batchDAO.findAll();
                    
                    request.setAttribute("batches", batches);
                    request.setAttribute("selectedBatch", batch);
                    request.setAttribute("movementTypes", Movement.Type.values());
                    request.setAttribute("pageTitle", "Nouveau mouvement");
                    request.getRequestDispatcher("/jsp/movement-form.jsp").forward(request, response);
                } else {
                    // Batch not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Lot non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/movements/add");
                }
            } catch (NumberFormatException e) {
                // Invalid batch ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de lot invalide.");
                response.sendRedirect(request.getContextPath() + "/movements/add");
            }
        } else if (pathInfo.equals("/by-type/entry")) {
            // Display entry movements
            List<Movement> movements = movementDAO.findByType(Movement.Type.entry);
            request.setAttribute("movements", movements);
            request.setAttribute("pageTitle", "Mouvements d'entrée");
            request.getRequestDispatcher("/jsp/movements.jsp").forward(request, response);
        } else if (pathInfo.equals("/by-type/exit")) {
            // Display exit movements
            List<Movement> movements = movementDAO.findByType(Movement.Type.exit);
            request.setAttribute("movements", movements);
            request.setAttribute("pageTitle", "Mouvements de sortie");
            request.getRequestDispatcher("/jsp/movements.jsp").forward(request, response);
        } else if (pathInfo.equals("/by-type/adjustment")) {
            // Display adjustment movements
            List<Movement> movements = movementDAO.findByType(Movement.Type.adjustment);
            request.setAttribute("movements", movements);
            request.setAttribute("pageTitle", "Mouvements d'ajustement");
            request.getRequestDispatcher("/jsp/movements.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/by-batch/")) {
            // Display movements for a specific batch
            try {
                int batchId = Integer.parseInt(pathInfo.substring(10));
                List<Movement> movements = movementDAO.findByBatchId(batchId);
                Batch batch = batchDAO.findById(batchId);
                
                if (batch != null) {
                    request.setAttribute("movements", movements);
                    request.setAttribute("batch", batch);
                    request.setAttribute("pageTitle", "Mouvements du lot " + batch.getBatchNumber());
                    request.getRequestDispatcher("/jsp/movements.jsp").forward(request, response);
                } else {
                    // Batch not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Lot non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/movements");
                }
            } catch (NumberFormatException e) {
                // Invalid batch ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de lot invalide.");
                response.sendRedirect(request.getContextPath() + "/movements");
            }
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/movements");
        }
    }

    /**
     * Handle POST requests - process movement form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        if ("add".equals(action)) {
            // Process new movement
            try {
                int batchId = Integer.parseInt(request.getParameter("batchId"));
                String typeStr = request.getParameter("type");
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                String reason = request.getParameter("reason");
                
                // Get user ID from session
                Integer userId = (Integer) session.getAttribute("userId");
                if (userId == null) {
                    session.setAttribute("errorMessage", "Session expirée. Veuillez vous reconnecter.");
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                
                // Validate batch
                Batch batch = batchDAO.findById(batchId);
                if (batch == null) {
                    session.setAttribute("errorMessage", "Lot non trouvé.");
                    response.sendRedirect(request.getContextPath() + "/movements/add");
                    return;
                }
                
                // Validate type
                Movement.Type type;
                try {
                    type = Movement.Type.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    session.setAttribute("errorMessage", "Type de mouvement invalide.");
                    response.sendRedirect(request.getContextPath() + "/movements/add");
                    return;
                }
                
                // Validate quantity
                if (quantity <= 0) {
                    session.setAttribute("errorMessage", "La quantité doit être supérieure à zéro.");
                    response.sendRedirect(request.getContextPath() + "/movements/add");
                    return;
                }
                
                // For exit movements, check if there's enough stock
                if (type == Movement.Type.exit && batch.getQuantity() < quantity) {
                    session.setAttribute("errorMessage", "Quantité insuffisante dans le lot.");
                    response.sendRedirect(request.getContextPath() + "/movements/add");
                    return;
                }
                
                // Create and save movement
                Movement movement = new Movement();
                movement.setBatchId(batchId);
                movement.setType(type);
                movement.setQuantity(quantity);
                movement.setReason(reason);
                movement.setUserId(userId);
                
                movement = movementDAO.save(movement);
                
                session.setAttribute("successMessage", "Mouvement enregistré avec succès.");
                response.sendRedirect(request.getContextPath() + "/movements/view/" + movement.getId());
                
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "Format de nombre invalide.");
                response.sendRedirect(request.getContextPath() + "/movements/add");
            } catch (Exception e) {
                session.setAttribute("errorMessage", "Erreur lors de l'enregistrement du mouvement: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/movements/add");
            }
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/movements");
        }
    }
}
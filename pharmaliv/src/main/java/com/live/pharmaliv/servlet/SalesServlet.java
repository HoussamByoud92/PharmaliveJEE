package com.live.pharmaliv.servlet;

import com.live.pharmaliv.dao.*;
import com.live.pharmaliv.model.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet for handling sales management.
 */
@WebServlet(name = "salesServlet", urlPatterns = {"/sales", "/sales/*"})
public class SalesServlet extends HttpServlet {
    private SaleDAO saleDAO;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private BatchDAO batchDAO;
    private UserDAO userDAO;
    private MovementDAO movementDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        saleDAO = new SaleDAOImpl();
        customerDAO = new CustomerDAOImpl();
        productDAO = new ProductDAOImpl();
        batchDAO = new BatchDAOImpl();
        userDAO = new UserDAOImpl();
        movementDAO = new MovementDAOImpl();
    }

    /**
     * Handle GET requests - display sales list, sale details, or export PDF.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Display sales list
            List<Sale> sales = saleDAO.findAll();
            request.setAttribute("sales", sales);
            request.setAttribute("pageTitle", "Ventes");
            request.getRequestDispatcher("/jsp/sales.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // Display sale details
            try {
                int saleId = Integer.parseInt(pathInfo.substring(6));
                Sale sale = saleDAO.findById(saleId);
                
                if (sale != null) {
                    request.setAttribute("sale", sale);
                    request.setAttribute("pageTitle", "Détails de la vente");
                    request.getRequestDispatcher("/jsp/sale-details.jsp").forward(request, response);
                } else {
                    // Sale not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Vente non trouvée.");
                    response.sendRedirect(request.getContextPath() + "/sales");
                }
            } catch (NumberFormatException e) {
                // Invalid sale ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de vente invalide.");
                response.sendRedirect(request.getContextPath() + "/sales");
            }
        } else if (pathInfo.equals("/add")) {
            // Display add sale form
            List<Customer> customers = customerDAO.findAll();
            List<Product> products = productDAO.findAll();
            
            request.setAttribute("customers", customers);
            request.setAttribute("products", products);
            request.setAttribute("pageTitle", "Nouvelle vente");
            request.getRequestDispatcher("/jsp/sale-form.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/export-pdf/")) {
            // Export sale as PDF
            try {
                int saleId = Integer.parseInt(pathInfo.substring(12));
                Sale sale = saleDAO.findById(saleId);
                
                if (sale != null) {
                    // Generate and send PDF
                    generateSalePdf(sale, response);
                } else {
                    // Sale not found
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "Vente non trouvée.");
                    response.sendRedirect(request.getContextPath() + "/sales");
                }
            } catch (NumberFormatException e) {
                // Invalid sale ID
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "ID de vente invalide.");
                response.sendRedirect(request.getContextPath() + "/sales");
            } catch (DocumentException e) {
                // PDF generation error
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Erreur lors de la génération du PDF: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/sales");
            }
        } else {
            // Invalid path
            response.sendRedirect(request.getContextPath() + "/sales");
        }
    }

    /**
     * Handle POST requests - process sale form submission.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            // Process new sale
            HttpSession session = request.getSession();
            
            try {
                // Get customer ID (can be null for anonymous customer)
                String customerIdStr = request.getParameter("customerId");
                Integer customerId = null;
                if (customerIdStr != null && !customerIdStr.isEmpty() && !customerIdStr.equals("0")) {
                    customerId = Integer.parseInt(customerIdStr);
                }
                
                // Get user ID from session
                Integer userId = (Integer) session.getAttribute("userId");
                if (userId == null) {
                    session.setAttribute("errorMessage", "Session expirée. Veuillez vous reconnecter.");
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                
                // Create new sale
                Sale sale = new Sale();
                sale.setCustomerId(customerId);
                sale.setUserId(userId);
                sale.setTotalAmount(BigDecimal.ZERO); // Will be calculated based on items
                
                // Get sale items
                String[] batchIds = request.getParameterValues("batchId");
                String[] quantities = request.getParameterValues("quantity");
                
                if (batchIds == null || quantities == null || batchIds.length == 0 || batchIds.length != quantities.length) {
                    session.setAttribute("errorMessage", "Veuillez ajouter au moins un produit à la vente.");
                    response.sendRedirect(request.getContextPath() + "/sales/add");
                    return;
                }
                
                // Create sale items
                List<SaleItem> saleItems = new ArrayList<>();
                for (int i = 0; i < batchIds.length; i++) {
                    int batchId = Integer.parseInt(batchIds[i]);
                    int quantity = Integer.parseInt(quantities[i]);
                    
                    if (quantity <= 0) {
                        continue; // Skip items with zero or negative quantity
                    }
                    
                    Batch batch = batchDAO.findById(batchId);
                    if (batch == null) {
                        session.setAttribute("errorMessage", "Lot non trouvé.");
                        response.sendRedirect(request.getContextPath() + "/sales/add");
                        return;
                    }
                    
                    if (batch.getQuantity() < quantity) {
                        session.setAttribute("errorMessage", "Quantité insuffisante pour le lot " + batch.getBatchNumber() + ".");
                        response.sendRedirect(request.getContextPath() + "/sales/add");
                        return;
                    }
                    
                    SaleItem saleItem = new SaleItem();
                    saleItem.setBatchId(batchId);
                    saleItem.setQuantity(quantity);
                    saleItem.setUnitPrice(batch.getProduct().getPrice());
                    saleItem.setBatch(batch);
                    
                    saleItems.add(saleItem);
                }
                
                if (saleItems.isEmpty()) {
                    session.setAttribute("errorMessage", "Veuillez ajouter au moins un produit à la vente.");
                    response.sendRedirect(request.getContextPath() + "/sales/add");
                    return;
                }
                
                // Save sale and items
                sale.setSaleItems(saleItems);
                sale.recalculateTotalAmount();
                
                // Start transaction (handled by SaleDAO)
                sale = saleDAO.save(sale);
                
                // Create movement records for each sale item (exit)
                for (SaleItem item : saleItems) {
                    Movement movement = new Movement();
                    movement.setBatchId(item.getBatchId());
                    movement.setType(Movement.Type.exit);
                    movement.setQuantity(item.getQuantity());
                    movement.setReason("Vente #" + sale.getId());
                    movement.setUserId(userId);
                    
                    movementDAO.save(movement);
                }
                
                session.setAttribute("successMessage", "Vente enregistrée avec succès.");
                response.sendRedirect(request.getContextPath() + "/sales/view/" + sale.getId());
                
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "Format de nombre invalide.");
                response.sendRedirect(request.getContextPath() + "/sales/add");
            } catch (Exception e) {
                session.setAttribute("errorMessage", "Erreur lors de l'enregistrement de la vente: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/sales/add");
            }
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/sales");
        }
    }
    
    /**
     * Generate a PDF invoice for a sale.
     * @param sale The sale
     * @param response The HTTP response
     * @throws IOException If an I/O error occurs
     * @throws DocumentException If a document error occurs
     */
    private void generateSalePdf(Sale sale, HttpServletResponse response) throws IOException, DocumentException {
        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=facture_" + sale.getId() + ".pdf");
        
        // Create document
        Document document = new Document(PageSize.A4);
        OutputStream out = response.getOutputStream();
        PdfWriter.getInstance(document, out);
        
        document.open();
        
        // Add logo
        try {
            Image logo = Image.getInstance(getServletContext().getRealPath("/img/logo.png"));
            logo.scaleToFit(200, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Logo not found, add text instead
            Paragraph logoText = new Paragraph("PHARMALIVE", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY));
            logoText.setAlignment(Element.ALIGN_CENTER);
            document.add(logoText);
        }
        
        // Add title
        Paragraph title = new Paragraph("FACTURE N°" + sale.getId(), new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Add date
        Paragraph date = new Paragraph("Date: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(sale.getCreatedAt()), 
                new Font(Font.FontFamily.HELVETICA, 12));
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);
        
        // Add customer info
        PdfPTable customerTable = new PdfPTable(2);
        customerTable.setWidthPercentage(100);
        customerTable.setSpacingBefore(10);
        customerTable.setSpacingAfter(20);
        
        PdfPCell cell = new PdfPCell(new Paragraph("Informations client", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        cell.setColspan(2);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        customerTable.addCell(cell);
        
        if (sale.getCustomer() != null) {
            customerTable.addCell("Nom:");
            customerTable.addCell(sale.getCustomer().getName());
            
            if (sale.getCustomer().getPhone() != null && !sale.getCustomer().getPhone().isEmpty()) {
                customerTable.addCell("Téléphone:");
                customerTable.addCell(sale.getCustomer().getPhone());
            }
            
            if (sale.getCustomer().getEmail() != null && !sale.getCustomer().getEmail().isEmpty()) {
                customerTable.addCell("Email:");
                customerTable.addCell(sale.getCustomer().getEmail());
            }
            
            if (sale.getCustomer().getAddress() != null && !sale.getCustomer().getAddress().isEmpty()) {
                customerTable.addCell("Adresse:");
                customerTable.addCell(sale.getCustomer().getAddress());
            }
        } else {
            cell = new PdfPCell(new Paragraph("Client anonyme"));
            cell.setColspan(2);
            customerTable.addCell(cell);
        }
        
        document.add(customerTable);
        
        // Add items table
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setSpacingBefore(10);
        itemsTable.setSpacingAfter(20);
        
        // Set column widths
        float[] columnWidths = {3, 1, 2, 2, 2};
        itemsTable.setWidths(columnWidths);
        
        // Add header row
        cell = new PdfPCell(new Paragraph("Produit", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        itemsTable.addCell(cell);
        
        cell = new PdfPCell(new Paragraph("Qté", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        itemsTable.addCell(cell);
        
        cell = new PdfPCell(new Paragraph("Lot", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        itemsTable.addCell(cell);
        
        cell = new PdfPCell(new Paragraph("Prix unitaire", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        itemsTable.addCell(cell);
        
        cell = new PdfPCell(new Paragraph("Total", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        itemsTable.addCell(cell);
        
        // Add item rows
        for (SaleItem item : sale.getSaleItems()) {
            // Load batch and product if not already loaded
            if (item.getBatch() == null) {
                item.setBatch(batchDAO.findById(item.getBatchId()));
            }
            
            if (item.getBatch().getProduct() == null) {
                item.getBatch().setProduct(productDAO.findById(item.getBatch().getProductId()));
            }
            
            itemsTable.addCell(item.getBatch().getProduct().getName());
            itemsTable.addCell(String.valueOf(item.getQuantity()));
            itemsTable.addCell(item.getBatch().getBatchNumber());
            itemsTable.addCell(String.format("%.2f €", item.getUnitPrice()));
            itemsTable.addCell(String.format("%.2f €", item.getTotalPrice()));
        }
        
        // Add total row
        cell = new PdfPCell(new Paragraph("Total", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setColspan(4);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        itemsTable.addCell(cell);
        
        cell = new PdfPCell(new Paragraph(String.format("%.2f €", sale.getTotalAmount()), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setPadding(5);
        itemsTable.addCell(cell);
        
        document.add(itemsTable);
        
        // Add footer
        Paragraph footer = new Paragraph("Merci pour votre achat chez PHARMALIVE!", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
        
        // Close document
        document.close();
    }
}
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Détails du client</h1>
    <div>
        <a href="${pageContext.request.contextPath}/customers/edit/${customer.id}" class="btn btn-warning me-2">
            <i class="fas fa-edit"></i> Modifier
        </a>
        <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour à la liste
        </a>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card mb-4 customer-info-card">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations du client</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">ID :</th>
                        <td>${customer.id}</td>
                    </tr>
                    <tr>
                        <th>Nom :</th>
                        <td>${customer.name}</td>
                    </tr>
                    <tr>
                        <th>Téléphone :</th>
                        <td>${empty customer.phone ? '-' : customer.phone}</td>
                    </tr>
                    <tr>
                        <th>Email :</th>
                        <td>${empty customer.email ? '-' : customer.email}</td>
                    </tr>
                    <tr>
                        <th>Adresse :</th>
                        <td>${empty customer.address ? '-' : customer.address}</td>
                    </tr>
                    <tr>
                        <th>Date de création :</th>
                        <td><fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                    <tr>
                        <th>Dernière mise à jour :</th>
                        <td><fmt:formatDate value="${customer.updatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
    
    <div class="col-md-6">
        <div class="card sales-history-card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">Historique des achats</h5>
                <c:if test="${not empty customerSales}">
                    <span class="badge bg-primary">${customerSales.size()} vente(s)</span>
                </c:if>
            </div>
            <div class="card-body">
                <c:if test="${empty customerSales}">
                    <p class="text-muted">Aucun achat enregistré pour ce client.</p>
                </c:if>
                <c:if test="${not empty customerSales}">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Date</th>
                                    <th>Montant</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${customerSales}" var="sale">
                                    <tr class="sale-row">
                                        <td>${sale.id}</td>
                                        <td><fmt:formatDate value="${sale.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        <td><fmt:formatNumber value="${sale.totalAmount}" type="currency" currencySymbol="€" /></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/sales/view/${sale.id}" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i> Voir
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<script>
    // Animation for customer info card
    document.addEventListener('DOMContentLoaded', function() {
        const customerInfoCard = document.querySelector('.customer-info-card');
        const salesHistoryCard = document.querySelector('.sales-history-card');
        
        // Initial state
        customerInfoCard.style.opacity = '0';
        customerInfoCard.style.transform = 'translateX(-20px)';
        customerInfoCard.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        
        salesHistoryCard.style.opacity = '0';
        salesHistoryCard.style.transform = 'translateX(20px)';
        salesHistoryCard.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        
        // Animate in
        setTimeout(() => {
            customerInfoCard.style.opacity = '1';
            customerInfoCard.style.transform = 'translateX(0)';
        }, 100);
        
        setTimeout(() => {
            salesHistoryCard.style.opacity = '1';
            salesHistoryCard.style.transform = 'translateX(0)';
        }, 300);
        
        // Animate sale rows
        const saleRows = document.querySelectorAll('.sale-row');
        saleRows.forEach((row, index) => {
            row.style.opacity = '0';
            row.style.transform = 'translateY(10px)';
            row.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            
            setTimeout(() => {
                row.style.opacity = '1';
                row.style.transform = 'translateY(0)';
            }, 500 + (index * 100)); // Staggered animation
        });
    });
</script>

<jsp:include page="includes/footer.jsp" />
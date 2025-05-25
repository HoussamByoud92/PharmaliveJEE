<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Détails du lot</h1>
    <div>
        <a href="${pageContext.request.contextPath}/batches/edit/${batch.id}" class="btn btn-warning me-2">
            <i class="fas fa-edit"></i> Modifier
        </a>
        <a href="${pageContext.request.contextPath}/movements/add?batchId=${batch.id}" class="btn btn-success me-2">
            <i class="fas fa-exchange-alt"></i> Ajouter un mouvement
        </a>
        <a href="${pageContext.request.contextPath}/batches" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour à la liste
        </a>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations du lot</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">Numéro de lot :</th>
                        <td>${batch.batchNumber}</td>
                    </tr>
                    <tr>
                        <th>Produit :</th>
                        <td>
                            <a href="${pageContext.request.contextPath}/products/view/${batch.product.id}">
                                ${batch.product.name} (${batch.product.code})
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <th>Quantité :</th>
                        <td>${batch.quantity}</td>
                    </tr>
                    <tr>
                        <th>Date d'expiration :</th>
                        <td>
                            <fmt:formatDate value="${batch.expiryDate}" pattern="dd/MM/yyyy" />
                            <c:if test="${batch.isExpired()}">
                                <span class="badge bg-danger ms-1">Expiré</span>
                            </c:if>
                            <c:if test="${batch.isExpiringSoon()}">
                                <span class="badge bg-warning ms-1">Bientôt expiré</span>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <th>Prix d'achat :</th>
                        <td><fmt:formatNumber value="${batch.purchasePrice}" type="currency" currencySymbol="€" /></td>
                    </tr>
                    <tr>
                        <th>Date de création :</th>
                        <td><fmt:formatDate value="${batch.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                    <tr>
                        <th>Dernière mise à jour :</th>
                        <td><fmt:formatDate value="${batch.updatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
    
    <div class="col-md-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">Mouvements</h5>
                <a href="${pageContext.request.contextPath}/movements/by-batch/${batch.id}" class="btn btn-sm btn-primary">
                    <i class="fas fa-list"></i> Voir tous les mouvements
                </a>
            </div>
            <div class="card-body">
                <c:if test="${empty movements}">
                    <p class="text-muted">Aucun mouvement enregistré pour ce lot.</p>
                </c:if>
                <c:if test="${not empty movements}">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Type</th>
                                    <th>Quantité</th>
                                    <th>Utilisateur</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${movements}" var="movement" end="4">
                                    <tr>
                                        <td><fmt:formatDate value="${movement.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${movement.type eq 'entry'}">
                                                    <span class="badge bg-success">Entrée</span>
                                                </c:when>
                                                <c:when test="${movement.type eq 'exit'}">
                                                    <span class="badge bg-danger">Sortie</span>
                                                </c:when>
                                                <c:when test="${movement.type eq 'adjustment'}">
                                                    <span class="badge bg-warning">Ajustement</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td>${movement.quantity}</td>
                                        <td>${movement.user.fullName}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/movements/view/${movement.id}" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${movements.size() > 5}">
                        <div class="text-center mt-3">
                            <a href="${pageContext.request.contextPath}/movements/by-batch/${batch.id}" class="btn btn-sm btn-outline-primary">
                                Voir tous les mouvements
                            </a>
                        </div>
                    </c:if>
                </c:if>
            </div>
        </div>
    </div>
</div>

<div class="row mt-4">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Historique des mouvements</h5>
            </div>
            <div class="card-body">
                <canvas id="movementChart" height="100"></canvas>
            </div>
        </div>
    </div>
</div>

<script>
    // Movement history chart
    document.addEventListener('DOMContentLoaded', function() {
        const ctx = document.getElementById('movementChart').getContext('2d');
        
        // Extract movement data
        const movements = [
            <c:forEach items="${movements}" var="movement" varStatus="status">
                {
                    date: new Date('<fmt:formatDate value="${movement.createdAt}" pattern="yyyy-MM-dd" />'),
                    type: '${movement.type}',
                    quantity: ${movement.quantity}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        
        // Sort movements by date
        movements.sort((a, b) => a.date - b.date);
        
        // Calculate running total
        let runningTotal = 0;
        const chartData = movements.map(m => {
            if (m.type === 'entry') {
                runningTotal += m.quantity;
            } else if (m.type === 'exit') {
                runningTotal -= m.quantity;
            } else if (m.type === 'adjustment') {
                runningTotal += m.quantity; // Adjustment can be positive or negative
            }
            return {
                date: m.date,
                total: runningTotal
            };
        });
        
        // Create chart
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: chartData.map(d => d.date.toLocaleDateString()),
                datasets: [{
                    label: 'Quantité en stock',
                    data: chartData.map(d => d.total),
                    backgroundColor: 'rgba(0, 123, 255, 0.2)',
                    borderColor: 'rgba(0, 123, 255, 1)',
                    borderWidth: 2,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Quantité'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Date'
                        }
                    }
                }
            }
        });
    });
</script>

<jsp:include page="includes/footer.jsp" />
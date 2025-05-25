<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Gestion des lots</h1>
    <div>
        <a href="${pageContext.request.contextPath}/batches/add" class="btn btn-primary">
            <i class="fas fa-plus"></i> Ajouter un lot
        </a>
    </div>
</div>

<div class="row mb-4">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <ul class="nav nav-tabs card-header-tabs">
                    <li class="nav-item">
                        <a class="nav-link ${empty param.filter ? 'active' : ''}" href="${pageContext.request.contextPath}/batches">
                            Tous les lots
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'expiring-soon' ? 'active' : ''}" href="${pageContext.request.contextPath}/batches/expiring-soon">
                            Expirant bientôt
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'expired' ? 'active' : ''}" href="${pageContext.request.contextPath}/batches/expired">
                            Expirés
                        </a>
                    </li>
                    <c:if test="${not empty product}">
                        <li class="nav-item ms-auto">
                            <span class="nav-link disabled">
                                Produit: ${product.name}
                            </span>
                        </li>
                    </c:if>
                </ul>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>Numéro de lot</th>
                                <th>Produit</th>
                                <th>Quantité</th>
                                <th>Date d'expiration</th>
                                <th>Prix d'achat</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty batches}">
                                <tr>
                                    <td colspan="6" class="text-center">Aucun lot trouvé.</td>
                                </tr>
                            </c:if>
                            <c:forEach items="${batches}" var="batch">
                                <tr>
                                    <td>${batch.batchNumber}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/products/view/${batch.product.id}">
                                            ${batch.product.name}
                                        </a>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${batch.quantity <= 0}">
                                                <span class="badge bg-danger">Épuisé</span>
                                            </c:when>
                                            <c:when test="${batch.quantity < batch.product.thresholdQuantity}">
                                                <span class="badge bg-warning">${batch.quantity}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-success">${batch.quantity}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${batch.expiryDate}" pattern="dd/MM/yyyy" />
                                        <c:if test="${batch.isExpired()}">
                                            <span class="badge bg-danger ms-1">Expiré</span>
                                        </c:if>
                                        <c:if test="${batch.isExpiringSoon()}">
                                            <span class="badge bg-warning ms-1">Bientôt expiré</span>
                                        </c:if>
                                    </td>
                                    <td><fmt:formatNumber value="${batch.purchasePrice}" type="currency" currencySymbol="€" /></td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <a href="${pageContext.request.contextPath}/batches/view/${batch.id}" class="btn btn-info" title="Voir">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/batches/edit/${batch.id}" class="btn btn-warning" title="Modifier">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/movements/add?batchId=${batch.id}" class="btn btn-success" title="Ajouter un mouvement">
                                                <i class="fas fa-exchange-alt"></i>
                                            </a>
                                            <button type="button" class="btn btn-danger" title="Supprimer" 
                                                    onclick="confirmDelete(${batch.id}, '${batch.batchNumber}')">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">Confirmer la suppression</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir supprimer le lot <span id="batchNumber"></span> ?
                <p class="text-danger mt-2">
                    <i class="fas fa-exclamation-triangle"></i> Attention : Cette action supprimera également tous les mouvements associés à ce lot.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <a href="#" id="deleteLink" class="btn btn-danger">Supprimer</a>
            </div>
        </div>
    </div>
</div>

<script>
    function confirmDelete(batchId, batchNumber) {
        document.getElementById('batchNumber').textContent = batchNumber;
        document.getElementById('deleteLink').href = '${pageContext.request.contextPath}/batches/delete/' + batchId;
        var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        deleteModal.show();
    }
</script>

<jsp:include page="includes/footer.jsp" />
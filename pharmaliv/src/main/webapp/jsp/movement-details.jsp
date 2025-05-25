<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Détails du mouvement</h1>
    <a href="${pageContext.request.contextPath}/movements" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Retour à la liste
    </a>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations du mouvement</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">ID :</th>
                        <td>${movement.id}</td>
                    </tr>
                    <tr>
                        <th>Type :</th>
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
                    </tr>
                    <tr>
                        <th>Quantité :</th>
                        <td>${movement.quantity}</td>
                    </tr>
                    <tr>
                        <th>Raison :</th>
                        <td>${movement.reason}</td>
                    </tr>
                    <tr>
                        <th>Date :</th>
                        <td><fmt:formatDate value="${movement.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                    <tr>
                        <th>Utilisateur :</th>
                        <td>
                            <c:if test="${not empty movement.user}">
                                ${movement.user.fullName}
                            </c:if>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations du lot</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty movement.batch}">
                    <table class="table table-borderless">
                        <tr>
                            <th style="width: 30%">Numéro :</th>
                            <td>${movement.batch.batchNumber}</td>
                        </tr>
                        <tr>
                            <th>Produit :</th>
                            <td>
                                <c:if test="${not empty movement.batch.product}">
                                    ${movement.batch.product.name}
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <th>Quantité actuelle :</th>
                            <td>${movement.batch.quantity}</td>
                        </tr>
                        <tr>
                            <th>Date d'expiration :</th>
                            <td>
                                <fmt:formatDate value="${movement.batch.expiryDate}" pattern="dd/MM/yyyy" />
                                <c:if test="${movement.batch.isExpired()}">
                                    <span class="badge bg-danger ms-1">Expiré</span>
                                </c:if>
                                <c:if test="${movement.batch.isExpiringSoon()}">
                                    <span class="badge bg-warning ms-1">Bientôt expiré</span>
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <th>Prix d'achat :</th>
                            <td><fmt:formatNumber value="${movement.batch.purchasePrice}" type="currency" currencySymbol="€" /></td>
                        </tr>
                    </table>
                    <div class="mt-3">
                        <a href="${pageContext.request.contextPath}/batches/view/${movement.batch.id}" class="btn btn-primary">
                            <i class="fas fa-eye"></i> Voir le lot
                        </a>
                        <c:if test="${not empty movement.batch.product}">
                            <a href="${pageContext.request.contextPath}/products/view/${movement.batch.product.id}" class="btn btn-info ms-2">
                                <i class="fas fa-eye"></i> Voir le produit
                            </a>
                        </c:if>
                    </div>
                </c:if>
                <c:if test="${empty movement.batch}">
                    <p class="text-muted">Lot non disponible.</p>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
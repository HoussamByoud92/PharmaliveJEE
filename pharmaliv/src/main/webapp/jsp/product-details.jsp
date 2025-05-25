<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Détails du produit</h1>
    <div>
        <a href="${pageContext.request.contextPath}/products/edit/${product.id}" class="btn btn-warning me-2">
            <i class="fas fa-edit"></i> Modifier
        </a>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour à la liste
        </a>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations générales</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">Code :</th>
                        <td>${product.code}</td>
                    </tr>
                    <tr>
                        <th>Nom :</th>
                        <td>${product.name}</td>
                    </tr>
                    <tr>
                        <th>DCI :</th>
                        <td>${product.dci}</td>
                    </tr>
                    <tr>
                        <th>Prix :</th>
                        <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="€" /></td>
                    </tr>
                    <tr>
                        <th>Seuil d'alerte :</th>
                        <td>${product.thresholdQuantity}</td>
                    </tr>
                    <tr>
                        <th>Stock total :</th>
                        <td>
                            <c:choose>
                                <c:when test="${totalStock < product.thresholdQuantity}">
                                    <span class="badge bg-danger">${totalStock}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success">${totalStock}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Description</h5>
            </div>
            <div class="card-body">
                <p>${empty product.description ? 'Aucune description disponible.' : product.description}</p>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">Lots</h5>
                <a href="${pageContext.request.contextPath}/batches/add?productId=${product.id}" class="btn btn-sm btn-primary">
                    <i class="fas fa-plus"></i> Ajouter un lot
                </a>
            </div>
            <div class="card-body">
                <c:if test="${empty batches}">
                    <p class="text-muted">Aucun lot disponible pour ce produit.</p>
                </c:if>
                <c:if test="${not empty batches}">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Numéro</th>
                                    <th>Quantité</th>
                                    <th>Date d'expiration</th>
                                    <th>Prix d'achat</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${batches}" var="batch">
                                    <tr>
                                        <td>${batch.batchNumber}</td>
                                        <td>${batch.quantity}</td>
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
                                            </div>
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

<jsp:include page="includes/footer.jsp" />

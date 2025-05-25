<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Détails de l'alerte</h1>
    <div>
        <c:if test="${not alert.resolved}">
            <form action="${pageContext.request.contextPath}/alerts" method="post" class="d-inline">
                <input type="hidden" name="action" value="resolve">
                <input type="hidden" name="id" value="${alert.id}">
                <button type="submit" class="btn btn-success me-2">
                    <i class="fas fa-check"></i> Marquer comme résolu
                </button>
            </form>
        </c:if>
        <a href="${pageContext.request.contextPath}/alerts" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour à la liste
        </a>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations de l'alerte</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">Type :</th>
                        <td>
                            <c:if test="${alert.type eq 'stock'}">
                                <span class="badge bg-danger">Stock</span>
                            </c:if>
                            <c:if test="${alert.type eq 'expiry'}">
                                <span class="badge bg-warning">Péremption</span>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <th>Message :</th>
                        <td>${alert.message}</td>
                    </tr>
                    <tr>
                        <th>Date de création :</th>
                        <td><fmt:formatDate value="${alert.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                    <tr>
                        <th>Statut :</th>
                        <td>
                            <c:if test="${alert.resolved}">
                                <span class="text-success"><i class="fas fa-check-circle"></i> Résolu</span>
                                <br><small>Le <fmt:formatDate value="${alert.resolvedAt}" pattern="dd/MM/yyyy HH:mm" /></small>
                            </c:if>
                            <c:if test="${not alert.resolved}">
                                <span class="text-danger"><i class="fas fa-exclamation-circle"></i> Non résolu</span>
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
                <h5 class="card-title mb-0">Informations du produit</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">Code :</th>
                        <td>${alert.product.code}</td>
                    </tr>
                    <tr>
                        <th>Nom :</th>
                        <td>${alert.product.name}</td>
                    </tr>
                    <tr>
                        <th>DCI :</th>
                        <td>${alert.product.dci}</td>
                    </tr>
                    <tr>
                        <th>Prix :</th>
                        <td><fmt:formatNumber value="${alert.product.price}" type="currency" currencySymbol="€" /></td>
                    </tr>
                    <tr>
                        <th>Seuil d'alerte :</th>
                        <td>${alert.product.thresholdQuantity}</td>
                    </tr>
                    <tr>
                        <th>Stock actuel :</th>
                        <td>
                            <c:set var="totalStock" value="${productDAO.getTotalStockQuantity(alert.product.id)}" />
                            <c:choose>
                                <c:when test="${totalStock < alert.product.thresholdQuantity}">
                                    <span class="badge bg-danger">${totalStock}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success">${totalStock}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/products/view/${alert.product.id}" class="btn btn-primary">
                        <i class="fas fa-eye"></i> Voir le produit
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
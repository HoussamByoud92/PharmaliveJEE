<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Détails de l'utilisateur</h1>
    <div>
        <a href="${pageContext.request.contextPath}/users/edit/${user.id}" class="btn btn-warning me-2">
            <i class="fas fa-edit"></i> Modifier
        </a>
        <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour à la liste
        </a>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations de l'utilisateur</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">ID :</th>
                        <td>${user.id}</td>
                    </tr>
                    <tr>
                        <th>Nom d'utilisateur :</th>
                        <td>${user.username}</td>
                    </tr>
                    <tr>
                        <th>Nom complet :</th>
                        <td>${user.fullName}</td>
                    </tr>
                    <tr>
                        <th>Email :</th>
                        <td>${user.email}</td>
                    </tr>
                    <tr>
                        <th>Rôle :</th>
                        <td>
                            <c:choose>
                                <c:when test="${user.role eq 'admin'}">
                                    <span class="badge bg-danger">Administrateur</span>
                                </c:when>
                                <c:when test="${user.role eq 'pharmacist'}">
                                    <span class="badge bg-success">Pharmacien</span>
                                </c:when>
                                <c:when test="${user.role eq 'seller'}">
                                    <span class="badge bg-primary">Vendeur</span>
                                </c:when>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>Date de création :</th>
                        <td><fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                    <tr>
                        <th>Dernière mise à jour :</th>
                        <td><fmt:formatDate value="${user.updatedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Activité récente</h5>
            </div>
            <div class="card-body">
                <ul class="nav nav-tabs" id="activityTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="movements-tab" data-bs-toggle="tab" data-bs-target="#movements" type="button" role="tab" aria-controls="movements" aria-selected="true">Mouvements</button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="sales-tab" data-bs-toggle="tab" data-bs-target="#sales" type="button" role="tab" aria-controls="sales" aria-selected="false">Ventes</button>
                    </li>
                </ul>
                <div class="tab-content mt-3" id="activityTabsContent">
                    <div class="tab-pane fade show active" id="movements" role="tabpanel" aria-labelledby="movements-tab">
                        <c:if test="${empty userMovements}">
                            <p class="text-muted">Aucun mouvement récent.</p>
                        </c:if>
                        <c:if test="${not empty userMovements}">
                            <div class="list-group">
                                <c:forEach items="${userMovements}" var="movement" end="4">
                                    <a href="${pageContext.request.contextPath}/movements/view/${movement.id}" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h6 class="mb-1">
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
                                                ${movement.batch.product.name}
                                            </h6>
                                            <small><fmt:formatDate value="${movement.createdAt}" pattern="dd/MM/yyyy" /></small>
                                        </div>
                                        <p class="mb-1">Quantité: ${movement.quantity}</p>
                                        <small>${movement.reason}</small>
                                    </a>
                                </c:forEach>
                            </div>
                            <c:if test="${userMovements.size() > 5}">
                                <div class="text-center mt-3">
                                    <a href="${pageContext.request.contextPath}/movements/by-user/${user.id}" class="btn btn-sm btn-outline-primary">Voir tous les mouvements</a>
                                </div>
                            </c:if>
                        </c:if>
                    </div>
                    <div class="tab-pane fade" id="sales" role="tabpanel" aria-labelledby="sales-tab">
                        <c:if test="${empty userSales}">
                            <p class="text-muted">Aucune vente récente.</p>
                        </c:if>
                        <c:if test="${not empty userSales}">
                            <div class="list-group">
                                <c:forEach items="${userSales}" var="sale" end="4">
                                    <a href="${pageContext.request.contextPath}/sales/view/${sale.id}" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h6 class="mb-1">Vente #${sale.id}</h6>
                                            <small><fmt:formatDate value="${sale.createdAt}" pattern="dd/MM/yyyy" /></small>
                                        </div>
                                        <p class="mb-1">
                                            <fmt:formatNumber value="${sale.totalAmount}" type="currency" currencySymbol="€" />
                                            - ${sale.saleItems.size()} produit(s)
                                        </p>
                                        <small>
                                            <c:choose>
                                                <c:when test="${not empty sale.customer}">
                                                    Client: ${sale.customer.name}
                                                </c:when>
                                                <c:otherwise>
                                                    Client anonyme
                                                </c:otherwise>
                                            </c:choose>
                                        </small>
                                    </a>
                                </c:forEach>
                            </div>
                            <c:if test="${userSales.size() > 5}">
                                <div class="text-center mt-3">
                                    <a href="${pageContext.request.contextPath}/sales/by-user/${user.id}" class="btn btn-sm btn-outline-primary">Voir toutes les ventes</a>
                                </div>
                            </c:if>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
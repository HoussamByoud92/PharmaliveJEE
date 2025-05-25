<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Gestion des alertes</h1>
    <div>
        <a href="${pageContext.request.contextPath}/alerts/create-stock-alerts" class="btn btn-primary me-2">
            <i class="fas fa-sync"></i> Vérifier stock
        </a>
        <a href="${pageContext.request.contextPath}/alerts/create-expiry-alerts" class="btn btn-warning">
            <i class="fas fa-sync"></i> Vérifier péremptions
        </a>
    </div>
</div>

<div class="row mb-4">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <ul class="nav nav-tabs card-header-tabs">
                    <li class="nav-item">
                        <a class="nav-link ${empty param.filter ? 'active' : ''}" href="${pageContext.request.contextPath}/alerts">
                            Toutes les alertes
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'unresolved' ? 'active' : ''}" href="${pageContext.request.contextPath}/alerts/unresolved">
                            Non résolues
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'stock' ? 'active' : ''}" href="${pageContext.request.contextPath}/alerts/stock">
                            Stock
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'expiry' ? 'active' : ''}" href="${pageContext.request.contextPath}/alerts/expiry">
                            Péremption
                        </a>
                    </li>
                </ul>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>Type</th>
                                <th>Produit</th>
                                <th>Message</th>
                                <th>Date</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty alerts}">
                                <tr>
                                    <td colspan="6" class="text-center">Aucune alerte trouvée.</td>
                                </tr>
                            </c:if>
                            <c:forEach items="${alerts}" var="alert">
                                <tr>
                                    <td>
                                        <c:if test="${alert.type eq 'stock'}">
                                            <span class="badge bg-danger">Stock</span>
                                        </c:if>
                                        <c:if test="${alert.type eq 'expiry'}">
                                            <span class="badge bg-warning">Péremption</span>
                                        </c:if>
                                    </td>
                                    <td>${alert.product.name}</td>
                                    <td>${alert.message}</td>
                                    <td><fmt:formatDate value="${alert.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    <td>
                                        <c:if test="${alert.resolved}">
                                            <span class="text-success"><i class="fas fa-check-circle"></i> Résolu</span>
                                            <br><small><fmt:formatDate value="${alert.resolvedAt}" pattern="dd/MM/yyyy HH:mm" /></small>
                                        </c:if>
                                        <c:if test="${not alert.resolved}">
                                            <span class="text-danger"><i class="fas fa-exclamation-circle"></i> Non résolu</span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <a href="${pageContext.request.contextPath}/alerts/view/${alert.id}" class="btn btn-info" title="Voir">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <c:if test="${not alert.resolved}">
                                                <a href="${pageContext.request.contextPath}/alerts/resolve/${alert.id}" class="btn btn-success" title="Marquer comme résolu">
                                                    <i class="fas fa-check"></i>
                                                </a>
                                            </c:if>
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

<jsp:include page="includes/footer.jsp" />
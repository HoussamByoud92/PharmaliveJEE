<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Gestion des ventes</h1>
    <a href="${pageContext.request.contextPath}/sales/add" class="btn btn-primary">
        <i class="fas fa-plus"></i> Nouvelle vente
    </a>
</div>

<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Date</th>
                        <th>Client</th>
                        <th>Vendeur</th>
                        <th>Montant</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty sales}">
                        <tr>
                            <td colspan="6" class="text-center">Aucune vente trouvée.</td>
                        </tr>
                    </c:if>
                    <c:forEach items="${sales}" var="sale">
                        <tr>
                            <td>${sale.id}</td>
                            <td><fmt:formatDate value="${sale.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty sale.customer}">
                                        ${sale.customer.name}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">Client anonyme</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${sale.user.fullName}</td>
                            <td><fmt:formatNumber value="${sale.totalAmount}" type="currency" currencySymbol="€" /></td>
                            <td>
                                <div class="btn-group btn-group-sm" role="group">
                                    <a href="${pageContext.request.contextPath}/sales/view/${sale.id}" class="btn btn-info" title="Voir">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/sales/export-pdf/${sale.id}" class="btn btn-secondary" title="Exporter PDF">
                                        <i class="fas fa-file-pdf"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />

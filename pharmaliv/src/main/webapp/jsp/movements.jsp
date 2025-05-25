<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Gestion des mouvements</h1>
    <a href="${pageContext.request.contextPath}/movements/add" class="btn btn-primary">
        <i class="fas fa-plus"></i> Nouveau mouvement
    </a>
</div>

<div class="row mb-4">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <ul class="nav nav-tabs card-header-tabs">
                    <li class="nav-item">
                        <a class="nav-link ${empty param.filter ? 'active' : ''}" href="${pageContext.request.contextPath}/movements">
                            Tous les mouvements
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'entry' ? 'active' : ''}" href="${pageContext.request.contextPath}/movements/by-type/entry">
                            Entrées
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'exit' ? 'active' : ''}" href="${pageContext.request.contextPath}/movements/by-type/exit">
                            Sorties
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${param.filter eq 'adjustment' ? 'active' : ''}" href="${pageContext.request.contextPath}/movements/by-type/adjustment">
                            Ajustements
                        </a>
                    </li>
                    <c:if test="${not empty batch}">
                        <li class="nav-item ms-auto">
                            <span class="nav-link disabled">
                                Lot: ${batch.batchNumber} (${batch.product.name})
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
                                <th>#</th>
                                <th>Date</th>
                                <th>Type</th>
                                <th>Produit</th>
                                <th>Lot</th>
                                <th>Quantité</th>
                                <th>Utilisateur</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty movements}">
                                <tr>
                                    <td colspan="8" class="text-center">Aucun mouvement trouvé.</td>
                                </tr>
                            </c:if>
                            <c:forEach items="${movements}" var="movement">
                                <tr>
                                    <td>${movement.id}</td>
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
                                    <td>
                                        <c:if test="${not empty movement.batch and not empty movement.batch.product}">
                                            ${movement.batch.product.name}
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:if test="${not empty movement.batch}">
                                            ${movement.batch.batchNumber}
                                        </c:if>
                                    </td>
                                    <td>${movement.quantity}</td>
                                    <td>
                                        <c:if test="${not empty movement.user}">
                                            ${movement.user.fullName}
                                        </c:if>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <a href="${pageContext.request.contextPath}/movements/view/${movement.id}" class="btn btn-info" title="Voir">
                                                <i class="fas fa-eye"></i>
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
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
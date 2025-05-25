<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Gestion des utilisateurs</h1>
    <a href="${pageContext.request.contextPath}/users/add" class="btn btn-primary">
        <i class="fas fa-plus"></i> Nouvel utilisateur
    </a>
</div>

<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nom d'utilisateur</th>
                        <th>Nom complet</th>
                        <th>Email</th>
                        <th>Rôle</th>
                        <th>Date de création</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty users}">
                        <tr>
                            <td colspan="7" class="text-center">Aucun utilisateur trouvé.</td>
                        </tr>
                    </c:if>
                    <c:forEach items="${users}" var="user">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.fullName}</td>
                            <td>${user.email}</td>
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
                            <td><fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy" /></td>
                            <td>
                                <div class="btn-group btn-group-sm" role="group">
                                    <a href="${pageContext.request.contextPath}/users/view/${user.id}" class="btn btn-info" title="Voir">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/users/edit/${user.id}" class="btn btn-warning" title="Modifier">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <c:if test="${sessionScope.userId ne user.id}">
                                        <button type="button" class="btn btn-danger" title="Supprimer" 
                                                onclick="confirmDelete(${user.id}, '${user.username}')">
                                            <i class="fas fa-trash"></i>
                                        </button>
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

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">Confirmer la suppression</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir supprimer l'utilisateur <span id="userName"></span> ?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <a href="#" id="deleteLink" class="btn btn-danger">Supprimer</a>
            </div>
        </div>
    </div>
</div>

<script>
    function confirmDelete(userId, userName) {
        document.getElementById('userName').textContent = userName;
        document.getElementById('deleteLink').href = '${pageContext.request.contextPath}/users/delete/' + userId;
        var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        deleteModal.show();
    }
</script>

<jsp:include page="includes/footer.jsp" />
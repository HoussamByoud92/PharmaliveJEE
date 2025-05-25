<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Gestion des clients</h1>
    <div class="d-flex">
        <form action="${pageContext.request.contextPath}/customers/search" method="get" class="d-flex me-2">
            <input type="text" name="q" class="form-control me-2" placeholder="Rechercher un client..." value="${searchQuery}">
            <button type="submit" class="btn btn-outline-primary">
                <i class="fas fa-search"></i>
            </button>
        </form>
        <a href="${pageContext.request.contextPath}/customers/add" class="btn btn-primary">
            <i class="fas fa-plus"></i> Nouveau client
        </a>
    </div>
</div>

<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nom</th>
                        <th>Téléphone</th>
                        <th>Email</th>
                        <th>Date de création</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty customers}">
                        <tr>
                            <td colspan="6" class="text-center">
                                <c:choose>
                                    <c:when test="${not empty searchQuery}">
                                        Aucun client trouvé pour la recherche "${searchQuery}".
                                    </c:when>
                                    <c:otherwise>
                                        Aucun client trouvé.
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                    <c:forEach items="${customers}" var="customer">
                        <tr class="customer-row" data-id="${customer.id}">
                            <td>${customer.id}</td>
                            <td>${customer.name}</td>
                            <td>${empty customer.phone ? '-' : customer.phone}</td>
                            <td>${empty customer.email ? '-' : customer.email}</td>
                            <td><fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy" /></td>
                            <td>
                                <div class="btn-group btn-group-sm" role="group">
                                    <a href="${pageContext.request.contextPath}/customers/view/${customer.id}" class="btn btn-info" title="Voir">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/customers/edit/${customer.id}" class="btn btn-warning" title="Modifier">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <button type="button" class="btn btn-danger" title="Supprimer" 
                                            onclick="confirmDelete(${customer.id}, '${customer.name}')">
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

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">Confirmer la suppression</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir supprimer le client <span id="customerName"></span> ?
                <p class="text-danger mt-2">
                    <i class="fas fa-exclamation-triangle"></i> Attention : Cette action est irréversible.
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
    // Animation for customer rows
    document.addEventListener('DOMContentLoaded', function() {
        const customerRows = document.querySelectorAll('.customer-row');
        customerRows.forEach((row, index) => {
            row.style.opacity = '0';
            row.style.transform = 'translateY(20px)';
            row.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            
            setTimeout(() => {
                row.style.opacity = '1';
                row.style.transform = 'translateY(0)';
            }, 100 + (index * 50)); // Staggered animation
        });
    });

    function confirmDelete(customerId, customerName) {
        document.getElementById('customerName').textContent = customerName;
        document.getElementById('deleteLink').href = '${pageContext.request.contextPath}/customers/delete/' + customerId;
        var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        deleteModal.show();
    }
</script>

<jsp:include page="includes/footer.jsp" />
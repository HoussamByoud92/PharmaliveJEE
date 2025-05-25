<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Gestion des produits</h1>
    <a href="${pageContext.request.contextPath}/products/add" class="btn btn-primary">
        <i class="fas fa-plus"></i> Ajouter un produit
    </a>
</div>

<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Nom</th>
                        <th>DCI</th>
                        <th>Prix</th>
                        <th>Seuil d'alerte</th>
                        <th>Stock total</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty products}">
                        <tr>
                            <td colspan="7" class="text-center">Aucun produit trouvé.</td>
                        </tr>
                    </c:if>
                    <c:forEach items="${products}" var="product">
                        <tr>
                            <td>${product.code}</td>
                            <td>${product.name}</td>
                            <td>${product.dci}</td>
                            <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="€" /></td>
                            <td>${product.thresholdQuantity}</td>
                            <td>
                                <c:set var="totalStock" value="${productDAO.getTotalStockQuantity(product.id)}" />
                                <c:choose>
                                    <c:when test="${totalStock < product.thresholdQuantity}">
                                        <span class="badge bg-danger">${totalStock}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-success">${totalStock}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="btn-group btn-group-sm" role="group">
                                    <a href="${pageContext.request.contextPath}/products/view/${product.id}" class="btn btn-info" title="Voir">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/products/edit/${product.id}" class="btn btn-warning" title="Modifier">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <button type="button" class="btn btn-danger" title="Supprimer" 
                                            onclick="confirmDelete(${product.id}, '${product.name}')">
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
                Êtes-vous sûr de vouloir supprimer le produit <span id="productName"></span> ?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <a href="#" id="deleteLink" class="btn btn-danger">Supprimer</a>
            </div>
        </div>
    </div>
</div>

<script>
    function confirmDelete(productId, productName) {
        document.getElementById('productName').textContent = productName;
        document.getElementById('deleteLink').href = '${pageContext.request.contextPath}/products/delete/' + productId;
        var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        deleteModal.show();
    }
</script>

<jsp:include page="includes/footer.jsp" />

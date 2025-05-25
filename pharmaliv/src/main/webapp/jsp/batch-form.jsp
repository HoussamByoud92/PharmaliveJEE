<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">
        <c:choose>
            <c:when test="${empty batch}">Nouveau lot</c:when>
            <c:otherwise>Modifier le lot</c:otherwise>
        </c:choose>
    </h1>
    <a href="${pageContext.request.contextPath}/batches" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Retour à la liste
    </a>
</div>

<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/batches" method="post">
            <c:choose>
                <c:when test="${empty batch}">
                    <input type="hidden" name="action" value="add">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${batch.id}">
                </c:otherwise>
            </c:choose>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="productId" class="form-label">Produit*</label>
                    <select class="form-select" id="productId" name="productId" required 
                            <c:if test="${not empty batch}">disabled</c:if>>
                        <option value="">Sélectionner un produit</option>
                        <c:forEach items="${products}" var="product">
                            <option value="${product.id}" 
                                <c:if test="${(not empty selectedProduct and selectedProduct.id eq product.id) or (not empty batch and batch.productId eq product.id)}">selected</c:if>>
                                ${product.name} (${product.code})
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty batch}">
                        <input type="hidden" name="productId" value="${batch.productId}">
                        <div class="form-text">Le produit ne peut pas être modifié après la création du lot.</div>
                    </c:if>
                </div>
                <div class="col-md-6">
                    <label for="batchNumber" class="form-label">Numéro de lot*</label>
                    <input type="text" class="form-control" id="batchNumber" name="batchNumber" 
                           value="${batch.batchNumber}" required
                           <c:if test="${not empty batch}">readonly</c:if>>
                    <c:if test="${not empty batch}">
                        <div class="form-text">Le numéro de lot ne peut pas être modifié après la création.</div>
                    </c:if>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="quantity" class="form-label">Quantité*</label>
                    <input type="number" class="form-control" id="quantity" name="quantity" 
                           value="${empty batch ? '0' : batch.quantity}" min="0" required>
                </div>
                <div class="col-md-6">
                    <label for="purchasePrice" class="form-label">Prix d'achat*</label>
                    <div class="input-group">
                        <input type="number" class="form-control" id="purchasePrice" name="purchasePrice" 
                               value="${batch.purchasePrice}" step="0.01" min="0" required>
                        <span class="input-group-text">€</span>
                    </div>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="expiryDate" class="form-label">Date d'expiration*</label>
                    <input type="date" class="form-control" id="expiryDate" name="expiryDate" 
                           value="<c:if test="${not empty batch}"><fmt:formatDate value="${batch.expiryDate}" pattern="yyyy-MM-dd" /></c:if>" 
                           required>
                </div>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/batches" class="btn btn-secondary me-md-2">Annuler</a>
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${empty batch}">Créer</c:when>
                        <c:otherwise>Enregistrer</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    // Set minimum date for expiry date to today
    document.addEventListener('DOMContentLoaded', function() {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        const formattedDate = yyyy + '-' + mm + '-' + dd;
        
        document.getElementById('expiryDate').min = formattedDate;
        
        // If no expiry date is set, default to 1 year from now
        if (!document.getElementById('expiryDate').value) {
            const nextYear = new Date();
            nextYear.setFullYear(nextYear.getFullYear() + 1);
            const nextYearYyyy = nextYear.getFullYear();
            const nextYearMm = String(nextYear.getMonth() + 1).padStart(2, '0');
            const nextYearDd = String(nextYear.getDate()).padStart(2, '0');
            document.getElementById('expiryDate').value = nextYearYyyy + '-' + nextYearMm + '-' + nextYearDd;
        }
    });
</script>

<jsp:include page="includes/footer.jsp" />
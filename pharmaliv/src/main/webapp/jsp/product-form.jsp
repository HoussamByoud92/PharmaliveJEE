<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">
        <c:choose>
            <c:when test="${empty product}">Ajouter un produit</c:when>
            <c:otherwise>Modifier un produit</c:otherwise>
        </c:choose>
    </h1>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Retour à la liste
    </a>
</div>

<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/products" method="post">
            <c:choose>
                <c:when test="${empty product}">
                    <input type="hidden" name="action" value="add">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${product.id}">
                </c:otherwise>
            </c:choose>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="code" class="form-label">Code*</label>
                    <input type="text" class="form-control" id="code" name="code" value="${product.code}" required>
                </div>
                <div class="col-md-6">
                    <label for="name" class="form-label">Nom*</label>
                    <input type="text" class="form-control" id="name" name="name" value="${product.name}" required>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="dci" class="form-label">DCI</label>
                    <input type="text" class="form-control" id="dci" name="dci" value="${product.dci}">
                </div>
                <div class="col-md-6">
                    <label for="price" class="form-label">Prix*</label>
                    <div class="input-group">
                        <input type="number" class="form-control" id="price" name="price" value="${product.price}" step="0.01" min="0" required>
                        <span class="input-group-text">€</span>
                    </div>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="thresholdQuantity" class="form-label">Seuil d'alerte*</label>
                    <input type="number" class="form-control" id="thresholdQuantity" name="thresholdQuantity" value="${product.thresholdQuantity}" min="0" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3">${product.description}</textarea>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary me-md-2">Annuler</a>
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${empty product}">Ajouter</c:when>
                        <c:otherwise>Enregistrer</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />

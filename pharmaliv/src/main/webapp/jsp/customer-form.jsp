<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">
        <c:choose>
            <c:when test="${empty customer}">Nouveau client</c:when>
            <c:otherwise>Modifier le client</c:otherwise>
        </c:choose>
    </h1>
    <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Retour à la liste
    </a>
</div>

<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/customers" method="post" id="customerForm">
            <c:choose>
                <c:when test="${empty customer}">
                    <input type="hidden" name="action" value="add">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${customer.id}">
                </c:otherwise>
            </c:choose>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="name" class="form-label">Nom*</label>
                    <input type="text" class="form-control" id="name" name="name" value="${customer.name}" required>
                </div>
                <div class="col-md-6">
                    <label for="phone" class="form-label">Téléphone</label>
                    <input type="tel" class="form-control" id="phone" name="phone" value="${customer.phone}">
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" value="${customer.email}">
                </div>
            </div>

            <div class="mb-3">
                <label for="address" class="form-label">Adresse</label>
                <textarea class="form-control" id="address" name="address" rows="3">${customer.address}</textarea>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary me-md-2">Annuler</a>
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${empty customer}">Créer</c:when>
                        <c:otherwise>Enregistrer</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    // Form animation
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('customerForm');
        form.style.opacity = '0';
        form.style.transform = 'translateY(20px)';
        form.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        
        setTimeout(() => {
            form.style.opacity = '1';
            form.style.transform = 'translateY(0)';
        }, 100);
        
        // Input field animations
        const inputs = document.querySelectorAll('input, textarea');
        inputs.forEach((input, index) => {
            input.addEventListener('focus', function() {
                this.style.transition = 'transform 0.3s ease, box-shadow 0.3s ease';
                this.style.transform = 'scale(1.02)';
                this.style.boxShadow = '0 0 8px rgba(0, 123, 255, 0.5)';
            });
            
            input.addEventListener('blur', function() {
                this.style.transform = 'scale(1)';
                this.style.boxShadow = 'none';
            });
        });
    });
</script>

<jsp:include page="includes/footer.jsp" />
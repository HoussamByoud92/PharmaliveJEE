<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">
        <c:choose>
            <c:when test="${empty user}">Nouvel utilisateur</c:when>
            <c:otherwise>Modifier l'utilisateur</c:otherwise>
        </c:choose>
    </h1>
    <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Retour à la liste
    </a>
</div>

<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/users" method="post">
            <c:choose>
                <c:when test="${empty user}">
                    <input type="hidden" name="action" value="add">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${user.id}">
                </c:otherwise>
            </c:choose>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="username" class="form-label">Nom d'utilisateur*</label>
                    <input type="text" class="form-control" id="username" name="username" value="${user.username}" required>
                </div>
                <div class="col-md-6">
                    <label for="password" class="form-label">
                        <c:choose>
                            <c:when test="${empty user}">Mot de passe*</c:when>
                            <c:otherwise>Mot de passe (laisser vide pour ne pas changer)</c:otherwise>
                        </c:choose>
                    </label>
                    <input type="password" class="form-control" id="password" name="password" 
                           <c:if test="${empty user}">required</c:if>>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="fullName" class="form-label">Nom complet*</label>
                    <input type="text" class="form-control" id="fullName" name="fullName" value="${user.fullName}" required>
                </div>
                <div class="col-md-6">
                    <label for="email" class="form-label">Email*</label>
                    <input type="email" class="form-control" id="email" name="email" value="${user.email}" required>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="role" class="form-label">Rôle*</label>
                    <select class="form-select" id="role" name="role" required>
                        <option value="">Sélectionner un rôle</option>
                        <c:forEach items="${userRoles}" var="role">
                            <option value="${role}" <c:if test="${user.role eq role}">selected</c:if>>
                                <c:choose>
                                    <c:when test="${role eq 'admin'}">Administrateur</c:when>
                                    <c:when test="${role eq 'pharmacist'}">Pharmacien</c:when>
                                    <c:when test="${role eq 'seller'}">Vendeur</c:when>
                                </c:choose>
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary me-md-2">Annuler</a>
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${empty user}">Créer</c:when>
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
        // Animate form elements
        const formElements = document.querySelectorAll('.card, .form-control, .form-select, .btn');
        formElements.forEach((element, index) => {
            // Set initial state
            element.style.opacity = '0';
            element.style.transform = 'translateY(20px)';
            element.style.transition = 'opacity 0.5s ease, transform 0.5s ease';

            // Animate in with staggered delay
            setTimeout(() => {
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }, 100 + (index * 50));
        });

        // Add focus animations to form controls
        const inputs = document.querySelectorAll('.form-control, .form-select');
        inputs.forEach(input => {
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

        // Add hover animation to submit button
        const submitBtn = document.querySelector('button[type="submit"]');
        submitBtn.style.transition = 'all 0.3s ease';
        submitBtn.style.position = 'relative';
        submitBtn.style.overflow = 'hidden';

        submitBtn.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-3px)';
            this.style.boxShadow = '0 5px 15px rgba(0, 123, 255, 0.3)';
        });

        submitBtn.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = 'none';
        });
    });
</script>

<jsp:include page="includes/footer.jsp" />

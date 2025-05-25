<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Nouveau mouvement</h1>
    <a href="${pageContext.request.contextPath}/movements" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Retour à la liste
    </a>
</div>

<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/movements" method="post">
            <input type="hidden" name="action" value="add">

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="batchId" class="form-label">Lot*</label>
                    <select class="form-select" id="batchId" name="batchId" required>
                        <option value="">Sélectionner un lot</option>
                        <c:forEach items="${batches}" var="batch">
                            <option value="${batch.id}" <c:if test="${selectedBatch.id eq batch.id}">selected</c:if>>
                                ${batch.batchNumber} - ${batch.product.name} (Stock: ${batch.quantity})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label for="type" class="form-label">Type de mouvement*</label>
                    <select class="form-select" id="type" name="type" required>
                        <option value="">Sélectionner un type</option>
                        <c:forEach items="${movementTypes}" var="type">
                            <option value="${type}">
                                <c:choose>
                                    <c:when test="${type eq 'entry'}">Entrée</c:when>
                                    <c:when test="${type eq 'exit'}">Sortie</c:when>
                                    <c:when test="${type eq 'adjustment'}">Ajustement</c:when>
                                </c:choose>
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="quantity" class="form-label">Quantité*</label>
                    <input type="number" class="form-control" id="quantity" name="quantity" min="1" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="reason" class="form-label">Raison*</label>
                <textarea class="form-control" id="reason" name="reason" rows="3" required></textarea>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/movements" class="btn btn-secondary me-md-2">Annuler</a>
                <button type="submit" class="btn btn-primary">Enregistrer</button>
            </div>
        </form>
    </div>
</div>

<script>
    // Validate form before submission
    document.querySelector('form').addEventListener('submit', function(e) {
        const type = document.getElementById('type').value;
        const quantity = parseInt(document.getElementById('quantity').value);
        const batchId = document.getElementById('batchId').value;
        
        if (!type || !quantity || !batchId) {
            return; // Let the browser handle required fields
        }
        
        // For exit movements, check if there's enough stock
        if (type === 'exit') {
            const batchOption = document.querySelector(`option[value="${batchId}"]`);
            if (batchOption) {
                const stockText = batchOption.textContent.match(/Stock: (\d+)/);
                if (stockText && stockText[1]) {
                    const stock = parseInt(stockText[1]);
                    if (quantity > stock) {
                        e.preventDefault();
                        alert('Quantité insuffisante dans le lot sélectionné. Stock disponible: ' + stock);
                    }
                }
            }
        }
    });
</script>

<jsp:include page="includes/footer.jsp" />
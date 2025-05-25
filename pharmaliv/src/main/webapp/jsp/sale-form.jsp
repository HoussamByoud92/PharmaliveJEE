<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Nouvelle vente</h1>
    <a href="${pageContext.request.contextPath}/sales" class="btn btn-secondary">
        <i class="fas fa-arrow-left"></i> Retour à la liste
    </a>
</div>

<div class="card">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/sales" method="post" id="saleForm">
            <input type="hidden" name="action" value="add">
            
            <div class="row mb-4">
                <div class="col-md-6">
                    <label for="customerId" class="form-label">Client</label>
                    <select class="form-select" id="customerId" name="customerId">
                        <option value="0">Client anonyme</option>
                        <c:forEach items="${customers}" var="customer">
                            <option value="${customer.id}">${customer.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            
            <h5 class="mb-3">Produits</h5>
            
            <div id="productItems">
                <div class="product-item mb-3 p-3 border rounded">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Produit*</label>
                            <select class="form-select product-select" required>
                                <option value="">Sélectionner un produit</option>
                                <c:forEach items="${products}" var="product">
                                    <option value="${product.id}" data-price="${product.price}">${product.name} (${product.code})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 batch-container">
                            <label class="form-label">Lot*</label>
                            <select class="form-select batch-select" name="batchId" required disabled>
                                <option value="">Sélectionner un lot</option>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-4">
                            <label class="form-label">Quantité*</label>
                            <input type="number" class="form-control quantity-input" name="quantity" min="1" value="1" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Prix unitaire</label>
                            <div class="input-group">
                                <input type="text" class="form-control unit-price" readonly>
                                <span class="input-group-text">€</span>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Total</label>
                            <div class="input-group">
                                <input type="text" class="form-control item-total" readonly>
                                <span class="input-group-text">€</span>
                            </div>
                        </div>
                    </div>
                    <div class="mt-2 text-end">
                        <button type="button" class="btn btn-danger btn-sm remove-item" style="display: none;">
                            <i class="fas fa-trash"></i> Supprimer
                        </button>
                    </div>
                </div>
            </div>
            
            <div class="mb-4">
                <button type="button" class="btn btn-success" id="addProductBtn">
                    <i class="fas fa-plus"></i> Ajouter un produit
                </button>
            </div>
            
            <div class="row mb-4">
                <div class="col-md-6 offset-md-6">
                    <div class="card">
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-2">
                                <h5>Total:</h5>
                                <h5 id="totalAmount">0.00 €</h5>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <a href="${pageContext.request.contextPath}/sales" class="btn btn-secondary me-md-2">Annuler</a>
                <button type="submit" class="btn btn-primary">Enregistrer la vente</button>
            </div>
        </form>
    </div>
</div>

<script>
    // Get batches for a product
    function getBatchesForProduct(productId, selectElement) {
        fetch('${pageContext.request.contextPath}/api/batches?productId=' + productId)
            .then(response => response.json())
            .then(batches => {
                selectElement.innerHTML = '<option value="">Sélectionner un lot</option>';
                
                if (batches.length === 0) {
                    selectElement.innerHTML += '<option value="" disabled>Aucun lot disponible</option>';
                } else {
                    batches.forEach(batch => {
                        if (batch.quantity > 0) {
                            const expiryDate = new Date(batch.expiryDate).toLocaleDateString();
                            selectElement.innerHTML += `<option value="\${batch.id}" data-quantity="\${batch.quantity}">\${batch.batchNumber} (Qté: \${batch.quantity}, Exp: \${expiryDate})</option>`;
                        }
                    });
                }
                
                selectElement.disabled = false;
                updateTotals();
            })
            .catch(error => {
                console.error('Error fetching batches:', error);
                selectElement.innerHTML = '<option value="">Erreur lors du chargement des lots</option>';
                selectElement.disabled = false;
            });
    }
    
    // Update item total
    function updateItemTotal(itemElement) {
        const quantityInput = itemElement.querySelector('.quantity-input');
        const unitPriceInput = itemElement.querySelector('.unit-price');
        const itemTotalInput = itemElement.querySelector('.item-total');
        
        const quantity = parseInt(quantityInput.value) || 0;
        const unitPrice = parseFloat(unitPriceInput.value) || 0;
        
        const total = quantity * unitPrice;
        itemTotalInput.value = total.toFixed(2);
        
        return total;
    }
    
    // Update all totals
    function updateTotals() {
        let grandTotal = 0;
        
        document.querySelectorAll('.product-item').forEach(item => {
            grandTotal += updateItemTotal(item);
        });
        
        document.getElementById('totalAmount').textContent = grandTotal.toFixed(2) + ' €';
    }
    
    // Add product item
    document.getElementById('addProductBtn').addEventListener('click', function() {
        const productItems = document.getElementById('productItems');
        const itemTemplate = productItems.querySelector('.product-item').cloneNode(true);
        
        // Reset values
        itemTemplate.querySelector('.product-select').value = '';
        itemTemplate.querySelector('.batch-select').innerHTML = '<option value="">Sélectionner un lot</option>';
        itemTemplate.querySelector('.batch-select').disabled = true;
        itemTemplate.querySelector('.quantity-input').value = '1';
        itemTemplate.querySelector('.unit-price').value = '';
        itemTemplate.querySelector('.item-total').value = '';
        
        // Show remove button for all items
        document.querySelectorAll('.remove-item').forEach(btn => {
            btn.style.display = 'inline-block';
        });
        
        // Add event listeners
        addItemEventListeners(itemTemplate);
        
        productItems.appendChild(itemTemplate);
    });
    
    // Add event listeners to item
    function addItemEventListeners(itemElement) {
        // Product select change
        itemElement.querySelector('.product-select').addEventListener('change', function() {
            const productId = this.value;
            const batchSelect = itemElement.querySelector('.batch-select');
            const unitPriceInput = itemElement.querySelector('.unit-price');
            
            if (productId) {
                const selectedOption = this.options[this.selectedIndex];
                const price = selectedOption.getAttribute('data-price');
                unitPriceInput.value = price;
                
                batchSelect.disabled = true;
                batchSelect.innerHTML = '<option value="">Chargement des lots...</option>';
                
                getBatchesForProduct(productId, batchSelect);
            } else {
                batchSelect.innerHTML = '<option value="">Sélectionner un lot</option>';
                batchSelect.disabled = true;
                unitPriceInput.value = '';
            }
            
            updateTotals();
        });
        
        // Quantity input change
        itemElement.querySelector('.quantity-input').addEventListener('input', function() {
            const batchSelect = itemElement.querySelector('.batch-select');
            const selectedBatchOption = batchSelect.options[batchSelect.selectedIndex];
            
            if (selectedBatchOption && selectedBatchOption.hasAttribute('data-quantity')) {
                const maxQuantity = parseInt(selectedBatchOption.getAttribute('data-quantity'));
                if (parseInt(this.value) > maxQuantity) {
                    this.value = maxQuantity;
                    alert('La quantité ne peut pas dépasser le stock disponible (' + maxQuantity + ').');
                }
            }
            
            updateTotals();
        });
        
        // Batch select change
        itemElement.querySelector('.batch-select').addEventListener('change', function() {
            const quantityInput = itemElement.querySelector('.quantity-input');
            const selectedOption = this.options[this.selectedIndex];
            
            if (selectedOption && selectedOption.hasAttribute('data-quantity')) {
                const maxQuantity = parseInt(selectedOption.getAttribute('data-quantity'));
                if (parseInt(quantityInput.value) > maxQuantity) {
                    quantityInput.value = maxQuantity;
                }
            }
            
            updateTotals();
        });
        
        // Remove button click
        itemElement.querySelector('.remove-item').addEventListener('click', function() {
            if (document.querySelectorAll('.product-item').length > 1) {
                itemElement.remove();
                
                // Hide remove button if only one item left
                if (document.querySelectorAll('.product-item').length === 1) {
                    document.querySelector('.remove-item').style.display = 'none';
                }
                
                updateTotals();
            }
        });
    }
    
    // Add event listeners to the first item
    addItemEventListeners(document.querySelector('.product-item'));
    
    // Form validation
    document.getElementById('saleForm').addEventListener('submit', function(e) {
        let valid = true;
        let hasItems = false;
        
        document.querySelectorAll('.product-item').forEach(item => {
            const productSelect = item.querySelector('.product-select');
            const batchSelect = item.querySelector('.batch-select');
            const quantityInput = item.querySelector('.quantity-input');
            
            if (productSelect.value && batchSelect.value && parseInt(quantityInput.value) > 0) {
                hasItems = true;
            } else if (productSelect.value || batchSelect.value || parseInt(quantityInput.value) > 0) {
                valid = false;
            }
        });
        
        if (!valid) {
            e.preventDefault();
            alert('Veuillez compléter tous les champs pour chaque produit ou supprimer les lignes vides.');
        } else if (!hasItems) {
            e.preventDefault();
            alert('Veuillez ajouter au moins un produit à la vente.');
        }
    });
</script>

<jsp:include page="includes/footer.jsp" />
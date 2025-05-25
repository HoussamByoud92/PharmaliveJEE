<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2">Détails de la vente #${sale.id}</h1>
    <div>
        <a href="${pageContext.request.contextPath}/sales/export-pdf/${sale.id}" class="btn btn-primary me-2">
            <i class="fas fa-file-pdf"></i> Exporter PDF
        </a>
        <a href="${pageContext.request.contextPath}/sales" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour à la liste
        </a>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations générales</h5>
            </div>
            <div class="card-body">
                <table class="table table-borderless">
                    <tr>
                        <th style="width: 30%">Numéro :</th>
                        <td>${sale.id}</td>
                    </tr>
                    <tr>
                        <th>Date :</th>
                        <td><fmt:formatDate value="${sale.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                    <tr>
                        <th>Vendeur :</th>
                        <td>${sale.user.fullName}</td>
                    </tr>
                    <tr>
                        <th>Montant total :</th>
                        <td><fmt:formatNumber value="${sale.totalAmount}" type="currency" currencySymbol="€" /></td>
                    </tr>
                </table>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Informations client</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty sale.customer}">
                        <table class="table table-borderless">
                            <tr>
                                <th style="width: 30%">Nom :</th>
                                <td>${sale.customer.name}</td>
                            </tr>
                            <c:if test="${not empty sale.customer.phone}">
                                <tr>
                                    <th>Téléphone :</th>
                                    <td>${sale.customer.phone}</td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty sale.customer.email}">
                                <tr>
                                    <th>Email :</th>
                                    <td>${sale.customer.email}</td>
                                </tr>
                            </c:if>
                            <c:if test="${not empty sale.customer.address}">
                                <tr>
                                    <th>Adresse :</th>
                                    <td>${sale.customer.address}</td>
                                </tr>
                            </c:if>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">Client anonyme</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Produits vendus</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Produit</th>
                                <th>Lot</th>
                                <th>Quantité</th>
                                <th>Prix unitaire</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${sale.saleItems}" var="item">
                                <tr>
                                    <td>${item.batch.product.name}</td>
                                    <td>${item.batch.batchNumber}</td>
                                    <td>${item.quantity}</td>
                                    <td><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="€" /></td>
                                    <td><fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="€" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr>
                                <th colspan="4" class="text-end">Total :</th>
                                <th><fmt:formatNumber value="${sale.totalAmount}" type="currency" currencySymbol="€" /></th>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />

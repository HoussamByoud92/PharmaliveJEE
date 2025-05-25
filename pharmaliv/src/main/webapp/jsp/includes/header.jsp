<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PHARMALIVE - ${pageTitle}</title>
    <!-- Favicon -->
    <link rel="icon" href="${pageContext.request.contextPath}/img/logo.svg" type="image/svg+xml">
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/css/pharmalive.css" rel="stylesheet">
    <!-- No inline styles - using pharmalive.css -->
</head>
<body>
    <nav class="navbar navbar-dark fixed-top flex-md-nowrap p-0 shadow">
        <a class="navbar-brand px-3 d-flex align-items-center" href="${pageContext.request.contextPath}/dashboard">
            <img src="${pageContext.request.contextPath}/img/logo.svg" alt="PharmaLive Logo" width="30" height="30" class="me-2">
            <span style="color: #A5D6A7; font-weight: 700; letter-spacing: 1px;">PHARMA</span><span style="color: #FFFFFF; font-weight: 700; letter-spacing: 1px;">LIVE</span>
        </a>
        <button class="navbar-toggler position-absolute d-md-none collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="w-100"></div>
        <ul class="navbar-nav px-3 d-flex flex-row">
            <!-- Notifications -->
            <li class="nav-item text-nowrap me-3 dropdown">
                <a class="nav-link notification-bell" href="#" id="notificationsDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="fas fa-bell"></i>
                    <c:if test="${not empty unresolvedAlerts and unresolvedAlerts > 0}">
                        <span class="notification-badge">${unresolvedAlerts}</span>
                    </c:if>
                </a>
                <div class="dropdown-menu dropdown-menu-end notification-dropdown" aria-labelledby="notificationsDropdown">
                    <div class="notification-header">
                        <h6 class="m-0">Notifications</h6>
                        <a href="${pageContext.request.contextPath}/alerts" class="text-white text-decoration-none">
                            <small>Voir tout</small>
                        </a>
                    </div>
                    <c:if test="${empty recentAlerts}">
                        <div class="notification-item">
                            <p class="text-muted mb-0">Aucune notification récente.</p>
                        </div>
                    </c:if>
                    <c:forEach items="${recentAlerts}" var="alert">
                        <a href="${pageContext.request.contextPath}/alerts/view/${alert.id}" class="dropdown-item notification-item ${not alert.resolved ? 'unread' : ''} ${alert.type}">
                            <div class="d-flex align-items-center">
                                <div class="notification-icon">
                                    <c:if test="${alert.type eq 'stock'}">
                                        <i class="fas fa-exclamation-triangle"></i>
                                    </c:if>
                                    <c:if test="${alert.type eq 'expiry'}">
                                        <i class="fas fa-calendar-times"></i>
                                    </c:if>
                                </div>
                                <div>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h6 class="mb-0">
                                            <c:if test="${alert.type eq 'stock'}">
                                                <span class="badge bg-danger">Stock</span>
                                            </c:if>
                                            <c:if test="${alert.type eq 'expiry'}">
                                                <span class="badge bg-warning">Péremption</span>
                                            </c:if>
                                            ${alert.product.name}
                                        </h6>
                                        <small class="text-muted ms-2"><fmt:formatDate value="${alert.createdAt}" pattern="dd/MM HH:mm" /></small>
                                    </div>
                                    <p class="mb-0 small">${alert.message}</p>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                    <div class="notification-footer">
                        <a href="${pageContext.request.contextPath}/alerts/create-stock-alerts" class="btn btn-sm btn-outline-primary me-1">Vérifier stock</a>
                        <a href="${pageContext.request.contextPath}/alerts/create-expiry-alerts" class="btn btn-sm btn-outline-warning">Vérifier péremptions</a>
                    </div>
                </div>
            </li>
            <li class="nav-item text-nowrap me-3">
                <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                    <i class="fas fa-user-circle"></i> Mon profil
                </a>
            </li>
            <li class="nav-item text-nowrap">
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt"></i> Déconnexion
                </a>
            </li>
        </ul>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block sidebar collapse">
                <div class="sidebar-sticky pt-3">
                    <div class="text-center mb-4 d-none d-md-block">
                        <img src="${pageContext.request.contextPath}/img/logo.svg" alt="PharmaLive Logo" width="80" height="80" class="mb-2">
                        <h5><span style="color: #A5D6A7; font-weight: 700; letter-spacing: 1px;">PHARMA</span><span style="color: #FFFFFF; font-weight: 700; letter-spacing: 1px;">LIVE</span></h5>
                    </div>
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link ${pageTitle eq 'Tableau de bord' ? 'active' : ''}" href="${pageContext.request.contextPath}/dashboard">
                                <i class="fas fa-tachometer-alt"></i> Tableau de bord
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageTitle eq 'Produits' ? 'active' : ''}" href="${pageContext.request.contextPath}/products">
                                <i class="fas fa-pills"></i> Produits
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageTitle eq 'Lots' ? 'active' : ''}" href="${pageContext.request.contextPath}/batches">
                                <i class="fas fa-boxes"></i> Lots
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageTitle eq 'Mouvements' ? 'active' : ''}" href="${pageContext.request.contextPath}/movements">
                                <i class="fas fa-exchange-alt"></i> Mouvements
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageTitle eq 'Ventes' ? 'active' : ''}" href="${pageContext.request.contextPath}/sales">
                                <i class="fas fa-shopping-cart"></i> Ventes
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageTitle eq 'Alertes' ? 'active' : ''}" href="${pageContext.request.contextPath}/alerts">
                                <i class="fas fa-exclamation-triangle"></i> Alertes
                                <c:if test="${not empty unresolvedAlerts and unresolvedAlerts > 0}">
                                    <span class="badge bg-danger rounded-pill ms-1">${unresolvedAlerts}</span>
                                </c:if>
                            </a>
                        </li>
                        <c:if test="${sessionScope.userRole eq 'admin'}">
                            <li class="nav-item">
                                <a class="nav-link ${pageTitle eq 'Utilisateurs' ? 'active' : ''}" href="${pageContext.request.contextPath}/users">
                                    <i class="fas fa-users"></i> Utilisateurs
                                </a>
                            </li>
                        </c:if>
                        <li class="nav-item">
                            <a class="nav-link ${pageTitle eq 'Clients' ? 'active' : ''}" href="${pageContext.request.contextPath}/customers">
                                <i class="fas fa-user-friends"></i> Clients
                            </a>
                        </li>
                    </ul>

                    <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-white-50">
                        <span>Actions rapides</span>
                    </h6>
                    <ul class="nav flex-column mb-2">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/sales/add">
                                <i class="fas fa-plus-circle"></i> Nouvelle vente
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/products/add">
                                <i class="fas fa-plus-circle"></i> Nouveau produit
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/batches/add">
                                <i class="fas fa-plus-circle"></i> Nouveau lot
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>

            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 main-content">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">${pageTitle}</h1>
                </div>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${sessionScope.errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${sessionScope.successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

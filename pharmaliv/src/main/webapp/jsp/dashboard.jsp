<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="includes/header.jsp" />

<div class="row fade-in">
    <!-- Statistics Cards -->
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon">
                <i class="fas fa-pills"></i>
            </div>
            <div>
                <div class="stat-value">${totalProducts}</div>
                <div class="stat-label">Produits</div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon" style="background-color: rgba(211, 47, 47, 0.2); color: var(--danger);">
                <i class="fas fa-exclamation-triangle"></i>
            </div>
            <div>
                <div class="stat-value" style="color: var(--danger);">${productsBelowThreshold}</div>
                <div class="stat-label">Produits en alerte</div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon" style="background-color: rgba(30, 136, 229, 0.2); color: var(--secondary);">
                <i class="fas fa-shopping-cart"></i>
            </div>
            <div>
                <div class="stat-value" style="color: var(--secondary);"><fmt:formatNumber value="${todaySales}" type="currency" currencySymbol="€" /></div>
                <div class="stat-label">Ventes du jour</div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon" style="background-color: rgba(56, 142, 60, 0.2); color: var(--success);">
                <i class="fas fa-chart-line"></i>
            </div>
            <div>
                <div class="stat-value" style="color: var(--success);">${bestSellingProducts.size()}</div>
                <div class="stat-label">Meilleures ventes</div>
            </div>
        </div>
    </div>
</div>

<div class="row mt-4 slide-in-up" style="animation-delay: 0.2s;">
    <!-- Additional Statistics Cards -->
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon">
                <i class="fas fa-boxes"></i>
            </div>
            <div>
                <div class="stat-value">${totalBatches}</div>
                <div class="stat-label">Lots</div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon" style="background-color: rgba(255, 160, 0, 0.2); color: var(--warning);">
                <i class="fas fa-calendar-times"></i>
            </div>
            <div>
                <div class="stat-value" style="color: var(--warning);">${expiringSoonBatches}</div>
                <div class="stat-label">Lots expirant bientôt</div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon" style="background-color: rgba(30, 136, 229, 0.2); color: var(--secondary);">
                <i class="fas fa-users"></i>
            </div>
            <div>
                <div class="stat-value" style="color: var(--secondary);">${totalCustomers}</div>
                <div class="stat-label">Clients</div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="stat-card">
            <div class="icon" style="background-color: rgba(56, 142, 60, 0.2); color: var(--success);">
                <i class="fas fa-money-bill-wave"></i>
            </div>
            <div>
                <div class="stat-value" style="color: var(--success);"><fmt:formatNumber value="${monthSales}" type="currency" currencySymbol="€" /></div>
                <div class="stat-label">Ventes du mois</div>
            </div>
        </div>
    </div>
</div>

<div class="row mt-4 fade-in" style="animation-delay: 0.4s;">
    <!-- Sales Chart -->
    <div class="col-md-6">
        <div class="card card-dashboard">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0"><i class="fas fa-chart-line me-2 text-primary"></i>Évolution des ventes</h5>
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-outline-primary active" id="salesWeekBtn">7 jours</button>
                    <button type="button" class="btn btn-outline-primary" id="salesMonthBtn">30 jours</button>
                </div>
            </div>
            <div class="card-body">
                <canvas id="salesChart" height="250"></canvas>
            </div>
        </div>
    </div>

    <!-- Product Status Chart -->
    <div class="col-md-6">
        <div class="card card-dashboard">
            <div class="card-header">
                <h5 class="card-title mb-0"><i class="fas fa-chart-pie me-2 text-primary"></i>Statut des produits</h5>
            </div>
            <div class="card-body">
                <canvas id="productStatusChart" height="250"></canvas>
            </div>
        </div>
    </div>
</div>

<div class="row mt-4 fade-in" style="animation-delay: 0.6s;">
    <!-- Batch Expiry Chart -->
    <div class="col-md-6">
        <div class="card card-dashboard">
            <div class="card-header">
                <h5 class="card-title mb-0"><i class="fas fa-calendar-times me-2 text-warning"></i>Expiration des lots</h5>
            </div>
            <div class="card-body">
                <canvas id="batchExpiryChart" height="250"></canvas>
            </div>
        </div>
    </div>

    <!-- Top Products Chart -->
    <div class="col-md-6">
        <div class="card card-dashboard">
            <div class="card-header">
                <h5 class="card-title mb-0"><i class="fas fa-trophy me-2 text-success"></i>Top 5 des produits vendus</h5>
            </div>
            <div class="card-body">
                <canvas id="topProductsChart" height="250"></canvas>
            </div>
        </div>
    </div>
</div>

<div class="row mt-4 fade-in" style="animation-delay: 0.8s;">
    <!-- Recent Alerts -->
    <div class="col-md-6">
        <div class="card card-dashboard">
            <div class="card-header">
                <h5 class="card-title mb-0">
                    <i class="fas fa-bell me-2 text-warning"></i>Alertes récentes
                    <c:if test="${not empty unresolvedAlerts and unresolvedAlerts > 0}">
                        <span class="badge bg-danger ms-2">${unresolvedAlerts}</span>
                    </c:if>
                </h5>
            </div>
            <div class="card-body p-0">
                <c:if test="${empty recentAlerts}">
                    <div class="p-3">
                        <p class="text-muted mb-0">Aucune alerte récente.</p>
                    </div>
                </c:if>
                <c:if test="${not empty recentAlerts}">
                    <div class="list-group list-group-flush">
                        <c:forEach items="${recentAlerts}" var="alert">
                            <a href="${pageContext.request.contextPath}/alerts/view/${alert.id}" class="list-group-item list-group-item-action">
                                <div class="d-flex align-items-center">
                                    <div class="notification-icon me-3 ${alert.type}">
                                        <c:if test="${alert.type eq 'stock'}">
                                            <i class="fas fa-exclamation-triangle"></i>
                                        </c:if>
                                        <c:if test="${alert.type eq 'expiry'}">
                                            <i class="fas fa-calendar-times"></i>
                                        </c:if>
                                    </div>
                                    <div class="flex-grow-1">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h6 class="mb-1">
                                                <c:if test="${alert.type eq 'stock'}">
                                                    <span class="badge bg-danger">Stock</span>
                                                </c:if>
                                                <c:if test="${alert.type eq 'expiry'}">
                                                    <span class="badge bg-warning">Péremption</span>
                                                </c:if>
                                                ${alert.product.name}
                                            </h6>
                                            <small class="text-muted"><fmt:formatDate value="${alert.createdAt}" pattern="dd/MM/yyyy HH:mm" /></small>
                                        </div>
                                        <p class="mb-1 small">${alert.message}</p>
                                        <small>
                                            <c:if test="${alert.resolved}">
                                                <span class="text-success"><i class="fas fa-check-circle"></i> Résolu</span>
                                            </c:if>
                                            <c:if test="${not alert.resolved}">
                                                <span class="text-danger"><i class="fas fa-exclamation-circle"></i> Non résolu</span>
                                            </c:if>
                                        </small>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                    <div class="card-footer text-center">
                        <a href="${pageContext.request.contextPath}/alerts" class="btn btn-sm btn-outline-primary">Voir toutes les alertes</a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Best Selling Products -->
    <div class="col-md-6">
        <div class="card card-dashboard">
            <div class="card-header">
                <h5 class="card-title mb-0">
                    <i class="fas fa-star me-2 text-warning"></i>Produits les plus vendus
                </h5>
            </div>
            <div class="card-body p-0">
                <c:if test="${empty bestSellingProducts}">
                    <div class="p-3">
                        <p class="text-muted mb-0">Aucune donnée disponible.</p>
                    </div>
                </c:if>
                <c:if test="${not empty bestSellingProducts}">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Produit</th>
                                    <th>Code</th>
                                    <th>Prix</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${bestSellingProducts}" var="product">
                                    <tr>
                                        <td>
                                            <strong>${product.name}</strong>
                                            <c:if test="${productDAO.getTotalStockQuantity(product.id) < product.thresholdQuantity}">
                                                <span class="badge bg-danger ms-1">Stock bas</span>
                                            </c:if>
                                        </td>
                                        <td><span class="badge bg-secondary">${product.code}</span></td>
                                        <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="€" /></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/products/view/${product.id}" class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="card-footer text-center">
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-sm btn-outline-primary">Voir tous les produits</a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- Chart.js Scripts -->
<script>
    // Chart.js Global Configuration
    Chart.defaults.font.family = "'Poppins', 'Roboto', sans-serif";
    Chart.defaults.color = '#757575';
    Chart.defaults.plugins.tooltip.backgroundColor = 'rgba(46, 125, 50, 0.8)';
    Chart.defaults.plugins.tooltip.titleColor = '#FFFFFF';
    Chart.defaults.plugins.tooltip.bodyColor = '#FFFFFF';
    Chart.defaults.plugins.tooltip.padding = 10;
    Chart.defaults.plugins.tooltip.cornerRadius = 6;
    Chart.defaults.plugins.tooltip.displayColors = false;

    // Color Variables
    const primaryColor = '#2E7D32';
    const primaryLightColor = '#A5D6A7';
    const secondaryColor = '#1E88E5';
    const dangerColor = '#D32F2F';
    const warningColor = '#FFA000';
    const successColor = '#388E3C';

    // Sales Chart
    const salesCtx = document.getElementById('salesChart').getContext('2d');

    // Format dates for better display
    function formatDate(dateStr) {
        const date = new Date(dateStr);
        return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });
    }

    // Weekly sales data
    const weeklySalesData = {
        labels: [
            <c:forEach items="${salesData}" var="entry" varStatus="status">
                formatDate('${entry.key}')<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ],
        datasets: [{
            label: 'Ventes (€)',
            data: [
                <c:forEach items="${salesData}" var="entry" varStatus="status">
                    ${entry.value}<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ],
            backgroundColor: 'rgba(30, 136, 229, 0.2)',
            borderColor: secondaryColor,
            borderWidth: 2,
            tension: 0.4,
            pointBackgroundColor: secondaryColor,
            pointBorderColor: '#FFFFFF',
            pointBorderWidth: 2,
            pointRadius: 4,
            pointHoverRadius: 6,
            fill: true
        }]
    };

    // Monthly sales data
    const monthlySalesData = {
        labels: [
            <c:forEach items="${monthlySalesData}" var="entry" varStatus="status">
                formatDate('${entry.key}')<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ],
        datasets: [{
            label: 'Ventes (€)',
            data: [
                <c:forEach items="${monthlySalesData}" var="entry" varStatus="status">
                    ${entry.value}<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ],
            backgroundColor: 'rgba(30, 136, 229, 0.2)',
            borderColor: secondaryColor,
            borderWidth: 2,
            tension: 0.4,
            pointBackgroundColor: secondaryColor,
            pointBorderColor: '#FFFFFF',
            pointBorderWidth: 2,
            pointRadius: 4,
            pointHoverRadius: 6,
            fill: true
        }]
    };

    const salesChartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            y: {
                beginAtZero: true,
                grid: {
                    color: 'rgba(0, 0, 0, 0.05)'
                },
                ticks: {
                    callback: function(value) {
                        return value + ' €';
                    },
                    font: {
                        size: 11
                    }
                }
            },
            x: {
                grid: {
                    display: false
                },
                ticks: {
                    font: {
                        size: 11
                    }
                }
            }
        },
        plugins: {
            tooltip: {
                callbacks: {
                    label: function(context) {
                        return context.parsed.y + ' €';
                    }
                }
            },
            legend: {
                display: false
            }
        }
    };

    const salesChart = new Chart(salesCtx, {
        type: 'line',
        data: weeklySalesData,
        options: salesChartOptions
    });

    // Toggle between weekly and monthly sales data
    document.getElementById('salesWeekBtn').addEventListener('click', function() {
        this.classList.add('active');
        document.getElementById('salesMonthBtn').classList.remove('active');
        salesChart.data = weeklySalesData;
        salesChart.update();
    });

    document.getElementById('salesMonthBtn').addEventListener('click', function() {
        this.classList.add('active');
        document.getElementById('salesWeekBtn').classList.remove('active');
        salesChart.data = monthlySalesData;
        salesChart.update();
    });

    // Product Status Chart
    const productStatusCtx = document.getElementById('productStatusChart').getContext('2d');
    const productStatusChart = new Chart(productStatusCtx, {
        type: 'doughnut',
        data: {
            labels: ['Stock normal', 'Stock bas', 'Péremption proche'],
            datasets: [{
                data: [${normal}, ${belowThreshold}, ${expiringSoon}],
                backgroundColor: [
                    successColor + 'CC',  // Green with transparency
                    warningColor + 'CC',  // Warning with transparency
                    dangerColor + 'CC'    // Danger with transparency
                ],
                borderColor: [
                    successColor,
                    warningColor,
                    dangerColor
                ],
                borderWidth: 2,
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        usePointStyle: true,
                        pointStyle: 'circle'
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((acc, data) => acc + data, 0);
                            const percentage = Math.round((value / total) * 100);
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });

    // Batch Expiry Chart
    const batchExpiryCtx = document.getElementById('batchExpiryChart').getContext('2d');
    const batchExpiryChart = new Chart(batchExpiryCtx, {
        type: 'bar',
        data: {
            labels: ['Ce mois', '1-3 mois', '3-6 mois', '6-12 mois', '> 12 mois'],
            datasets: [{
                label: 'Nombre de lots',
                data: [${expiryThisMonth}, ${expiryOneToThreeMonths}, ${expiryThreeToSixMonths}, ${expirySixToTwelveMonths}, ${expiryMoreThanYear}],
                backgroundColor: [
                    dangerColor + 'CC',     // Red with transparency
                    warningColor + 'CC',     // Warning with transparency
                    '#FF9800' + 'CC',        // Orange with transparency
                    secondaryColor + 'CC',   // Secondary with transparency
                    successColor + 'CC'      // Success with transparency
                ],
                borderColor: [
                    dangerColor,
                    warningColor,
                    '#FF9800',
                    secondaryColor,
                    successColor
                ],
                borderWidth: 2,
                borderRadius: 6,
                maxBarThickness: 40
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        precision: 0,
                        font: {
                            size: 11
                        }
                    }
                },
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        font: {
                            size: 11
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return context.parsed.y + ' lot(s)';
                        }
                    }
                }
            }
        }
    });

    // Top Products Chart
    const topProductsCtx = document.getElementById('topProductsChart').getContext('2d');
    const topProductsChart = new Chart(topProductsCtx, {
        type: 'bar',
        data: {
            labels: [
                <c:forEach items="${topProducts}" var="product" varStatus="status">
                    '${product.name}'<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ],
            datasets: [{
                label: 'Ventes',
                data: [
                    <c:forEach items="${topProductsSales}" var="sales" varStatus="status">
                        ${sales}<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ],
                backgroundColor: primaryColor + 'CC',
                borderColor: primaryColor,
                borderWidth: 2,
                borderRadius: 6,
                maxBarThickness: 30
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        precision: 0,
                        font: {
                            size: 11
                        }
                    }
                },
                y: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        font: {
                            size: 11
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return context.parsed.x + ' vente(s)';
                        }
                    }
                }
            }
        }
    });
</script>

<jsp:include page="includes/footer.jsp" />

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
        </main>
    </div>
</div>

<!-- Bootstrap JS Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Custom JavaScript for sidebar toggle -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Toggle sidebar on mobile
        const navbarToggler = document.querySelector('.navbar-toggler');
        const sidebar = document.getElementById('sidebarMenu');

        if (navbarToggler && sidebar) {
            navbarToggler.addEventListener('click', function() {
                sidebar.classList.toggle('show');
            });

            // Close sidebar when clicking outside on mobile
            document.addEventListener('click', function(event) {
                const isClickInsideNavbar = navbarToggler.contains(event.target);
                const isClickInsideSidebar = sidebar.contains(event.target);

                if (!isClickInsideNavbar && !isClickInsideSidebar && sidebar.classList.contains('show')) {
                    sidebar.classList.remove('show');
                }
            });
        }
    });
</script>

</body>
</html>

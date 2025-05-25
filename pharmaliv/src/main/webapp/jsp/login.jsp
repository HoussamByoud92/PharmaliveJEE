<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PHARMALIVE - Connexion</title>
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
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/css/pharmalive.css" rel="stylesheet">
    <style>
        body {
            background-color: var(--gray-light);
            overflow-x: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            background: 
                radial-gradient(circle at 10% 20%, rgba(216, 241, 230, 0.2) 0%, rgba(233, 226, 226, 0.2) 90.1%),
                linear-gradient(135deg, var(--primary-light) 0%, var(--primary) 100%);
            position: relative;
        }

        body::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, rgba(46, 125, 50, 0.9) 0%, rgba(56, 142, 60, 0.8) 100%);
            z-index: -1;
        }

        .login-container {
            max-width: 420px;
            width: 100%;
            padding: 35px;
            background-color: var(--white);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
            opacity: 0;
            transform: translateY(30px);
            animation: fadeInUp 0.8s ease forwards;
            position: relative;
            overflow: hidden;
        }

        .login-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
            background: linear-gradient(to bottom, var(--primary), var(--secondary));
        }

        @keyframes fadeInUp {
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .logo {
            text-align: center;
            margin-bottom: 30px;
            position: relative;
        }

        .logo::after {
            content: '';
            position: absolute;
            bottom: -10px;
            left: 50%;
            transform: translateX(-50%);
            width: 60px;
            height: 3px;
            background: linear-gradient(to right, var(--primary), var(--secondary));
            border-radius: 3px;
        }

        .logo img {
            width: 110px;
            height: 110px;
            margin-bottom: 15px;
            animation: pulse 2s infinite;
            filter: drop-shadow(0 5px 15px rgba(0, 0, 0, 0.1));
        }

        @keyframes pulse {
            0% {
                transform: scale(1);
            }
            50% {
                transform: scale(1.05);
            }
            100% {
                transform: scale(1);
            }
        }

        .logo h1 {
            color: var(--primary);
            font-weight: 700;
            margin-bottom: 5px;
            letter-spacing: 1px;
            animation: fadeIn 1s ease forwards;
            animation-delay: 0.3s;
            opacity: 0;
        }

        .logo span {
            color: var(--secondary);
            font-weight: 800;
        }

        .logo p {
            color: var(--gray-dark);
            animation: fadeIn 1s ease forwards;
            animation-delay: 0.5s;
            opacity: 0;
        }

        .error-message {
            color: var(--danger);
            margin-bottom: 15px;
            animation: shake 0.5s ease-in-out;
            padding: 10px;
            border-radius: 5px;
            background-color: rgba(211, 47, 47, 0.1);
            border-left: 3px solid var(--danger);
        }

        @keyframes shake {
            0%, 100% {transform: translateX(0);}
            10%, 30%, 50%, 70%, 90% {transform: translateX(-5px);}
            20%, 40%, 60%, 80% {transform: translateX(5px);}
        }

        .form-label {
            color: var(--primary);
            font-weight: 500;
            margin-bottom: 8px;
            display: block;
            animation: fadeIn 0.5s ease forwards;
            animation-delay: 0.6s;
            opacity: 0;
        }

        .form-control {
            transition: all 0.3s ease;
            border: 1px solid var(--gray);
            padding: 14px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 1rem;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            animation: fadeIn 0.5s ease forwards;
            animation-delay: 0.7s;
            opacity: 0;
        }

        .form-control:focus {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(46, 125, 50, 0.15);
            border-color: var(--primary-light);
        }

        .btn-primary {
            background-color: var(--primary);
            border-color: var(--primary);
            padding: 14px;
            font-weight: 600;
            font-size: 1.1rem;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            z-index: 1;
            animation: fadeIn 0.5s ease forwards;
            animation-delay: 0.8s;
            opacity: 0;
            border-radius: 8px;
        }

        .btn-primary:hover {
            background-color: var(--success);
            border-color: var(--success);
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(46, 125, 50, 0.25);
        }

        .btn-primary::after {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
            transition: all 0.5s ease;
            z-index: -1;
        }

        .btn-primary:hover::after {
            left: 100%;
        }

        @keyframes fadeIn {
            to {
                opacity: 1;
            }
        }

        /* Floating pills animation in background */
        .pills-animation {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
            z-index: -1;
        }

        .pill {
            position: absolute;
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 50px;
            animation: float 15s infinite linear;
        }

        .pill:nth-child(1) {
            width: 60px;
            height: 20px;
            top: 10%;
            left: 10%;
            animation-duration: 20s;
            animation-delay: 0s;
            transform: rotate(45deg);
        }

        .pill:nth-child(2) {
            width: 80px;
            height: 25px;
            top: 70%;
            left: 80%;
            animation-duration: 25s;
            animation-delay: 2s;
            transform: rotate(-30deg);
        }

        .pill:nth-child(3) {
            width: 40px;
            height: 15px;
            top: 40%;
            left: 60%;
            animation-duration: 18s;
            animation-delay: 5s;
            transform: rotate(60deg);
        }

        .pill:nth-child(4) {
            width: 70px;
            height: 20px;
            top: 80%;
            left: 20%;
            animation-duration: 22s;
            animation-delay: 8s;
            transform: rotate(20deg);
        }

        .pill:nth-child(5) {
            width: 50px;
            height: 18px;
            top: 20%;
            left: 85%;
            animation-duration: 24s;
            animation-delay: 10s;
            transform: rotate(-60deg);
        }

        @keyframes float {
            0% {
                transform: translateY(0) rotate(0deg);
                opacity: 0;
            }
            10% {
                opacity: 1;
            }
            90% {
                opacity: 1;
            }
            100% {
                transform: translateY(-1000px) rotate(360deg);
                opacity: 0;
            }
        }
    </style>
</head>
<body>
    <div class="pills-animation">
        <div class="pill"></div>
        <div class="pill"></div>
        <div class="pill"></div>
        <div class="pill"></div>
        <div class="pill"></div>
    </div>
    <div class="container">
        <div class="login-container">
            <div class="logo">
                <img src="${pageContext.request.contextPath}/img/logo.svg" alt="PharmaLive Logo">
                <h1>PHARMA<span>LIVE</span></h1>
                <p class="text-muted">Plateforme de gestion de pharmacie</p>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger error-message">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label">Nom d'utilisateur</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label">Mot de passe</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">Se connecter</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

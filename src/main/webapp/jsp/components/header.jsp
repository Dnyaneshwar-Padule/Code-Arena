<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${not empty pageTitle ? pageTitle : 'CP Portal | Home'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .hero-animate {
            animation: fadeSlideUp 0.9s ease-out both;
        }

        .feature-card {
            transition: transform 0.25s ease, box-shadow 0.25s ease;
        }

        .feature-card:hover {
            transform: translateY(-6px) scale(1.01);
            box-shadow: 0 1rem 2rem rgba(0, 0, 0, 0.08);
        }

        .cta-btn {
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .cta-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 0.5rem 1rem rgba(13, 110, 253, 0.2);
        }

        @media (max-width: 991.98px) {
            .navbar-collapse.collapse:not(.show) {
                display: none !important;
            }

            #mainNavbar {
                background-color: #ffffff;
                padding-top: 0.75rem;
                padding-bottom: 0.5rem;
            }

            #mainNavbar .nav-link {
                color: #212529 !important;
            }

            #mainNavbar .nav-link.active {
                color: #0d6efd !important;
            }

            #mainNavbar .auth-actions {
                margin-top: 0.75rem;
            }
        }

        @keyframes fadeSlideUp {
            from {
                opacity: 0;
                transform: translateY(24px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body class="bg-light text-dark d-flex flex-column min-vh-100">
<header>
    <nav class="navbar navbar-expand-lg bg-white border-bottom sticky-top shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/index.jsp">CP Portal</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar"
                    aria-controls="mainNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="mainNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/problems">Problems</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/contests">Contests</a></li>
                    <li class="nav-item"><a class="nav-link" href="#">Leaderboard</a></li>
                    <c:if test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role == 'ADMIN'}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/problems">Admin</a>
                        </li>
                    </c:if>
                </ul>

                <div class="d-flex gap-2 auth-actions">
                    <c:choose>
                        <c:when test="${not empty sessionScope.loggedInUser}">
                            <span class="align-self-center text-secondary small">
                                Hi, ${sessionScope.loggedInUser.username}
                            </span>
                            <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/logout">Logout</a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/login">Login</a>
                            <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/register">Register</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>
</header>

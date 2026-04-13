<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CP Portal | Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
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
    <nav class="navbar navbar-expand-lg bg-white border-bottom sticky-top">
        <div class="container">
            <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/jsp/index.jsp">CP Portal</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar"
                    aria-controls="mainNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="mainNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/jsp/index.jsp">Home</a></li>
                    <li class="nav-item"><a class="nav-link" href="#">Problems</a></li>
                    <li class="nav-item"><a class="nav-link" href="#">Contests</a></li>
                    <li class="nav-item"><a class="nav-link" href="#">Leaderboard</a></li>
                </ul>

                <div class="d-flex gap-2">
                    <a class="btn btn-outline-primary btn-sm" href="#">Login</a>
                    <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/register">Register</a>
                </div>
            </div>
        </div>
    </nav>
</header>

<main class="flex-grow-1">
    <section class="py-5 py-lg-6">
        <div class="container hero-animate">
            <div class="row justify-content-center text-center">
                <div class="col-lg-8">
                    <h1 class="display-5 fw-bold mb-3">Sharpen Your Coding Skills</h1>
                    <p class="lead text-secondary mb-4">
                        Practice problems, compete in contests, and improve your ranking
                    </p>
                    <div class="d-flex flex-column flex-sm-row gap-2 justify-content-center">
                        <a class="btn btn-primary btn-lg px-4 cta-btn" href="#">Start Practicing</a>
                        <a class="btn btn-outline-secondary btn-lg px-4 cta-btn" href="#">View Contests</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="pb-5">
        <div class="container">
            <div class="row g-4">
                <div class="col-md-4">
                    <article class="card border-0 h-100 feature-card">
                        <div class="card-body p-4">
                            <i class="bi bi-code-slash fs-2 text-primary"></i>
                            <h3 class="h5 mt-3 mb-2">Practice Problems</h3>
                            <p class="text-secondary mb-0">
                                Solve curated challenges across multiple difficulty levels.
                            </p>
                        </div>
                    </article>
                </div>
                <div class="col-md-4">
                    <article class="card border-0 h-100 feature-card">
                        <div class="card-body p-4">
                            <i class="bi bi-trophy fs-2 text-primary"></i>
                            <h3 class="h5 mt-3 mb-2">Compete in Contests</h3>
                            <p class="text-secondary mb-0">
                                Join timed contests and test your speed and accuracy.
                            </p>
                        </div>
                    </article>
                </div>
                <div class="col-md-4">
                    <article class="card border-0 h-100 feature-card">
                        <div class="card-body p-4">
                            <i class="bi bi-graph-up-arrow fs-2 text-primary"></i>
                            <h3 class="h5 mt-3 mb-2">Track Progress</h3>
                            <p class="text-secondary mb-0">
                                Monitor solved problems, ratings, and performance trends.
                            </p>
                        </div>
                    </article>
                </div>
            </div>
        </div>
    </section>
</main>

<footer class="bg-white border-top py-3 mt-auto">
    <div class="container d-flex flex-column flex-md-row justify-content-between align-items-center gap-2">
        <p class="mb-0 text-secondary small">&copy; 2026 CP Portal. All rights reserved.</p>
        <div class="d-flex gap-3">
            <a class="link-secondary text-decoration-none small" href="#">About</a>
            <a class="link-secondary text-decoration-none small" href="#">Contact</a>
        </div>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
</body>
</html>

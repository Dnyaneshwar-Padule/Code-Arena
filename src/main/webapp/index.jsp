<%@ include file="jsp/components/header.jsp" %>

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
                        <a class="btn btn-primary btn-lg px-4 cta-btn" href="${pageContext.request.contextPath}/problems">Start Practicing</a>
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

<%@ include file="jsp/components/footer.jsp" %>

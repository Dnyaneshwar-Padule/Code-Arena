<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Contest Detail | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <a class="btn btn-outline-secondary btn-sm mb-4" href="${pageContext.request.contextPath}/contests">Back to Contests</a>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:if test="${not empty contest}">
            <article class="card border-0 shadow-sm">
                <div class="card-body">
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-3">
                        <h1 class="h3 mb-0">${contest.title}</h1>
                        <a class="btn btn-outline-primary btn-sm"
                           href="${pageContext.request.contextPath}/contest/leaderboard?contestId=${contest.id}">
                            Leaderboard
                        </a>
                    </div>
                    <p class="text-secondary">${contest.description}</p>
                    <div class="d-flex flex-wrap gap-3 mb-4">
                        <span class="badge text-bg-secondary">State: ${contestState}</span>
                        <span class="badge text-bg-dark">Time Remaining: <span id="contestTimer" data-seconds="${remainingSeconds}">${remainingSeconds}</span>s</span>
                    </div>

                    <h2 class="h5 mb-3">Problems</h2>
                    <c:choose>
                        <c:when test="${empty contestProblems}">
                            <div class="alert alert-secondary mb-0">No problems configured for this contest.</div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-striped align-middle">
                                    <thead class="table-light">
                                    <tr>
                                        <th>#</th>
                                        <th>Title</th>
                                        <th>Difficulty</th>
                                        <th>Points</th>
                                        <th class="text-end">Action</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="contestProblem" items="${contestProblems}">
                                        <tr>
                                            <td>${contestProblem.order}</td>
                                            <td>${contestProblem.problem.title}</td>
                                            <td>${contestProblem.problem.difficulty}</td>
                                            <td>${contestProblem.points}</td>
                                            <td class="text-end">
                                                <a class="btn btn-sm btn-primary"
                                                   href="${pageContext.request.contextPath}/problem?id=${contestProblem.problem.id}&contestId=${contest.id}">
                                                    Solve
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </article>
        </c:if>
    </section>
</main>

<script>
    (function () {
        const timer = document.getElementById('contestTimer');
        if (!timer) {
            return;
        }
        let remaining = Number(timer.dataset.seconds || '0');
        timer.textContent = remaining;
        if (remaining <= 0) {
            return;
        }
        window.setInterval(function () {
            remaining = Math.max(0, remaining - 1);
            timer.textContent = remaining;
        }, 1000);
    })();
</script>

<%@ include file="components/footer.jsp" %>

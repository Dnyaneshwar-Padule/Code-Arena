<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Contest Leaderboard | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <a class="btn btn-outline-secondary btn-sm mb-4"
           href="${pageContext.request.contextPath}/contest?id=${contest.id}">Back to Contest</a>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h3 mb-0">${contest.title} - Leaderboard</h1>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty leaderboardEntries}">
                <div class="alert alert-secondary mb-0">No scores yet.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead class="table-light">
                        <tr>
                            <th>Rank</th>
                            <th>Username</th>
                            <th>Score</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="entry" items="${leaderboardEntries}">
                            <tr>
                                <td>${entry.rank}</td>
                                <td>${entry.username}</td>
                                <td>${entry.score}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="components/footer.jsp" %>

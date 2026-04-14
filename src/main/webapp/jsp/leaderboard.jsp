<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Leaderboard | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-4">
            <div>
                <h1 class="h3 fw-bold mb-1">Practice Leaderboard</h1>
                <p class="text-secondary mb-0">Top users by solved practice points.</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty currentUserEntry and empty leaderboardEntries}">
                <div class="alert alert-secondary mb-0">No leaderboard data available.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead class="table-light">
                        <tr>
                            <th>Rank</th>
                            <th>User</th>
                            <th>Solved</th>
                            <th>Points</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${not empty currentUserEntry}">
                            <tr class="table-primary">
                                <td>${currentUserEntry.rank}</td>
                                <td>You (${currentUserEntry.username})</td>
                                <td>${currentUserEntry.solvedCount}</td>
                                <td>${currentUserEntry.points}</td>
                            </tr>
                        </c:if>
                        <c:forEach var="entry" items="${leaderboardEntries}">
                            <tr>
                                <td>${entry.rank}</td>
                                <td>${entry.username}</td>
                                <td>${entry.solvedCount}</td>
                                <td>${entry.points}</td>
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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Leaderboard | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-4">
            <div>
                <h1 class="h3 fw-bold mb-1">Leaderboard</h1>
                <p class="text-secondary mb-0">Top rated users on CP Portal.</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty leaderboardUsers}">
                <div class="alert alert-secondary mb-0">No leaderboard data available.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead class="table-light">
                        <tr>
                            <th>Rank</th>
                            <th>User</th>
                            <th>Rating</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="user" items="${leaderboardUsers}" varStatus="status">
                            <tr class="${currentUserId == user.id ? 'table-primary' : ''}">
                                <td>${status.index + 1}</td>
                                <td>
                                    ${user.username}
                                    <c:if test="${currentUserId == user.id}">
                                        <span class="badge text-bg-primary ms-2">You</span>
                                    </c:if>
                                </td>
                                <td>${user.rating}</td>
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

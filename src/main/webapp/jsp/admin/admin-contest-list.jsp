<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Contests | CP Portal" scope="request"/>
<%@ include file="../components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-2">
            <div>
                <h1 class="h3 fw-bold mb-1">Manage Contests</h1>
                <p class="text-secondary mb-0">Create and manage contests and their problems.</p>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/contest/create">Create Contest</a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty contests}">
                <div class="alert alert-secondary mb-0">No contests available yet.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead class="table-light">
                        <tr>
                            <th>Title</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th class="text-end">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="contest" items="${contests}">
                            <tr>
                                <td>${contest.title}</td>
                                <td>${contest.startTime}</td>
                                <td>${contest.endTime}</td>
                                <td class="text-end">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/admin/contest/edit?id=${contest.id}">Edit</a>
                                    <a class="btn btn-sm btn-outline-secondary"
                                       href="${pageContext.request.contextPath}/admin/contest/problems?contestId=${contest.id}">Add Problems</a>
                                    <a class="btn btn-sm btn-outline-danger"
                                       href="${pageContext.request.contextPath}/admin/contest/delete?id=${contest.id}"
                                       onclick="return confirm('Delete this contest and all linked contest problems?');">
                                        Delete
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="../components/footer.jsp" %>

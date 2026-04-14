<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Contests | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-4">
            <h1 class="h3 fw-bold mb-0">Contests</h1>
            <c:if test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role == 'ADMIN'}">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/contest/create">+ Create Contest</a>
            </c:if>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty contests}">
                <div class="alert alert-secondary mb-0">No contests available.</div>
            </c:when>
            <c:otherwise>
                <div class="row g-3">
                    <c:forEach var="contest" items="${contests}">
                        <div class="col-12">
                            <div class="card border-0 shadow-sm">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start gap-3">
                                        <div>
                                            <h2 class="h5 mb-1">${contest.title}</h2>
                                            <p class="text-secondary mb-2">${contest.description}</p>
                                            <div class="small text-secondary">
                                                <span>Start: ${contest.startTime}</span>
                                                <span class="mx-2">|</span>
                                                <span>End: ${contest.endTime}</span>
                                            </div>
                                        </div>
                                        <div class="d-flex flex-wrap gap-2 justify-content-end">
                                            <c:choose>
                                                <c:when test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role == 'ADMIN'}">
                                                    <a class="btn btn-sm btn-outline-primary"
                                                       href="${pageContext.request.contextPath}/admin/contest/edit?id=${contest.id}">Edit</a>
                                                    <a class="btn btn-sm btn-outline-secondary"
                                                       href="${pageContext.request.contextPath}/admin/contest/problems?contestId=${contest.id}">Add Problems</a>
                                                    <a class="btn btn-sm btn-outline-danger"
                                                       href="${pageContext.request.contextPath}/admin/contest/delete?id=${contest.id}"
                                                       onclick="return confirm('Delete this contest and all linked contest problems?');">Delete</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="btn btn-primary btn-sm"
                                                       href="${pageContext.request.contextPath}/contest?id=${contest.id}">
                                                        View Contest
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="components/footer.jsp" %>

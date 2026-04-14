<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Problems | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="d-flex flex-column flex-md-row align-items-md-center justify-content-between mb-4">
            <div>
                <h1 class="h2 fw-bold mb-1">Problem Set</h1>
                <p class="text-secondary mb-0">Practice curated coding challenges by difficulty.</p>
            </div>
            <span class="badge text-bg-primary fs-6 mt-3 mt-md-0">${empty totalCount ? 0 : totalCount} Problems</span>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty problems}">
                <div class="card border-0 shadow-sm">
                    <div class="card-body py-5 text-center">
                        <h2 class="h5">No problems available yet.</h2>
                        <p class="text-secondary mb-0">Please check back later.</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4">
                    <c:forEach var="problem" items="${problems}">
                        <div class="col-12 col-md-4">
                            <c:set var="showAdminActions" value="${false}" />
                            <%@ include file="components/problem-card.jsp" %>
                        </div>
                    </c:forEach>
                </div>

                <c:if test="${totalPages > 1}">
                    <nav aria-label="Problem pagination" class="mt-4">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/problems?page=${currentPage - 1}&size=${size}"
                                   aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>

                            <c:forEach var="pageNumber" begin="1" end="${totalPages}">
                                <li class="page-item ${pageNumber == currentPage ? 'active' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/problems?page=${pageNumber}&size=${size}">
                                            ${pageNumber}
                                    </a>
                                </li>
                            </c:forEach>

                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/problems?page=${currentPage + 1}&size=${size}"
                                   aria-label="Next">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="components/footer.jsp" %>

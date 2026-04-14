<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Contests | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 fw-bold mb-0">Contests</h1>
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
                                        <a class="btn btn-primary btn-sm"
                                           href="${pageContext.request.contextPath}/contest?id=${contest.id}">
                                            View Contest
                                        </a>
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

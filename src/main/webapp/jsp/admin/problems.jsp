<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Problems | CP Portal" scope="request"/>
<%@ include file="../components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="d-flex flex-column flex-md-row align-items-md-center justify-content-between mb-4">
            <div>
                <h1 class="h3 fw-bold mb-1">Manage Problems</h1>
                <p class="text-secondary mb-0">Create, edit, and delete problems.</p>
            </div>
            <a class="btn btn-primary mt-3 mt-md-0" href="${pageContext.request.contextPath}/admin/problem/create">Create Problem</a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <div class="row g-3">
            <c:forEach var="problem" items="${problems}">
                <div class="col-12 col-lg-6">
                    <c:set var="showAdminActions" value="${true}" />
                    <%@ include file="../components/problem-card.jsp" %>
                </div>
            </c:forEach>
        </div>
    </section>
</main>

<%@ include file="../components/footer.jsp" %>

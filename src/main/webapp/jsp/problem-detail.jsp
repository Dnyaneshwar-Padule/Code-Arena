<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Problem Detail | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <a class="btn btn-outline-secondary btn-sm mb-4" href="${pageContext.request.contextPath}/problems">Back to Problems</a>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:if test="${not empty problem}">
            <article class="card border-0 shadow-sm">
                <div class="card-body p-4 p-md-5">
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-4">
                        <h1 class="h3 fw-bold mb-0">${problem.title}</h1>
                        <span class="badge text-bg-primary">${problem.difficulty}</span>
                    </div>
                    <p class="text-secondary mb-4">${problem.description}</p>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="border rounded-3 p-3 bg-white h-100">
                                <h2 class="h6 fw-semibold">Time Limit</h2>
                                <p class="mb-0 text-secondary">${problem.timeLimit} ms</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="border rounded-3 p-3 bg-white h-100">
                                <h2 class="h6 fw-semibold">Memory Limit</h2>
                                <p class="mb-0 text-secondary">${problem.memoryLimit} MB</p>
                            </div>
                        </div>
                    </div>

                    <section class="mt-4">
                        <h2 class="h5 fw-semibold mb-3">Sample Test Cases</h2>
                        <c:choose>
                            <c:when test="${empty sampleTestCases}">
                                <p class="text-secondary mb-0">No sample test cases available for this problem.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="row g-3">
                                    <c:forEach var="testCase" items="${sampleTestCases}" varStatus="status">
                                        <div class="col-12">
                                            <div class="border rounded-3 p-3 bg-white">
                                                <h3 class="h6 fw-bold mb-3">Sample ${status.index + 1}</h3>
                                                <div class="mb-3">
                                                    <p class="fw-semibold mb-1">Input</p>
                                                    <pre class="bg-light border rounded p-3 mb-0"><code>${testCase.input}</code></pre>
                                                </div>
                                                <div>
                                                    <p class="fw-semibold mb-1">Expected Output</p>
                                                    <pre class="bg-light border rounded p-3 mb-0"><code>${testCase.expectedOutput}</code></pre>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </div>
            </article>
        </c:if>
    </section>
</main>

<%@ include file="components/footer.jsp" %>

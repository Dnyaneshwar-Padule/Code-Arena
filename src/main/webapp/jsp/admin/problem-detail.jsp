<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Admin Problem Detail | CP Portal" scope="request"/>
<%@ include file="../components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <a class="btn btn-outline-secondary btn-sm mb-4" href="${pageContext.request.contextPath}/admin/problems">Back to Problems</a>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <c:if test="${not empty problem}">
            <article class="card border-0 shadow-sm mb-4">
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
                                <p class="mb-0 text-secondary">${problem.memoryLimit} KB</p>
                            </div>
                        </div>
                    </div>
                </div>
            </article>

            <section class="card border-0 shadow-sm">
                <div class="card-body p-4 p-md-5">
                    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-3">
                        <h2 class="h5 fw-bold mb-0">Test Cases</h2>
                        <a class="btn btn-primary btn-sm"
                           href="${pageContext.request.contextPath}/admin/problem/testcase?problemId=${problem.id}">
                            Add Test Case
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${empty testCases}">
                            <p class="text-secondary mb-0">No test cases added yet.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-striped table-hover align-middle">
                                    <thead class="table-light">
                                    <tr>
                                        <th scope="col">Input</th>
                                        <th scope="col">Expected Output</th>
                                        <th scope="col">Sample</th>
                                        <th scope="col" class="text-end">Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="testCase" items="${testCases}">
                                        <tr>
                                            <td class="text-break">
                                                <c:choose>
                                                    <c:when test="${fn:length(testCase.input) > 80}">
                                                        ${fn:substring(testCase.input, 0, 80)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${testCase.input}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-break">${testCase.expectedOutput}</td>
                                            <td>
                                                <span class="badge ${testCase.sample ? 'text-bg-success' : 'text-bg-secondary'}">
                                                    ${testCase.sample ? 'Yes' : 'No'}
                                                </span>
                                            </td>
                                            <td class="text-end">
                                                <a class="btn btn-sm btn-outline-primary"
                                                   href="${pageContext.request.contextPath}/admin/problem/testcase/edit?id=${testCase.id}">
                                                    Edit
                                                </a>
                                                <a class="btn btn-sm btn-outline-danger"
                                                   href="${pageContext.request.contextPath}/admin/problem/testcase/delete?id=${testCase.id}&problemId=${problem.id}"
                                                   onclick="return confirm('Delete this test case?');">
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
                </div>
            </section>
        </c:if>
    </section>
</main>

<%@ include file="../components/footer.jsp" %>

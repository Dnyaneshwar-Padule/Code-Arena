<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Add Test Case | CP Portal" scope="request"/>
<%@ include file="../components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-lg-8">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4 p-md-5">
                        <h1 class="h4 fw-bold mb-2">Add Test Case</h1>
                        <p class="text-secondary mb-4">
                            Problem: <strong>${problem.title}</strong>
                        </p>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">${error}</div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/admin/problem/testcase">
                            <input type="hidden" name="problemId" value="${problem.id}">

                            <div class="mb-3">
                                <label for="input" class="form-label">Input</label>
                                <textarea id="input" name="input" rows="6" class="form-control" required>${param.input}</textarea>
                                <div class="form-text">Example: first line `n`, second line contains `n` integers.</div>
                            </div>

                            <div class="mb-3">
                                <label for="expectedOutput" class="form-label">Expected Output</label>
                                <textarea id="expectedOutput" name="expectedOutput" rows="4" class="form-control" required>${param.expectedOutput}</textarea>
                                <div class="form-text">Example: one line with the computed answer.</div>
                            </div>

                            <div class="form-check mb-4">
                                <input class="form-check-input" type="checkbox" id="isSample" name="isSample"
                                       ${param.isSample == 'on' ? 'checked' : ''}>
                                <label class="form-check-label" for="isSample">
                                    Mark as sample test case
                                </label>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">Save Test Case</button>
                                <a class="btn btn-outline-secondary"
                                   href="${pageContext.request.contextPath}/admin/problem/edit?id=${problem.id}">
                                    Back to Problem
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<%@ include file="../components/footer.jsp" %>

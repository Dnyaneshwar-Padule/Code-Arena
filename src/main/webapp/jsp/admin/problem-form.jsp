<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Problem Form | CP Portal" scope="request"/>
<%@ include file="../components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-lg-8">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4 p-md-5">
                        <h1 class="h4 fw-bold mb-3">
                            <c:choose>
                                <c:when test="${formMode == 'edit'}">Edit Problem</c:when>
                                <c:otherwise>Create Problem</c:otherwise>
                            </c:choose>
                        </h1>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">${error}</div>
                        </c:if>

                        <form method="post"
                              action="${pageContext.request.contextPath}${formMode == 'edit' ? '/admin/problem/update' : '/admin/problem'}">
                            <c:if test="${formMode == 'edit'}">
                                <input type="hidden" name="id" value="${problem.id}">
                            </c:if>

                            <div class="mb-3">
                                <label for="title" class="form-label">Title</label>
                                <input id="title" name="title" type="text" class="form-control"
                                       value="${problem.title}" required>
                            </div>

                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <textarea id="description" name="description" rows="5" class="form-control" required>${problem.description}</textarea>
                            </div>

                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <label for="inputFormat" class="form-label mb-0">Input Format</label>
                                    <div class="d-flex flex-wrap gap-1">
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="inputFormat" data-template="int n">Add Integer</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="inputFormat" data-template="int[] arr (size n)">Add Array</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="inputFormat" data-template="string s">Add String</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="inputFormat" data-template="1 <= n <= 10^5">Add Range</button>
                                    </div>
                                </div>
                                <textarea id="inputFormat" name="inputFormat" rows="3" class="form-control"
                                          placeholder="First line contains n&#10;Second line contains n integers">${problem.inputFormat}</textarea>
                            </div>

                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <label for="outputFormat" class="form-label mb-0">Output Format</label>
                                    <div class="d-flex flex-wrap gap-1">
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="outputFormat" data-template="int result">Add Integer</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="outputFormat" data-template="int[] ans">Add Array</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="outputFormat" data-template="string answer">Add String</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="outputFormat" data-template="Print one value per line">Add Range</button>
                                    </div>
                                </div>
                                <textarea id="outputFormat" name="outputFormat" rows="3" class="form-control"
                                          placeholder="Print the result">${problem.outputFormat}</textarea>
                            </div>

                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <label for="constraints" class="form-label mb-0">Constraints</label>
                                    <div class="d-flex flex-wrap gap-1">
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="constraints" data-template="1 <= x <= 10^9">Add Integer</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="constraints" data-template="1 <= n <= 10^5">Add Array</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="constraints" data-template="1 <= |s| <= 10^5">Add String</button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm format-helper-btn"
                                                data-target="constraints" data-template="Sum of n over all tests <= 2*10^5">Add Range</button>
                                    </div>
                                </div>
                                <textarea id="constraints" name="constraints" rows="3" class="form-control"
                                          placeholder="1 &lt;= n &lt;= 10^5">${problem.constraints}</textarea>
                            </div>

                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label for="difficulty" class="form-label">Difficulty</label>
                                    <select id="difficulty" name="difficulty" class="form-select" required>
                                        <option value="">Select</option>
                                        <option value="EASY" ${problem.difficulty == 'EASY' ? 'selected' : ''}>EASY</option>
                                        <option value="MEDIUM" ${problem.difficulty == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
                                        <option value="HARD" ${problem.difficulty == 'HARD' ? 'selected' : ''}>HARD</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label for="timeLimit" class="form-label">Time Limit (ms)</label>
                                    <input id="timeLimit" name="timeLimit" type="number" class="form-control"
                                           value="${problem.timeLimit}" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="memoryLimit" class="form-label">Memory Limit (KB)</label>
                                    <input id="memoryLimit" name="memoryLimit" type="number" class="form-control"
                                           value="${problem.memoryLimit}" required>
                                </div>
                            </div>

                            <div class="d-flex gap-2 mt-5">
                                <button type="submit" class="btn btn-primary">Save</button>
                                <c:if test="${formMode == 'edit'}">
                                    <a class="btn btn-outline-secondary"
                                       href="${pageContext.request.contextPath}/admin/problem/testcase?problemId=${problem.id}">
                                        Add Test Case
                                    </a>
                                </c:if>
                                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/problems">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<%@ include file="../components/footer.jsp" %>
<script>
    (function () {
        const helperButtons = document.querySelectorAll('.format-helper-btn');
        helperButtons.forEach(function (button) {
            button.addEventListener('click', function () {
                const targetId = this.getAttribute('data-target');
                const template = this.getAttribute('data-template');
                const target = document.getElementById(targetId);
                if (!target || !template) {
                    return;
                }
                const current = target.value.trim();
                target.value = current ? (current + "\n" + template) : template;
                target.focus();
            });
        });
    })();
</script>

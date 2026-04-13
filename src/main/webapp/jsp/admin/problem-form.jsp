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
                                    <label for="memoryLimit" class="form-label">Memory Limit (MB)</label>
                                    <input id="memoryLimit" name="memoryLimit" type="number" class="form-control"
                                           value="${problem.memoryLimit}" required>
                                </div>
                            </div>

                            <div class="d-flex gap-2 mt-4">
                                <button type="submit" class="btn btn-primary">Save</button>
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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Contest Problems | CP Portal" scope="request"/>
<%@ include file="../components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <a class="btn btn-outline-secondary btn-sm mb-4" href="${pageContext.request.contextPath}/contests">Back to Contests</a>

        <c:if test="${not empty contest}">
            <h1 class="h4 fw-bold mb-1">${contest.title}</h1>
            <p class="text-secondary mb-4">Add and arrange contest problems with custom points.</p>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>

        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body">
                <h2 class="h6 fw-semibold mb-3">Add Problem to Contest</h2>
                <form method="post" action="${pageContext.request.contextPath}/admin/contest/problems/add" class="row g-3">
                    <input type="hidden" name="contestId" value="${contest.id}">
                    <div class="col-md-6">
                        <label for="problemId" class="form-label">Problem</label>
                        <select id="problemId" name="problemId" class="form-select" required>
                            <option value="">Select problem</option>
                            <c:forEach var="problem" items="${problems}">
                                <option value="${problem.id}">${problem.title} (${problem.difficulty})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="order" class="form-label">Order</label>
                        <input id="order" name="order" type="number" min="1" class="form-control" required>
                    </div>
                    <div class="col-md-3">
                        <label for="points" class="form-label">Points</label>
                        <input id="points" name="points" type="number" min="1" class="form-control" value="100" required>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Add Problem</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="card border-0 shadow-sm">
            <div class="card-body">
                <h2 class="h6 fw-semibold mb-3">Current Contest Problems</h2>
                <c:choose>
                    <c:when test="${empty contestProblems}">
                        <div class="alert alert-secondary mb-0">No problems added yet.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped align-middle">
                                <thead class="table-light">
                                <tr>
                                    <th>Order</th>
                                    <th>Problem</th>
                                    <th>Difficulty</th>
                                    <th>Points</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="item" items="${contestProblems}">
                                    <tr>
                                        <td>${item.order}</td>
                                        <td>${item.problem.title}</td>
                                        <td>${item.problem.difficulty}</td>
                                        <td>${item.points}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>
</main>

<%@ include file="../components/footer.jsp" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<article class="card h-100 border-0 shadow-sm">
    <div class="card-body d-flex flex-column">
        <div class="d-flex align-items-start justify-content-between mb-2">
            <h3 class="h5 mb-0">${problem.title}</h3>
            <c:choose>
                <c:when test="${problem.difficulty == 'EASY'}">
                    <span class="badge text-bg-success">${problem.difficulty}</span>
                </c:when>
                <c:when test="${problem.difficulty == 'MEDIUM'}">
                    <span class="badge text-bg-warning">${problem.difficulty}</span>
                </c:when>
                <c:otherwise>
                    <span class="badge text-bg-danger">${problem.difficulty}</span>
                </c:otherwise>
            </c:choose>
        </div>
        <p class="text-secondary mb-3 description-clamp-2" title="${problem.description}">
            ${problem.description}
        </p>

        <div class="d-flex align-items-center justify-content-between mt-auto">
            <a class="btn btn-outline-primary btn-sm"
               href="${pageContext.request.contextPath}${showAdminActions ? '/admin/problem?id=' : '/problem?id='}${problem.id}">
                View Details
            </a>
            <small class="text-secondary">${problem.timeLimit} ms | ${problem.memoryLimit} MB</small>
        </div>

        <c:if test="${showAdminActions}">
            <div class="d-flex flex-wrap gap-2 mt-3">
                <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/admin/problem/edit?id=${problem.id}">Edit</a>
                <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin/problem/testcase?problemId=${problem.id}">
                    Add Test Case
                </a>
                <a class="btn btn-sm btn-outline-danger" href="${pageContext.request.contextPath}/admin/problem/delete?id=${problem.id}"
                   onclick="return confirm('Delete this problem?');">Delete</a>
            </div>
        </c:if>
    </div>
</article>

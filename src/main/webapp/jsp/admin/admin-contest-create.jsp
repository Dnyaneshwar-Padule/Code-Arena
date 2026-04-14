<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="${formMode == 'edit' ? 'Edit Contest | CP Portal' : 'Create Contest | CP Portal'}" scope="request"/>
<%@ include file="../components/header.jsp" %>

<main class="flex-grow-1 py-5 bg-light">
    <section class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-lg-8">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4 p-md-5">
                        <h1 class="h4 fw-bold mb-3">${formMode == 'edit' ? 'Edit Contest' : 'Create Contest'}</h1>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">${error}</div>
                        </c:if>

                        <form method="post"
                              action="${pageContext.request.contextPath}${formMode == 'edit' ? '/admin/contest/update' : '/admin/contest/create'}">
                            <c:if test="${formMode == 'edit'}">
                                <input type="hidden" name="id" value="${contest.id}">
                            </c:if>

                            <div class="mb-3">
                                <label for="title" class="form-label">Title</label>
                                <input id="title" name="title" type="text" class="form-control"
                                       value="${not empty param.title ? param.title : contest.title}" required>
                            </div>

                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <textarea id="description" name="description" rows="4" class="form-control">${not empty param.description ? param.description : contest.description}</textarea>
                            </div>

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="startTime" class="form-label">Start Time</label>
                                    <input id="startTime" name="startTime" type="datetime-local" class="form-control"
                                           value="${not empty param.startTime ? param.startTime : (not empty contest.startTime ? fn:substring(contest.startTime, 0, 16) : '')}" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="endTime" class="form-label">End Time</label>
                                    <input id="endTime" name="endTime" type="datetime-local" class="form-control"
                                           value="${not empty param.endTime ? param.endTime : (not empty contest.endTime ? fn:substring(contest.endTime, 0, 16) : '')}" required>
                                </div>
                            </div>

                            <div class="d-flex gap-2 mt-4">
                                <button type="submit" class="btn btn-primary">Save</button>
                                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/contests">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<%@ include file="../components/footer.jsp" %>

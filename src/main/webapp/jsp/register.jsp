<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 bg-light py-5">
    <section class="container py-4 py-md-5">
        <div class="row justify-content-center">
            <div class="col-12 col-md-9 col-lg-7 col-xl-6">
                <article class="card border-0 shadow-lg rounded-4 overflow-hidden">
                    <div class="card-body p-4 p-md-5">
                        <div class="text-center mb-4">
                            <span class="badge rounded-pill text-bg-success px-3 py-2 mb-3">Join CP Portal</span>
                            <h1 class="h3 fw-bold mb-2">Create Your Account</h1>
                            <p class="text-secondary mb-0">Start solving problems and building your coding streak today.</p>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger border-0 shadow-sm rounded-3" role="alert">${error}</div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/register">
                            <div class="mb-3">
                                <label class="form-label fw-semibold" for="username">Username</label>
                                <input class="form-control form-control-lg rounded-3 border-2" id="username" name="username"
                                       type="text" value="${param.username}" placeholder="Choose a username" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold" for="email">Email</label>
                                <input class="form-control form-control-lg rounded-3 border-2" id="email" name="email"
                                       type="email" value="${param.email}" placeholder="you@example.com" required>
                            </div>
                            <div class="mb-4">
                                <label class="form-label fw-semibold" for="password">Password</label>
                                <input class="form-control form-control-lg rounded-3 border-2" id="password" name="password"
                                       type="password" placeholder="Minimum 8 characters" required>
                            </div>
                            <button class="btn btn-success btn-lg w-100 rounded-3 shadow-sm" type="submit">Create Account</button>
                        </form>
                        <p class="text-center text-secondary mt-4 mb-0">
                            Already have an account?
                            <a class="fw-semibold text-decoration-none" href="${pageContext.request.contextPath}/login">Login now</a>
                        </p>
                    </div>
                </article>
            </div>
        </div>
    </section>
</main>

<%@ include file="components/footer.jsp" %>

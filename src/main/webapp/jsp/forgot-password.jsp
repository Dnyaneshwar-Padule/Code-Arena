<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Forgot Password | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 bg-light py-5">
    <section class="container py-4 py-md-5">
        <div class="row justify-content-center">
            <div class="col-12 col-md-8 col-lg-6 col-xl-5">
                <article class="card border-0 shadow-lg rounded-4 overflow-hidden">
                    <div class="card-body p-4 p-md-5">
                        <div class="text-center mb-4">
                            <span class="badge rounded-pill text-bg-warning px-3 py-2 mb-3">Password Recovery</span>
                            <h1 class="h3 fw-bold mb-2">Forgot Password?</h1>
                            <p class="text-secondary mb-0">Enter your email to receive an OTP.</p>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger border-0 shadow-sm rounded-3" role="alert">${error}</div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success border-0 shadow-sm rounded-3" role="alert">${success}</div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/forgot-password">
                            <div class="mb-4">
                                <label class="form-label fw-semibold" for="email">Email</label>
                                <input class="form-control form-control-lg rounded-3 border-2" id="email" name="email" type="email"
                                       value="${param.email}" placeholder="you@example.com" required>
                            </div>
                            <button class="btn btn-warning btn-lg w-100 rounded-3 shadow-sm" type="submit">Send OTP</button>
                        </form>

                        <p class="text-center text-secondary mt-4 mb-0">
                            Back to
                            <a class="fw-semibold text-decoration-none" href="${pageContext.request.contextPath}/login">Login</a>
                        </p>
                    </div>
                </article>
            </div>
        </div>
    </section>
</main>

<%@ include file="components/footer.jsp" %>

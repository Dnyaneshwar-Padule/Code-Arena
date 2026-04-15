<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Verify Reset OTP | CP Portal" scope="request"/>
<%@ include file="components/header.jsp" %>

<main class="flex-grow-1 bg-light py-5">
    <section class="container py-4 py-md-5">
        <div class="row justify-content-center">
            <div class="col-12 col-md-8 col-lg-6 col-xl-5">
                <article class="card border-0 shadow-lg rounded-4 overflow-hidden">
                    <div class="card-body p-4 p-md-5">
                        <div class="text-center mb-4">
                            <span class="badge rounded-pill text-bg-warning px-3 py-2 mb-3">Reset OTP</span>
                            <h1 class="h3 fw-bold mb-2">Verify OTP</h1>
                            <p class="text-secondary mb-0">Enter the 6-digit OTP sent to your email.</p>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger border-0 shadow-sm rounded-3" role="alert">${error}</div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success border-0 shadow-sm rounded-3" role="alert">${success}</div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/forgot-password/verify">
                            <input type="hidden" name="action" value="verify">
                            <div class="mb-4">
                                <label class="form-label fw-semibold" for="otp">OTP</label>
                                <input class="form-control form-control-lg rounded-3 border-2" id="otp" name="otp"
                                       type="text" inputmode="numeric" pattern="[0-9]{6}" maxlength="6"
                                       placeholder="Enter 6-digit OTP" required>
                            </div>
                            <button class="btn btn-warning btn-lg w-100 rounded-3 shadow-sm" type="submit">Verify OTP</button>
                        </form>

                        <form method="post" action="${pageContext.request.contextPath}/forgot-password/verify" class="mt-3">
                            <input type="hidden" name="action" value="resend">
                            <button class="btn btn-outline-secondary w-100 rounded-3" type="submit">Resend OTP</button>
                        </form>
                    </div>
                </article>
            </div>
        </div>
    </section>
</main>

<%@ include file="components/footer.jsp" %>

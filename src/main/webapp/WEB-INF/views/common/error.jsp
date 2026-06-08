<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>오류 - POPFLIX</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reservation/scheduleList.css">
<style>
    .error-main {
        width: min(720px, calc(100% - 56px));
        margin: 84px auto 120px;
        flex: 1 0 auto;
        position: relative;
        z-index: 2;
    }

    .error-box {
        min-height: 220px;
        padding: 42px 36px;
        border-radius: 14px;
        background: #fff;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 16px;
        text-align: center;
    }

    .error-title {
        margin: 0;
        font-size: 26px;
        font-weight: 900;
        color: #151515;
    }

    .error-message {
        margin: 0;
        color: #4a3320;
        font-size: 15px;
        font-weight: 800;
        line-height: 1.7;
        word-break: keep-all;
    }

    .error-actions {
        margin-top: 10px;
        display: flex;
        gap: 10px;
        justify-content: center;
        flex-wrap: wrap;
    }

    .error-actions a {
        min-width: 112px;
        height: 38px;
        padding: 0 18px;
        border-radius: 999px;
        background: #FF5C3B;
        color: #fff;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        text-decoration: none;
        font-size: 13px;
        font-weight: 900;
    }
</style>
</head>
<body>
<div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main class="error-main">
        <section class="error-box">
            <h1 class="error-title">오류가 발생했습니다</h1>
            <p class="error-message">
                ${empty errorMsg ? "요청을 처리하는 중 문제가 발생했습니다." : errorMsg}
            </p>
            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/main.do">메인으로</a>
                <a href="javascript:history.back()">이전으로</a>
            </div>
        </section>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
</div>
</body>
</html>

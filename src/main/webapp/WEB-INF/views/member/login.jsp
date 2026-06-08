<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix 로그인</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/member/member-style.css">
</head>
<body>
  <div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main class="auth-content auth-login">
      <form class="form-panel" action="${ctx}/login.do" method="post">
        <div class="brand-hero">
          <img src="${ctx}/img/popflex-logo.png" alt="POPFLIX">
        </div>
        <section class="page-title auth-title">
          <div>
            <h1>로그인</h1>
            <p>Popflix 계정으로 예매와 리뷰를 이어가세요.</p>
          </div>
        </section>

        <c:if test="${not empty errorMsg}">
          <div class="message error"><c:out value="${errorMsg}" /></div>
        </c:if>

        <label class="field" for="userId">
          <span>아이디</span>
          <input type="text" id="userId" name="userId" maxlength="30" autocomplete="username" required>
        </label>

        <label class="field" for="password">
          <span>비밀번호</span>
          <input type="password" id="password" name="password" autocomplete="current-password" required>
        </label>

        <button class="btn submit-btn" type="submit">로그인</button>

        <div class="social-divider">또는</div>
        <a class="naver-btn" href="${ctx}/member/naverLogin.do">네이버로 로그인</a>

        <div class="sub-link">
          아직 계정이 없다면 <a href="${ctx}/join.do">회원가입</a>
        </div>
      </form>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
  </div>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/main-style.css">
</head>
<body>
  <main class="home">
    <div class="brand-mark">
      <img class="logo" src="${ctx}/img/popflex-logo.png" alt="Popflix">
    </div>
    <h1>POPFLIX</h1>
    <p>영화 예매와 리뷰를 한 번에 즐겨보세요.</p>
    <div class="actions">
      <a class="button" href="${ctx}/movie/search.do">영화검색</a>
      <c:choose>
        <c:when test="${empty sessionScope.loginMember}">
          <a class="button" href="${ctx}/login.do">로그인</a>
          <a class="button" href="${ctx}/join.do">회원가입</a>
        </c:when>
        <c:otherwise>
          <a class="button" href="${ctx}/member/mypage.do">마이페이지</a>
          <c:if test="${sessionScope.loginMember.admin}">
            <a class="button" href="${ctx}/admin/main.do">관리자</a>
          </c:if>
          <a class="button" href="${ctx}/logout.do">로그아웃</a>
        </c:otherwise>
      </c:choose>
    </div>
  </main>
</body>
</html>

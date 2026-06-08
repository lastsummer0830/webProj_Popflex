<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:url var="diaryListUrl" value="/diary/list.do" />

<header class="site-header">
  <a class="brand" href="${ctx}/main.do">
    <img src="${ctx}/img/popflex-logo.png" alt="Popflix">
    <span>POPFLIX</span>
  </a>
  <nav class="nav" aria-label="주요 메뉴">
    <c:choose>
      <c:when test="${empty sessionScope.loginMember}">
        <a href="${ctx}/movie/search.do">영화검색</a>
        <a href="${ctx}/login.do">로그인</a>
        <a href="${ctx}/join.do">회원가입</a>
      </c:when>
      <c:otherwise>
        <a href="${ctx}/movie/search.do">영화검색</a>
        <a href="${ctx}/reservation/myList.do">예매내역</a>
        <a href="${ctx}/review/myList.do">리뷰</a>
        <a href="${diaryListUrl}">필름다이어리</a>
        <a href="${ctx}/friend/list.do">내친구</a>
        <a href="${ctx}/member/mypage.do">마이페이지</a>
        <a href="${ctx}/logout.do">로그아웃</a>
      </c:otherwise>
    </c:choose>
  </nav>
</header>

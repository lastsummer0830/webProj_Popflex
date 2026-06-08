<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:url var="diaryListUrl" value="/diary/list.do" />

<header class="ma-header">
    <a class="ma-logo" href="${ctx}/main.do">
        <img src="${ctx}/img/popflex-logo.png" alt="POPFLEX">
        <span>POPFLEX</span>
        <c:if test="${param.mode eq 'admin'}">
            <small>관리자</small>
        </c:if>
    </a>

    <nav class="ma-nav">
        <c:choose>
            <c:when test="${param.mode eq 'admin'}">
                <a href="${ctx}/admin/main.do">관리자 홈</a>
                <a href="${ctx}/admin/memberList.do">회원 관리</a>
                <a href="${ctx}/admin/scheduleList.do">상영 관리</a>
                <a href="${ctx}/main.do">사용자 메인</a>
                <a href="${ctx}/logout.do">로그아웃</a>
            </c:when>
            <c:when test="${empty sessionScope.loginMember}">
                <a href="${ctx}/movie/search.do">영화검색</a>
                <c:if test="${param.current ne 'login'}">
                    <a href="${ctx}/login.do">로그인</a>
                </c:if>
                <c:if test="${param.current ne 'join'}">
                    <a href="${ctx}/join.do">회원가입</a>
                </c:if>
            </c:when>
            <c:otherwise>
                <a href="${ctx}/movie/search.do">영화검색</a>
                <a href="${ctx}/reservation/myList.do">예매내역</a>
                <a href="${ctx}/review/myList.do">리뷰</a>
                <a href="${diaryListUrl}">필름 다이어리</a>
                <a href="${ctx}/friend/list.do">내 친구</a>
                <a href="${ctx}/member/mypage.do">마이페이지</a>
                <c:if test="${sessionScope.loginMember.admin}">
                    <a href="${ctx}/admin/main.do">관리자</a>
                </c:if>
                <a href="${ctx}/logout.do">로그아웃</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>

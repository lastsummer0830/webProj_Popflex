<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>POPFLEX - 관리자</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/admin-style.css">
</head>
<body>
<div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main class="content">
        <c:if test="${not empty adminMessage}">
            <div class="message ok"><c:out value="${adminMessage}" /></div>
        </c:if>
        <c:if test="${not empty adminError}">
            <div class="message error"><c:out value="${adminError}" /></div>
        </c:if>
        <c:if test="${not dashboard.dbReady}">
            <div class="message error">
                DB에 필요한 테이블이 아직 없습니다:
                <c:forEach var="tableName" items="${dashboard.missingTables}" varStatus="status">
                    <c:if test="${not status.first}">, </c:if><c:out value="${tableName}" />
                </c:forEach>
            </div>
        </c:if>

        <div class="title-row">
            <h1>관리자 홈</h1>
            <div class="quick-actions">
                <a class="button" href="${ctx}/admin/scheduleForm.do">상영 등록</a>
                <a class="button" href="${ctx}/admin/memberList.do">회원 관리</a>
                <a class="button" href="${ctx}/admin/scheduleList.do">상영 목록</a>
                <a class="button" href="${ctx}/admin/errorLogs.do">오류 로그 보기</a>
            </div>
        </div>

        <section class="metrics">
            <div class="metric">
                <div class="metric-label">활성 회원</div>
                <div class="metric-value"><c:out value="${dashboard.memberCount}" /></div>
            </div>
            <div class="metric">
                <div class="metric-label">등록 영화</div>
                <div class="metric-value"><c:out value="${dashboard.movieCount}" /></div>
            </div>
            <div class="metric">
                <div class="metric-label">상영 일정</div>
                <div class="metric-value"><c:out value="${dashboard.scheduleCount}" /></div>
            </div>
            <div class="metric">
                <div class="metric-label">활성 예매</div>
                <div class="metric-value"><c:out value="${dashboard.reservationCount}" /></div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
</div>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>POPFLEX - 오류 로그</title>
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

        <div class="title-row">
            <div>
                <h1>오류 로그</h1>
                <p class="subtitle">최근 발생한 서버 예외를 최신순으로 최대 100건까지 표시합니다.</p>
            </div>
            <div class="actions">
                <a class="button" href="${ctx}/admin/main.do">관리자 홈</a>
                <form action="${ctx}/admin/errorLogs.do" method="post">
                    <button class="button danger" type="submit">로그 비우기</button>
                </form>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty errorLogs}">
                <div class="empty">아직 수집된 오류 로그가 없습니다.</div>
            </c:when>
            <c:otherwise>
                <section class="log-list" aria-label="오류 로그 목록">
                    <c:forEach var="log" items="${errorLogs}">
                        <article class="log-item">
                            <div class="log-head">
                                <div class="log-meta">
                                    <span class="chip"><c:out value="${log.occurredAtText}" /></span>
                                    <span class="chip"><c:out value="${log.method}" /></span>
                                    <span class="chip">
                                        사용자:
                                        <c:choose>
                                            <c:when test="${empty log.userId}">-</c:when>
                                            <c:otherwise><c:out value="${log.userId}" /></c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <p class="exception"><c:out value="${log.exceptionType}" /></p>
                                <p class="path">
                                    <c:out value="${log.requestUri}" />
                                    <c:if test="${not empty log.queryString}">?<c:out value="${log.queryString}" /></c:if>
                                </p>
                                <c:if test="${not empty log.message}">
                                    <p class="path"><c:out value="${log.message}" /></p>
                                </c:if>
                            </div>
                            <pre class="stack"><c:out value="${log.stackTrace}" /></pre>
                        </article>
                    </c:forEach>
                </section>
            </c:otherwise>
        </c:choose>
    </main>
    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
</div>
</body>
</html>

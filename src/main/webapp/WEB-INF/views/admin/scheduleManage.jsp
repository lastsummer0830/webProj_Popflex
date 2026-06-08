<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>POPFLEX - 상영 관리</title>
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

        <div class="title-row">
            <h1>상영 정보 관리</h1>
            <div class="quick-actions">
                <a class="button" href="${ctx}/admin/main.do">관리자 홈</a>
                <a class="button" href="${ctx}/admin/scheduleForm.do">상영 등록</a>
            </div>
        </div>

        <div class="table-wrap">
            <c:choose>
                <c:when test="${empty schedules}">
                    <div class="empty">등록된 상영 정보가 없습니다.</div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>번호</th>
                                <th>영화</th>
                                <th>상영관</th>
                                <th>시작 시간</th>
                                <th>종료 시간</th>
                                <th>가격</th>
                                <th>예약</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="schedule" items="${schedules}">
                                <tr>
                                    <td><c:out value="${schedule.scheduleId}" /></td>
                                    <td class="movie-title"><c:out value="${schedule.movieTitle}" /></td>
                                    <td>
                                        <c:out value="${schedule.theaterName}" />
                                        <span class="muted"><c:out value="${schedule.screenName}" /></span>
                                    </td>
                                    <td><c:out value="${schedule.startTime}" /></td>
                                    <td><c:out value="${schedule.endTime}" /></td>
                                    <td><c:out value="${schedule.price}" />원</td>
                                    <td><c:out value="${schedule.reservationCount}" />건</td>
                                    <td>
                                        <div class="actions">
                                            <a class="button" href="${ctx}/admin/scheduleForm.do?scheduleId=${schedule.scheduleId}">수정</a>
                                            <form action="${ctx}/admin/scheduleDelete.do" method="post"
                                                  onsubmit="return confirm('이 상영 정보를 삭제하시겠습니까? 예약 내역이 있으면 삭제되지 않습니다.');">
                                                <input type="hidden" name="scheduleId" value="${schedule.scheduleId}">
                                                <button class="danger-button" type="submit">삭제</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
</div>
</body>
</html>

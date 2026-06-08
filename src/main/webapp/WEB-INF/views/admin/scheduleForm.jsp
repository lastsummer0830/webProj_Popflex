<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="isUpdate" value="${mode eq 'update'}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>POPFLEX - 상영 등록</title>
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
            <h1><c:out value="${isUpdate ? '상영 정보 수정' : '상영 정보 등록'}" /></h1>
            <a class="button" href="${ctx}/admin/scheduleList.do">목록</a>
        </div>

        <div class="layout">
            <section class="panel">
                <h2>상영 정보</h2>
                <form action="${ctx}${isUpdate ? '/admin/scheduleUpdate.do' : '/admin/scheduleInsert.do'}" method="post">
                    <c:if test="${isUpdate}">
                        <input type="hidden" name="scheduleId" value="${schedule.scheduleId}">
                    </c:if>

                    <div class="field">
                        <label for="movieId">영화</label>
                        <select id="movieId" name="movieId" required>
                            <option value="">영화를 선택하세요</option>
                            <c:forEach var="movie" items="${movies}">
                                <option value="${movie.movieId}" ${not empty schedule and schedule.movieId == movie.movieId ? 'selected' : ''}>
                                    <c:out value="${movie.title}" />
                                    <c:if test="${not empty movie.runtime}">(<c:out value="${movie.runtime}" />분)</c:if>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="field">
                        <label for="screenId">상영관</label>
                        <select id="screenId" name="screenId" required>
                            <option value="">상영관을 선택하세요</option>
                            <c:forEach var="screen" items="${screens}">
                                <option value="${screen.screenId}" ${not empty schedule and schedule.screenId == screen.screenId ? 'selected' : ''}>
                                    <c:out value="${screen.theaterName}" /> <c:out value="${screen.screenName}" />
                                    - 좌석 <c:out value="${screen.seatCount}" />개
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="grid-2">
                        <div class="field">
                            <label for="startTime">시작 시간</label>
                            <input type="datetime-local" id="startTime" name="startTime"
                                   value="${not empty schedule ? schedule.startInputValue : ''}" required>
                        </div>
                        <div class="field">
                            <label for="endTime">종료 시간</label>
                            <input type="datetime-local" id="endTime" name="endTime"
                                   value="${not empty schedule ? schedule.endInputValue : ''}">
                        </div>
                    </div>

                    <div class="field">
                        <label for="price">가격</label>
                        <input type="number" id="price" name="price" min="0" step="100"
                               value="${not empty schedule ? schedule.price : 12000}" required>
                    </div>

                    <div class="actions">
                        <button class="submit-btn" type="submit">${isUpdate ? '수정하기' : '등록하기'}</button>
                        <a class="button" href="${ctx}/admin/scheduleList.do">취소</a>
                    </div>
                </form>
            </section>

            <aside class="panel">
                <h2>좌석 생성</h2>
                <p class="hint">
                    상영관을 먼저 만든 뒤 기본 좌석을 생성합니다.
                    이미 있는 좌석은 그대로 두고 없는 좌석만 추가됩니다.
                </p>
                <form action="${ctx}/admin/seatManage.do" method="post">
                    <div class="field">
                        <label for="seatScreenId">상영관</label>
                        <select id="seatScreenId" name="screenId" required>
                            <option value="">상영관을 선택하세요</option>
                            <c:forEach var="screen" items="${screens}">
                                <option value="${screen.screenId}">
                                    <c:out value="${screen.theaterName}" /> <c:out value="${screen.screenName}" />
                                    - 현재 <c:out value="${screen.seatCount}" />개
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="grid-2">
                        <div class="field">
                            <label for="rowCount">행</label>
                            <input type="number" id="rowCount" name="rowCount" min="1" max="12" value="5">
                        </div>
                        <div class="field">
                            <label for="colCount">열</label>
                            <input type="number" id="colCount" name="colCount" min="1" max="30" value="8">
                        </div>
                    </div>
                    <div class="actions">
                        <button class="submit-btn" type="submit">좌석 생성</button>
                    </div>
                </form>
            </aside>
        </div>
    </main>
    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
</div>
</body>
</html>

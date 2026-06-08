<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>평가한 영화 - POPFLIX</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/review/review-style.css">
</head>
<body>
    <%--
        JSP 내부에서 통계 계산
        fresh_yn = 'Y' → 터졌다 카운트
    --%>
    <c:set var="totalCount" value="0" />
    <c:set var="freshCount" value="0" />
    <c:forEach var="review" items="${reviewList}">
        <c:set var="totalCount" value="${totalCount + 1}" />
        <c:if test="${review.freshYn eq 'Y'}">
            <c:set var="freshCount" value="${freshCount + 1}" />
        </c:if>
    </c:forEach>
    <c:set var="notFreshCount" value="${totalCount - freshCount}" />

    <%-- 0나누기 방지 --%>
    <c:choose>
        <c:when test="${totalCount > 0}">
            <c:set var="freshRate" value="${freshCount * 100 / totalCount}" />
        </c:when>
        <c:otherwise>
            <c:set var="freshRate" value="0" />
        </c:otherwise>
    </c:choose>
    
    <%-- 다른 회원 평가 목록일 때 이름 표시용. 리뷰가 없으면 회원번호만 표시한다. --%>
    <c:set var="targetMemberName" value="" />
    <c:forEach var="review" items="${reviewList}" begin="0" end="0">
        <c:set var="targetMemberName" value="${review.memberName}" />
    </c:forEach>

<div class="page">

    <%-- 공통 헤더 --%>
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <div class="myreview-wrap">

        <%-- 페이지 타이틀 행 --%>
        <div class="page-title-row">
            <span class="page-title">
                <c:choose>
                    <c:when test="${isMyPage}">
                        내 평가 목록
                    </c:when>
                    <c:otherwise>
                        친구 평가 목록
                    </c:otherwise>
                </c:choose>
            </span>
            <c:choose>
                <c:when test="${isMyPage}">
                    <span class="page-title-sub">
                        ${sessionScope.loginMember.name}
                        (${sessionScope.loginMember.userId})
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="page-title-sub">
                        <c:choose>
                            <c:when test="${not empty targetMemberName}">${targetMemberName}님</c:when>
                            <c:otherwise>회원 #${targetMemberId}</c:otherwise>
                        </c:choose>
                    </span>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- 통계 카드 --%>
        <div class="stat-card">
            <div class="stat-item">전체 <strong>${totalCount}</strong>편</div>
            <div class="stat-divider">|</div>
            <div class="stat-item"><img src="${pageContext.request.contextPath}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌다 <strong>${freshCount}</strong>편</div>
            <div class="stat-divider">|</div>
            <div class="stat-item"><img src="${pageContext.request.contextPath}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안터졌다 <strong>${notFreshCount}</strong>편</div>
            <div class="burst-bar-wrap">
                <div class="burst-bar-label">
                    <span><img src="${pageContext.request.contextPath}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌다 비율</span>
                    <span><fmt:formatNumber value="${freshRate}" maxFractionDigits="1" />%</span>
                </div>
                <div class="burst-bar">
                    <div class="burst-bar-fill"
                         style="width:<fmt:formatNumber value='${freshRate}' maxFractionDigits='1' />%;"></div>
                </div>
            </div>
        </div>

        <%-- 필터 헤더 --%>
        <div class="section-header">
            <div class="section-title">
                <c:choose>
                    <c:when test="${isMyPage}">평가한 영화</c:when>
                    <c:otherwise>친구가 평가한 영화</c:otherwise>
                </c:choose>
            </div>

            <form method="get"
                  action="${pageContext.request.contextPath}/review/myList.do"
                  id="filterForm">

                <c:if test="${!isMyPage}">
                    <input type="hidden" name="memberId" value="${targetMemberId}">
                </c:if>

                <%-- 전체 / 전체공개(Y) / 친구공개(N) --%>
                <select name="publicYn"
                        class="filter-select"
                        onchange="document.getElementById('filterForm').submit();">
                    <option value=""  ${empty publicYn  ? 'selected' : ''}>전체</option>
                    <option value="Y" ${'Y' eq publicYn ? 'selected' : ''}>전체공개</option>
                    <option value="N" ${'N' eq publicYn ? 'selected' : ''}>친구공개</option>
                </select>
            </form>
        </div>


        <%-- 영화 그리드 --%>
        <div class="movie-grid">
            <c:choose>
                <c:when test="${empty reviewList}">
                    <div class="empty-box">
                        <div class="icon">&#127902;</div>
                        <p>
                            <c:choose>
                                <c:when test="${isMyPage}">아직 평가한 영화가 없어요!</c:when>
                                <c:otherwise>평가한 영화가 없어요!</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="review" items="${reviewList}">
                        <div class="movie-card">

                            <%-- 터졌다 배지 --%>
                            <c:choose>
                                <c:when test="${review.freshYn eq 'Y'}">
                                    <span class="burst-badge yes"><img src="${pageContext.request.contextPath}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌다</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="burst-badge no"><img src="${pageContext.request.contextPath}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안터졌다</span>
                                </c:otherwise>
                            </c:choose>

                            <%-- 포스터 --%>
                            <div class="poster-wrap">
                                <c:choose>
                                    <c:when test="${not empty review.posterUrl}">
                                        <img src="${review.posterUrl}"
                                             alt="${review.movieTitle}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="poster-placeholder">
                                            NO IMAGE
                                            <c:if test="${not empty review.movieTitle}">
                                                <small>${review.movieTitle}</small>
                                            </c:if>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <%-- 호버 오버레이 --%>
                                <div class="poster-overlay">
                                    <div class="overlay-content">${review.content}</div>
                                    <c:if test="${isMyPage}">
                                        <div class="overlay-btn-wrap">
                                            <a href="${pageContext.request.contextPath}/review/updateForm.do?reviewId=${review.reviewId}"
                                               class="overlay-btn btn-edit">수정</a>
                                            <form action="${pageContext.request.contextPath}/review/delete.do"
                                                  method="post"
                                                  onsubmit="return confirm('정말 삭제할까요?');"
                                                  style="display:inline;">
                                                <input type="hidden"
                                                       name="reviewId"
                                                       value="${review.reviewId}">
                                                <button type="submit"
                                                        class="overlay-btn btn-delete">삭제</button>
                                            </form>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <%-- 영화 제목 --%>
                            <div class="movie-title-label">
                                <c:choose>
                                    <c:when test="${not empty review.movieTitle}">
                                        ${review.movieTitle}
                                    </c:when>
                                    <c:otherwise>영화 #${review.movieId}</c:otherwise>
                                </c:choose>
                            </div>

                            <%-- 공개 배지: 친구 평가 목록에서도 표시한다. 수정/삭제 버튼은 isMyPage일 때만 보인다. --%>
                            <div class="public-badge">
                                <c:choose>
                                    <c:when test="${review.publicYn eq 'Y'}">전체공개</c:when>
                                    <c:when test="${review.publicYn eq 'N'}">친구공개</c:when>
                                </c:choose>
                            </div>

                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

    </div><%-- /.myreview-wrap --%>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />

</div><%-- /.page --%>

</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>리뷰 수정 - POPFLIX</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/review/review-style.css">
</head>
<body>

<div class="page">

    <%-- 공통 헤더 --%>
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <div class="review-update-wrap">

        <%-- 페이지 타이틀 행 --%>
        <div class="page-title-row">
            <a href="${pageContext.request.contextPath}/review/myList.do"
               class="back-btn">&#8592; 목록으로</a>
            <span class="page-title">리뷰 수정</span>
        </div>

        <%-- 영화 정보 카드 (읽기 전용) --%>
        <div class="movie-info-card">
            <c:choose>
                <c:when test="${not empty review.posterUrl}">
                    <img class="movie-poster-thumb"
                         src="${review.posterUrl}"
                         alt="${review.movieTitle}">
                </c:when>
                <c:otherwise>
                    <div class="movie-poster-placeholder">NO IMG</div>
                </c:otherwise>
            </c:choose>

            <div class="movie-info-text">
                <span class="label-tag">수정 중인 영화</span>
                <div class="movie-title">
                    <c:choose>
                        <c:when test="${not empty review.movieTitle}">
                            ${review.movieTitle}
                        </c:when>
                        <c:otherwise>영화 #${review.movieId}</c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${not empty review.createdAt}">
                    <div class="written-at">작성일 : ${review.createdAt}</div>
                </c:if>
            </div>
        </div>

        <%--
            수정 폼
            action  : /review/update.do  (POST)
            hidden  : reviewId, memberId, movieId  (수정 불가)
            수정 가능 : freshYn, publicYn, content
        --%>
        <form class="form-card"
              action="${pageContext.request.contextPath}/review/update.do"
              method="post">

            <input type="hidden" name="reviewId" value="${review.reviewId}">
            <input type="hidden" name="memberId" value="${review.memberId}">
            <input type="hidden" name="movieId"  value="${review.movieId}">

            <%--
                터졌다 / 안터졌다
                fresh_yn = 'Y'(터졌다) / 'N'(안터졌다)
            --%>
            <div class="form-section">
                <label class="form-label">이 영화 어땠나요?</label>
                <div class="fresh-toggle">
                    <input type="radio" id="freshY" name="freshYn" value="Y"
                           ${review.freshYn eq 'Y' ? 'checked' : ''}>
                    <label for="freshY">
                        <img class="toggle-icon" src="${pageContext.request.contextPath}/img/popped.png" alt="터졌다" width="22" height="22">
                        터졌다!
                    </label>

                    <input type="radio" id="freshN" name="freshYn" value="N"
                           ${review.freshYn eq 'N' ? 'checked' : ''}>
                    <label for="freshN">
                        <img class="toggle-icon" src="${pageContext.request.contextPath}/img/unpopcorn.png" alt="안터졌다" width="22" height="22">
                        안터졌다
                    </label>
                </div>
            </div>

            <hr class="form-divider">

            <%--
                공개 설정
                public_yn = 'Y'(전체공개) / 'N'(친구공개)
                'F' 없음!
            --%>
            <div class="form-section">
                <label class="form-label">공개 설정</label>
                <div class="public-toggle">
                    <input type="radio" id="publicY" name="publicYn" value="Y"
                           ${review.publicYn eq 'Y' ? 'checked' : ''}>
                    <label for="publicY">
                        전체공개
                        <span class="toggle-sub">모든 사람이 볼 수 있어요</span>
                    </label>

                    <input type="radio" id="publicN" name="publicYn" value="N"
                           ${review.publicYn eq 'N' ? 'checked' : ''}>
                    <label for="publicN">
                        친구공개
                        <span class="toggle-sub">친구만 볼 수 있어요</span>
                    </label>
                </div>
            </div>

            <hr class="form-divider">

            <%-- 리뷰 내용 --%>
            <div class="form-section">
                <label class="form-label" for="content">한줄 리뷰</label>
                <textarea id="content"
                          name="content"
                          class="content-textarea"
                          maxlength="500"
                          placeholder="영화에 대한 생각을 자유롭게 적어주세요."
                          oninput="updateCharCount(this)">${review.content}</textarea>
                <div class="char-count" id="charCount">0 / 500</div>
            </div>

            <div class="btn-wrap">
                <a href="${pageContext.request.contextPath}/review/myList.do"
                   class="btn-cancel">취소</a>
                <button type="submit" class="btn-submit">수정 완료</button>
            </div>

        </form>
    </div>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />

</div><%-- /.page --%>

<script>
    function updateCharCount(textarea) {
        var len = textarea.value.length;
        var el  = document.getElementById('charCount');
        el.textContent = len + ' / 500';
        el.classList.toggle('warn', len >= 450);
    }

    window.addEventListener('DOMContentLoaded', function () {
        var ta = document.getElementById('content');
        if (ta) updateCharCount(ta);
    });
</script>

</body>
</html>

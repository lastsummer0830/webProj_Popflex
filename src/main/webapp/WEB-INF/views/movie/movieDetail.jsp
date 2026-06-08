<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="returnUrl" value="/movie/detail.do?movieId=${param.movieId}&movieSeq=${param.movieSeq}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${movie.title} - POPFLIX</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${ctx}/css/reservation/scheduleList.css">
</head>

<body>
<div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <!-- MOVIE DETAIL -->
    <main>
        <section class="movie" aria-label="영화 정보">
            <div class="poster" aria-label="영화 포스터 영역">
                <c:choose>
                    <c:when test="${not empty movie.posterUrl}">
                        <img src="${fn:escapeXml(movie.posterUrl)}" alt="${fn:escapeXml(movie.title)} 포스터">
                    </c:when>
                    <c:otherwise>
                        <div class="no-poster">NO IMAGE</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="movie-info">
                <div class="movie-title-row">
                    <h1><c:out value="${movie.title}" /></h1>
                    <c:if test="${not empty movie.genre}">
                        <div class="genre-list" aria-label="장르">
                            <c:forTokens var="genre" items="${movie.genre}" delims=",">
                                <span class="badge"><c:out value="${fn:trim(genre)}" /></span>
                            </c:forTokens>
                        </div>
                    </c:if>
                </div>

                <c:set var="ratingText" value="${empty movie.ratingGrade ? movie.rating : movie.ratingGrade}" />
                <c:if test="${not empty ratingText}">
                    <div class="rating">
                        <c:choose>
                            <c:when test="${fn:contains(ratingText, '전체')}">
                                <span class="rating-mark rating-all">ALL</span>
                            </c:when>
                            <c:when test="${fn:contains(ratingText, '12')}">
                                <span class="rating-mark rating-12">12</span>
                            </c:when>
                            <c:when test="${fn:contains(ratingText, '15')}">
                                <span class="rating-mark rating-15">15</span>
                            </c:when>
                            <c:when test="${fn:contains(ratingText, '18') || fn:contains(ratingText, '19') || fn:contains(ratingText, '청소년')}">
                                <span class="rating-mark rating-19">19</span>
                            </c:when>
                            <c:otherwise>
                                <span class="rating-mark rating-unknown">?</span>
                            </c:otherwise>
                        </c:choose>
                        <span><c:out value="${ratingText}" /></span>
                    </div>
                </c:if>

                <p class="description">
                    <c:choose>
                        <c:when test="${not empty movie.plot}">
                            <c:out value="${movie.plot}" />
                        </c:when>
                        <c:otherwise>줄거리 정보가 없습니다.</c:otherwise>
                    </c:choose>
                </p>

                <c:if test="${not empty movie.keywords}">
                    <div class="keyword-list">
                        <c:forTokens var="keyword" items="${movie.keywords}" delims=",">
                            <span class="keyword-pill"><c:out value="${fn:trim(keyword)}" /></span>
                        </c:forTokens>
                    </div>
                </c:if>

                <div class="like">
                    <c:choose>
                        <c:when test="${reviewStat.totalCount gt 0}">
                            <img src="${ctx}/img/popped.png" alt="터졌다" width="18" height="18">
                            <span><fmt:formatNumber value="${reviewStat.burstRate}" maxFractionDigits="0" />% 터졌어요</span>
                        </c:when>
                        <c:otherwise>
                            <img src="${ctx}/img/unpopcorn.png" alt="안터졌다" width="18" height="18">
                            <span>아직 터지기 전입니다</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="movie-actions">
                    <c:choose>
                        <c:when test="${not empty movie.vodUrl}">
                            <a class="btn" href="${movie.vodUrl}" target="_blank" rel="noopener noreferrer">예고편 보기</a>
                        </c:when>
                        <c:otherwise>
                            <button type="button" class="btn" disabled>예고편 없음</button>
                        </c:otherwise>
                    </c:choose>
                    <a class="btn" href="${ctx}/reservation/form.do?movieId=${movie.movieId}">예매하기</a>
                </div>
            </div>

            <aside class="meta" aria-label="상세 정보">
                <dl>
                    <dt>감독</dt>
                    <dd><c:out value="${empty movie.directorNm ? '정보 없음' : movie.directorNm}" /></dd>
                    <dt>배우</dt>
                    <dd><c:out value="${empty movie.actorNm ? '정보 없음' : movie.actorNm}" /></dd>
                    <dt>배급</dt>
                    <dd><c:out value="${empty movie.company ? '정보 없음' : movie.company}" /></dd>
                    <dt>개봉일</dt>
                    <dd><c:out value="${empty movie.releaseDate ? '정보 없음' : movie.releaseDate}" /></dd>
                    <dt>상영시간</dt>
                    <dd><c:out value="${empty movie.runtime ? '정보 없음' : movie.runtime}" /><c:if test="${not empty movie.runtime}">분</c:if></dd>
                    <dt>관람기준</dt>
                    <dd><c:out value="${empty movie.ratingGrade ? '정보 없음' : movie.ratingGrade}" /></dd>
                </dl>
            </aside>
        </section>

        <section class="review-section">
            <div class="divider"></div>
            <div class="review-heading-row">
                <h2 class="review-title">REVIEW</h2>
            </div>

            <div class="review-stat-box">
                <div class="review-stat-item">
                    <span>전체 리뷰</span>
                    <strong><c:out value="${reviewStat.totalCount}" />개</strong>
                </div>
                <div class="review-stat-item">
                    <span><img src="${ctx}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌다</span>
                    <strong><fmt:formatNumber value="${reviewStat.burstRate}" maxFractionDigits="0" />%</strong>
                </div>
                <div class="review-stat-item">
                    <span><img src="${ctx}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안 터졌다</span>
                    <strong><fmt:formatNumber value="${100 - reviewStat.burstRate}" maxFractionDigits="0" />%</strong>
                </div>
            </div>

            <div class="review-filter-row" aria-label="리뷰 필터">
                <button type="button" class="review-filter-btn active" data-filter="all">전체</button>
                <button type="button" class="review-filter-btn" data-filter="latest">최신순</button>
                <button type="button" class="review-filter-btn" data-filter="oldest">오래된순</button>
                <button type="button" class="review-filter-btn" data-filter="fresh">터진 리뷰</button>
                <button type="button" class="review-filter-btn" data-filter="notFresh">안 터진 리뷰</button>
            </div>

            <c:if test="${empty reviewList}">
                <div class="empty-review-box">
                    <div class="empty-review-main">아직 작성된 리뷰가 없습니다</div>
                    <div class="empty-review-sub">이 영화를 처음으로 평가해보세요.</div>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty sessionScope.loginMember}">
                    <div class="review-login-guide">
                        리뷰를 작성하려면 <a href="${ctx}/login.do">로그인</a>이 필요합니다.
                    </div>
                </c:when>

                <c:when test="${empty myReview}">
                    <form class="review-write-card" action="${ctx}/review/insert.do" method="post">
                        <input type="hidden" name="movieId" value="${movie.movieId}">
                        <input type="hidden" name="returnUrl" value="${returnUrl}">

                        <div class="review-radio-row">
                            <label><input type="radio" name="freshYn" value="Y" checked> <img src="${ctx}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌어요</label>
                            <label><input type="radio" name="freshYn" value="N"> <img src="${ctx}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안 터졌어요</label>
                            <select name="publicYn" title="공개 범위">
                                <option value="Y">공개</option>
                                <option value="N">친구공개</option>
                            </select>
                        </div>

                        <div class="review-form-body">
                            <textarea class="review-textarea" name="content" required maxlength="2000" placeholder="리뷰 내용을 입력하세요"></textarea>
                            <div class="review-side-actions">
                                <button type="submit" class="btn review-action-btn">등록</button>
                            </div>
                        </div>
                    </form>
                </c:when>

                <c:otherwise>
                    <form class="review-my-card" action="${ctx}/review/update.do" method="post">
                        <input type="hidden" name="reviewId" value="${myReview.reviewId}">
                        <input type="hidden" name="returnUrl" value="${returnUrl}">

                        <div class="review-card-top">
                            <span class="review-burst-pill ${myReview.freshYn eq 'N' ? 'no' : ''}">
                                <c:choose>
                                    <c:when test="${myReview.freshYn eq 'Y'}"><img src="${ctx}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌어요</c:when>
                                    <c:otherwise><img src="${ctx}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안 터졌어요</c:otherwise>
                                </c:choose>
                            </span>
                            <select name="publicYn" class="review-public-pill" title="공개 범위">
                                <option value="Y" ${myReview.publicYn eq 'Y' ? 'selected' : ''}>공개</option>
                                <option value="N" ${myReview.publicYn eq 'N' ? 'selected' : ''}>친구공개</option>
                            </select>
                            <label class="review-public-pill">
                                <input type="radio" name="freshYn" value="Y" ${myReview.freshYn eq 'Y' ? 'checked' : ''}> <img src="${ctx}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌어요
                            </label>
                            <label class="review-public-pill">
                                <input type="radio" name="freshYn" value="N" ${myReview.freshYn eq 'N' ? 'checked' : ''}> <img src="${ctx}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안 터졌어요
                            </label>
                            <span class="review-meta-right">
                                <c:choose>
                                    <c:when test="${not empty myReview.createdAt}">작성일 ${myReview.createdAt}</c:when>
                                    <c:otherwise>작성일 없음</c:otherwise>
                                </c:choose>
                            </span>
                        </div>

                        <div class="review-form-body">
                            <textarea class="review-textarea" name="content" required maxlength="2000">${myReview.content}</textarea>
                            <div class="review-side-actions">
                                <button type="submit" class="btn review-action-btn">수정</button>
                                <button type="submit" formaction="${ctx}/review/delete.do" formmethod="post" class="btn review-action-btn danger" onclick="return confirm('정말 삭제할까요?');">삭제</button>
                            </div>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>

            <div id="reviewListArea">
                <c:forEach var="review" items="${reviewList}">
                    <c:if test="${empty myReview or review.reviewId ne myReview.reviewId}">
                        <article class="review-list-card" data-fresh="${review.freshYn}" data-created="${review.createdAt}">
                            <div class="review-card-top">
                                <span class="review-burst-pill ${review.freshYn eq 'N' ? 'no' : ''}">
                                    <c:choose>
                                        <c:when test="${review.freshYn eq 'Y'}"><img src="${ctx}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌어요</c:when>
                                        <c:otherwise><img src="${ctx}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안 터졌어요</c:otherwise>
                                    </c:choose>
                                </span>
                                <span class="review-public-pill">
                                    <c:choose>
                                        <c:when test="${review.publicYn eq 'Y'}">공개</c:when>
                                        <c:otherwise>친구공개</c:otherwise>
                                    </c:choose>
                                </span>
                                <span class="review-meta-right">
                                    <c:out value="${review.memberName}" />
                                    <c:if test="${not empty review.createdAt}"> · ${review.createdAt}</c:if>
                                </span>
                            </div>
                            <div class="review-content-box"><c:out value="${review.content}" /></div>
                        </article>
                    </c:if>
                </c:forEach>
            </div>

            <div id="reviewNoResult" class="review-no-result" style="display:none;">
                조건에 맞는 리뷰가 없습니다.
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />

</div>

<!-- 상세페이지 리뷰 메뉴 버튼 추가함 -->
<script>
/* 리뷰 필터/정렬 버튼: JSP로 출력된 리뷰 카드들을 화면에서 바로 필터링한다. */
document.addEventListener('DOMContentLoaded', function () {
    const buttons = Array.from(document.querySelectorAll('.review-filter-btn'));
    const listArea = document.getElementById('reviewListArea');
    const noResult = document.getElementById('reviewNoResult');

    if (!listArea || buttons.length === 0) return;

    function sortCards(cards, direction) {
        cards.sort(function (a, b) {
            const aDate = a.dataset.created || '';
            const bDate = b.dataset.created || '';
            return direction === 'oldest'
                ? aDate.localeCompare(bDate)
                : bDate.localeCompare(aDate);
        });
        cards.forEach(function (card) {
            listArea.appendChild(card);
        });
    }

    function applyReviewFilter(filter) {
        buttons.forEach(function (btn) {
            btn.classList.toggle('active', btn.dataset.filter === filter);
        });

        const cards = Array.from(listArea.querySelectorAll('.review-list-card'));

        if (filter === 'oldest') {
            sortCards(cards, 'oldest');
        } else {
            // 전체/최신순/터진 리뷰/안터진 리뷰는 기본 최신순으로 정렬
            sortCards(cards, 'latest');
        }

        let visibleCount = 0;

        cards.forEach(function (card) {
            const freshYn = card.dataset.fresh;
            let visible = true;

            if (filter === 'fresh') {
                visible = freshYn === 'Y';
            } else if (filter === 'notFresh') {
                visible = freshYn === 'N';
            }

            card.style.display = visible ? '' : 'none';
            if (visible) visibleCount++;
        });

        if (noResult) {
            noResult.style.display = (cards.length > 0 && visibleCount === 0) ? 'block' : 'none';
        }
    }

    buttons.forEach(function (button) {
        button.addEventListener('click', function () {
            applyReviewFilter(button.dataset.filter || 'all');
        });
    });
});
</script>
</body>
</html>

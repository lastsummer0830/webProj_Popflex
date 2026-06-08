<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix 예매</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/reservation/scheduleList.css">
</head>
<body data-context-path="${ctx}">
  <div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main>
      <c:if test="${not empty errorMsg}">
        <p class="notice"><c:out value="${errorMsg}" /></p>
      </c:if>

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
          <div class="rating">
            <c:set var="ratingText" value="${empty movie.ratingGrade ? movie.rating : movie.ratingGrade}" />
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
            <span><c:out value="${movie.rating}" /></span>
          </div>
          <p class="description"><c:out value="${movie.plot}" /></p>

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
                <a class="btn" href="${movie.vodUrl}" target="_blank" rel="noopener">예고편 보기</a>
              </c:when>
              <c:otherwise>
                <button type="button" class="btn" disabled>예고편 없음</button>
              </c:otherwise>
            </c:choose>
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

      <div class="divider"></div>

      <c:choose>
        <c:when test="${not empty scheduleList}">
          <form id="reservationForm" action="${ctx}/reservation/insert.do" method="post">
            <input type="hidden" name="movieId" value="${movieId}">
            <div class="reservation-data" hidden>
              <select id="allScheduleSelect">
                <c:forEach var="schedule" items="${scheduleList}">
                  <option value="${schedule.scheduleId}"
                          data-theater-id="${schedule.theaterId}"
                          data-theater-name="${fn:escapeXml(schedule.theaterName)}"
                          data-screen-id="${schedule.screenId}"
                          data-screen-name="${fn:escapeXml(schedule.screenName)}"
                          data-date="${fn:substring(schedule.startTime, 0, 10)}"
                          data-time="${fn:substring(schedule.startTime, 11, 16)}"
                          data-price="${schedule.price}">
                    ${fn:substring(schedule.startTime, 11, 16)}
                  </option>
                </c:forEach>
              </select>
            </div>

            <section class="controls" id="bookingControls" aria-label="예매 조건 선택">
              <label class="field">
                <span>영화관</span>
                <select id="theaterSelect" aria-label="영화관 선택">
                  <option value="">선택</option>
                  <c:forEach var="theater" items="${theaterList}">
                    <option value="${theater.theaterId}">
                      <c:out value="${theater.theaterName}" />
                    </option>
                  </c:forEach>
                </select>
              </label>
              <label class="field">
                <span>상영관</span>
                <select id="screenSelect" aria-label="상영관 선택" disabled>
                  <option value="">선택</option>
                </select>
              </label>
              <label class="field">
                <span>날짜</span>
                <select id="dateSelect" aria-label="날짜 선택" disabled>
                  <option value="">선택</option>
                </select>
              </label>
              <label class="field">
                <span>시간</span>
                <select id="scheduleSelect" name="scheduleId" aria-label="시간 선택" disabled>
                  <option value="">선택</option>
                </select>
              </label>
              <label class="field">
                <span>인원</span>
                <select id="peopleSelect" aria-label="인원 선택" disabled>
                  <option value="">선택</option>
                  <option value="1">1</option>
                  <option value="2">2</option>
                  <option value="3">3</option>
                  <option value="4">4</option>
                  <option value="5">5</option>
                  <option value="6">6</option>
                </select>
              </label>
            </section>

            <section class="booking" id="bookingSection" aria-label="좌석 예매">
              <div>
                <div class="schedule">
                  <div>영화관: <strong><span id="theaterText">-</span></strong></div>
                  <div>상영관: <strong><span id="screenText">-</span></strong></div>
                  <div>날짜: <strong><span id="dateText">-</span></strong></div>
                  <div>시간: <strong><span id="timeText">-</span></strong></div>
                  <div>인원: <strong><span id="peopleText">0</span></strong></div>
                  <div>좌석: <strong><span id="seatText">-</span></strong></div>
                  <div>1인 금액: <strong><span id="unitPriceText">0</span>원</strong></div>
                  <div>총 금액: <strong><span id="totalPriceText">0</span>원</strong></div>
                </div>
                <button type="submit" class="btn reserve-btn" id="submitBookingButton" disabled>예매하기</button>
              </div>

              <div class="seat-panel">
                <div class="screen"></div>
                <div class="seat-map" id="seatMap" aria-label="좌석 선택"></div>
                <div class="legend">
                  <span class="legend-item">이미 예매된 좌석 <span class="legend-chip reserved" style="background: var(--reserved);"></span></span>
                  <span class="legend-item">예매 가능 <span class="legend-chip"></span></span>
                </div>
                <div class="notice" id="seatNotice">인원을 먼저 선택해 주세요.</div>
              </div>
            </section>

            <div id="selectedSeatInputs"></div>
          </form>
        </c:when>
        <c:otherwise>
          <p class="notice">등록된 상영 일정이 없습니다.</p>
        </c:otherwise>
      </c:choose>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
  </div>

  <script src="${ctx}/js/reservation/scheduleList.js"></script>
</body>
</html>

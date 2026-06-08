<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix 예매 변경</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/reservation/scheduleList.css">
</head>
<body data-context-path="${ctx}" data-current-seat-ids="${currentSeatIdCsv}" data-auto-open="true">
  <div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main>
      <section class="content page-title">
        <div>
          <h1>예매 변경</h1>
          <p>기존 좌석을 해제하고 인원수에 맞게 새 좌석을 선택합니다.</p>
        </div>
        <a class="btn" href="${ctx}/reservation/detail.do?reservationId=${reservation.reservation_id}">상세로</a>
      </section>

      <c:if test="${param.update == 'fail'}">
        <p class="message content">예매 변경에 실패했습니다. 이미 선택된 좌석이 있는지 확인해 주세요.</p>
      </c:if>

      <form id="reservationForm" action="${ctx}/reservation/update.do" method="post">
        <input type="hidden" name="reservationId" value="${reservation.reservation_id}">

        <section class="controls" id="bookingControls" aria-label="예매 변경 조건 선택">
          <label class="field">
            <span>날짜</span>
            <select id="dateSelect" aria-label="날짜 선택">
              <option value="${reservation.schedule_id}" data-price="${reservation.price}">
                <fmt:formatDate value="${reservation.startTime}" pattern="yyyy년 MM월 dd일" />
              </option>
            </select>
          </label>
          <label class="field">
            <span>시간</span>
            <select id="scheduleSelect" name="scheduleId" aria-label="시간 선택">
              <option value="${reservation.schedule_id}" data-price="${reservation.price}">
                <fmt:formatDate value="${reservation.startTime}" pattern="HH:mm" />
              </option>
            </select>
          </label>
          <label class="field">
            <span>인원</span>
            <select id="peopleSelect" aria-label="인원 선택">
              <c:forEach var="count" begin="1" end="6">
                <option value="${count}" ${reservation.headcount == count ? 'selected' : ''}>${count}</option>
              </c:forEach>
            </select>
          </label>
        </section>

        <section class="booking" id="bookingSection" aria-label="좌석 변경">
          <div>
            <div class="schedule">
              <div>날짜: <strong><span id="dateText">-</span></strong></div>
              <div>시간: <strong><span id="timeText">-</span></strong></div>
              <div>인원: <strong><span id="peopleText">0</span></strong></div>
              <div>좌석: <strong><span id="seatText">-</span></strong></div>
              <div>총 금액: <strong><span id="totalPriceText">0</span>원</strong></div>
            </div>
            <button type="submit" class="btn reserve-btn" id="submitBookingButton">변경하기</button>
          </div>

          <div class="seat-panel">
            <div class="screen"></div>
            <div class="seat-map" id="seatMap" aria-label="좌석 선택"></div>
            <div class="legend">
              <span class="legend-item">이미 예매된 좌석 <span class="legend-chip reserved" style="background: var(--reserved);"></span></span>
              <span class="legend-item">선택 좌석 <span class="legend-chip selected"></span></span>
              <span class="legend-item">예매 가능 <span class="legend-chip"></span></span>
            </div>
            <div class="notice" id="seatNotice">좌석을 불러오는 중입니다.</div>
          </div>
        </section>

        <div id="selectedSeatInputs"></div>
      </form>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
  </div>

  <script src="${ctx}/js/reservation/scheduleList.js"></script>
</body>
</html>

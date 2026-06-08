<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix 예매 완료</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/reservation/scheduleList.css">
</head>
<body>
  <div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main class="content">
      <section class="page-title">
        <div>
          <div class="complete-mark">✓</div>
          <h1>예매가 완료되었습니다</h1>
          <p>예매 내역은 마이페이지와 필름 다이어리에서 다시 확인할 수 있습니다.</p>
        </div>
        <a class="btn" href="${ctx}/reservation/myList.do">내 예매내역</a>
      </section>

      <section class="summary-panel" aria-label="예매 완료 정보">
        <div class="summary-grid">
          <div class="summary-item">
            <span>예매 번호</span>
            <strong><c:out value="${reservation.reservation_id}" /></strong>
          </div>
          <div class="summary-item">
            <span>영화</span>
            <strong><c:out value="${reservation.movieTitle}" /></strong>
          </div>
          <div class="summary-item">
            <span>상영 일시</span>
            <strong><fmt:formatDate value="${reservation.startTime}" pattern="yyyy.MM.dd HH:mm" /></strong>
          </div>
          <div class="summary-item">
            <span>영화관 / 상영관</span>
            <strong><c:out value="${reservation.theaterName}" /> <c:out value="${reservation.screenName}" /></strong>
          </div>
          <div class="summary-item">
            <span>좌석</span>
            <strong><c:out value="${reservation.seatNames}" /></strong>
          </div>
          <div class="summary-item">
            <span>금액</span>
            <strong><fmt:formatNumber value="${reservation.totalPrice}" pattern="#,##0" />원</strong>
          </div>
        </div>
      </section>

      <div class="action-row" style="margin-top: 22px;">
        <a class="btn" href="${ctx}/reservation/detail.do?reservationId=${reservation.reservation_id}">상세 보기</a>
        <a class="btn" href="${ctx}/movie/search.do">다른 영화 보기</a>
      </div>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
  </div>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix 마이페이지</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/mypage/mypage-style.css">
</head>
<body>
  <div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main class="content">
      <section class="page-title">
        <div>
          <h1>마이페이지</h1>
          <p>내 정보와 최근 예매 내역을 한 곳에서 확인하세요.</p>
        </div>
        <a class="btn" href="${ctx}/main.do">메인</a>
      </section>

      <div class="layout">
        <aside class="summary">
          <div class="profile-mark">
            <c:choose>
              <c:when test="${empty member.name}">M</c:when>
              <c:otherwise><c:out value="${fn:substring(member.name, 0, 1)}" /></c:otherwise>
            </c:choose>
          </div>
          <div class="summary-name"><c:out value="${member.name}" /></div>
          <div class="summary-id"><c:out value="${member.userId}" /></div>

          <nav class="mypage-menu" aria-label="마이페이지 메뉴">
            <a class="active" href="#my-info">내 정보</a>
            <a href="#reservation-summary">예매 내역</a>
            <a href="#review-summary">리뷰</a>
          </nav>

          <div class="meta-list">
            <div class="meta-row">
              <span>이메일</span>
              <strong><c:out value="${member.email}" /></strong>
            </div>
            <div class="meta-row">
              <span>권한</span>
              <strong>
                <c:choose>
                  <c:when test="${member.admin}">관리자</c:when>
                  <c:otherwise>일반회원</c:otherwise>
                </c:choose>
              </strong>
            </div>
            <div class="meta-row">
              <span>가입일</span>
              <strong><c:out value="${member.createdAt}" /></strong>
            </div>
          </div>

          <c:if test="${member.admin}">
            <a class="btn admin-btn" href="${ctx}/admin/main.do">관리자 페이지</a>
          </c:if>
        </aside>

        <div class="panel-stack">
          <form id="my-info" class="form-panel" action="${ctx}/member/update.do" method="post">
            <h2 class="section-title">회원정보 수정</h2>

            <c:if test="${not empty mypageMessage}">
              <div class="message ok"><c:out value="${mypageMessage}" /></div>
            </c:if>
            <c:if test="${not empty mypageError}">
              <div class="message error"><c:out value="${mypageError}" /></div>
            </c:if>
            <c:if test="${not empty errorMsg}">
              <div class="message error"><c:out value="${errorMsg}" /></div>
            </c:if>

            <label class="field" for="userId">
              <span>아이디</span>
              <input type="text" id="userId" value="${fn:escapeXml(member.userId)}" readonly>
            </label>

            <label class="field" for="name">
              <span>이름</span>
              <input type="text" id="name" name="name" value="${fn:escapeXml(member.name)}" required>
            </label>

            <label class="field" for="email">
              <span>이메일</span>
              <input type="email" id="email" name="email" value="${fn:escapeXml(member.email)}" required>
            </label>

            <div class="actions">
              <button class="btn submit-btn" type="submit">수정하기</button>
            </div>
          </form>

          <section id="reservation-summary" class="form-panel">
            <div class="section-head">
              <h2 class="section-title">예매 내역</h2>
              <a class="text-link" href="${ctx}/reservation/myList.do">전체보기</a>
            </div>

            <c:choose>
              <c:when test="${reservationLoadError}">
                <div class="empty-box">예매 내역을 불러오지 못했습니다.</div>
              </c:when>
              <c:when test="${empty reservationList}">
                <div class="empty-box">아직 예매 내역이 없습니다.</div>
              </c:when>
              <c:otherwise>
                <div class="reservation-list">
                  <c:forEach var="reservation" items="${reservationList}" varStatus="loop">
                    <c:if test="${loop.index lt 3}">
                      <article class="reservation-item">
                        <div class="item-top">
                          <div class="item-title"><c:out value="${reservation.movieTitle}" /></div>
                          <span class="status-badge ${reservation.statusText eq 'C' ? 'cancel' : ''}">
                            <c:choose>
                              <c:when test="${reservation.statusText eq 'C'}">취소</c:when>
                              <c:otherwise>예매완료</c:otherwise>
                            </c:choose>
                          </span>
                        </div>
                        <div class="item-meta">
                          상영:
                          <fmt:formatDate value="${reservation.startTime}" pattern="yyyy.MM.dd HH:mm" />
                          <br>
                          좌석:
                          <c:choose>
                            <c:when test="${empty reservation.seatNames}">좌석 정보 없음</c:when>
                            <c:otherwise><c:out value="${reservation.seatNames}" /></c:otherwise>
                          </c:choose>
                          · <c:out value="${reservation.headcount}" />명
                        </div>
                      </article>
                    </c:if>
                  </c:forEach>
                </div>
              </c:otherwise>
            </c:choose>
          </section>

          <section id="review-summary" class="form-panel">
            <div class="section-head">
              <h2 class="section-title">리뷰</h2>
              <a class="text-link" href="${ctx}/review/myList.do">전체보기</a>
            </div>

            <c:choose>
              <c:when test="${reviewLoadError}">
                <div class="empty-box">리뷰 내역을 불러올 수 없습니다.</div>
              </c:when>
              <c:when test="${empty reviewList}">
                <div class="empty-box">아직 작성한 리뷰가 없습니다.</div>
              </c:when>
              <c:otherwise>
                <div class="review-list">
                  <c:forEach var="review" items="${reviewList}" varStatus="loop">
                    <c:if test="${loop.index lt 3}">
                      <article class="review-item">
                        <div class="item-top">
                          <div class="item-title">
                            <c:choose>
                              <c:when test="${not empty review.movieTitle}">
                                <c:out value="${review.movieTitle}" />
                              </c:when>
                              <c:otherwise>영화 #<c:out value="${review.movieId}" /></c:otherwise>
                            </c:choose>
                          </div>
                          <span class="status-badge">
                            <c:choose>
                              <c:when test="${review.freshYn eq 'Y'}"><img src="${ctx}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌다</c:when>
                              <c:otherwise><img src="${ctx}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안터졌다</c:otherwise>
                            </c:choose>
                          </span>
                        </div>
                        <div class="item-meta">
                          <c:choose>
                            <c:when test="${review.publicYn eq 'Y'}">공개</c:when>
                            <c:otherwise>친구공개</c:otherwise>
                          </c:choose>
                          <c:if test="${not empty review.createdAt}">
                            · <c:out value="${review.createdAt}" />
                          </c:if>
                          <br>
                          <c:out value="${review.content}" />
                        </div>
                      </article>
                    </c:if>
                  </c:forEach>
                </div>
              </c:otherwise>
            </c:choose>
          </section>
        </div>
      </div>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
  </div>
</body>
</html>

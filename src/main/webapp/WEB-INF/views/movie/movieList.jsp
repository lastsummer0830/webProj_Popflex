<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix 영화 검색</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/movie/movie-style.css">
</head>
<body>
  <div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main class="content movie-list-content">
      <section class="page-title">
        <div>
          <h1>영화 검색</h1>
          <p>
            <c:choose>
              <c:when test="${result.hasQuery}">
                "<c:out value="${result.query}" />" 검색 결과
              </c:when>
              <c:otherwise>
                보고 싶은 영화를 검색해보세요.
              </c:otherwise>
            </c:choose>
          </p>
        </div>
        <form class="search-form" action="${ctx}/movie/search.do" method="get">
          <input type="text"
                 name="query"
                 value="${fn:escapeXml(result.query)}"
                 placeholder="영화 제목을 입력하세요">
          <button type="submit">검색</button>
        </form>
      </section>

      <c:if test="${result.hasQuery}">
        <div class="result-count">
          전체 검색 결과 <c:out value="${result.paging.totalCount}" />건
          <c:if test="${result.paging.totalPage > 0}">
            / <c:out value="${result.paging.currentPage}" /> 페이지
          </c:if>
        </div>
      </c:if>

      <c:choose>
        <c:when test="${result.hasResult}">
          <section class="movie-grid" aria-label="영화 검색 결과">
            <c:forEach var="movie" items="${result.movieItems}">
              <article class="movie-card">
                <div class="poster">
                  <c:choose>
                    <c:when test="${not empty movie.posterUrl}">
                      <img src="${fn:escapeXml(movie.posterUrl)}" alt="${fn:escapeXml(movie.title)} 포스터">
                    </c:when>
                    <c:otherwise>
                      <div class="no-poster">NO IMAGE</div>
                    </c:otherwise>
                  </c:choose>
                </div>
                <div class="movie-card-body">
                  <h2><c:out value="${movie.title}" /></h2>
                  <p><c:out value="${movie.directorNm}" /></p>
                  <strong><c:out value="${movie.displayReleaseDate}" /></strong>
                </div>
                <c:url var="detailUrl" value="/movie/detail.do">
                  <c:param name="movieId" value="${movie.kmdbMovieId}" />
                  <c:param name="movieSeq" value="${movie.kmdbMovieSeq}" />
                </c:url>
                <a class="btn card-btn" href="${detailUrl}">상세보기</a>
              </article>
            </c:forEach>
          </section>

          <c:if test="${result.paging.totalPage > 1}">
            <nav class="paging" aria-label="검색 결과 페이지">
              <c:if test="${result.paging.hasPrev}">
                <c:url var="prevUrl" value="/movie/search.do">
                  <c:param name="query" value="${result.query}" />
                  <c:param name="page" value="${result.paging.prevPage}" />
                </c:url>
                <a class="page-link" href="${prevUrl}">이전</a>
              </c:if>

              <c:forEach var="pageNo" items="${result.paging.pageNumbers}">
                <c:choose>
                  <c:when test="${pageNo == result.paging.currentPage}">
                    <span class="page-link active"><c:out value="${pageNo}" /></span>
                  </c:when>
                  <c:otherwise>
                    <c:url var="pageUrl" value="/movie/search.do">
                      <c:param name="query" value="${result.query}" />
                      <c:param name="page" value="${pageNo}" />
                    </c:url>
                    <a class="page-link" href="${pageUrl}"><c:out value="${pageNo}" /></a>
                  </c:otherwise>
                </c:choose>
              </c:forEach>

              <c:if test="${result.paging.hasNext}">
                <c:url var="nextUrl" value="/movie/search.do">
                  <c:param name="query" value="${result.query}" />
                  <c:param name="page" value="${result.paging.nextPage}" />
                </c:url>
                <a class="page-link" href="${nextUrl}">다음</a>
              </c:if>
            </nav>
          </c:if>
        </c:when>
        <c:otherwise>
          <section class="empty-state">
            <div class="empty-mark">?</div>
            <h2>
              <c:choose>
                <c:when test="${result.hasQuery}">검색 결과가 없습니다</c:when>
                <c:otherwise>검색어를 입력해주세요</c:otherwise>
              </c:choose>
            </h2>
            <p>영화 제목을 확인하거나 다른 검색어로 다시 검색해보세요.</p>
          </section>
        </c:otherwise>
      </c:choose>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
  </div>
</body>
</html>

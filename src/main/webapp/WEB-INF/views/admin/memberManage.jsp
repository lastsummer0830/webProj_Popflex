<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>POPFLEX - 회원 관리</title>
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
            <h1>회원 관리</h1>
            <div class="quick-actions">
                <a class="button" href="${ctx}/admin/main.do">관리자 홈</a>
                <form class="search-form" action="${ctx}/admin/memberList.do" method="get">
                    <input type="text" name="keyword" value="${fn:escapeXml(keyword)}" placeholder="아이디, 이름, 이메일 검색">
                    <button class="button" type="submit">검색</button>
                    <a class="button" href="${ctx}/admin/memberList.do">초기화</a>
                </form>
            </div>
        </div>

        <div class="table-wrap">
            <c:choose>
                <c:when test="${empty members}">
                    <div class="empty">조회된 회원이 없습니다.</div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>번호</th>
                                <th>아이디</th>
                                <th>이름</th>
                                <th>이메일</th>
                                <th>가입 유형</th>
                                <th>상태</th>
                                <th>권한</th>
                                <th>가입일</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="member" items="${members}">
                                <tr>
                                    <td><c:out value="${member.memberId}" /></td>
                                    <td><c:out value="${member.userId}" /></td>
                                    <td class="member-name"><c:out value="${member.name}" /></td>
                                    <td><c:out value="${member.email}" /></td>
                                    <td><c:out value="${member.socialType}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${member.active}">
                                                <span class="badge user">활성</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge inactive">탈퇴</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${member.admin}">
                                                <span class="badge admin">관리자</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge user">일반</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="muted"><c:out value="${member.createdAt}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${member.admin or not member.active}">
                                                <button class="role-button" type="button" disabled>권한 부여</button>
                                            </c:when>
                                            <c:otherwise>
                                                <form action="${ctx}/admin/memberRoleUpdate.do" method="post"
                                                      onsubmit="return confirm('이 회원에게 관리자 권한을 부여하시겠습니까?');">
                                                    <input type="hidden" name="memberId" value="${member.memberId}">
                                                    <input type="hidden" name="keyword" value="${fn:escapeXml(keyword)}">
                                                    <button class="role-button" type="submit">권한 부여</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
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

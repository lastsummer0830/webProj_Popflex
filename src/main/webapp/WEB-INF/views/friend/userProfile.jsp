<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>POPFLIX - 유저 프로필</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/friend/friendList.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/friend/friend-style.css">
</head>
<body>
<div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />
    <%-- ===== MAIN ===== --%>
    <main class="friend-main">

        <%-- 본인 프로필 여부: FriendCheckServlet에서 본인은 isFriend=true가 될 수 있으므로 JSP에서 분리 --%>
        <c:set var="isMe"
               value="${not empty sessionScope.loginMember and sessionScope.loginMember.memberId eq profileMember.memberId}" />

        <%-- 뒤로가기 --%>
        <div class="profile-back-row">
            <a href="${pageContext.request.contextPath}/friend/list.do"
               class="btn-back">← 친구 목록으로</a>
        </div>

        <%-- ===== 프로필 카드 ===== --%>
        <section class="profile-section">
            <div class="profile-card">
                <div class="profile-avatar"></div>
                <div class="profile-info">
                    <p class="profile-name">${profileMember.name}</p>
                    <p class="profile-userid">@${profileMember.userId}</p>
                </div>
                
                <a class="btn-review-view"
                   href="${pageContext.request.contextPath}/review/myList.do?memberId=${profileMember.memberId}">리뷰 보기</a>
                
                <c:choose>
                    <%-- 본인 프로필이면 친구 추가/삭제 버튼 숨김 --%>
                    <c:when test="${isMe}">
                        <span class="result-status already">내 프로필</span>
                    </c:when>

                    <%-- 진짜 친구면 친구 삭제 --%>
                    <c:when test="${isFriend}">
                        <button class="btn-delete"
                                data-target-id="${profileMember.memberId}"
                                onclick="deleteFriendFromProfile(this)">친구 삭제</button>
                    </c:when>

                    <%-- 본인도 아니고 친구도 아니면 친구 추가 --%>
                    <c:otherwise>
                        <button class="btn-friend-add"
                                onclick="addFriendFromProfile('${profileMember.userId}')">친구 추가</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <%-- ===== 리뷰 목록 ===== --%>
        <section class="profile-review-section">
            <div class="friend-list-header">
                <p class="section-label">
                    ${profileMember.name}님의
                    <c:choose>
                        <c:when test="${isMe}">내 리뷰</c:when>
                        <c:when test="${isFriend}">리뷰 (친구공개 포함)</c:when>
                        <c:otherwise>공개 리뷰</c:otherwise>
                    </c:choose>
                </p>
                <span class="friend-count">
                    총 <strong>${reviewList.size()}</strong>개
                </span>
            </div>

            <c:choose>
                <c:when test="${not empty reviewList}">
                    <div class="profile-review-grid">
                        <c:forEach var="r" items="${reviewList}">
                            <div class="profile-review-card">
                                <div class="review-top-row">
                                    <span class="review-movie-title">${r.movieTitle}</span>
                                    <span class="review-badge ${r.freshYn eq 'Y' ? 'fresh' : 'rotten'}">
                                        <c:choose>
                                            <c:when test="${r.freshYn eq 'Y'}">
                                                <img src="${pageContext.request.contextPath}/img/popped.png" alt="터졌다" width="18" height="18"> 터졌다
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/img/unpopcorn.png" alt="안터졌다" width="18" height="18"> 안터졌다
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                    <c:if test="${r.publicYn eq 'N'}">
                                        <span class="review-private-badge">친구공개</span>
                                    </c:if>
                                </div>
                                <p class="review-content">${r.content}</p>
                                <span class="review-date">
                                    <fmt:formatDate value="${r.createdAt}" pattern="yyyy.MM.dd" />
                                </span>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="no-friend-msg">
                        <p>아직 작성된 리뷰가 없습니다.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

    </main>
    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
</div>

<script>
function addFriendFromProfile(targetUserId) {
    fetch('${pageContext.request.contextPath}/friend/insert.do', {
        method : 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body   : 'targetUserId=' + encodeURIComponent(targetUserId)
    })
    .then(r => r.json())
    .then(data => {
        if (data.result === 'OK') {
            alert('친구가 추가되었습니다!');
            location.reload();
        } else if (data.result === 'DUPLICATE') {
            alert('이미 친구입니다.');
        } else if (data.result === 'SELF') {
            alert('자기 자신은 추가할 수 없습니다.');
        } else if (data.result === 'NOT_FOUND') {
            alert('존재하지 않는 회원입니다.');
        } else if (data.result === 'NOT_LOGIN') {
            alert('로그인이 필요합니다.');
            location.href = '${pageContext.request.contextPath}/login.do';
        } else {
            alert('오류가 발생했습니다.');
        }
    })
    .catch(() => alert('서버 오류가 발생했습니다.'));
}

function deleteFriendFromProfile(btn) {
    const targetId = btn.dataset.targetId;
    if (!confirm('친구를 삭제하시겠습니까?')) return;

    fetch('${pageContext.request.contextPath}/friend/delete.do', {
        method : 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body   : 'targetMemberId=' + encodeURIComponent(targetId)
    })
    .then(r => r.json())
    .then(data => {
        if (data.result === 'OK') {
            alert('친구가 삭제되었습니다.');
            location.reload();
        } else if (data.result === 'NOT_LOGIN') {
            alert('로그인이 필요합니다.');
            location.href = '${pageContext.request.contextPath}/login.do';
        } else {
            alert('삭제 중 오류가 발생했습니다.');
        }
    })
    .catch(() => alert('서버 오류가 발생했습니다.'));
}
</script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>POPFLIX - 친구 관리</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/friend/friendList.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/friend/friend-style.css">
</head>
<body>
<div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />
    <%-- ===== MAIN ===== --%>
    <main class="friend-main">

        <%-- 페이지 타이틀 --%>
        <div class="friend-title-area">
            <h1 class="friend-title">친구 관리</h1>
            <p class="friend-desc">
                친구를 추가하면 친구의 비공개 리뷰를 확인할 수 있습니다.
            </p>
        </div>

        <%-- ===== 친구 검색 + 추가 ===== --%>
        <section class="friend-search-section">
            <p class="section-label">친구 검색</p>

            <div class="search-bar-row">
                <input type="text"
                       id="searchInput"
                       class="search-input"
                       placeholder="친구의 아이디를 입력하세요"
                       maxlength="30">
                <button type="button" class="btn-search" onclick="searchMember()">검색</button>
                <button type="button" class="btn-friend-add"
                        id="addBtn" style="display:none"
                        onclick="addFriend()">친구 추가</button>
            </div>

            <%-- 검색 결과 카드 --%>
            <div class="search-result-row" id="searchResult" style="display:none">
                <div class="search-result-card">
                    <div class="avatar-sm"></div>
                    <div class="search-result-info">
                        <span class="result-name"   id="resultName"></span>
                        <span class="result-userid" id="resultUserId"></span>
                    </div>
                    <span class="result-status" id="resultStatus"></span>
                </div>
            </div>

            <p class="search-msg" id="searchMsg"></p>
        </section>

        <%-- ===== 내 친구 목록 ===== --%>
        <section class="friend-list-section">
            <div class="friend-list-header">
                <p class="section-label">내 친구 목록</p>
                <span class="friend-count">
                    총 <strong>${friendList.size()}</strong>명
                </span>
            </div>

            <c:choose>
                <c:when test="${not empty friendList}">
                    <div class="friend-grid">
                        <c:forEach var="f" items="${friendList}">
                            <div class="friend-card" id="card-${f.friendId}">
                                <div class="avatar"></div>
                                <div class="friend-info">
                                    <span class="friend-name">${f.friendName}</span>
                                    <span class="friend-userid">@${f.friendUserId}</span>
                                    <span class="friend-since">
                                        <fmt:formatDate value="${f.createdAt}"
                                                        pattern="yyyy.MM.dd"/> 친구 추가
                                    </span>
                                </div>
                                <div class="friend-card-actions">
                                    <a class="btn-review-view"
                                       href="${pageContext.request.contextPath}/review/myList.do?memberId=${sessionScope.loginMember.memberId == f.memberAId ? f.memberBId : f.memberAId}">리뷰 보기</a>
                                    <button class="btn-delete"
                                            data-friend-id="${f.friendId}"
                                            data-target-id="${sessionScope.loginMember.memberId == f.memberAId
                                                              ? f.memberBId : f.memberAId}"
                                            onclick="deleteFriend(this)">삭제하기</button>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="no-friend-msg">
                        <p>아직 친구가 없습니다.</p>
                        <p>친구를 추가하면 친구의 비공개 리뷰도 확인할 수 있습니다.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

    </main>
    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
</div>

<%-- 숨겨진 검색 결과 보관 --%>
<input type="hidden" id="foundMemberId" value="">
<input type="hidden" id="foundUserId"   value="">
<input type="hidden" id="alreadyFriend" value="">

<script>
/*회원 검색*/
function searchMember() {
    const keyword = document.getElementById('searchInput').value.trim();
    const msg     = document.getElementById('searchMsg');
    const result  = document.getElementById('searchResult');
    const addBtn  = document.getElementById('addBtn');

    msg.textContent      = '';
    result.style.display = 'none';
    addBtn.style.display = 'none';

    if (!keyword) {
        msg.textContent = '아이디를 입력해주세요.';
        msg.className   = 'search-msg error';
        return;
    }

    fetch('${pageContext.request.contextPath}/friend/searchMember.do?userId='
          + encodeURIComponent(keyword))
        .then(r => r.json())
        .then(data => {
            if (data.result === 'OK') {
                document.getElementById('resultName').textContent   = data.name;
                document.getElementById('resultUserId').textContent = '@' + data.userId;
                document.getElementById('foundMemberId').value      = data.memberId;
                document.getElementById('foundUserId').value        = data.userId;
                document.getElementById('alreadyFriend').value      = data.alreadyFriend;

                const statusEl = document.getElementById('resultStatus');
                if (data.alreadyFriend) {
                    statusEl.textContent = '이미 친구';
                    statusEl.className   = 'result-status already';
                    addBtn.style.display = 'none';
                } else {
                    statusEl.textContent = '친구 추가 가능';
                    statusEl.className   = 'result-status possible';
                    addBtn.style.display = 'inline-block';
                }
                result.style.display = 'flex';

            } else if (data.result === 'SELF') {
                msg.textContent = '자기 자신은 추가할 수 없습니다.';
                msg.className   = 'search-msg error';
            } else if (data.result === 'NOT_FOUND') {
                msg.textContent = '존재하지 않는 아이디입니다.';
                msg.className   = 'search-msg error';
            } else {
                msg.textContent = '검색 중 오류가 발생했습니다.';
                msg.className   = 'search-msg error';
            }
        })
        .catch(() => {
            msg.textContent = '서버와 통신 중 오류가 발생했습니다.';
            msg.className   = 'search-msg error';
        });
}

/*친구 추가*/
function addFriend() {
    const targetUserId = document.getElementById('foundUserId').value;
    if (!targetUserId) return;

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
        } else {
            alert('오류가 발생했습니다. 다시 시도해주세요.');
        }
    })
    .catch(() => alert('서버와 통신 중 오류가 발생했습니다.'));
}

/*친구 삭제*/
function deleteFriend(btn) {
    const targetId = btn.dataset.targetId;
    const friendId = btn.dataset.friendId;

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
            const card = document.getElementById('card-' + friendId);
            if (card) card.remove();
            const countEl = document.querySelector('.friend-count strong');
            if (countEl) countEl.textContent = parseInt(countEl.textContent) - 1;
        } else {
            alert('삭제 중 오류가 발생했습니다.');
        }
    })
    .catch(() => alert('서버와 통신 중 오류가 발생했습니다.'));
}

/*엔터키 지원*/
document.getElementById('searchInput')
        .addEventListener('keydown', e => { if (e.key === 'Enter') searchMember(); });
</script>
</body>
</html>

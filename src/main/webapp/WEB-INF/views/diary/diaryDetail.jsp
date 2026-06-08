<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>>${diary.movieTitle} — 필름 다이어리</title>

<style>
* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	font-family: 'Pretendard', 'Apple SD Gothic Neo', 'Malgun Gothic',
		sans-serif;
	background: #f0ece4;
	color: #1a1816;
}

.container {
	max-width: 720px;
	margin: 40px auto;
	padding: 0 20px;
}

.back-link {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	text-decoration: none;
	color: #888;
	font-size: 13px;
	margin-bottom: 20px;
}

.back-link:hover {
	color: #e8a838;
}

/* 티켓 카드 디자인 */
.ticket-card {
	background: white;
	border-radius: 16px;
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
	overflow: hidden;
}

.ticket-top {
	display: flex;
	gap: 0;
}

.ticket-poster {
	width: 180px;
	min-width: 180px;
	object-fit: cover;
	display: block;
}

.ticket-poster-placeholder {
	width: 180px;
	min-width: 180px;
	background: #2d2a26;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 48px;
}

.ticket-info {
	padding: 28px 24px;
	flex: 1;
}

.ticket-movie-title {
	font-size: 22px;
	font-weight: 900;
	margin-bottom: 8px;
}

.ticket-meta {
	display: flex;
	flex-direction: column;
	gap: 6px;
	color: #5a534c;
	font-size: 13px;
}

.ticket-meta span {
	display: flex;
	align-items: center;
	gap: 6px;
}

.ticket-stars {
	font-size: 22px;
	color: #e8a838;
	margin: 12px 0;
}

.ticket-divider {
	border: none;
	border-top: 2px dashed #e2ddd8;
	margin: 0 20px;
	position: relative;
}

.ticket-divider::before, .ticket-divider::after {
	content: '';
	position: absolute;
	top: -12px;
	width: 22px;
	height: 22px;
	background: #f0ece4;
	border-radius: 50%;
}

.ticket-divider::before {
	left: -32px;
}

.ticket-divider::after {
	right: -32px;
}

.ticket-bottom {
	padding: 20px 28px;
}

.ticket-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 14px;
}

.ticket-tag {
	background: #fff3dc;
	border: 1px solid #f0c84a;
	border-radius: 16px;
	padding: 4px 12px;
	font-size: 12px;
	color: #7a5a00;
	font-weight: 600;
}

.ticket-no-tag {
	color: #bbb;
	font-size: 13px;
}

.btn-edit {
	display: inline-block;
	background: #e8a838;
	color: white;
	border: none;
	border-radius: 8px;
	padding: 10px 24px;
	font-size: 14px;
	font-weight: 700;
	cursor: pointer;
	text-decoration: none;
	margin-top: 8px;
}

.btn-edit:hover {
	background: #d4942a;
}

.btn-back {
	display: inline-block;
	background: #f5f3ef;
	color: #5a534c;
	border: 1px solid #e2ddd8;
	border-radius: 8px;
	padding: 10px 24px;
	font-size: 14px;
	font-weight: 700;
	cursor: pointer;
	text-decoration: none;
	margin-top: 8px;
	margin-left: 8px;
}

/* 태그 수정 모달 (filmDiary.jsp와 동일) */
.modal-backdrop {
	display: none;
	position: fixed;
	inset: 0;
	background: rgba(0, 0, 0, 0.45);
	z-index: 500;
}

.modal-backdrop.open {
	display: flex;
	align-items: center;
	justify-content: center;
}

.modal {
	background: white;
	border-radius: 14px;
	padding: 28px;
	width: 500px;
	max-width: 95vw;
	max-height: 80vh;
	overflow-y: auto;
}

.modal-title {
	font-size: 18px;
	font-weight: 800;
	margin-bottom: 20px;
}

.tag-grid {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 16px;
}

.tag-cb {
	display: none;
}

.tag-label {
	background: #f5f3ef;
	border: 1px solid #e2ddd8;
	border-radius: 16px;
	padding: 5px 12px;
	font-size: 12px;
	cursor: pointer;
	transition: all 0.15s;
}

.tag-cb:checked+.tag-label {
	background: #fff3dc;
	border-color: #e8a838;
	color: #7a5a00;
	font-weight: 700;
}

.btn-save {
	background: #e8a838;
	color: white;
	border: none;
	border-radius: 8px;
	padding: 10px;
	width: 100%;
	font-size: 14px;
	font-weight: 700;
	cursor: pointer;
}

.btn-close {
	background: none;
	border: none;
	font-size: 20px;
	float: right;
	cursor: pointer;
	color: #aaa;
}
</style>

</head>
<body>

	<div class="container">
		<a href="${pageContext.request.contextPath}/diary/list.do"
			class="back-link">← 다이어리 목록으로</a>

		<div class="ticket-card">
			<div class="ticket-top">
				<!-- 포스터 -->
				<c:choose>
					<c:when test="${not empty diary.posterUrl}">
						<img class="ticket-poster" src="${diary.posterUrl}"
							alt="${diary.movieTitle}">
					</c:when>
					<c:otherwise>
						<div class="ticket-poster-placeholder">🎬</div>
					</c:otherwise>
				</c:choose>

				<!-- 영화 정보 -->
				<div class="ticket-info">
					<div class="ticket-movie-title">${diary.movieTitle}</div>
					<div class="ticket-meta">
						<span>📅 관람일: <fmt:formatDate value="${diary.watchDate}"
								pattern="yyyy년 MM월 dd일" />
						</span>
						<c:if test="${not empty diary.theaterName}">
							<span>🎭 극장: ${diary.theaterName} ${diary.screenName}</span>
						</c:if>
						<c:if test="${diary.runtime > 0}">
							<span>⏱ 상영시간: ${diary.runtime}분</span>
						</c:if>
					</div>
					<c:if test="${diary.starRating > 0}">
						<div class="ticket-stars">
							⭐
							<fmt:formatNumber value="${diary.starRating}"
								maxFractionDigits="1" />
							/ 5.0
						</div>
					</c:if>
					<c:if test="${diary.starRating == 0}">
						<div style="color: #bbb; font-size: 13px; margin-top: 10px;">별점
							미입력</div>
					</c:if>
				</div>
			</div>

			<hr class="ticket-divider">

			<div class="ticket-bottom">
				<div style="font-size: 13px; font-weight: 700; margin-bottom: 10px;">😊
					감정 태그</div>
				<div class="ticket-tags">
					<c:choose>
						<c:when test="${not empty diary.tagList}">
							<c:forEach var="tag" items="${diary.tagList}">
								<span class="ticket-tag">${tag}</span>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<span class="ticket-no-tag">태그가 없어요. 수정 버튼으로 추가해보세요!</span>
						</c:otherwise>
					</c:choose>
				</div>
				<!-- 수정 버튼 (모달 오픈) -->
				<button class="btn-edit" onclick="openModal()">✏️ 태그·별점 수정</button>
				<a href="${pageContext.request.contextPath}/diary/list.do"
					class="btn-back">목록으로</a>
			</div>
		</div>
	</div>

	<!-- 태그 수정 모달 -->
	<div class="modal-backdrop" id="tagModal">
		<div class="modal">
			<button class="btn-close"
				onclick="document.getElementById('tagModal').classList.remove('open')">&times;</button>
			<div class="modal-title">감정 태그 & 별점 수정</div>
			<form action="${pageContext.request.contextPath}/diary/tagUpdate.do"
				method="post">
				<input type="hidden" name="diaryId" value="${diary.diaryId}">

				<!-- 별점 -->
				<div style="margin-bottom: 16px;">
					<div style="font-size: 13px; font-weight: 700; margin-bottom: 8px;">⭐
						별점</div>
					<div id="starRow"></div>
					<input type="hidden" name="starRating" id="starRatingInput"
						value="${diary.starRating}">
				</div>

				<!-- 태그 선택 -->
				<div style="margin-bottom: 16px;">
					<div style="font-size: 13px; font-weight: 700; margin-bottom: 8px;">😊
						감정 태그</div>
					<div class="tag-grid">
						<c:forEach var="tag" items="${allTags}">
							<c:set var="checked" value="false" />
							<c:forEach var="myTag" items="${diary.tagList}">
								<c:if test="${myTag eq tag.tagName}">
									<c:set var="checked" value="true" />
								</c:if>
							</c:forEach>
							<input type="checkbox" class="tag-cb" name="tagIds"
								id="dtag_${tag.tagId}" value="${tag.tagId}"
								${checked ? 'checked' : ''}>
							<label class="tag-label" for="dtag_${tag.tagId}">${tag.tagName}</label>
						</c:forEach>
					</div>
				</div>

				<button type="submit" class="btn-save">저장하기</button>
			</form>
		</div>
	</div>

	<script>
		function openModal() {
			document.getElementById('tagModal').classList.add('open');
			renderStarUI(parseFloat('${diary.starRating}') || 0);
		}
		document.getElementById('tagModal').addEventListener('click',
				function(e) {
					if (e.target === this)
						this.classList.remove('open');
				});

		function renderStarUI(selectedVal) {
			const row = document.getElementById('starRow');
			row.innerHTML = '';
			for (let i = 0.5; i <= 5.0; i += 0.5) {
				const btn = document.createElement('button');
				btn.type = 'button';
				btn.style.cssText = 'background:none;border:none;cursor:pointer;font-size:24px;padding:0 2px;';
				btn.textContent = i <= selectedVal ? '⭐' : '☆';
				btn.dataset.val = i;
				btn.onclick = function() {
					document.getElementById('starRatingInput').value = this.dataset.val;
					renderStarUI(parseFloat(this.dataset.val));
				};
				row.appendChild(btn);
			}
		}
		renderStarUI(parseFloat('${diary.starRating}') || 0);
	</script>

</body>
</html>
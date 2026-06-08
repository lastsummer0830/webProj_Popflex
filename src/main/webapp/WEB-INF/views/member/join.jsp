<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Popflix 회원가입</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/member/member-style.css">
</head>
<body>
  <div class="page">
    <jsp:include page="/WEB-INF/views/common/site-header.jsp" />

    <main class="auth-content auth-join">
      <form class="form-panel" id="joinForm" action="${ctx}/join.do" method="post">
        <div class="brand-hero">
          <img src="${ctx}/img/popflex-logo.png" alt="POPFLIX">
        </div>
        <section class="page-title auth-title">
          <div>
            <h1>회원가입</h1>
            <p>Popflix에서 영화 기록과 예매를 시작하세요.</p>
          </div>
        </section>

        <c:if test="${not empty errorMsg}">
          <div class="message error"><c:out value="${errorMsg}" /></div>
        </c:if>

        <c:if test="${not empty sessionScope.naverProfile}">
          <div class="message ok">
            네이버 계정 확인이 완료되었습니다. Popflix에서 사용할 아이디를 입력해 주세요.
          </div>
        </c:if>

        <input type="hidden" id="idCheckPassed" name="idCheckPassed" value="false">
        <input type="hidden" id="checkedUserId" name="checkedUserId" value="">

        <label class="field" for="userId">
          <span>아이디</span>
          <div class="id-row">
            <input type="text"
                   id="userId"
                   name="userId"
                   maxlength="30"
                   autocomplete="username"
                   value="${fn:escapeXml(member.userId)}"
                   required>
            <button class="check-btn" id="idCheckBtn" type="button">중복확인</button>
          </div>
          <div class="field-note" id="idCheckMessage"></div>
        </label>

        <c:if test="${empty sessionScope.naverProfile}">
          <label class="field" for="password">
            <span>비밀번호</span>
            <input type="password" id="password" name="password" minlength="4" autocomplete="new-password" required>
          </label>

          <label class="field" for="passwordConfirm">
            <span>비밀번호 확인</span>
            <input type="password" id="passwordConfirm" autocomplete="new-password" required>
            <div class="field-note" id="passwordMessage"></div>
          </label>
        </c:if>

        <label class="field" for="name">
          <span>이름</span>
          <input type="text"
                 id="name"
                 name="name"
                 value="${not empty sessionScope.naverProfile ? fn:escapeXml(sessionScope.naverProfile.name) : fn:escapeXml(member.name)}"
                 ${not empty sessionScope.naverProfile ? 'readonly' : ''}
                 required>
        </label>

        <label class="field" for="email">
          <span>이메일</span>
          <input type="email"
                 id="email"
                 name="email"
                 value="${not empty sessionScope.naverProfile ? fn:escapeXml(sessionScope.naverProfile.email) : fn:escapeXml(member.email)}"
                 ${not empty sessionScope.naverProfile ? 'readonly' : ''}
                 required>
        </label>

        <button class="btn submit-btn" type="submit">가입하기</button>

        <div class="sub-link">
          이미 계정이 있다면 <a href="${ctx}/login.do">로그인</a>
        </div>
      </form>
    </main>

    <jsp:include page="/WEB-INF/views/common/site-footer.jsp" />
  </div>

  <script>
    const contextPath = '${ctx}';
    const joinForm = document.getElementById('joinForm');
    const userIdInput = document.getElementById('userId');
    const idCheckBtn = document.getElementById('idCheckBtn');
    const idCheckPassed = document.getElementById('idCheckPassed');
    const checkedUserId = document.getElementById('checkedUserId');
    const idCheckMessage = document.getElementById('idCheckMessage');
    const passwordInput = document.getElementById('password');
    const passwordConfirmInput = document.getElementById('passwordConfirm');
    const passwordMessage = document.getElementById('passwordMessage');
    const naverJoin = ${not empty sessionScope.naverProfile};

    function setIdCheckMessage(text, passed) {
      idCheckMessage.textContent = text;
      idCheckMessage.className = 'field-note ' + (passed ? 'ok' : 'fail');
    }

    function resetIdCheck() {
      idCheckPassed.value = 'false';
      checkedUserId.value = '';
      idCheckMessage.textContent = '';
      idCheckMessage.className = 'field-note';
    }

    userIdInput.addEventListener('input', resetIdCheck);

    idCheckBtn.addEventListener('click', function () {
      const userId = userIdInput.value.trim();

      if (!userId) {
        setIdCheckMessage('아이디를 입력해 주세요.', false);
        userIdInput.focus();
        return;
      }

      fetch(contextPath + '/member/idCheck.do?userId=' + encodeURIComponent(userId), {
        headers: {
          'Accept': 'application/json'
        }
      })
        .then(function (response) {
          if (!response.ok) {
            throw new Error('id check failed');
          }
          return response.json();
        })
        .then(function (data) {
          const available = data.available === true;
          idCheckPassed.value = available ? 'true' : 'false';
          checkedUserId.value = available ? userId : '';
          setIdCheckMessage(available ? '사용 가능한 아이디입니다.' : '사용할 수 없는 아이디입니다.', available);
        })
        .catch(function () {
          idCheckPassed.value = 'false';
          checkedUserId.value = '';
          setIdCheckMessage('아이디 중복 확인 중 오류가 발생했습니다.', false);
        });
    });

    function validatePassword() {
      if (naverJoin) {
        return true;
      }

      if (!passwordConfirmInput.value) {
        passwordMessage.textContent = '';
        passwordMessage.className = 'field-note';
        return false;
      }

      const matched = passwordInput.value === passwordConfirmInput.value;
      passwordMessage.textContent = matched ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.';
      passwordMessage.className = 'field-note ' + (matched ? 'ok' : 'fail');
      return matched;
    }

    if (!naverJoin) {
      passwordInput.addEventListener('input', validatePassword);
      passwordConfirmInput.addEventListener('input', validatePassword);
    }

    joinForm.addEventListener('submit', function (event) {
      if (idCheckPassed.value !== 'true' || checkedUserId.value !== userIdInput.value.trim()) {
        event.preventDefault();
        setIdCheckMessage('아이디 중복 확인을 완료해 주세요.', false);
        userIdInput.focus();
        return;
      }

      if (!validatePassword()) {
        event.preventDefault();
        if (passwordConfirmInput) {
          passwordConfirmInput.focus();
        }
      }
    });
  </script>
</body>
</html>

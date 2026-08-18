<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<%-- ══ 팝콘 연출 강화 (0819) — 알갱이 12→24개, 크기 확대, 좌우 여백까지 배치.
       팝콘 CSS가 페이지별 파일 10곳에 중복돼 있어 여기서 한 번에 덮어쓴다.
       푸터는 모든 페이지에 include되고 head CSS보다 뒤에 오므로 우선한다. --%>
<style>
  /* 푸터에 붙어 있어서 아래로 긴 페이지에선 화면 밖에 있었다 → 화면 기준으로 올린다 */
  .footer-popcorn {
    position: fixed !important; left: 0 !important; right: 0 !important;
    top: auto !important; bottom: 0 !important;
    height: 100vh !important; min-height: 0 !important;
    z-index: -1 !important;            /* 내용 뒤로만 지나간다 */
    pointer-events: none !important;
  }
  /* 화면 전체를 가로지르도록 상승 폭을 넓힌다 */
  @keyframes popcornBounce {
    0%   { opacity: 0; transform: translate3d(0, 18px, 0) scale(0.84) rotate(0deg); }
    8%   { opacity: 0.95; }
    52%  { opacity: 1; transform: translate3d(var(--kernel-drift, 0), -86vh, 0) scale(1) rotate(var(--kernel-rotate, 210deg)); }
    100% { opacity: 0; transform: translate3d(var(--kernel-end-drift, 0), -104vh, 0) scale(0.9) rotate(var(--kernel-end-rotate, 320deg)); }
  }
  .popcorn-kernel { opacity: 0; }
  .popcorn-kernel:nth-child(1) { --kernel-size:42px; --kernel-speed:6.07s; --kernel-delay:-3.8s; --kernel-drift:30px; --kernel-rotate:160deg; left:2%; }
  .popcorn-kernel:nth-child(2) { --kernel-size:52px; --kernel-speed:5.65s; --kernel-delay:-3.71s; --kernel-drift:-22px; --kernel-rotate:190deg; left:5.1%; }
  .popcorn-kernel:nth-child(3) { --kernel-size:54px; --kernel-speed:5.16s; --kernel-delay:-2.92s; --kernel-drift:-14px; --kernel-rotate:250deg; left:10.5%; }
  .popcorn-kernel:nth-child(4) { --kernel-size:84px; --kernel-speed:6.74s; --kernel-delay:-5.38s; --kernel-drift:30px; --kernel-rotate:280deg; left:12.9%; }
  .popcorn-kernel:nth-child(5) { --kernel-size:88px; --kernel-speed:6.44s; --kernel-delay:-6.05s; --kernel-drift:22px; --kernel-rotate:220deg; left:18.3%; }
  .popcorn-kernel:nth-child(6) { --kernel-size:60px; --kernel-speed:4.98s; --kernel-delay:-3.03s; --kernel-drift:6px; --kernel-rotate:280deg; left:21.8%; }
  .popcorn-kernel:nth-child(7) { --kernel-size:96px; --kernel-speed:6.04s; --kernel-delay:-4.25s; --kernel-drift:-14px; --kernel-rotate:280deg; left:25.0%; }
  .popcorn-kernel:nth-child(8) { --kernel-size:50px; --kernel-speed:5.88s; --kernel-delay:-0.39s; --kernel-drift:22px; --kernel-rotate:220deg; left:30.3%; }
  .popcorn-kernel:nth-child(9) { --kernel-size:40px; --kernel-speed:7.0s; --kernel-delay:-4.49s; --kernel-drift:6px; --kernel-rotate:190deg; left:33.1%; }
  .popcorn-kernel:nth-child(10) { --kernel-size:92px; --kernel-speed:6.53s; --kernel-delay:-4.14s; --kernel-drift:22px; --kernel-rotate:310deg; left:38.2%; }
  .popcorn-kernel:nth-child(11) { --kernel-size:68px; --kernel-speed:5.12s; --kernel-delay:-6.36s; --kernel-drift:14px; --kernel-rotate:340deg; left:41.2%; }
  .popcorn-kernel:nth-child(12) { --kernel-size:90px; --kernel-speed:7.34s; --kernel-delay:-5.26s; --kernel-drift:22px; --kernel-rotate:280deg; left:45.3%; }
  .popcorn-kernel:nth-child(13) { --kernel-size:46px; --kernel-speed:4.58s; --kernel-delay:-1.82s; --kernel-drift:-6px; --kernel-rotate:280deg; left:50.3%; }
  .popcorn-kernel:nth-child(14) { --kernel-size:64px; --kernel-speed:5.54s; --kernel-delay:-2.47s; --kernel-drift:-14px; --kernel-rotate:310deg; left:55.5%; }
  .popcorn-kernel:nth-child(15) { --kernel-size:48px; --kernel-speed:6.62s; --kernel-delay:-4.28s; --kernel-drift:-22px; --kernel-rotate:310deg; left:57.6%; }
  .popcorn-kernel:nth-child(16) { --kernel-size:58px; --kernel-speed:4.69s; --kernel-delay:-0.14s; --kernel-drift:-14px; --kernel-rotate:310deg; left:61.9%; }
  .popcorn-kernel:nth-child(17) { --kernel-size:70px; --kernel-speed:5.76s; --kernel-delay:-2.95s; --kernel-drift:-14px; --kernel-rotate:160deg; left:67.1%; }
  .popcorn-kernel:nth-child(18) { --kernel-size:72px; --kernel-speed:4.33s; --kernel-delay:-1.08s; --kernel-drift:-6px; --kernel-rotate:250deg; left:72.0%; }
  .popcorn-kernel:nth-child(19) { --kernel-size:56px; --kernel-speed:6.34s; --kernel-delay:-0.75s; --kernel-drift:-6px; --kernel-rotate:340deg; left:75.0%; }
  .popcorn-kernel:nth-child(20) { --kernel-size:78px; --kernel-speed:6.65s; --kernel-delay:-0.38s; --kernel-drift:-30px; --kernel-rotate:340deg; left:78.5%; }
  .popcorn-kernel:nth-child(21) { --kernel-size:62px; --kernel-speed:6.03s; --kernel-delay:-4.92s; --kernel-drift:30px; --kernel-rotate:340deg; left:84.2%; }
  .popcorn-kernel:nth-child(22) { --kernel-size:86px; --kernel-speed:7.0s; --kernel-delay:-0.83s; --kernel-drift:-14px; --kernel-rotate:310deg; left:85.6%; }
  .popcorn-kernel:nth-child(23) { --kernel-size:74px; --kernel-speed:5.2s; --kernel-delay:-1.58s; --kernel-drift:6px; --kernel-rotate:340deg; left:92.0%; }
  .popcorn-kernel:nth-child(24) { --kernel-size:44px; --kernel-speed:6.42s; --kernel-delay:-2.5s; --kernel-drift:6px; --kernel-rotate:280deg; left:94.4%; }
</style>
<footer class="footer">
  <div class="footer-popcorn" aria-hidden="true">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
    <img class="popcorn-kernel" src="${pageContext.request.contextPath}/img/Logo.png" alt="">
  </div>
  <div class="footer-inner">
    <div class="contact">
      <div class="contact-title">문의 시간 &gt;</div>
      <strong>010-xxxx-xxxx</strong>
      <div>평일 09:00 - 18:00<br>주말/공휴일 휴무</div>
    </div>
    <div class="footer-links">
      <span>회사소개</span>
      <span>이용약관</span>
      <span>개인정보처리방침</span>
      <span>제휴문의</span>
    </div>
  </div>
</footer>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>나의 뱃지 - 팝플릭스</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Chewy&family=Noto+Sans+KR:wght@400;500;700;800&display=swap" rel="stylesheet">
<style>
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
body {
  font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', 'Noto Sans KR', sans-serif;
  background: #faf6ee;
  color: #1a1816;
  min-height: 100vh;
}

/* ── 책 외부 래퍼 ── */
.book-outer {
  position: relative;
  max-width: min(1440px, calc(100vw - 40px));
  margin: 24px auto 120px;
  padding-bottom: 20px;
}
.book-cover {
  position: absolute;
  top: -14px; bottom: -28px; left: -18px; right: -18px;
  background:
    repeating-linear-gradient(135deg, transparent, transparent 3px,
      rgba(0,0,0,0.02) 3px, rgba(0,0,0,0.02) 6px),
    linear-gradient(145deg, #ca9560 0%, #a07030 28%, #b88448 60%, #c29460 100%);
  border-radius: 24px;
  box-shadow:
    0 24px 80px rgba(0,0,0,0.35),
    0 10px 28px rgba(0,0,0,0.20),
    inset 0 1px 0 rgba(255,255,255,0.22),
    inset 0 -2px 8px rgba(0,0,0,0.14);
  z-index: 0;
}
.book-cover::after {
  content: '';
  position: absolute;
  top: 10px; bottom: 10px;
  left: calc(50% - 2px); width: 4px;
  background: linear-gradient(to bottom,
    rgba(0,0,0,0.12), rgba(0,0,0,0.22) 40%, rgba(0,0,0,0.12));
  border-radius: 2px;
}
.page-layer-3 { position: absolute; top: 12px; left: -7px; right: -7px; bottom: -22px; background: #ccc4ba; border-radius: 18px; z-index: 1; box-shadow: 0 6px 20px rgba(0,0,0,0.12); }
.page-layer-2 { position: absolute; top: 8px; left: -5px; right: -5px; bottom: -15px; background: #dbd2c8; border-radius: 17px; z-index: 2; }
.page-layer-1 { position: absolute; top: 4px; left: -3px; right: -3px; bottom: -8px; background: #ede7de; border-radius: 16px; z-index: 3; }

/* ── 레이아웃 ── */
.page-wrap {
  display: flex;
  align-items: stretch;
  padding: 0;
  gap: 8px;
  position: relative;
  z-index: 10;
  min-height: calc(100vh - 118px);
  height: calc(100vh - 118px);
  overflow: visible;
}

/* ── 사이드바 ── */
.sidebar {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e6e0d8;
  padding: 24px 24px 20px;
  position: relative;
  align-self: stretch;
  min-height: 0;
  height: 100%;
  z-index: 10;
  display: flex;
  flex-direction: column;
  box-shadow: inset -14px 0 20px rgba(70,45,25,0.045);
  background-image: repeating-linear-gradient(
    to bottom, transparent 0px, transparent 27px,
    rgba(200,190,180,0.13) 27px, rgba(200,190,180,0.13) 28px);
  background-position: 0 68px;
}
.sidebar::before { display: none; }
.sidebar-page-title {
  font-size: 11px; font-weight: 900; letter-spacing: 0.25em;
  color: #c8bfb4; text-align: center; margin-bottom: 20px;
  padding-bottom: 14px; border-bottom: 2px solid #f0ece4; text-transform: uppercase;
}
.sidebar a {
  display: flex; align-items: center; justify-content: space-between;
  padding: 9px 20px; text-decoration: none; color: #5a534c;
  font-size: 13px; font-weight: 600;
  border-left: 3px solid transparent; transition: all 0.15s;
}
.sidebar a:hover, .sidebar a.active {
  background: #fff8ed; border-left-color: #e8a838; color: #1a1816;
}
.sidebar-hr { border: none; border-top: 1px solid #ede8e0; margin: 12px 0; }
.stat-link {
  display: flex; align-items: center; gap: 6px;
  margin: 0 12px; padding: 9px 14px;
  background: #fff8ed; border-radius: 8px;
  text-decoration: none; color: #c07a10;
  font-size: 12px; font-weight: 700; transition: background 0.15s;
}
.stat-link:hover { background: #ffecc8; }

/* ── 스프링 ── */
.spring-col {
  width: 50px; min-width: 50px;
  position: relative; align-self: stretch; min-height: 0; height: 100%;
  z-index: 30; overflow: hidden;
  display: flex; flex-direction: column; align-items: center; justify-content: flex-start;
  padding: 14px 0; gap: 0;
  background: linear-gradient(90deg,
    rgba(92,63,37,0.08) 0%, rgba(92,63,37,0.18) 12%,
    #b58a57 46%, #8d6235 50%, #b58a57 54%,
    rgba(92,63,37,0.18) 88%, rgba(92,63,37,0.08) 100%);
  box-shadow: inset 8px 0 12px rgba(255,255,255,0.55), inset -8px 0 12px rgba(0,0,0,0.10);
}
.spring-col::before {
  content: ""; position: absolute; top: 0; bottom: 0; left: 50%; width: 2px;
  transform: translateX(-50%);
  background: rgba(67,43,24,0.28);
  box-shadow: -10px 0 16px rgba(255,255,255,0.38), 10px 0 16px rgba(0,0,0,0.14);
  z-index: 0;
}
.ring {
  position: relative; width: 76px; height: 18px; margin: 7px 0; flex-shrink: 0;
  background: transparent; border: 0; border-radius: 0; z-index: 2;
  transform: translateX(0); filter: drop-shadow(0 2px 2px rgba(0,0,0,0.22));
}
.ring::before {
  content: ""; position: absolute; left: 11px; right: 11px; top: 7px; height: 4px;
  border-radius: 999px;
  background: linear-gradient(to bottom, #ffffff 0%, #e8e8e8 24%, #9c9c9c 52%, #565656 76%, #d9d9d9 100%);
  box-shadow: inset 0 1px 1px rgba(255,255,255,0.9), inset 0 -1px 1px rgba(0,0,0,0.35), 0 1px 2px rgba(0,0,0,0.25);
}
.ring::after {
  content: ""; position: absolute; inset: 0; border-radius: 999px;
  background:
    radial-gradient(circle at 9px 9px, #2f2f2f 0 3px, #686868 3.2px 4.4px, rgba(255,255,255,0.95) 4.6px 5.8px, transparent 6px),
    radial-gradient(circle at 67px 9px, #2f2f2f 0 3px, #686868 3.2px 4.4px, rgba(255,255,255,0.95) 4.6px 5.8px, transparent 6px),
    radial-gradient(ellipse at 9px 9px, rgba(0,0,0,0.16) 0 8px, transparent 8.5px),
    radial-gradient(ellipse at 67px 9px, rgba(0,0,0,0.16) 0 8px, transparent 8.5px);
}

/* ── 노트 본문 ── */
.notebook-body { flex: 1; position: relative; z-index: 5; display: flex; align-items: stretch; min-height: 0; height: 100%; overflow: visible; }
.notebook-body::after { display: none; }
.nb-content {
  flex: 1; background: #fff;
  border-radius: 14px;
  border: 1px solid #e6e0d8;
  position: relative;
  background-image: repeating-linear-gradient(
    to bottom, transparent 0px, transparent 27px,
    rgba(200,190,180,0.18) 27px, rgba(200,190,180,0.18) 28px);
  background-position: 0 52px;
  display: flex; flex-direction: column;
  min-height: 0;
  height: 100%;
  overflow: hidden;
  box-shadow: inset 14px 0 20px rgba(70,45,25,0.045), 0 2px 8px rgba(0,0,0,0.04);
}
.nb-content::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 72px;
  z-index: 18;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.18s ease;
  background: linear-gradient(to bottom, rgba(255,255,255,0), rgba(255,255,255,0.72) 58%, rgba(255,255,255,0.94));
  backdrop-filter: blur(1.5px);
}
.nb-content::before {
  content: '⌄';
  position: absolute;
  left: 50%;
  bottom: 14px;
  z-index: 19;
  width: 26px;
  height: 26px;
  transform: translateX(-50%);
  border-radius: 999px;
  background: rgba(232,168,56,0.88);
  color: #fff;
  font-size: 18px;
  line-height: 22px;
  text-align: center;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.18s ease, transform 0.18s ease;
  box-shadow: 0 4px 12px rgba(120,78,26,0.22);
}
.nb-content.has-scroll:not(.at-bottom)::after,
.nb-content.has-scroll:not(.at-bottom)::before { opacity: 1; }
.nb-content.has-scroll:not(.at-bottom)::before { transform: translateX(-50%) translateY(2px); }

/* ── 인덱스 탭 ── */
.index-tabs {
  position: absolute; right: -42px; top: 80px;
  display: flex; flex-direction: column; gap: 5px; z-index: 20;
}
.index-tab {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  width: 42px; height: 54px;
  background: linear-gradient(to right, #b07838, #986428);
  border-radius: 0 10px 10px 0;
  text-decoration: none; color: rgba(255,255,255,0.9);
  font-size: 17px; box-shadow: 3px 2px 10px rgba(0,0,0,0.22);
  transition: all 0.15s; border-left: 3px solid rgba(0,0,0,0.12);
}
.index-tab span { font-size: 8px; font-weight: 700; letter-spacing: 0.03em; margin-top: 2px; opacity: 0.85; }
.index-tab:hover { background: linear-gradient(to right, #c88840, #a87430); transform: translateX(4px); }
.index-tab.active { background: linear-gradient(to right, #e8a838, #d09020); color: #fff; box-shadow: 4px 2px 14px rgba(0,0,0,0.28); }

/* ── 헤더 ── */
.badge-header {
  background: #e8a838;
  padding: 20px 28px;
  display: flex; align-items: center; gap: 12px;
  background-image: none;
  flex-shrink: 0;
}
.badge-header-title { font-size: 24px; font-weight: 900; color: #fff; }
.badge-header-title span { font-size: 28px; }
.badge-earned-count {
  margin-left: auto;
  background: rgba(255,255,255,0.25); color: #fff;
  font-size: 13px; font-weight: 800;
  border-radius: 20px; padding: 5px 14px;
}

/* ── 콘텐츠 영역 ── */
.badge-body { padding: 20px 24px 24px; display: flex; flex-direction: column; gap: 22px; flex: 1; min-height: 0; overflow: auto; }
.badge-body {
  scrollbar-width: thin;
  scrollbar-color: rgba(176,120,56,0.34) transparent;
}
.badge-body::-webkit-scrollbar { width: 6px; }
.badge-body::-webkit-scrollbar-track { background: transparent; }
.badge-body::-webkit-scrollbar-thumb { background: rgba(176,120,56,0.28); border-radius: 999px; }
.badge-body::-webkit-scrollbar-thumb:hover { background: rgba(176,120,56,0.45); }

/* ── 섹션 타이틀 ── */
.section-title {
  font-size: 14px; font-weight: 800; color: #5a534c;
  display: flex; align-items: center; gap: 8px;
  margin-bottom: 10px;
}
.section-title::after {
  content: ''; flex: 1; height: 1px; background: #e8e2da;
}

/* ── 뱃지 그리드 ── */
.badge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 10px;
}

/* ── 뱃지 카드 (기본 = 달성) ── */
.badge-card {
  background: #fff;
  border: 1.5px solid #e8c870;
  border-radius: 16px;
  padding: 16px 12px 14px;
  text-align: center;
  position: relative;
  box-shadow: 0 2px 10px rgba(232,168,56,0.12);
  transition: transform 0.15s, box-shadow 0.15s;
}
.badge-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(232,168,56,0.2); }
.badge-check { position: absolute; top: 10px; right: 12px; font-size: 13px; color: #e8a838; }
.badge-icon { width: 38px; height: 38px; display: block; margin: 0 auto 7px; object-fit: contain; }
.badge-name { font-size: 14px; font-weight: 800; margin-bottom: 3px; color: #1a1816; }
.badge-desc { font-size: 11px; color: #aaa; margin-bottom: 6px; }
.badge-status-done { font-size: 12px; color: #888; font-weight: 600; }
.badge-date { font-size: 12px; font-weight: 700; color: #e8a838; margin-top: 3px; }

/* ── 뱃지 카드 (잠금) ── */
.badge-card.locked {
  border-color: #e2ddd8;
  box-shadow: none;
  filter: grayscale(55%);
  opacity: 0.7;
  background: #f9f7f4;
}
.badge-card.locked:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.badge-lock { position: absolute; top: 10px; right: 12px; font-size: 13px; color: #ccc; }
.badge-remain { font-size: 12px; color: #bbb; font-weight: 600; margin-top: 4px; }

/* ── 진행 바 (잠금 카드) ── */
.badge-progress-bar {
  width: 100%; height: 4px; background: #ede8e0; border-radius: 2px; margin: 8px 0 4px;
}
.badge-progress-fill {
  height: 100%; background: linear-gradient(to right, #e8a838, #f0c040);
  border-radius: 2px;
}
.badge-progress-txt { font-size: 10px; color: #ccc; }

/* ── 빈 상태 (달성 배지 없음) ── */
.empty-earned {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; gap: 10px;
  padding: 36px 20px;
  text-align: center;
}
.empty-earned img { width: 80px; height: 80px; opacity: 0.85; }
.empty-earned-title { font-size: 15px; font-weight: 700; color: #5a534c; }
.empty-earned-sub { font-size: 12px; color: #b0a898; }

/* ── 구분선 ── */
.section-divider {
  border: none;
  border-top: 2px dashed #e8e2da;
  margin: 0;
}

/* ── 푸터 ── */
/* common header / footer */
.site-header {
  position: relative;
  z-index: 20;
  width: min(1180px, calc(100% - 56px));
  height: 78px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}
.brand {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #101010;
  text-decoration: none;
  font-family: "Chewy", cursive;
  font-size: 20px;
  line-height: 1;
  white-space: nowrap;
}
.brand img { width: 34px; height: 34px; object-fit: contain; display: block; }
.nav {
  display: flex;
  align-items: center;
  gap: 34px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
}
.nav a { color: #111; text-decoration: none; }
.footer {
  position: relative;
  flex-shrink: 0;
  margin-top: 96px;
  min-height: 220px;
  background: #FFB020;
  overflow: visible;
  z-index: 1;
}
.footer::before {
  content: "";
  position: absolute;
  left: 0;
  top: -64px;
  width: 200%;
  height: 66px;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 1440 120' xmlns='http://www.w3.org/2000/svg' preserveAspectRatio='none'%3E%3Cpath fill='%23FFB020' d='M0 66C240 38 480 38 720 66C960 94 1200 94 1440 66V120H0V66Z'/%3E%3C/svg%3E");
  background-repeat: repeat-x;
  background-size: 960px 66px;
  animation: footerWave 18s linear infinite;
  pointer-events: none;
  z-index: 0;
}
@keyframes footerWave { from { transform: translateX(0); } to { transform: translateX(-960px); } }
.footer-popcorn {
  position: absolute;
  left: 0;
  right: 0;
  bottom: calc(100% - 18px);
  height: 50vh;
  min-height: 320px;
  pointer-events: none;
  z-index: -1;
}
.popcorn-kernel {
  position: absolute;
  bottom: 0;
  width: var(--kernel-size);
  height: var(--kernel-size);
  object-fit: contain;
  opacity: 0;
  filter: drop-shadow(0 10px 10px rgba(102, 51, 0, 0.22));
  animation: popcornBounce var(--kernel-speed) ease-in-out infinite;
  animation-delay: var(--kernel-delay);
  transform-origin: center bottom;
}
.popcorn-kernel:nth-child(1) { --kernel-size: 28px; --kernel-speed: 4.8s; --kernel-delay: -0.4s; left: 7%; }
.popcorn-kernel:nth-child(2) { --kernel-size: 42px; --kernel-speed: 5.6s; --kernel-delay: -2.1s; left: 16%; }
.popcorn-kernel:nth-child(3) { --kernel-size: 34px; --kernel-speed: 4.4s; --kernel-delay: -1.2s; left: 25%; }
.popcorn-kernel:nth-child(4) { --kernel-size: 52px; --kernel-speed: 6.2s; --kernel-delay: -3.4s; left: 34%; }
.popcorn-kernel:nth-child(5) { --kernel-size: 24px; --kernel-speed: 5.1s; --kernel-delay: -2.8s; left: 42%; }
.popcorn-kernel:nth-child(6) { --kernel-size: 38px; --kernel-speed: 4.7s; --kernel-delay: -0.9s; left: 50%; }
.popcorn-kernel:nth-child(7) { --kernel-size: 46px; --kernel-speed: 5.8s; --kernel-delay: -4.1s; left: 59%; }
.popcorn-kernel:nth-child(8) { --kernel-size: 30px; --kernel-speed: 4.9s; --kernel-delay: -1.7s; left: 67%; }
.popcorn-kernel:nth-child(9) { --kernel-size: 56px; --kernel-speed: 6.4s; --kernel-delay: -3.1s; left: 75%; }
.popcorn-kernel:nth-child(10) { --kernel-size: 36px; --kernel-speed: 5.3s; --kernel-delay: -0.2s; left: 83%; }
.popcorn-kernel:nth-child(11) { --kernel-size: 44px; --kernel-speed: 5.9s; --kernel-delay: -2.5s; left: 91%; }
.popcorn-kernel:nth-child(12) { --kernel-size: 32px; --kernel-speed: 4.6s; --kernel-delay: -3.8s; left: 12%; }
@keyframes popcornBounce {
  0% { opacity: 0; transform: translate3d(0, 18px, 0) scale(0.84) rotate(0deg); }
  10% { opacity: 0.95; }
  50% { opacity: 1; transform: translate3d(var(--kernel-drift, 0), -50vh, 0) scale(1) rotate(var(--kernel-rotate, 210deg)); }
  100% { opacity: 0; transform: translate3d(var(--kernel-end-drift, 0), 20px, 0) scale(0.9) rotate(var(--kernel-end-rotate, 320deg)); }
}
.popcorn-kernel:nth-child(odd) { --kernel-drift: 42px; --kernel-end-drift: 72px; --kernel-rotate: 240deg; --kernel-end-rotate: 390deg; }
.popcorn-kernel:nth-child(even) { --kernel-drift: -34px; --kernel-end-drift: -58px; --kernel-rotate: -220deg; --kernel-end-rotate: -360deg; }
.footer-inner {
  position: relative;
  z-index: 2;
  width: min(1080px, calc(100% - 56px));
  margin: 0 auto;
  padding: 44px 0 28px;
  display: grid;
  grid-template-columns: auto auto;
  justify-content: start;
  gap: 72px;
  align-items: end;
  font-size: 12px;
  font-weight: 800;
}
.contact { text-align: left; line-height: 1.9; }
.contact-title { font-size: 14px; margin-bottom: 4px; }
.footer-links { display: grid; gap: 11px; font-size: 11px; font-weight: 800; text-align: left; }
@media (max-width: 900px) {
  .site-header,
  .footer-inner { width: min(100% - 28px, 680px); }
  .site-header { height: auto; padding: 18px 0; flex-direction: column; align-items: flex-start; }
  .nav { width: 100%; gap: 12px; overflow-x: auto; padding-bottom: 4px; }
  .footer-inner { grid-template-columns: 1fr; gap: 28px; }
}

/* POPFLIX visual tone override: keep diary structure, align color/weight */
:root {
  --diary-bg: #FFF4DE;
  --diary-box: #FFB020;
  --diary-point: #FF5C3B;
  --diary-ink: #151515;
  --diary-line: rgba(21, 21, 21, 0.16);
  --diary-soft: rgba(255, 255, 255, 0.48);
}

body {
  background: var(--diary-bg);
  color: var(--diary-ink);
  font-family: "Noto Sans KR", Arial, sans-serif;
  font-weight: 500;
}

.book-cover {
  background:
    repeating-linear-gradient(135deg, transparent, transparent 3px, rgba(255,255,255,0.08) 3px, rgba(255,255,255,0.08) 6px),
    linear-gradient(145deg, #FFB020 0%, #F59C19 48%, #FF5C3B 100%);
}

.page-layer-3 { background: #ffd889; }
.page-layer-2 { background: #ffe4aa; }
.page-layer-1 { background: #fff0cf; }

.sidebar,
.notebook-body,
.nb-content,
.badge-body,
.badge-card,
.badge-card.locked,
.empty-earned {
  background: #fff;
  border-color: var(--diary-line);
  color: var(--diary-ink);
}

.badge-header {
  background: var(--diary-box);
  color: var(--diary-ink);
}

.badge-header-title,
.section-title,
.sidebar-page-title,
.badge-name,
.empty-earned-title {
  color: var(--diary-ink);
  font-weight: 800;
}

.sidebar a.active,
.sidebar .stat-link[style],
.badge-earned-count {
  background: #fff !important;
  border-color: var(--diary-point) !important;
  color: var(--diary-ink) !important;
}

.badge-progress-fill {
  background: var(--diary-box);
  color: var(--diary-ink);
}

.badge-check,
.badge-date,
.stat-link {
  color: var(--diary-point);
  font-weight: 800;
}

.sidebar a,
.stat-link,
.badge-status-done,
.badge-remain {
  font-weight: 800;
}

</style>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/site-header.jsp" />

<div class="book-outer">
  <div class="book-cover"></div>
  <div class="page-layer page-layer-3"></div>
  <div class="page-layer page-layer-2"></div>
  <div class="page-layer page-layer-1"></div>

  <div class="page-wrap">

    <!-- 사이드바 -->
    <aside class="sidebar">
      <div class="sidebar-page-title">MY FILM DIARY</div>
      <a href="${pageContext.request.contextPath}/diary/list.do">전체 기록</a>
      <hr class="sidebar-hr">
      <a class="stat-link" href="${pageContext.request.contextPath}/diary/stat.do">📊 연간 통계</a>
      <hr class="sidebar-hr">
      <a class="stat-link" href="${pageContext.request.contextPath}/diary/badge.do"
         style="background:#fff3dc; border: 1px solid #f0c84a;">🏅 나의 뱃지</a>
    </aside>

    <!-- 스프링 -->
    <div class="spring-col" id="springCol"></div>

    <!-- 노트 본문 -->
    <div class="notebook-body">

      <!-- 인덱스 탭 -->
      <div class="index-tabs">
        <a class="index-tab" href="${pageContext.request.contextPath}/diary/list.do" title="Calendar"><span>Calendar</span></a>
        <a class="index-tab" href="${pageContext.request.contextPath}/diary/stat.do" title="Analytics"><span>Analytics</span></a>
        <a class="index-tab active" href="${pageContext.request.contextPath}/diary/badge.do" title="Badge"><span>Badge</span></a>
        <a class="index-tab" href="${pageContext.request.contextPath}/diary/list.do#write" title="Write"><span>Write</span></a>
        <a class="index-tab" href="${pageContext.request.contextPath}/diary/list.do#archive" title="Archive"><span>Archive</span></a>
      </div>

      <div class="nb-content">

        <!-- 헤더 -->
        <div class="badge-header">
          <div class="badge-header-title">
            🥇 배지 <span>${earnedCount}</span>개 보유자!
          </div>
          <div class="badge-earned-count">${earnedCount} / 12개 달성</div>
        </div>

        <!-- 콘텐츠 -->
        <div class="badge-body">

          <!-- ── 상단: 달성한 뱃지 ── -->
          <div>
            <div class="section-title">획득한 뱃지</div>

            <c:choose>
              <c:when test="${earnedCount == 0}">
                <!-- 빈 상태 -->
                <div class="empty-earned">
                  <img src="${pageContext.request.contextPath}/img/free-icon-achievements-7057479.png" alt="뱃지 없음">
                  <div class="empty-earned-title">아직 획득한 배지가 없어요!</div>
                  <div class="empty-earned-sub">다양한 달성을 통해 나만의 배지를 모아보세요!</div>
                </div>
              </c:when>
              <c:otherwise>
                <div class="badge-grid">
                  <c:forEach var="b" items="${badgeList}">
                    <c:if test="${b.earned}">
                      <div class="badge-card">
                        <span class="badge-check">✔</span>
                        <img class="badge-icon" src="${pageContext.request.contextPath}/img/${b.icon}" alt="${b.name}">
                        <div class="badge-name">${b.name}</div>
                        <div class="badge-desc">${b.desc}</div>
                        <div class="badge-status-done">달성 완료</div>
                        <c:if test="${not empty b.earnedDate}">
                          <div class="badge-date">${b.earnedDate}</div>
                        </c:if>
                      </div>
                    </c:if>
                  </c:forEach>
                </div>
              </c:otherwise>
            </c:choose>
          </div>

          <hr class="section-divider">

          <!-- ── 하단: 잠긴 뱃지 ── -->
          <div>
            <div class="section-title">잠긴 뱃지</div>
            <div class="badge-grid">
              <c:forEach var="b" items="${badgeList}">
                <c:if test="${!b.earned}">
                  <div class="badge-card locked">
                    <span class="badge-lock">🔒</span>
                    <img class="badge-icon" src="${pageContext.request.contextPath}/img/${b.icon}" alt="${b.name}">
                    <div class="badge-name">${b.name}</div>
                    <div class="badge-desc">${b.desc}</div>
                    <div class="badge-progress-bar">
                      <div class="badge-progress-fill"
                           style="width:${b.target > 0 ? (b.progress * 100 / b.target) : 0}%"></div>
                    </div>
                    <c:choose>
                      <c:when test="${b.target == 1}">
                        <div class="badge-remain">아직 미달성</div>
                      </c:when>
                      <c:otherwise>
                        <div class="badge-remain">${b.target - b.progress}편 남음 (${b.progress}/${b.target})</div>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </c:if>
              </c:forEach>
            </div>
          </div>

        </div><!-- /.badge-body -->
      </div><!-- /.nb-content -->
    </div><!-- /.notebook-body -->
  </div><!-- /.page-wrap -->
</div><!-- /.book-outer -->

<jsp:include page="/WEB-INF/views/common/site-footer.jsp" />

<script>
(function(){
  const col = document.getElementById('springCol');
  function fillRings(){
    col.innerHTML = '';
    const h = col.offsetHeight || 600;
    const count = Math.max(12, Math.ceil(h / 26));
    for(let i = 0; i < count; i++){
      const r = document.createElement('div');
      r.className = 'ring';
      col.appendChild(r);
    }
  }
  fillRings();
  new ResizeObserver(fillRings).observe(col);
})();

function updateScrollHint(){
  const content = document.querySelector('.nb-content');
  const scroller = document.querySelector('.badge-body');
  if(!content || !scroller) return;
  const hasScroll = scroller.scrollHeight - scroller.clientHeight > 2;
  const atBottom = !hasScroll || scroller.scrollTop + scroller.clientHeight >= scroller.scrollHeight - 2;
  content.classList.toggle('has-scroll', hasScroll);
  content.classList.toggle('at-bottom', atBottom);
}

document.querySelector('.badge-body').addEventListener('scroll', updateScrollHint, { passive: true });
window.addEventListener('resize', updateScrollHint);
new ResizeObserver(updateScrollHint).observe(document.querySelector('.badge-body'));
setTimeout(updateScrollHint, 0);
setTimeout(updateScrollHint, 250);
</script>
</body>
</html>

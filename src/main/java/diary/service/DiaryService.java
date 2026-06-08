package diary.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import diary.dao.DiaryDAO;
import diary.dto.BadgeDTO;
import diary.dto.DiaryDTO;
import diary.dto.DiaryStatDTO;

/*
  DiaryService
  필름 다이어리 비즈니스 로직 담당
  - DAO 호출 + 뱃지 집계 + 통계 조합
 */

public class DiaryService {

	private final DiaryDAO diaryDAO = new DiaryDAO();

	// ─────────────────────────────────────────────────────────────
	// 1. 다이어리 목록 조회 (태그 목록도 같이 붙여서 반환)
	// ─────────────────────────────────────────────────────────────

	public List<DiaryDTO> getDiaryList(int memberId, String year, String sort) throws Exception {
		List<DiaryDTO> list = diaryDAO.getDiaryList(memberId, year, sort);
		// 각 다이어리에 태그 목록 붙이기
		for (DiaryDTO dto : list) {
			List<String> tags = diaryDAO.getTagsByDiaryId(dto.getDiaryId());
			dto.setTagList(tags);
		}
		return list;
	}

	public List<DiaryDTO> getWritableDiaryList(int memberId) throws Exception {
		return diaryDAO.getWritableDiaryList(memberId);
	}

	// ─────────────────────────────────────────────────────────────
	// 2. 달력용 데이터 조회 (AJAX JSON 응답)
	// ─────────────────────────────────────────────────────────────
	public List<DiaryDTO> getDiaryByMonth(int memberId, String year, String month) throws Exception {
		return diaryDAO.getDiaryByMonth(memberId, year, month);
	}

	// ─────────────────────────────────────────────────────────────
	// 3. 다이어리 상세 (태그 목록 붙여서 반환)
	// ─────────────────────────────────────────────────────────────
	public DiaryDTO getDiaryDetail(int diaryId) throws Exception {
		DiaryDTO dto = diaryDAO.getDiaryDetail(diaryId);
		if (dto != null) {
			dto.setTagList(diaryDAO.getTagsByDiaryId(diaryId));
		}
		return dto;
	}

	// ─────────────────────────────────────────────────────────────
	// 4. 전체 태그 목록 조회 (태그 선택 UI용)
	// ─────────────────────────────────────────────────────────────
	public List<Map<String, Object>> getAllTags() throws Exception {
		return diaryDAO.getAllTags();
	}

	// ─────────────────────────────────────────────────────────────
	// 5. 감정 태그 + 팝콘 평점 등록/수정
	// ─────────────────────────────────────────────────────────────
	public void updateTagsAndPopcorn(int diaryId, int[] tagIds, double popcornRating) throws Exception {
		diaryDAO.updateTags(diaryId, tagIds);
		if (popcornRating > 0) {
			diaryDAO.updatePopcornRating(diaryId, popcornRating);
		}
	}

	public boolean updateTagsPopcornAndReview(DiaryDTO diary, int[] tagIds, double popcornRating, String freshYn, String content)
			throws Exception {
		updateTagsAndPopcorn(diary.getDiaryId(), tagIds, popcornRating);
		return diaryDAO.saveReviewAndLinkDiary(diary, freshYn, content);
	}

	public boolean deleteDiaryReviewState(int diaryId, int memberId) throws Exception {
		return diaryDAO.deleteDiaryReviewState(diaryId, memberId);
	}

	// ─────────────────────────────────────────────────────────────
	// 6. 다이어리 자동 등록 (예매 완료 후 ReservationService에서 호출)
	// - reservation_id UNIQUE → 중복 등록 자동 방지
	// ─────────────────────────────────────────────────────────────
	public int insertDiary(DiaryDTO dto) throws Exception {
		return diaryDAO.insertDiary(dto);
	}

	// ─────────────────────────────────────────────────────────────
	// 7. 연도 목록 (사이드바 폴더 구조)
	// ─────────────────────────────────────────────────────────────
	public List<String> getYearList(int memberId) throws Exception {
		return diaryDAO.getYearList(memberId);
	}

	// ─────────────────────────────────────────────────────────────
	// 8. 연간 통계 조회 + 뱃지 집계
	// ─────────────────────────────────────────────────────────────
	@SuppressWarnings("unchecked")
	public DiaryStatDTO getStat(int memberId, int year) throws Exception {
		Map<String, Object> data = diaryDAO.getStatData(memberId, year);

		DiaryStatDTO stat = new DiaryStatDTO();
		stat.setYear(year);

		// 총 관람 편수
		int total = (int) data.getOrDefault("totalCount", 0);
		stat.setTotalCount(total);

		// 평균 팝콘
		stat.setAvgPopcornRating((double) data.getOrDefault("avgPopcornRating", 0.0));

		// 가장 많이 간 극장
		stat.setTopTheater((String) data.getOrDefault("topTheater", "정보 없음"));

		// 월별 카운트
		stat.setMonthlyCount((int[]) data.getOrDefault("monthlyCount", new int[12]));

		// 감정 태그 빈도
		stat.setTagFreqList((List<Map.Entry<String, Integer>>) data.getOrDefault("tagFreqList", new ArrayList<>()));

		// ── 뱃지 집계 (동적 집계, DB 저장 없이 Java 조건 판단) ──
		stat.setEarnedBadges(calcBadges(memberId, total));

		return stat;
	}

	// ─────────────────────────────────────────────────────────────
	// 9. 뱃지 목록 조회 (전체 12개, 달성 여부 + 진행도 포함)
	// ─────────────────────────────────────────────────────────────
	public List<BadgeDTO> getBadgeList(int memberId) throws Exception {
		List<BadgeDTO> badges = new ArrayList<>();

		int totalCount = 0;
		try { totalCount = diaryDAO.countAllDiary(memberId); } catch (Exception e) { e.printStackTrace(); }
		int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
		int yearCount = 0;
		try { yearCount = diaryDAO.getYearCount(memberId, currentYear); } catch (Exception e) { e.printStackTrace(); }
		int oneStarCnt = 0;
		try { oneStarCnt = diaryDAO.countLowRating(memberId); } catch (Exception e) { e.printStackTrace(); }
		int highRatingCnt = 0;
		try { highRatingCnt = diaryDAO.countHighRating(memberId); } catch (Exception e) { e.printStackTrace(); }
		int taggedDiaryCnt = 0;
		try { taggedDiaryCnt = diaryDAO.countTaggedDiary(memberId); } catch (Exception e) { e.printStackTrace(); }
		int midnightCnt = 0;
		try { midnightCnt = diaryDAO.countMidnightWatch(memberId); } catch (Exception e) { e.printStackTrace(); }
		int maxGenreCnt = 0;
		try { maxGenreCnt = diaryDAO.getMaxGenreCount(memberId); } catch (Exception e) { e.printStackTrace(); }
		int maxDirectorCnt = 0;
		try { maxDirectorCnt = diaryDAO.getMaxDirectorCount(memberId); } catch (Exception e) { e.printStackTrace(); }
		List<String> weeks = new ArrayList<>();
		try { weeks = diaryDAO.getAllWatchWeeks(memberId); } catch (Exception e) { e.printStackTrace(); }
		int maxStreak = calcMaxStreak(weeks);
		int fiveStarCnt = 0;
		try { fiveStarCnt = diaryDAO.countRatingEquals(memberId, 5.0); } catch (Exception e) { e.printStackTrace(); }
		int burnedPopcornCnt = 0;
		try { burnedPopcornCnt = diaryDAO.countRatingAtMost(memberId, 2.0); } catch (Exception e) { e.printStackTrace(); }
		int longReviewCnt = 0;
		try { longReviewCnt = diaryDAO.countLongReview(memberId, 300); } catch (Exception e) { e.printStackTrace(); }
		int distinctTagCnt = 0;
		try { distinctTagCnt = diaryDAO.countDistinctTagsUsed(memberId); } catch (Exception e) { e.printStackTrace(); }
		int diaryWithFiveTagsCnt = 0;
		try { diaryWithFiveTagsCnt = diaryDAO.countDiaryWithAtLeastTags(memberId, 5); } catch (Exception e) { e.printStackTrace(); }
		int romanceCnt = 0;
		try { romanceCnt = diaryDAO.countGenreLike(memberId, "로맨스"); } catch (Exception e) { e.printStackTrace(); }
		int horrorCnt = 0;
		try { horrorCnt = diaryDAO.countGenreLike(memberId, "공포"); } catch (Exception e) { e.printStackTrace(); }
		int sfCnt = 0;
		try { sfCnt = diaryDAO.countGenreLike(memberId, "SF"); } catch (Exception e) { e.printStackTrace(); }
		int mysteryCnt = 0;
		try { mysteryCnt = diaryDAO.countGenreAny(memberId, "범죄", "추리"); } catch (Exception e) { e.printStackTrace(); }
		int animationCnt = 0;
		try { animationCnt = diaryDAO.countGenreLike(memberId, "애니메이션"); } catch (Exception e) { e.printStackTrace(); }
		double popcornSum = 0;
		try { popcornSum = diaryDAO.sumPopcornRating(memberId); } catch (Exception e) { e.printStackTrace(); }

		String date1st = null;
		try { if (totalCount >= 1) date1st = diaryDAO.getNthWatchDate(memberId, 1); } catch (Exception e) { e.printStackTrace(); }
		String date10th = null;
		try { if (totalCount >= 10) date10th = diaryDAO.getNthWatchDate(memberId, 10); } catch (Exception e) { e.printStackTrace(); }
		String date20th = null;
		try { if (totalCount >= 20) date20th = diaryDAO.getNthWatchDate(memberId, 20); } catch (Exception e) { e.printStackTrace(); }
		String date50th = null;
		try { if (totalCount >= 50) date50th = diaryDAO.getNthWatchDate(memberId, 50); } catch (Exception e) { e.printStackTrace(); }

		badges.add(new BadgeDTO("FIRST_FILM", "첫필름.png", "첫 필름", "영화 다이어리 기록 1개 이상", totalCount >= 1, date1st, Math.min(totalCount, 1), 1));
		badges.add(new BadgeDTO("RECORD_COLLECTOR", "기록수집가.png", "기록 수집가", "영화 다이어리 기록 10개 이상", totalCount >= 10, date10th, Math.min(totalCount, 10), 10));
		badges.add(new BadgeDTO("MANIA", "시네마마니아.png", "시네마 마니아", "영화 다이어리 기록 50개 이상", totalCount >= 50, date50th, Math.min(totalCount, 50), 50));
		badges.add(new BadgeDTO("LOW_RATER", "혹평가.png", "혹평가", "팝콘 1.0점을 5회 이상", oneStarCnt >= 5, oneStarCnt >= 5 ? "달성" : null, Math.min(oneStarCnt, 5), 5));
		badges.add(new BadgeDTO("STREAK", "연속관람.png", "연속 관람", "3주 연속 영화 관람", maxStreak >= 3, maxStreak >= 3 ? "달성" : null, Math.min(maxStreak, 3), 3));
		badges.add(new BadgeDTO("FRESH_EYE", "신선한눈.png", "신선한 눈", "팝콘 4.5 이상을 10회 이상", highRatingCnt >= 10, highRatingCnt >= 10 ? "달성" : null, Math.min(highRatingCnt, 10), 10));
		badges.add(new BadgeDTO("POPCORN_LOVER", "팝콘러버.png", "팝콘 러버", "감정 태그를 5편 이상 기록", taggedDiaryCnt >= 5, taggedDiaryCnt >= 5 ? "달성" : null, Math.min(taggedDiaryCnt, 5), 5));
		badges.add(new BadgeDTO("NIGHT_OWL", "부엉이족.png", "부엉이족", "밤 10시 이후 상영 영화 5개 이상", midnightCnt >= 5, midnightCnt >= 5 ? "달성" : null, Math.min(midnightCnt, 5), 5));
		badges.add(new BadgeDTO("GENRE_MASTER", "장르마스터.png", "장르 마스터", "같은 장르 영화 20편 이상", maxGenreCnt >= 20, maxGenreCnt >= 20 ? "달성" : null, Math.min(maxGenreCnt, 20), 20));
		badges.add(new BadgeDTO("DIRECTOR_FAN", "감독팬.png", "감독 팬", "같은 감독의 영화 5편 이상", maxDirectorCnt >= 5, maxDirectorCnt >= 5 ? "달성" : null, Math.min(maxDirectorCnt, 5), 5));
		badges.add(new BadgeDTO("YEAR_BEST", "올해의관객.png", "올해의 관객", currentYear + "년 영화 50편 이상", yearCount >= 50, yearCount >= 50 ? "달성" : null, Math.min(yearCount, 50), 50));
		badges.add(new BadgeDTO("REGULAR_VIEWER", "단골관람객.png", "단골 관람객", "영화 다이어리 기록 20개 이상", totalCount >= 20, date20th, Math.min(totalCount, 20), 20));
		badges.add(new BadgeDTO("LIFE_MOVIE", "인생작발견.png", "인생작 발견", "팝콘 평점 5.0점 기록 1개 이상", fiveStarCnt >= 1, fiveStarCnt >= 1 ? "달성" : null, Math.min(fiveStarCnt, 1), 1));
		badges.add(new BadgeDTO("GOLDEN_POPCORN", "골든팝콘.png", "골든 팝콘", "팝콘 평점 5.0점 기록 5개 이상", fiveStarCnt >= 5, fiveStarCnt >= 5 ? "달성" : null, Math.min(fiveStarCnt, 5), 5));
		badges.add(new BadgeDTO("BURNT_POPCORN", "탄팝콘.png", "탄 팝콘", "팝콘 평점 2.0점 이하 기록 3개 이상", burnedPopcornCnt >= 3, burnedPopcornCnt >= 3 ? "달성" : null, Math.min(burnedPopcornCnt, 3), 3));
		badges.add(new BadgeDTO("DETAILED_WRITER", "꼼꼼한기록러.png", "꼼꼼한 기록러", "감상문 300자 이상 1개 이상", longReviewCnt >= 1, longReviewCnt >= 1 ? "달성" : null, Math.min(longReviewCnt, 1), 1));
		badges.add(new BadgeDTO("EMOTION_COLLECTOR", "감정수집가.png", "감정 수집가", "서로 다른 감정 태그 5종 이상 사용", distinctTagCnt >= 5, distinctTagCnt >= 5 ? "달성" : null, Math.min(distinctTagCnt, 5), 5));
		badges.add(new BadgeDTO("EMOTION_WHIRL", "감정의소용돌이.png", "감정의 소용돌이", "하나의 다이어리에 감정 태그 5종 이상", diaryWithFiveTagsCnt >= 1, diaryWithFiveTagsCnt >= 1 ? "달성" : null, Math.min(diaryWithFiveTagsCnt, 1), 1));
		badges.add(new BadgeDTO("ROMANTIST", "로맨티스트.png", "로맨티스트", "로맨스 장르 영화 5개 이상", romanceCnt >= 5, romanceCnt >= 5 ? "달성" : null, Math.min(romanceCnt, 5), 5));
		badges.add(new BadgeDTO("BRAVE_HEART", "강심장.png", "강심장", "공포 장르 영화 5개 이상", horrorCnt >= 5, horrorCnt >= 5 ? "달성" : null, Math.min(horrorCnt, 5), 5));
		badges.add(new BadgeDTO("SPACE_CONQUEROR", "우주정복자.png", "우주 정복자", "SF 장르 영화 10개 이상", sfCnt >= 10, sfCnt >= 10 ? "달성" : null, Math.min(sfCnt, 10), 10));
		badges.add(new BadgeDTO("SHERLOCK", "셜록홈즈.png", "셜록홈즈", "범죄 또는 추리 장르 영화 5개 이상", mysteryCnt >= 5, mysteryCnt >= 5 ? "달성" : null, Math.min(mysteryCnt, 5), 5));
		badges.add(new BadgeDTO("CHILDHOOD_GUARDIAN", "동심수호자.png", "동심 수호자", "애니메이션 장르 영화 5개 이상", animationCnt >= 5, animationCnt >= 5 ? "달성" : null, Math.min(animationCnt, 5), 5));
		badges.add(new BadgeDTO("POPCORN_RICH", "팝콘부자.png", "팝콘 부자", "팝콘 평점 총합 50점 이상", popcornSum >= 50, popcornSum >= 50 ? "달성" : null, Math.min((int) popcornSum, 50), 50));

		return badges;
	}
	private int calcMaxStreak(List<String> weeks) {
		if (weeks == null || weeks.isEmpty()) return 0;
		int max = 1, cur = 1;
		for (int i = 1; i < weeks.size(); i++) {
			String prev = weeks.get(i - 1);
			String curr = weeks.get(i);
			// 같은 연도-주가 연속인지 확인 (ISO 주 번호 차이가 1)
			try {
				int prevY = Integer.parseInt(prev.substring(0, 4));
				int prevW = Integer.parseInt(prev.substring(5));
				int currY = Integer.parseInt(curr.substring(0, 4));
				int currW = Integer.parseInt(curr.substring(5));
				// 연도 같고 주 1 차이, 또는 연도 바뀌면서 첫 주(52/53→1)
				boolean consecutive = (currY == prevY && currW == prevW + 1)
						|| (currY == prevY + 1 && prevW >= 52 && currW == 1);
				if (consecutive) { cur++; max = Math.max(max, cur); }
				else { cur = 1; }
			} catch (NumberFormatException e) {
				cur = 1;
			}
		}
		return max;
	}

	// ─────────────────────────────────────────────────────────────
	// (기존) 연간 통계용 뱃지 집계 - getStat()에서 사용
	// ─────────────────────────────────────────────────────────────
	private List<String> calcBadges(int memberId, int totalCount) throws Exception {
		List<String> badges = new ArrayList<>();
		if (totalCount >= 1)  badges.add("FIRST_MOVIE");
		if (totalCount >= 10) badges.add("REGULAR");
		if (totalCount >= 50) badges.add("MANIA");
		return badges;
	}

}

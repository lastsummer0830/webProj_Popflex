package diary.dto;

import java.util.List;
import java.util.Map;

/*
  DiaryStatDTO
  연간 통계 페이지(diaryStat.jsp)에 전달할 집계 데이터를 담는 DTO
  - Java에서 집계 후 넘기는 방식 (DB에 별도 저장 없음)
 */

public class DiaryStatDTO {

	private int year; // 조회 연도
	private int totalCount; // 총 관람 편수

	// 평균 팝콘 (소수점 한 자리, 미입력 시 0.0)
	private double avgPopcornRating;

	// 가장 많이 간 극장명 (예매 기반 데이터에서 집계)
	private String topTheater;

	// 감정 태그 빈도 맵 (태그명 → 횟수), 빈도 내림차순
	private List<Map.Entry<String, Integer>> tagFreqList;

	// 월별 관람 편수 (1월~12월, 인덱스 0=1월)
	private int[] monthlyCount;

	// ── 뱃지 시스템 (동적 집계, DB 저장 없이 Java 조건 판단) ──
	// 뱃지 코드 목록 (획득한 뱃지만 포함)
	private List<String> earnedBadges;
	
	
	// ─────────────────────────────────────────────────────
	// Getters & Setters
	// ─────────────────────────────────────────────────────

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public double getAvgPopcornRating() {
		return avgPopcornRating;
	}

	public void setAvgPopcornRating(double avgPopcornRating) {
		this.avgPopcornRating = avgPopcornRating;
	}

	public String getTopTheater() {
		return topTheater;
	}

	public void setTopTheater(String topTheater) {
		this.topTheater = topTheater;
	}

	public List<Map.Entry<String, Integer>> getTagFreqList() {
		return tagFreqList;
	}

	public void setTagFreqList(List<Map.Entry<String, Integer>> tagFreqList) {
		this.tagFreqList = tagFreqList;
	}

	public int[] getMonthlyCount() {
		return monthlyCount;
	}

	public void setMonthlyCount(int[] monthlyCount) {
		this.monthlyCount = monthlyCount;
	}

	public List<String> getEarnedBadges() {
		return earnedBadges;
	}

	public void setEarnedBadges(List<String> earnedBadges) {
		this.earnedBadges = earnedBadges;
	}

	
	
	
	

}

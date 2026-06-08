package diary.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/*
  DiaryDTO
  DIARY_ENTRY 테이블 + 조인 결과(영화명, 포스터, 태그 목록 등)를 담는 DTO
  - star_rating: 별점 (1.0 ~ 5.0, 0.5 단위)
  - review_id: 리뷰 연동 FK (NULL 가능)
  - tagList: DIARY_TAG 조인 결과 (태그명 목록)
 */
public class DiaryDTO {
	
	// ── DIARY_ENTRY 기본 컬럼 ─────────────────────────────
    private int    diaryId;        // diary_id (PK)
    private int    memberId;       // member_id (FK)
    private int    movieId;        // movie_id (FK)
    private Integer reservationId;  // reservation_id (FK, NULL 가능)
    private Integer reviewId;       // review_id (FK, NULL 가능 - 리뷰 연동 선택)
    private Date   watchDate;      // watch_date (관람일)
    private double starRating;     // star_rating (1.0~5.0, 0.5 단위)
    private double popcornRating;  // popcorn_rating (1.0~5.0)
    private Timestamp createdAt;   // created_at

    // ── 조인 결과 (MOVIE 테이블 조인) ────────────────────
    private String movieTitle;     // MOVIE.title
    private String posterUrl;      // MOVIE.poster_url
    private String genre;          // MOVIE.genre
    private int    runtime;        // MOVIE.runtime (분)

    // ── 조인 결과 (TAG, DIARY_TAG 조인) ──────────────────
    private List<String> tagList;  // 선택된 감정 태그명 목록

    // ── 조인 결과 (SCHEDULE, SCREEN, THEATER) ────────────
    private String theaterName;    // 극장명 (예매 기반 다이어리에서 표시용)
    private String screenName;     // 상영관명

    // ── 조인 결과 (REVIEW) ───────────────────────────────
    private String reviewContent;  // REVIEW.content
    private String reviewFreshYn;  // REVIEW.fresh_yn
    private String reviewPublicYn; // REVIEW.public_yn
    
    
    // ─────────────────────────────────────────────────────
    // Getters & Setters
    // ─────────────────────────────────────────────────────
    
	public int getDiaryId() {
		return diaryId;
	}
	public void setDiaryId(int diaryId) {
		this.diaryId = diaryId;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public Integer getReservationId() {
		return reservationId;
	}
	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}
	public Integer getReviewId() {
		return reviewId;
	}
	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}
	public Date getWatchDate() {
		return watchDate;
	}
	public void setWatchDate(Date watchDate) {
		this.watchDate = watchDate;
	}
	public double getStarRating() {
		return starRating;
	}
	public void setStarRating(double starRating) {
		this.starRating = starRating;
	}
	public double getPopcornRating() {
		return popcornRating;
	}
	public void setPopcornRating(double popcornRating) {
		this.popcornRating = popcornRating;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public String getMovieTitle() {
		return movieTitle;
	}
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public int getRuntime() {
		return runtime;
	}
	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}
	public List<String> getTagList() {
		return tagList;
	}
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	public String getTheaterName() {
		return theaterName;
	}
	public void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getReviewContent() {
		return reviewContent;
	}
	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}
	public String getReviewFreshYn() {
		return reviewFreshYn;
	}
	public void setReviewFreshYn(String reviewFreshYn) {
		this.reviewFreshYn = reviewFreshYn;
	}
	public String getReviewPublicYn() {
		return reviewPublicYn;
	}
	public void setReviewPublicYn(String reviewPublicYn) {
		this.reviewPublicYn = reviewPublicYn;
	}

    
    

}

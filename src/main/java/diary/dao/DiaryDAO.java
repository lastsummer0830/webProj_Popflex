package diary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import diary.dto.DiaryDTO;

/*
  DiaryDAO
  DIARY_ENTRY, DIARY_TAG 테이블 접근 클래스
 */

public class DiaryDAO {

	// DB 연결 반환 - common.DBUtil 사용
	private Connection getConnection() throws Exception {

		return common.DBUtil.getConnection();
	}

	private Integer getNullableInt(ResultSet rs, String columnName) throws SQLException {
		int value = rs.getInt(columnName);
		return rs.wasNull() ? null : value;
	}

	// ─────────────────────────────────────────────────────────────
	// 1. 다이어리 목록 조회 (본인 전체, 연도 필터 포함)
	// - DIARY_ENTRY JOIN MOVIE
		// - 정렬: 최신순(기본) / 오래된순 / 팝콘높은순
	// ─────────────────────────────────────────────────────────────

	public List<DiaryDTO> getDiaryList(int memberId, String year, String sort) throws Exception {
		List<DiaryDTO> list = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.diary_id, d.movie_id, d.reservation_id, d.review_id, ");
		sql.append("       d.watch_date, d.popcorn_rating, d.created_at, ");
		sql.append("       m.title AS movie_title, m.poster_url, CAST(NULL AS VARCHAR2(100)) AS genre, m.runtime, ");
		sql.append("       rv.content AS review_content, rv.fresh_yn AS review_fresh_yn, rv.public_yn AS review_public_yn, ");
		sql.append("       th.theater_name, sc.screen_name ");
		sql.append("  FROM DIARY_ENTRY d ");
		sql.append("  JOIN MOVIE m ON d.movie_id = m.movie_id ");
		sql.append("  LEFT JOIN REVIEW rv ON d.review_id = rv.review_id ");
		sql.append("  LEFT JOIN RESERVATION r ON d.reservation_id = r.reservation_id ");
		sql.append("  LEFT JOIN SCHEDULE sch ON r.schedule_id = sch.schedule_id ");
		sql.append("  LEFT JOIN SCREEN sc ON sch.screen_id = sc.screen_id ");
		sql.append("  LEFT JOIN THEATER th ON sc.theater_id = th.theater_id ");
		sql.append(" WHERE d.member_id = ? ");

		// 연도 필터 (선택)
		if (year != null && !year.isEmpty()) {
			sql.append(" AND TO_CHAR(d.watch_date, 'YYYY') = ? ");
		}

		// 정렬 옵션
		if ("oldest".equals(sort)) {
			sql.append(" ORDER BY d.watch_date ASC ");
		} else if ("star".equals(sort)) {
			sql.append(" ORDER BY d.popcorn_rating DESC NULLS LAST ");
		} else {
			sql.append(" ORDER BY d.watch_date DESC "); // 기본: 최신순
		}

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

			ps.setInt(1, memberId);
			if (year != null && !year.isEmpty()) {
				ps.setString(2, year);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					DiaryDTO dto = new DiaryDTO();
					dto.setDiaryId(rs.getInt("diary_id"));
					dto.setMemberId(memberId);
					dto.setMovieId(rs.getInt("movie_id"));
					dto.setReservationId(getNullableInt(rs, "reservation_id"));
					dto.setReviewId(getNullableInt(rs, "review_id"));
					dto.setWatchDate(rs.getDate("watch_date"));
					dto.setPopcornRating(rs.getDouble("popcorn_rating"));
					dto.setCreatedAt(rs.getTimestamp("created_at"));
					dto.setMovieTitle(rs.getString("movie_title"));
					dto.setPosterUrl(rs.getString("poster_url"));
					dto.setGenre(rs.getString("genre"));
					dto.setRuntime(rs.getInt("runtime"));
					dto.setReviewContent(rs.getString("review_content"));
					dto.setReviewFreshYn(rs.getString("review_fresh_yn"));
					dto.setReviewPublicYn(rs.getString("review_public_yn"));
					dto.setTheaterName(rs.getString("theater_name"));
					dto.setScreenName(rs.getString("screen_name"));
					list.add(dto);
				}
			}
		}
		return list;

	}

	public List<DiaryDTO> getWritableDiaryList(int memberId) throws Exception {
		List<DiaryDTO> list = new ArrayList<>();

		String sql = "SELECT d.diary_id, d.movie_id, d.reservation_id, d.review_id, "
				+ "       d.watch_date, d.popcorn_rating, d.created_at, "
				+ "       m.title AS movie_title, m.poster_url, CAST(NULL AS VARCHAR2(100)) AS genre, m.runtime, "
				+ "       th.theater_name, sc.screen_name "
				+ "  FROM DIARY_ENTRY d "
				+ "  JOIN MOVIE m ON d.movie_id = m.movie_id "
				+ "  LEFT JOIN RESERVATION r ON d.reservation_id = r.reservation_id "
				+ "  LEFT JOIN SCHEDULE sch ON r.schedule_id = sch.schedule_id "
				+ "  LEFT JOIN SCREEN sc ON sch.screen_id = sc.screen_id "
				+ "  LEFT JOIN THEATER th ON sc.theater_id = th.theater_id "
				+ " WHERE d.member_id = ? "
				+ "   AND d.review_id IS NULL "
				+ "   AND (r.reservation_id IS NULL OR r.status = 'Y') "
				+ " ORDER BY d.watch_date DESC ";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					DiaryDTO dto = new DiaryDTO();
					dto.setDiaryId(rs.getInt("diary_id"));
					dto.setMemberId(memberId);
					dto.setMovieId(rs.getInt("movie_id"));
					dto.setReservationId(getNullableInt(rs, "reservation_id"));
					dto.setReviewId(getNullableInt(rs, "review_id"));
					dto.setWatchDate(rs.getDate("watch_date"));
					dto.setPopcornRating(rs.getDouble("popcorn_rating"));
					dto.setCreatedAt(rs.getTimestamp("created_at"));
					dto.setMovieTitle(rs.getString("movie_title"));
					dto.setPosterUrl(rs.getString("poster_url"));
					dto.setGenre(rs.getString("genre"));
					dto.setRuntime(rs.getInt("runtime"));
					dto.setTheaterName(rs.getString("theater_name"));
					dto.setScreenName(rs.getString("screen_name"));
					list.add(dto);
				}
			}
		}
		return list;
	}

	// ─────────────────────────────────────────────────────────────
	// 2. 달력용 조회 (연-월 기준, watch_date 그룹)
	// - AJAX 요청, JSON으로 내려줄 데이터
	// - 반환: diary_id, watch_date, movie_title, poster_url
	// ─────────────────────────────────────────────────────────────

	public List<DiaryDTO> getDiaryByMonth(int memberId, String year, String month) throws Exception {
		List<DiaryDTO> list = new ArrayList<>();

		String sql = "SELECT d.diary_id, d.movie_id, d.review_id, d.watch_date, d.popcorn_rating, "
				+ "       m.title AS movie_title, m.poster_url " + "  FROM DIARY_ENTRY d "
				+ "  JOIN MOVIE m ON d.movie_id = m.movie_id " + " WHERE d.member_id = ? "
				+ "   AND TO_CHAR(d.watch_date, 'YYYY') = ? " + "   AND TO_CHAR(d.watch_date, 'MM')   = ? "
				+ " ORDER BY d.watch_date ASC ";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, memberId);
			ps.setString(2, year);
			// 월은 두 자리로 맞춤 (예: "5" → "05")
			ps.setString(3, String.format("%02d", Integer.parseInt(month)));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					DiaryDTO dto = new DiaryDTO();
					dto.setDiaryId(rs.getInt("diary_id"));
					dto.setMovieId(rs.getInt("movie_id"));
					dto.setReviewId(getNullableInt(rs, "review_id"));
					dto.setWatchDate(rs.getDate("watch_date"));
					dto.setPopcornRating(rs.getDouble("popcorn_rating"));
					dto.setMovieTitle(rs.getString("movie_title"));
					dto.setPosterUrl(rs.getString("poster_url"));
					list.add(dto);
				}
			}
		}
		return list;

	}

	// ─────────────────────────────────────────────────────────────
	// 3. 다이어리 상세 조회 (diary_id 기준)
	// ─────────────────────────────────────────────────────────────

	public DiaryDTO getDiaryDetail(int diaryId) throws Exception {

		DiaryDTO dto = null;

		String sql = "SELECT d.diary_id, d.member_id, d.movie_id, d.reservation_id, d.review_id, "
				+ "       d.watch_date, d.popcorn_rating, d.created_at, "
				+ "       m.title AS movie_title, m.poster_url, m.runtime, " + "       th.theater_name, sc.screen_name "
				+ "  FROM DIARY_ENTRY d " + "  JOIN MOVIE m ON d.movie_id = m.movie_id "
				+ "  LEFT JOIN RESERVATION r  ON d.reservation_id = r.reservation_id "
				+ "  LEFT JOIN SCHEDULE   sch ON r.schedule_id = sch.schedule_id "
				+ "  LEFT JOIN SCREEN     sc  ON sch.screen_id = sc.screen_id "
				+ "  LEFT JOIN THEATER    th  ON sc.theater_id = th.theater_id " + " WHERE d.diary_id = ? ";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, diaryId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					dto = new DiaryDTO();
					dto.setDiaryId(rs.getInt("diary_id"));
					dto.setMemberId(rs.getInt("member_id"));
					dto.setMovieId(rs.getInt("movie_id"));
					dto.setReservationId(getNullableInt(rs, "reservation_id"));
					dto.setReviewId(getNullableInt(rs, "review_id"));
					dto.setWatchDate(rs.getDate("watch_date"));
					dto.setPopcornRating(rs.getDouble("popcorn_rating"));
					dto.setCreatedAt(rs.getTimestamp("created_at"));
					dto.setMovieTitle(rs.getString("movie_title"));
					dto.setPosterUrl(rs.getString("poster_url"));
					dto.setRuntime(rs.getInt("runtime"));
					dto.setTheaterName(rs.getString("theater_name"));
					dto.setScreenName(rs.getString("screen_name"));
				}
			}
		}
		return dto;

	}

	// ─────────────────────────────────────────────────────────────
	// 4. 태그 조회 (diary_id 기준 - 해당 다이어리에 연결된 태그명 목록)
	// ─────────────────────────────────────────────────────────────
	public List<String> getTagsByDiaryId(int diaryId) throws Exception {
		List<String> tags = new ArrayList<>();

		String sql = "SELECT t.tag_name " + "  FROM DIARY_TAG dt " + "  JOIN TAG t ON dt.tag_id = t.tag_id "
				+ " WHERE dt.diary_id = ? " + " ORDER BY t.tag_id ";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, diaryId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					tags.add(rs.getString("tag_name"));
				}
			}
		}
		return tags;
	}

	// ─────────────────────────────────────────────────────────────
	// 5. 전체 태그 목록 조회 (태그 선택 UI용)
	// ─────────────────────────────────────────────────────────────
	public List<Map<String, Object>> getAllTags() throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();

		String sql = "SELECT tag_id, tag_name FROM TAG ORDER BY tag_id";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();
				map.put("tagId", rs.getInt("tag_id"));
				map.put("tagName", rs.getString("tag_name"));
				list.add(map);
			}
		}
		return list;
	}

	// ─────────────────────────────────────────────────────────────
	// 6. 감정 태그 등록/수정
	// - 기존 DIARY_TAG 삭제 후 새로 INSERT (교체 방식)
	// ─────────────────────────────────────────────────────────────
	public void updateTags(int diaryId, int[] tagIds) throws Exception {
		String deleteSql = "DELETE FROM DIARY_TAG WHERE diary_id = ?";
		String insertSql = "INSERT INTO DIARY_TAG (diary_id, tag_id) VALUES (?, ?)";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try {
				// 기존 태그 전체 삭제
				try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
					ps.setInt(1, diaryId);
					ps.executeUpdate();
				}
				// 새 태그 삽입
				if (tagIds != null && tagIds.length > 0) {
					try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
						for (int tagId : tagIds) {
							ps.setInt(1, diaryId);
							ps.setInt(2, tagId);
							ps.addBatch();
						}
						ps.executeBatch();
					}
				}
				// 팝콘 평점은 별도 메서드에서 함께 처리
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
		}
	}

	// ─────────────────────────────────────────────────────────────
	// 7. 팝콘 평점 업데이트
	// ─────────────────────────────────────────────────────────────
	public void updatePopcornRating(int diaryId, double popcornRating) throws Exception {
		String sql = "UPDATE DIARY_ENTRY SET popcorn_rating = ? WHERE diary_id = ?";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDouble(1, popcornRating);
			ps.setInt(2, diaryId);
			ps.executeUpdate();
		}
	}

	public boolean saveReviewAndLinkDiary(DiaryDTO diary, String freshYn, String content) throws Exception {
		String reviewText = content == null ? "" : content.trim();
		if (reviewText.isEmpty()) {
			return false;
		}

		String normalizedFreshYn = "N".equals(freshYn) ? "N" : "Y";
		Integer reviewId = diary.getReviewId();

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try {
				if (reviewId == null) {
					reviewId = findLatestReviewId(conn, diary.getMemberId(), diary.getMovieId());
				}

				if (reviewId == null) {
					reviewId = insertReview(conn, diary, normalizedFreshYn, reviewText);
				} else {
					updateReview(conn, diary, reviewId, normalizedFreshYn, reviewText);
				}

				if (reviewId != null) {
					linkDiaryReview(conn, diary.getDiaryId(), reviewId);
				}

				conn.commit();
				return reviewId != null;
			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
		}
	}

	public boolean deleteDiaryReviewState(int diaryId, int memberId) throws Exception {
		String selectSql = "SELECT review_id FROM DIARY_ENTRY WHERE diary_id = ? AND member_id = ?";
		String deleteTagsSql = "DELETE FROM DIARY_TAG WHERE diary_id = ?";
		String clearDiarySql = "UPDATE DIARY_ENTRY SET review_id = NULL, popcorn_rating = NULL WHERE diary_id = ? AND member_id = ?";
		String deleteReviewSql = "DELETE FROM REVIEW WHERE review_id = ? AND member_id = ? AND public_yn = 'N' "
				+ "AND NOT EXISTS (SELECT 1 FROM DIARY_ENTRY WHERE review_id = ?)";

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try {
				Integer reviewId = null;
				try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
					ps.setInt(1, diaryId);
					ps.setInt(2, memberId);
					try (ResultSet rs = ps.executeQuery()) {
						if (!rs.next()) {
							conn.rollback();
							return false;
						}
						reviewId = getNullableInt(rs, "review_id");
					}
				}

				try (PreparedStatement ps = conn.prepareStatement(deleteTagsSql)) {
					ps.setInt(1, diaryId);
					ps.executeUpdate();
				}

				int updated;
				try (PreparedStatement ps = conn.prepareStatement(clearDiarySql)) {
					ps.setInt(1, diaryId);
					ps.setInt(2, memberId);
					updated = ps.executeUpdate();
				}

				if (reviewId != null) {
					try (PreparedStatement ps = conn.prepareStatement(deleteReviewSql)) {
						ps.setInt(1, reviewId);
						ps.setInt(2, memberId);
						ps.setInt(3, reviewId);
						ps.executeUpdate();
					}
				}

				conn.commit();
				return updated > 0;
			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
		}
	}
	private Integer findLatestReviewId(Connection conn, int memberId, int movieId) throws SQLException {
		String sql = "SELECT review_id FROM ("
				+ "SELECT review_id FROM REVIEW WHERE member_id = ? AND movie_id = ? ORDER BY created_at DESC"
				+ ") WHERE ROWNUM = 1";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setInt(2, movieId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("review_id");
				}
			}
		}
		return null;
	}

	private Integer insertReview(Connection conn, DiaryDTO diary, String freshYn, String content) throws SQLException {
		String sql = "INSERT INTO REVIEW (movie_id, member_id, fresh_yn, public_yn, content) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, diary.getMovieId());
			ps.setInt(2, diary.getMemberId());
			ps.setString(3, freshYn);
			ps.setString(4, "N");
			ps.setString(5, content);
			ps.executeUpdate();
		}

		return findLatestReviewId(conn, diary.getMemberId(), diary.getMovieId());
	}

	private void updateReview(Connection conn, DiaryDTO diary, int reviewId, String freshYn, String content)
			throws SQLException {
		String sql = "UPDATE REVIEW SET fresh_yn = ?, content = ?, updated_at = SYSTIMESTAMP "
				+ "WHERE review_id = ? AND member_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, freshYn);
			ps.setString(2, content);
			ps.setInt(3, reviewId);
			ps.setInt(4, diary.getMemberId());
			ps.executeUpdate();
		}
	}

	private void linkDiaryReview(Connection conn, int diaryId, int reviewId) throws SQLException {
		String sql = "UPDATE DIARY_ENTRY SET review_id = ? WHERE diary_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, reviewId);
			ps.setInt(2, diaryId);
			ps.executeUpdate();
		}
	}

	// ─────────────────────────────────────────────────────────────
	// 8. 다이어리 자동 등록 (예매 완료 후 DiaryService에서 호출)
	// - reservation_id UNIQUE 제약 때문에 중복 INSERT 불가
	// ─────────────────────────────────────────────────────────────
	public int insertDiary(DiaryDTO dto) throws Exception {
		String sql = "INSERT INTO DIARY_ENTRY " + "(member_id, movie_id, reservation_id, watch_date) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, new String[] { "diary_id" })) {

			ps.setInt(1, dto.getMemberId());
			ps.setInt(2, dto.getMovieId());
			if (dto.getReservationId() != null) {
				ps.setInt(3, dto.getReservationId());
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			ps.setDate(4, new java.sql.Date(dto.getWatchDate().getTime()));
			ps.executeUpdate();

			// 생성된 diary_id 반환
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				}
			}
		}
		return -1;
	}

	// ─────────────────────────────────────────────────────────────
	// 뱃지 집계용 쿼리 메서드들
	// ─────────────────────────────────────────────────────────────

	// 전체 기간 총 관람 편수 (뱃지 기준)
	public int countAllDiary(int memberId) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY WHERE member_id = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	private int queryCount(String sql, int memberId) throws Exception {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	public int countRatingEquals(int memberId, double rating) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY WHERE member_id = ? AND popcorn_rating = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setDouble(2, rating);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	public int countRatingAtMost(int memberId, double rating) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY WHERE member_id = ? AND popcorn_rating <= ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setDouble(2, rating);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	public int countLongReview(int memberId, int minLength) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY d "
				+ "JOIN REVIEW rv ON d.review_id = rv.review_id "
				+ "WHERE d.member_id = ? AND LENGTH(rv.content) >= ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setInt(2, minLength);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	public int countDistinctTagsUsed(int memberId) throws Exception {
		String sql = "SELECT COUNT(DISTINCT dt.tag_id) FROM DIARY_TAG dt "
				+ "JOIN DIARY_ENTRY d ON dt.diary_id = d.diary_id WHERE d.member_id = ?";
		return queryCount(sql, memberId);
	}

	public int countDiaryWithAtLeastTags(int memberId, int minTags) throws Exception {
		String sql = "SELECT COUNT(*) FROM ("
				+ "SELECT d.diary_id FROM DIARY_ENTRY d "
				+ "JOIN DIARY_TAG dt ON d.diary_id = dt.diary_id "
				+ "WHERE d.member_id = ? GROUP BY d.diary_id HAVING COUNT(dt.tag_id) >= ?)";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setInt(2, minTags);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	public int countGenreLike(int memberId, String genreKeyword) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY d "
				+ "JOIN MOVIE m ON d.movie_id = m.movie_id "
				+ "WHERE d.member_id = ? AND m.genre LIKE ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setString(2, "%" + genreKeyword + "%");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	public int countGenreAny(int memberId, String... genreKeywords) throws Exception {
		if (genreKeywords == null || genreKeywords.length == 0) return 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM DIARY_ENTRY d JOIN MOVIE m ON d.movie_id = m.movie_id ");
		sql.append("WHERE d.member_id = ? AND (");
		for (int i = 0; i < genreKeywords.length; i++) {
			if (i > 0) sql.append(" OR ");
			sql.append("m.genre LIKE ?");
		}
		sql.append(")");
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			ps.setInt(1, memberId);
			for (int i = 0; i < genreKeywords.length; i++) {
				ps.setString(i + 2, "%" + genreKeywords[i] + "%");
			}
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	public double sumPopcornRating(int memberId) throws Exception {
		String sql = "SELECT NVL(SUM(NVL(popcorn_rating, 0)), 0) FROM DIARY_ENTRY WHERE member_id = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getDouble(1);
			}
		}
		return 0;
	}
	// 팝콘 1.0점 기록 횟수 (혹평가 뱃지)
	public int countLowRating(int memberId) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY WHERE member_id = ? AND popcorn_rating = 1.0";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	// 팝콘 4.5 이상 기록 횟수 (신선한 눈 뱃지)
	public int countHighRating(int memberId) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY WHERE member_id = ? AND popcorn_rating >= 4.5";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	// 감정 태그 달린 다이어리 편수 (팝콘 러버 뱃지)
	public int countTaggedDiary(int memberId) throws Exception {
		String sql = "SELECT COUNT(DISTINCT dt.diary_id) FROM DIARY_TAG dt "
				+ "JOIN DIARY_ENTRY d ON dt.diary_id = d.diary_id WHERE d.member_id = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	// 심야(22시 이후) 상영 관람 횟수 (심야 시네마 뱃지)
	public int countMidnightWatch(int memberId) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY d "
				+ "JOIN RESERVATION r ON d.reservation_id = r.reservation_id "
				+ "JOIN SCHEDULE sch ON r.schedule_id = sch.schedule_id "
				+ "WHERE d.member_id = ? AND TO_NUMBER(TO_CHAR(sch.start_time, 'HH24')) >= 22";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	// 가장 많이 본 장르의 편수 (장르 마스터 뱃지)
	public int getMaxGenreCount(int memberId) throws Exception {
		String sql = "SELECT MAX(cnt) FROM ("
				+ "SELECT m.genre, COUNT(*) AS cnt FROM DIARY_ENTRY d "
				+ "JOIN MOVIE m ON d.movie_id = m.movie_id "
				+ "WHERE d.member_id = ? AND m.genre IS NOT NULL "
				+ "GROUP BY m.genre) t";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	// 같은 감독 영화 최대 편수 (감독 팬 뱃지)
	public int getMaxDirectorCount(int memberId) throws Exception {
		String sql = "SELECT MAX(cnt) FROM ("
				+ "SELECT m.director_nm, COUNT(*) AS cnt FROM DIARY_ENTRY d "
				+ "JOIN MOVIE m ON d.movie_id = m.movie_id "
				+ "WHERE d.member_id = ? AND m.director_nm IS NOT NULL AND m.director_nm <> '' "
				+ "GROUP BY m.director_nm) t";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	// 특정 연도 관람 편수 (올해의 관객 뱃지)
	public int getYearCount(int memberId, int year) throws Exception {
		String sql = "SELECT COUNT(*) FROM DIARY_ENTRY WHERE member_id = ? AND TO_CHAR(watch_date,'YYYY') = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setString(2, String.valueOf(year));
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		}
		return 0;
	}

	// n번째 관람일 조회 (달성일 계산용, yyyy.MM.dd 형태)
	public String getNthWatchDate(int memberId, int n) throws Exception {
		String sql = "SELECT TO_CHAR(watch_date, 'YYYY.MM.DD') AS dt FROM ("
				+ "SELECT watch_date, ROW_NUMBER() OVER (ORDER BY watch_date ASC) AS rn "
				+ "FROM DIARY_ENTRY WHERE member_id = ?) t WHERE t.rn = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			ps.setInt(2, n);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getString("dt");
			}
		}
		return null;
	}

	// 전체 관람 주(ISO 연-주) 목록 (연속 관람 판단용)
	public List<String> getAllWatchWeeks(int memberId) throws Exception {
		List<String> weeks = new ArrayList<>();
		String sql = "SELECT DISTINCT TO_CHAR(watch_date, 'IYYY-IW') AS yw "
				+ "FROM DIARY_ENTRY WHERE member_id = ? ORDER BY yw";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) weeks.add(rs.getString("yw"));
			}
		}
		return weeks;
	}

	// ─────────────────────────────────────────────────────────────
	// 9. 연도 목록 조회 (사이드바 연도 폴더용)
	// ─────────────────────────────────────────────────────────────
	public List<String> getYearList(int memberId) throws Exception {
		List<String> years = new ArrayList<>();

		String sql = "SELECT DISTINCT TO_CHAR(watch_date, 'YYYY') AS yr " + "  FROM DIARY_ENTRY "
				+ " WHERE member_id = ? " + " ORDER BY yr DESC";

		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, memberId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					years.add(rs.getString("yr"));
				}
			}
		}
		return years;
	}

	// ─────────────────────────────────────────────────────────────
	// 10. 연간 통계용 데이터 조회 (DiaryStatDTO 구성용)
	// - 총 편수, 월별 카운트, 평균 팝콘, 극장별 카운트
	// ─────────────────────────────────────────────────────────────
	public Map<String, Object> getStatData(int memberId, int year) throws Exception {
		Map<String, Object> result = new HashMap<>();
		String yearStr = String.valueOf(year);

		try (Connection conn = getConnection()) {
			// 총 관람 편수
			String totalSql = "SELECT COUNT(*) AS cnt FROM DIARY_ENTRY "
					+ " WHERE member_id = ? AND TO_CHAR(watch_date,'YYYY') = ?";
			try (PreparedStatement ps = conn.prepareStatement(totalSql)) {
				ps.setInt(1, memberId);
				ps.setString(2, yearStr);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next())
						result.put("totalCount", rs.getInt("cnt"));
				}
			}

		// 평균 팝콘
		String avgSql = "SELECT AVG(popcorn_rating) AS avg_popcorn FROM DIARY_ENTRY "
					+ " WHERE member_id = ? AND TO_CHAR(watch_date,'YYYY') = ? "
					+ "   AND popcorn_rating IS NOT NULL AND popcorn_rating > 0";
			try (PreparedStatement ps = conn.prepareStatement(avgSql)) {
				ps.setInt(1, memberId);
				ps.setString(2, yearStr);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next())
						result.put("avgPopcornRating", rs.getDouble("avg_popcorn"));
				}
			}

			// 가장 많이 간 극장
			String theaterSql = "SELECT th.theater_name, COUNT(*) AS cnt " + "  FROM DIARY_ENTRY d "
					+ "  JOIN RESERVATION r   ON d.reservation_id = r.reservation_id "
					+ "  JOIN SCHEDULE   sch  ON r.schedule_id   = sch.schedule_id "
					+ "  JOIN SCREEN     sc   ON sch.screen_id   = sc.screen_id "
					+ "  JOIN THEATER    th   ON sc.theater_id   = th.theater_id "
					+ " WHERE d.member_id = ? AND TO_CHAR(d.watch_date,'YYYY') = ? "
					+ " GROUP BY th.theater_name ORDER BY cnt DESC FETCH FIRST 1 ROWS ONLY";
			try (PreparedStatement ps = conn.prepareStatement(theaterSql)) {
				ps.setInt(1, memberId);
				ps.setString(2, yearStr);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next())
						result.put("topTheater", rs.getString("theater_name"));
				}
			}

			// 월별 관람 편수 (1~12월)
			String monthlySql = "SELECT TO_NUMBER(TO_CHAR(watch_date,'MM')) AS mon, COUNT(*) AS cnt "
					+ "  FROM DIARY_ENTRY " + " WHERE member_id = ? AND TO_CHAR(watch_date,'YYYY') = ? "
					+ " GROUP BY TO_CHAR(watch_date,'MM') ORDER BY mon";
			int[] monthly = new int[12];
			try (PreparedStatement ps = conn.prepareStatement(monthlySql)) {
				ps.setInt(1, memberId);
				ps.setString(2, yearStr);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						int mon = rs.getInt("mon");
						monthly[mon - 1] = rs.getInt("cnt");
					}
				}
			}
			result.put("monthlyCount", monthly);

			// 감정 태그 빈도
			String tagSql = "SELECT t.tag_name, COUNT(*) AS cnt " + "  FROM DIARY_TAG dt "
					+ "  JOIN TAG t ON dt.tag_id = t.tag_id " + "  JOIN DIARY_ENTRY d ON dt.diary_id = d.diary_id "
					+ " WHERE d.member_id = ? AND TO_CHAR(d.watch_date,'YYYY') = ? "
					+ "   AND d.review_id IS NOT NULL "
					+ " GROUP BY t.tag_name ORDER BY cnt DESC";
			List<Map.Entry<String, Integer>> tagFreqList = new ArrayList<>();
			try (PreparedStatement ps = conn.prepareStatement(tagSql)) {
				ps.setInt(1, memberId);
				ps.setString(2, yearStr);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						final String tagName = rs.getString("tag_name");
						final int cnt = rs.getInt("cnt");
						tagFreqList.add(new AbstractMap.SimpleEntry<>(tagName, cnt));
					}
				}
			}
			result.put("tagFreqList", tagFreqList);
		}
		return result;
	}
}

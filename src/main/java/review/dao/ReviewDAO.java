package review.dao;

import common.DBUtil;
import review.dto.ReviewDTO;
import review.dto.ReviewStatDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private Connection getConnection() throws Exception {
        return DBUtil.getConnection();
    }

    private ReviewDTO mapReview(ResultSet rs) throws Exception {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(rs.getInt("review_id"));
        dto.setMovieId(rs.getInt("movie_id"));
        dto.setMemberId(rs.getInt("member_id"));
        dto.setFreshYn(rs.getString("fresh_yn"));  // 컬럼명 fresh_yn, 뜻은 터졌다 
        dto.setPublicYn(rs.getString("public_yn"));  // 'Y'=전체 공개, 'N'=친구공개  
        dto.setContent(rs.getString("content"));
        dto.setCreatedAt(rs.getString("created_at"));
        dto.setUpdatedAt(rs.getString("updated_at"));
        dto.setMemberName(rs.getString("member_name"));
        dto.setPosterUrl(rs.getString("poster_url"));
        dto.setMovieTitle(rs.getString("movie_title"));
        return dto;
    }

    /**
     * 영화별 리뷰 목록.
     * 기본 호출은 전체공개 리뷰만 반환한다.
     * 친구공개까지 보여주려면 getReviewListByMovie(movieId, viewerMemberId)를 사용한다.
     */
    public List<ReviewDTO> getReviewListByMovie(int movieId) {
        return getReviewListByMovie(movieId, 0);
    }

    // 리뷰 목록 조회 (영화별)
    /**
     * 영화별 리뷰 목록 + 조회자 권한 반영.
     * - 전체공개(public_yn='Y')는 모두 조회
     * - 친구공개(public_yn='N')는 작성자 본인 또는 친구만 조회
     * public_yn = 'Y'(전체 공개), 'N'(친구공개)     
     * fresh_yn  = 'Y'(터졌다),  'N'(안터졌다) → 컬럼명은 fresh_yn 그대로
     */
    public List<ReviewDTO> getReviewListByMovie(int movieId, int viewerMemberId) {
        List<ReviewDTO> list = new ArrayList<>();

        // REVIEW + MEMBER 테이블 JOIN → 작성자 이름도 함께 가져옴
        // public_yn IN ('Y','N') : 공개 + 친구공개만 조회
        String sql =
            "SELECT r.review_id, r.movie_id, r.member_id, r.fresh_yn, " +
            "       r.public_yn, r.content, " +
            "       TO_CHAR(r.created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at, " +
            "       TO_CHAR(r.updated_at, 'YYYY-MM-DD HH24:MI:SS') AS updated_at, " +
            "       m.name AS member_name, " +
            "       mv.poster_url, mv.title AS movie_title " +
            "FROM review r " +
            "JOIN member m ON r.member_id = m.member_id " +
            "JOIN movie mv ON r.movie_id = mv.movie_id " +
            "WHERE r.movie_id = ? " +
            "  AND m.is_use = 'Y' " +
            "  AND ( " +
            "        r.public_yn = 'Y' " +
            "        OR (? > 0 AND r.member_id = ?) " +
            "        OR (? > 0 AND EXISTS ( " +
            "              SELECT 1 FROM friend f " +
            "              WHERE f.member_a_id = LEAST(r.member_id, ?) " +
            "                AND f.member_b_id = GREATEST(r.member_id, ?) " +
            "        )) " +
            "      ) " +
            "ORDER BY r.created_at DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, viewerMemberId);
            ps.setInt(3, viewerMemberId);
            ps.setInt(4, viewerMemberId);
            ps.setInt(5, viewerMemberId);
            ps.setInt(6, viewerMemberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReview(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** 리뷰 단건 조회 */
    /**
     * 특정 리뷰 1개 가져오기
     * @param reviewId 조회할 리뷰 번호
     * @return ReviewDTO (없으면 null)
     */
    public ReviewDTO getReviewById(int reviewId) {
        ReviewDTO dto = null;

        // MEMBER, MOVIE 테이블 JOIN → 작성자 이름 + 영화 제목 + 포스터 포함
        String sql =
            "SELECT r.review_id, r.movie_id, r.member_id, r.fresh_yn, " +
            "       r.public_yn, r.content, " +
            "       TO_CHAR(r.created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at, " +
            "       TO_CHAR(r.updated_at, 'YYYY-MM-DD HH24:MI:SS') AS updated_at, " +
            "       m.name AS member_name, " +
            "       mv.poster_url, mv.title AS movie_title " +
            "FROM review r " +
            "JOIN member m ON r.member_id = m.member_id " +
            "JOIN movie mv ON r.movie_id = mv.movie_id " +
            "WHERE r.review_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reviewId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = mapReview(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    /** 리뷰 등록 */
    /**
     * 새 리뷰 DB에 저장
     * fresh_yn  = 'Y'(터졌다) 또는 'N'(안터졌다) → 컬럼명은 fresh_yn 그대로    
     * public_yn = 'Y'(전체 공개) 또는 'N'(친구공개)
     * @param dto 저장할 리뷰 데이터
     * @return 성공 1, 실패 0
     */
    public int insertReview(ReviewDTO dto) {
        int result = 0;

        // review_id는 IDENTITY 자동증가 → INSERT 제외     
        // created_at, updated_at은 DEFAULT SYSTIMESTAMP 자동 처리  
        String sql =
            "INSERT INTO review (movie_id, member_id, fresh_yn, public_yn, content) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, dto.getMovieId());
            ps.setInt(2, dto.getMemberId());
            ps.setString(3, dto.getFreshYn());  // 터졌다 여부, 컬럼명 fresh_yn  
            ps.setString(4, dto.getPublicYn());  // 'Y'=전체 공개, 'N'=친구공개 
            ps.setString(5, dto.getContent());

            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** 리뷰 수정 - 본인 리뷰만 수정 */
    /**
     * 리뷰 내용 수정(본인만 가능)
     * fresh_yn  = 'Y'(터졌다)    또는 'N'(안터졌다) → 컬럼명은 fresh_yn 그대로     
     * public_yn = 'Y'(전체 공개) 또는 'N'(친구공개)
     * @param dto 수정할 데이터 (reviewId, memberId 필수!)
     * @return 성공 1, 실패 0
     */
    public int updateReview(ReviewDTO dto) {
        int result = 0;

        // AND member_id=? -> 본인 리뷰만 수정 가능하도록 보안 처리
        // updated_at은 수정 시 현재 시간으로 자동 갱신
        String sql =
            "UPDATE review " +
            "SET fresh_yn = ?, public_yn = ?, content = ?, updated_at = SYSTIMESTAMP " +
            "WHERE review_id = ? AND member_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getFreshYn());  // 터졌다 여부, DB 컬럼명은 fresh_yn 그대로
            ps.setString(2, dto.getPublicYn());
            ps.setString(3, dto.getContent());
            ps.setInt(4, dto.getReviewId());
            ps.setInt(5, dto.getMemberId());  // 본인 확인

            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** 리뷰 삭제 - 본인 리뷰만 삭제 */
    /**
     * 리뷰 삭제(본인만 가능)
     * @param reviewId 삭제할 리뷰 번호
     * @param memberId 삭제 요청한 회원 번호 (본인 확인용)
     * @return 성공 1, 실패 0
     */
    public int deleteReview(int reviewId, int memberId) {
        int result = 0;

        // AND member_id=? -> 본인 리뷰만 삭제 가능
        String sql = "DELETE FROM review WHERE review_id = ? AND member_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            ps.setInt(2, memberId);  // 본인 확인

            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** 특정 회원의 리뷰 목록. 권한 필터링은 Servlet/Service에서 처리한다. */
    /**
     * 로그인한 회원의 내 리뷰 목록 가져오기
     * MOVIE JOIN → poster_url, movie_title 포함     
     * 내 페이지는 전체 공개(Y) + 친구공개(N) 모두 조회
     * @param memberId 로그인한 회원 번호
     * @return 내 리뷰 목록 (최신순)
     */
    public List<ReviewDTO> getMyReviewList(int memberId) {
        List<ReviewDTO> list = new ArrayList<>();

        // MEMBER, MOVIE 테이블 JOIN → 영화 제목 + 포스터 포함    
        // 내 리뷰 페이지 → 전체 공개(Y) + 친구공개(N) 모두 보임 
        String sql =
            "SELECT r.review_id, r.movie_id, r.member_id, r.fresh_yn, " +
            "       r.public_yn, r.content, " +
            "       TO_CHAR(r.created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at, " +
            "       TO_CHAR(r.updated_at, 'YYYY-MM-DD HH24:MI:SS') AS updated_at, " +
            "       m.name AS member_name, " +
            "       mv.poster_url, mv.title AS movie_title " +
            "FROM review r " +
            "JOIN member m ON r.member_id = m.member_id " +
            "JOIN movie mv ON r.movie_id = mv.movie_id " +
            "WHERE r.member_id = ? " +
            "  AND r.public_yn IN ('Y', 'N') " +
            "ORDER BY r.created_at DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReview(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** 영화별 터졌다 통계. 공개 리뷰 기준. */
    public ReviewStatDTO getReviewStat(int movieId) {
        ReviewStatDTO stat = new ReviewStatDTO();
        stat.setMovieId(movieId);

        String sql =
            "SELECT COUNT(*) AS total_count, " +
            "       SUM(CASE WHEN fresh_yn = 'Y' THEN 1 ELSE 0 END) AS fresh_count " +
            "FROM review " +
            "WHERE movie_id = ? AND public_yn = 'Y'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_count");
                    int burst = rs.getInt("fresh_count");

                    stat.setTotalCount(total);
                    stat.setBurstCount(burst);
                    stat.setNotBurstCount(total - burst);
                    stat.setBurstRate(total > 0 ? burst * 100.0 / total : 0.0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }
}

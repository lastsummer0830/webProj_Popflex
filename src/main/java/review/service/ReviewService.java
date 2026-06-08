package review.service;

import review.dao.ReviewDAO;
import review.dto.ReviewDTO;
import review.dto.ReviewStatDTO;

import java.util.List;

public class ReviewService {

    private final ReviewDAO dao = new ReviewDAO();

    /** 영화별 공개 리뷰 목록 */
    public List<ReviewDTO> getReviewListByMovie(int movieId) {
        return dao.getReviewListByMovie(movieId);
    }

    /** 영화별 리뷰 목록 + 조회자 친구 권한 반영 */
    public List<ReviewDTO> getReviewListByMovie(int movieId, int viewerMemberId) {
        return dao.getReviewListByMovie(movieId, viewerMemberId);
    }

    /** 리뷰 1개 반환 */
    public ReviewDTO getReviewById(int reviewId) {
        return dao.getReviewById(reviewId);
    }

    /** 새 리뷰 등록 */
    public int insertReview(ReviewDTO dto) {
        return dao.insertReview(dto);
    }

    /** 리뷰 수정. 실제 본인 검증은 DAO의 WHERE review_id=? AND member_id=?에서도 한 번 더 처리된다. */
    public int updateReview(ReviewDTO dto, int loginMemberId) {
        // 본인 리뷰가 아니면 수정 불가
        if (dto.getMemberId() != loginMemberId) {
            return -1; // 권한 없음
        }
        return dao.updateReview(dto);
    }

    /** 리뷰 삭제 */
    public int deleteReview(int reviewId, int loginMemberId) {
        // 먼저 해당 리뷰 조회
        ReviewDTO dto = dao.getReviewById(reviewId);
        // 리뷰가 없거나 본인 리뷰가 아니면 삭제 불가
        if (dto == null || dto.getMemberId() != loginMemberId) {
            return -1;
        }
        return dao.deleteReview(reviewId, loginMemberId);
    }

    /** 특정 회원의 리뷰 목록 */
    public List<ReviewDTO> getMyReviewList(int memberId) {
        return dao.getMyReviewList(memberId);
    }

    /** 터졌다 리뷰 통계 */
    public ReviewStatDTO getReviewStat(int movieId) {
        return dao.getReviewStat(movieId);
    }
}

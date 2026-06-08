package review.controller;

import member.dto.MemberDTO;
import review.dto.ReviewDTO;
import review.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 리뷰 목록 조회
 * 명세서: GET /review/list.do -> JSON
 * movie_id 기준, 전체공개 + 친구공개 권한 반영
 */
@WebServlet("/review/list.do")
public class ReviewListServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String movieIdParam = firstNonEmpty(
                req.getParameter("movieId"),
                req.getParameter("movie_id")
        );

        if (movieIdParam == null) {
            resp.getWriter().write("{\"result\":\"EMPTY_MOVIE_ID\",\"reviews\":[]}");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdParam.trim());
            int viewerMemberId = getLoginMemberId(req);

            List<ReviewDTO> reviewList = reviewService.getReviewListByMovie(movieId, viewerMemberId);

            resp.getWriter().write("{\"result\":\"OK\",\"reviews\":"
                    + toReviewArrayJson(reviewList)
                    + "}");

        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"result\":\"INVALID_MOVIE_ID\",\"reviews\":[]}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"result\":\"ERROR\",\"reviews\":[]}");
        }
    }

    private int getLoginMemberId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return 0;

        Object obj = session.getAttribute("loginMember");
        if (!(obj instanceof MemberDTO)) return 0;

        return ((MemberDTO) obj).getMemberId();
    }

    private String firstNonEmpty(String a, String b) {
        if (a != null && !a.trim().isEmpty()) return a;
        if (b != null && !b.trim().isEmpty()) return b;
        return null;
    }

    private String toReviewArrayJson(List<ReviewDTO> reviewList) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < reviewList.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(toReviewJson(reviewList.get(i)));
        }
        sb.append(']');
        return sb.toString();
    }

    private String toReviewJson(ReviewDTO r) {
        return "{"
                + "\"reviewId\":" + r.getReviewId() + ","
                + "\"movieId\":" + r.getMovieId() + ","
                + "\"memberId\":" + r.getMemberId() + ","
                + "\"memberName\":\"" + json(r.getMemberName()) + "\","
                + "\"freshYn\":\"" + json(r.getFreshYn()) + "\","
                + "\"publicYn\":\"" + json(r.getPublicYn()) + "\","
                + "\"content\":\"" + json(r.getContent()) + "\","
                + "\"createdAt\":\"" + json(r.getCreatedAt()) + "\","
                + "\"updatedAt\":\"" + json(r.getUpdatedAt()) + "\","
                + "\"movieTitle\":\"" + json(r.getMovieTitle()) + "\","
                + "\"posterUrl\":\"" + json(r.getPosterUrl()) + "\""
                + "}";
    }

    private String json(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}

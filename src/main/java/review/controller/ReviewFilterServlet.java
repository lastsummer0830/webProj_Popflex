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
import java.util.Comparator;
import java.util.List;

/**
 * 리뷰 정렬/필터
 * 명세서: GET /review/filter.do -> JSON
 * 최신순/오래된순/터졌다/안터졌다/내 리뷰
 */
@WebServlet("/review/filter.do")
public class ReviewFilterServlet extends HttpServlet {

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

            String filter = nvl(req.getParameter("filter"));
            String sort = nvl(req.getParameter("sort"));
            String freshYn = nvl(req.getParameter("freshYn"));

            List<ReviewDTO> reviewList = reviewService.getReviewListByMovie(movieId, viewerMemberId);

            // filter 파라미터 지원: fresh, notFresh, my
            if ("fresh".equalsIgnoreCase(filter)) {
                freshYn = "Y";
            } else if ("notFresh".equalsIgnoreCase(filter)
                    || "nonFresh".equalsIgnoreCase(filter)
                    || "rotten".equalsIgnoreCase(filter)) {
                freshYn = "N";
            }

            if ("Y".equalsIgnoreCase(freshYn) || "N".equalsIgnoreCase(freshYn)) {
                final String targetFreshYn = freshYn.toUpperCase();
                reviewList.removeIf(r -> !targetFreshYn.equals(r.getFreshYn()));
            }

            if ("my".equalsIgnoreCase(filter) || "true".equalsIgnoreCase(req.getParameter("myOnly"))) {
                if (viewerMemberId <= 0) {
                    resp.getWriter().write("{\"result\":\"NOT_LOGIN\",\"reviews\":[]}");
                    return;
                }
                reviewList.removeIf(r -> r.getMemberId() != viewerMemberId);
            }

            // 기본은 DAO에서 최신순. 오래된순 요청만 정렬 변경.
            if ("oldest".equalsIgnoreCase(sort)
                    || "old".equalsIgnoreCase(sort)
                    || "asc".equalsIgnoreCase(sort)) {
                reviewList.sort(Comparator.comparing(
                        ReviewDTO::getCreatedAt,
                        Comparator.nullsLast(String::compareTo)
                ));
            }

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

    private String nvl(String value) {
        return value == null ? "" : value.trim();
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

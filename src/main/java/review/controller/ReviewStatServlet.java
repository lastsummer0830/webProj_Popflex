package review.controller;

import review.dto.ReviewStatDTO;
import review.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * 리뷰 통계 조회
 * 명세서: GET /review/stat.do -> JSON
 * 공개 리뷰 기준
 */
@WebServlet("/review/stat.do")
public class ReviewStatServlet extends HttpServlet {

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
            resp.getWriter().write("{\"result\":\"EMPTY_MOVIE_ID\"}");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdParam.trim());
            ReviewStatDTO stat = reviewService.getReviewStat(movieId);

            if (stat == null) {
                resp.getWriter().write("{\"result\":\"ERROR\"}");
                return;
            }

            resp.getWriter().write("{"
                    + "\"result\":\"OK\"," 
                    + "\"movieId\":" + stat.getMovieId() + ","
                    + "\"totalCount\":" + stat.getTotalCount() + ","
                    + "\"burstCount\":" + stat.getBurstCount() + ","
                    + "\"notBurstCount\":" + stat.getNotBurstCount() + ","
                    + "\"burstRate\":" + String.format(Locale.US, "%.1f", stat.getBurstRate())
                    + "}");

        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"result\":\"INVALID_MOVIE_ID\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"result\":\"ERROR\"}");
        }
    }

    private String firstNonEmpty(String a, String b) {
        if (a != null && !a.trim().isEmpty()) return a;
        if (b != null && !b.trim().isEmpty()) return b;
        return null;
    }
}

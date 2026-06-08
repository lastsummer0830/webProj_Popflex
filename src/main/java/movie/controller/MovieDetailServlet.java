package movie.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import movie.dto.MovieDTO;
import movie.service.MovieService;
import review.dto.ReviewDTO;
import review.dto.ReviewStatDTO;
import review.service.ReviewService;

@WebServlet("/movie/detail.do")
public class MovieDetailServlet extends HttpServlet {

	private final MovieService movieService = new MovieService();
    private final ReviewService reviewService = new ReviewService();


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

		String kmdbMovieId = req.getParameter("movieId");
		String kmdbMovieSeq = req.getParameter("movieSeq");

		if (kmdbMovieId == null || kmdbMovieId.trim().isEmpty() || kmdbMovieSeq == null
				|| kmdbMovieSeq.trim().isEmpty()) {

			resp.sendRedirect(req.getContextPath() + "/movie/search.do");
			return;
		}

		MovieDTO movie = movieService.getOrSaveMovieDetail(kmdbMovieId, kmdbMovieSeq);

		if (movie == null) {
			resp.sendRedirect(req.getContextPath() + "/movie/search.do?error=detail");
			return;
		}
		
		int viewerMemberId = 0;
        MemberDTO loginMember = null;
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("loginMember") != null) {
            loginMember = (MemberDTO) session.getAttribute("loginMember");
            viewerMemberId = loginMember.getMemberId();
        }

        // 영화 상세에서 보여줄 리뷰 목록.
        // publicYn='Y'는 모두 조회, publicYn='N'은 작성자 본인/친구만 조회하도록 ReviewDAO에서 처리한다.
        List<ReviewDTO> reviewList = reviewService.getReviewListByMovie(movie.getMovieId(), viewerMemberId);
        ReviewStatDTO reviewStat = reviewService.getReviewStat(movie.getMovieId());

        ReviewDTO myReview = null;
        if (loginMember != null) {
            for (ReviewDTO review : reviewList) {
                if (review.getMemberId() == loginMember.getMemberId()) {
                    myReview = review;
                    break;
                }
            }
        }

        req.setAttribute("movie", movie);
        req.setAttribute("reviewList", reviewList);
        req.setAttribute("reviewStat", reviewStat);
        req.setAttribute("myReview", myReview);

		req.setAttribute("movie", movie);
		req.getRequestDispatcher("/WEB-INF/views/movie/movieDetail.jsp").forward(req, resp);
	}
}
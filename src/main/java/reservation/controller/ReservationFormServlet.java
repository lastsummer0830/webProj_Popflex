package reservation.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import movie.dto.MovieDTO;
import reservation.dto.ReservationScheduleDTO;
import reservation.dto.ReservationTheaterDTO;
import reservation.service.ReservationService;
import review.service.ReviewService;

@WebServlet("/reservation/form.do")
// 예매 시작 화면 컨트롤러
// movieId를 받아 영화 정보와 상영 일정 목록을 조회한 뒤 reservationForm.jsp로 이동한다.
public class ReservationFormServlet extends HttpServlet {

	private ReservationService reservationService = new ReservationService();
	private ReviewService reviewService = new ReviewService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html; charset=utf-8");

		// 영화 상세/검색 결과에서 전달된 movieId 파라미터 확인
		String movieIdParameter = req.getParameter("movieId");
		if (movieIdParameter == null || movieIdParameter.trim().isEmpty()) {
			req.setAttribute("errorMsg", "영화 번호가 전달되지 않았습니다.");
			req.getRequestDispatcher("/WEB-INF/views/reservation/reservationForm.jsp").forward(req, resp);
			return;
		}

		int movieId;
		try {
			movieId = Integer.parseInt(movieIdParameter);
		} catch (NumberFormatException e) {
			req.setAttribute("errorMsg", "잘못된 영화 번호입니다.");
			req.getRequestDispatcher("/WEB-INF/views/reservation/reservationForm.jsp").forward(req, resp);
			return;
		}

		try {
			// 예매 화면에 필요한 영화, 상영 일정, 극장 정보를 Service에서 조회한다.
			MovieDTO movie = reservationService.getMovieById(movieId);
			ArrayList<ReservationScheduleDTO> scheduleList = new ArrayList<>();
			ArrayList<ReservationTheaterDTO> theaterList = new ArrayList<>();

			if (movie == null) {
				req.setAttribute("errorMsg", "영화 번호 " + movieId + "번이 DB에 없습니다.");
			} else {
				reservationService.ensureDefaultSchedulesForMovie(movieId);
				scheduleList = reservationService.getReservationScheduleListByMovieId(movieId);
				theaterList = reservationService.getTheaterList();
				req.setAttribute("reviewStat", reviewService.getReviewStat(movie.getMovieId()));
			}

			req.setAttribute("movieId", movieId);
			req.setAttribute("movie", movie);
			req.setAttribute("scheduleList", scheduleList);
			req.setAttribute("theaterList", theaterList);
			req.getRequestDispatcher("/WEB-INF/views/reservation/reservationForm.jsp").forward(req, resp);
		} catch (SQLException e) {
			e.printStackTrace();
			resp.sendError(500);
		}
	}
}

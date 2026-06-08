package diary.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import diary.dto.DiaryDTO;
import diary.service.DiaryService;
import member.dto.MemberDTO;

/*
  DiaryCalendarServlet
  GET /diary/calendar.do → JSON (AJAX)
  - 달력 월 이동 시 호출
  - 파라미터: year, month
  - 반환: [{diaryId, watchDate, movieTitle, posterUrl, popcornRating}, ...]
 */

@WebServlet("/diary/calendar.do")
public class DiaryCalendarServlet extends HttpServlet {

	private final DiaryService diaryService = new DiaryService();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("loginMember") == null) {
			out.print("{\"result\":\"fail\",\"msg\":\"로그인 필요\"}");
			return;
		}

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
		int memberId = loginMember.getMemberId();

		String year = req.getParameter("year");
		String month = req.getParameter("month");

		// 기본값: 현재 연/월
		Calendar cal = Calendar.getInstance();
		if (year == null || year.isEmpty())
			year = String.valueOf(cal.get(Calendar.YEAR));
		if (month == null || month.isEmpty())
			month = String.valueOf(cal.get(Calendar.MONTH) + 1);

		try {
			List<DiaryDTO> list = diaryService.getDiaryByMonth(memberId, year, month);
			List<Map<String, Object>> calendarItems = new ArrayList<>();

			for (DiaryDTO diary : list) {
				Map<String, Object> item = new LinkedHashMap<>();
				item.put("diaryId", diary.getDiaryId());
				item.put("movieId", diary.getMovieId());
				item.put("reviewId", diary.getReviewId());
				item.put("watchDate", diary.getWatchDate() == null ? "" : dateFormat.format(diary.getWatchDate()));
				item.put("popcornRating", diary.getPopcornRating());
				item.put("movieTitle", diary.getMovieTitle());
				item.put("posterUrl", diary.getPosterUrl());
				calendarItems.add(item);
			}

			// JSON 변환 (Gson 사용)
			// Gson이 없으면 수동으로 JSON 문자열 조립 가능
			Gson gson = new Gson();
			out.print(gson.toJson(calendarItems));

		} catch (Exception e) {
			e.printStackTrace();
			out.print("{\"result\":\"fail\",\"msg\":\"서버 오류\"}");
		}
	}

}

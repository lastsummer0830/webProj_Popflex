package diary.controller;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import diary.dto.DiaryStatDTO;
import diary.service.DiaryService;
import member.dto.MemberDTO;

/*
  DiaryStatServlet
  GET /diary/stat.do?year={year} → diaryStat.jsp
  - 연간 통계 (총 편수, 평균 팝콘, 극장, 태그 빈도)
  - 뱃지 시스템 (동적 집계, DB 저장 없이 Java 조건 판단)
 */

@WebServlet("/diary/stat.do")
public class DiaryStatServlet extends HttpServlet {

	private final DiaryService diaryService = new DiaryService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("loginMember") == null) {
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
		int memberId = loginMember.getMemberId();

		// 기본 연도: 현재 연도
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String yearParam = req.getParameter("year");
		if (yearParam != null && !yearParam.isEmpty()) {
			try {
				year = Integer.parseInt(yearParam);
			} catch (NumberFormatException ignored) {
			}
		}

		try {
			DiaryStatDTO stat = diaryService.getStat(memberId, year);

			req.setAttribute("stat", stat);

			req.getRequestDispatcher("/WEB-INF/views/diary/diaryStat.jsp").forward(req, resp);

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("errorMsg", "통계 조회 중 오류가 발생했습니다.");
			req.getRequestDispatcher("/WEB-INF/views/common/error.jsp").forward(req, resp);
		}
	}

}

package diary.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import diary.dto.DiaryDTO;
import diary.service.DiaryService;
import member.dto.MemberDTO;

/*
  DiaryListServlet
  GET /diary/list.do → filmDiary.jsp
  - 로그인 필요 (loginMember 세션)
  - 파라미터: year(연도 필터), sort(정렬: latest/oldest/star)
 */

@WebServlet("/diary/list.do")
public class DiaryListServlet extends HttpServlet {

	private final DiaryService diaryService = new DiaryService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);

		// ── 로그인 체크 ─────────────────────────────────────────
		if (session == null || session.getAttribute("loginMember") == null) {
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
		int memberId = loginMember.getMemberId(); // 팀 MemberDTO의 PK getter 확인

		// ── 파라미터 ─────────────────────────────────────────────
		String year = req.getParameter("year"); // 연도 필터 (없으면 전체)
		String sort = req.getParameter("sort"); // latest / oldest / star

		try {
			// 다이어리 목록 조회 (태그 포함)
			List<DiaryDTO> diaryList = diaryService.getDiaryList(memberId, year, sort);
			List<DiaryDTO> writeDiaryList = diaryService.getWritableDiaryList(memberId);

			// 연도 목록 (사이드바 폴더)
			List<String> yearList = diaryService.getYearList(memberId);

			// 전체 태그 목록 (태그 선택 UI)
			List<?> allTags = diaryService.getAllTags();

			// ── request에 담아 JSP로 전달 ──────────────────────
			req.setAttribute("diaryList", diaryList);
			req.setAttribute("writeDiaryList", writeDiaryList);
			req.setAttribute("yearList", yearList);
			req.setAttribute("allTags", allTags);
			req.setAttribute("selectedYear", year);
			req.setAttribute("selectedSort", sort == null ? "latest" : sort);

			req.getRequestDispatcher("/WEB-INF/views/diary/filmDiary.jsp").forward(req, resp);

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("errorMsg", "다이어리 목록을 불러오는 중 오류가 발생했습니다.");
			req.getRequestDispatcher("/WEB-INF/views/common/error.jsp").forward(req, resp);
		}
	}

}

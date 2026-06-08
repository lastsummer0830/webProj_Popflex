package diary.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import diary.service.DiaryService;
import member.dto.MemberDTO;

@WebServlet("/diary/delete.do")
public class DiaryDeleteServlet extends HttpServlet {

	private final DiaryService diaryService = new DiaryService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("loginMember") == null) {
			resp.sendRedirect(req.getContextPath() + "/login.do");
			return;
		}

		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
		int memberId = loginMember.getMemberId();

		int diaryId;
		try {
			diaryId = Integer.parseInt(req.getParameter("diaryId"));
		} catch (NumberFormatException e) {
			resp.sendRedirect(req.getContextPath() + "/diary/list.do#archive");
			return;
		}

		boolean deleted = false;
		try {
			deleted = diaryService.deleteDiaryReviewState(diaryId, memberId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(req.getContextPath() + "/diary/list.do" + (deleted ? "?deleted=1#archive" : "#archive"));
	}
}
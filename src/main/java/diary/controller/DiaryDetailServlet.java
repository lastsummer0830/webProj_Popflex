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
  DiaryDetailServlet
  GET /diary/detail.do?diaryId={id} → diaryDetail.jsp
  - 본인 다이어리만 조회 가능
 */

@WebServlet("/diary/detail.do")
public class DiaryDetailServlet extends HttpServlet {

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

        int diaryId = 0;
        try {
            diaryId = Integer.parseInt(req.getParameter("diaryId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/diary/list.do");
            return;
        }

        try {
            DiaryDTO diary = diaryService.getDiaryDetail(diaryId);

            // 본인 여부 확인
            if (diary == null || diary.getMemberId() != memberId) {
                resp.sendRedirect(req.getContextPath() + "/diary/list.do");
                return;
            }

            // 태그 선택 UI를 위한 전체 태그 목록
            List<?> allTags = diaryService.getAllTags();

            req.setAttribute("diary",   diary);
            req.setAttribute("allTags", allTags);

            req.getRequestDispatcher("/WEB-INF/views/diary/diaryDetail.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMsg", "다이어리 상세 조회 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
               .forward(req, resp);
        }
		
	}

}

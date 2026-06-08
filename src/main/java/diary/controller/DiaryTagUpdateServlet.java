package diary.controller;

import java.io.IOException;

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
  DiaryTagUpdateServlet
  POST /diary/tagUpdate.do → redirect /diary/list.do
  - 감정 태그 다중 선택 업데이트 + 팝콘 평점 업데이트
  - 파라미터: diaryId, tagIds[] (다중값), popcornRating
  - 본인 다이어리만 수정 가능
 */

@WebServlet("/diary/tagUpdate.do")
public class DiaryTagUpdateServlet extends HttpServlet {

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

        // ── 파라미터 파싱 ────────────────────────────────────────
        int diaryId = 0;
        try {
            diaryId = Integer.parseInt(req.getParameter("diaryId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/diary/list.do");
            return;
        }

        // 태그 ID 배열 (다중 선택 체크박스)
        String[] tagIdStrs = req.getParameterValues("tagIds");
        int[] tagIds = new int[0];
        if (tagIdStrs != null) {
            tagIds = new int[tagIdStrs.length];
            for (int i = 0; i < tagIdStrs.length; i++) {
                try { tagIds[i] = Integer.parseInt(tagIdStrs[i]); }
                catch (NumberFormatException ignored) {}
            }
        }

        double popcornRating = 0;
        String popcornStr = req.getParameter("popcornRating");
        if (popcornStr != null && !popcornStr.isEmpty()) {
            try { popcornRating = Double.parseDouble(popcornStr); }
            catch (NumberFormatException ignored) {}
        }

        String freshYn = req.getParameter("freshYn");
        String diaryText = req.getParameter("diaryText");

		boolean saved = false;

		try {
            // ── 본인 다이어리 여부 확인 후 업데이트 ─────────────
            DiaryDTO detail = diaryService.getDiaryDetail(diaryId);
            if (detail == null || detail.getMemberId() != memberId) {
                resp.sendRedirect(req.getContextPath() + "/diary/list.do");
                return;
            }

			saved = diaryService.updateTagsPopcornAndReview(detail, tagIds, popcornRating, freshYn, diaryText);

        } catch (Exception e) {
            e.printStackTrace();
        }

		String returnTo = req.getParameter("returnTo");
		if ("archive".equals(returnTo)) {
			resp.sendRedirect(req.getContextPath() + "/diary/list.do" + (saved ? "?updated=1#archive" : "#archive"));
		} else {
			resp.sendRedirect(req.getContextPath() + "/diary/list.do" + (saved ? "?saved=1#write" : ""));
		}
	}
}

package diary.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import diary.dto.BadgeDTO;
import diary.service.DiaryService;
import member.dto.MemberDTO;

/*
  DiaryBadgeServlet
  GET /diary/badge.do -> diaryBadge.jsp
  - 뱃지 12개 달성 현황 조회 및 표시
 */

@WebServlet("/diary/badge.do")
public class DiaryBadgeServlet extends HttpServlet {

    private final DiaryService diaryService = new DiaryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginMember") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        int memberId = loginMember.getMemberId();

        try {
            List<BadgeDTO> badgeList = diaryService.getBadgeList(memberId);
            long earnedCount = badgeList.stream().filter(BadgeDTO::isEarned).count();

            req.setAttribute("badgeList",   badgeList);
            req.setAttribute("earnedCount", earnedCount);

            req.getRequestDispatcher("/WEB-INF/views/diary/diaryBadge.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/diary/list.do");
        }
    }
}

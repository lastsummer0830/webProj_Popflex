package friend.controller;


import friend.dto.FriendDTO;
import friend.service.FriendService;
import member.dto.MemberDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/friend/list.do")
public class FriendListServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // 비로그인 → 로그인 페이지
        if (session == null || session.getAttribute("loginMember") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        try {
            List<FriendDTO> friendList =
                friendService.getFriendList(loginMember.getMemberId());

            req.setAttribute("friendList", friendList);
            req.getRequestDispatcher("/WEB-INF/views/friend/friendList.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

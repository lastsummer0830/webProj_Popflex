package friend.controller;


import friend.service.FriendService;
import member.dto.MemberDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/friend/insert.do")
public class FriendInsertServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);

        // 비로그인 체크
        if (session == null || session.getAttribute("loginMember") == null) {
            writeJsonOrRedirect(req, resp, "NOT_LOGIN", "/login.do");
            return;
        }

        String targetUserId = req.getParameter("targetUserId");


        // 파라미터 체크
        if (targetUserId == null || targetUserId.trim().isEmpty()) {
            writeJsonOrRedirect(req, resp, "EMPTY", "/friend/list.do");
            return;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        try {
            int code = friendService.addFriend(loginMember.getMemberId(), targetUserId);

            String result;
            switch (code) {
                case 1:
                    result = "OK";
                    break; // 성공
                case -1:
                    result = "SELF";
                    break; // 자기 자신
                case -2:
                    result = "DUPLICATE";
                    break; // 이미 친구
                case -3:
                    result = "NOT_FOUND";
                    break; // 존재하지 않는 회원
                default:
                    result = "ERROR";
                    break;
            }

            writeJsonOrRedirect(req, resp, result, "/friend/list.do");

        } catch (Exception e) {
            e.printStackTrace();
            writeJsonOrRedirect(req, resp, "ERROR", "/friend/list.do");
        }
    }
    
    private void writeJsonOrRedirect(HttpServletRequest req, HttpServletResponse resp,
            String result, String redirectPath) throws IOException {
		if (isAjax(req)) {
			resp.getWriter().write("{\"result\":\"" + result + "\"}");
		} else {
			resp.sendRedirect(req.getContextPath() + redirectPath);
		}
	}
	
	private boolean isAjax(HttpServletRequest req) {
		return "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
	}
}

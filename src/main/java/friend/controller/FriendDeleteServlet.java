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

@WebServlet("/friend/delete.do")
public class FriendDeleteServlet extends HttpServlet {

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

        String targetIdParam = req.getParameter("targetMemberId");


        // 파라미터 체크
        if (targetIdParam == null || targetIdParam.trim().isEmpty()) {
            writeJsonOrRedirect(req, resp, "EMPTY", "/friend/list.do");
            return;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        try {
            int targetMemberId = Integer.parseInt(targetIdParam.trim());

            // 자기 자신 삭제 방지
            if (targetMemberId == loginMember.getMemberId()) {
                writeJsonOrRedirect(req, resp, "SELF", "/friend/list.do");
                return;
            }

            boolean deleted =
                friendService.deleteFriend(loginMember.getMemberId(), targetMemberId);

            writeJsonOrRedirect(req, resp,
                deleted ? "OK" : "NOT_FRIEND",
                "/friend/list.do");

        } catch (NumberFormatException e) {
            writeJsonOrRedirect(req, resp, "INVALID", "/friend/list.do");
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

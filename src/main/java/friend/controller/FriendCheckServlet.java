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

// 두 회원 간 친구 여부 확인 (리뷰 비공개 조회 연동용)
@WebServlet("/friend/check.do")
public class FriendCheckServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);

        // 비로그인 → 친구 아님으로 처리
        if (session == null || session.getAttribute("loginMember") == null) {
            resp.getWriter().write("{\"result\":\"NOT_LOGIN\",\"isFriend\":false}");
            return;
        }

        String targetIdParam = req.getParameter("targetMemberId");

        if (targetIdParam == null || targetIdParam.trim().isEmpty()) {
            resp.getWriter().write("{\"result\":\"EMPTY\",\"isFriend\":false}");
            return;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        try {
            int targetMemberId = Integer.parseInt(targetIdParam.trim());
            int loginMemberId = loginMember.getMemberId();

            boolean isFriend;

            // 본인이면 true 처리
            // 이유: 본인은 친구가 아니어도 자기 친구공개/비공개성 리뷰 조회 가능해야 함
            if (loginMemberId == targetMemberId) {
                isFriend = true;
            } else {
                isFriend = friendService.isFriend(loginMemberId, targetMemberId);
            }

            resp.getWriter().write(
                "{\"result\":\"OK\",\"isFriend\":" + isFriend + "}");

        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"result\":\"INVALID\",\"isFriend\":false}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"result\":\"ERROR\",\"isFriend\":false}");
        }
    }
}


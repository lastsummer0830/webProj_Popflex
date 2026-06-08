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

// 친구 추가 전 회원 검색 AJAX용
@WebServlet("/friend/searchMember.do")
public class FriendMemberSearchServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            resp.getWriter().write("{\"result\":\"NOT_LOGIN\"}");
            return;
        }

        String keyword = req.getParameter("userId");

        if (keyword == null || keyword.trim().isEmpty()) {
            resp.getWriter().write("{\"result\":\"EMPTY\"}");
            return;
        }

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        try {
            MemberDTO found = friendService.findByUserId(keyword.trim());

            // 본인인지 없는 회원인지 구분
            if (found == null) {
                resp.getWriter().write("{\"result\":\"NOT_FOUND\"}");
                return;
            }

            if (found.getMemberId() == loginMember.getMemberId()) {
                resp.getWriter().write("{\"result\":\"SELF\"}");
                return;
            }

            // 이미 친구인지 확인
            boolean alreadyFriend =
                friendService.isFriend(loginMember.getMemberId(), found.getMemberId());

            resp.getWriter().write(
                "{\"result\":\"OK\"," +
                "\"memberId\":" + found.getMemberId() + "," +
                "\"userId\":\"" + escapeJson(found.getUserId()) + "\"," +
                "\"name\":\"" + escapeJson(found.getName()) + "\"," +
                "\"alreadyFriend\":" + alreadyFriend + "}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"result\":\"ERROR\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 32) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}

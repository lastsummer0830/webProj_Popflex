package member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import member.service.MemberService;

@WebServlet("/member/idCheck.do")
public class IdCheckServlet extends HttpServlet {

    private final MemberService memberService = new MemberService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        boolean available = false;
        String message;

        response.setContentType("application/json; charset=UTF-8");

        try {
            if (userId == null || userId.trim().isEmpty()) {
                message = "아이디를 입력하세요.";
            } else if (userId.trim().length() > 30) {
                message = "아이디는 30자 이하로 입력하세요.";
            } else if (memberService.isDuplicatedUserId(userId)) {
                message = "이미 사용 중인 아이디입니다.";
            } else {
                available = true;
                message = "사용 가능한 아이디입니다.";
            }
        } catch (RuntimeException e) {
            message = "아이디 중복 확인 중 오류가 발생했습니다.";
        }

        response.getWriter().write("{\"available\":" + available
                + ",\"message\":\"" + escapeJson(message) + "\"}");
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            switch (ch) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(ch);
            }
        }

        return escaped.toString();
    }
}

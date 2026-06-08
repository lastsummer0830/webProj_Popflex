package member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import member.dto.MemberDTO;
import member.service.MemberService;

@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {

    private final MemberService memberService = new MemberService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object naverLoginError = request.getSession().getAttribute("naverLoginError");
        if (naverLoginError != null) {
            request.setAttribute("errorMsg", naverLoginError);
            request.getSession().removeAttribute("naverLoginError");
        }

        request.getRequestDispatcher("/WEB-INF/views/member/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        try {
            MemberDTO member = memberService.login(userId, password);

            if (member == null) {
                request.setAttribute("errorMsg", "아이디 또는 비밀번호를 확인하세요.");
                request.getRequestDispatcher("/WEB-INF/views/member/login.jsp")
                       .forward(request, response);
                return;
            }

            request.getSession().setAttribute("loginMember", member);
            response.sendRedirect(request.getContextPath() + "/main.do");
        } catch (RuntimeException e) {
            request.setAttribute("errorMsg", "로그인 처리 중 오류가 발생했습니다.");
            request.getRequestDispatcher("/WEB-INF/views/member/login.jsp")
                   .forward(request, response);
        }
    }
}

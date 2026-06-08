package member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import member.service.MemberService;

@WebServlet("/member/update.do")
public class MemberUpdateServlet extends HttpServlet {

    private final MemberService memberService = new MemberService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        MemberDTO member = new MemberDTO();
        member.setMemberId(loginMember.getMemberId());
        member.setName(request.getParameter("name"));
        member.setEmail(request.getParameter("email"));

        try {
            if (memberService.updateMember(member)) {
                MemberDTO updatedMember = memberService.getMember(loginMember.getMemberId());
                session.setAttribute("loginMember", updatedMember);
                response.sendRedirect(request.getContextPath() + "/member/mypage.do");
                return;
            }

            request.setAttribute("errorMsg", "이름 또는 이메일을 확인하세요.");
            request.setAttribute("member", memberService.getMember(loginMember.getMemberId()));
            request.getRequestDispatcher("/WEB-INF/views/member/mypage.jsp")
                   .forward(request, response);
        } catch (RuntimeException e) {
            request.setAttribute("errorMsg", "회원정보 수정 중 오류가 발생했습니다.");
            request.setAttribute("member", memberService.getMember(loginMember.getMemberId()));
            request.getRequestDispatcher("/WEB-INF/views/member/mypage.jsp")
                   .forward(request, response);
        }
    }
}

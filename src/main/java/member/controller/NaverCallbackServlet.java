package member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import member.dto.NaverProfileDTO;
import member.service.MemberService;
import member.service.NaverOAuthService;

@WebServlet("/member/naverCallback.do")
public class NaverCallbackServlet extends HttpServlet {

    private final NaverOAuthService naverOAuthService = new NaverOAuthService();
    private final MemberService memberService = new MemberService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String error = request.getParameter("error");
        if (error != null) {
            redirectLoginWithError(request, response);
            return;
        }

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String savedState = (String) session.getAttribute("naverOAuthState");
        session.removeAttribute("naverOAuthState");

        if (isBlank(code) || isBlank(state) || !state.equals(savedState)) {
            redirectLoginWithError(request, response);
            return;
        }

        try {
            String accessToken = naverOAuthService.requestAccessToken(code, state);
            NaverProfileDTO profile = naverOAuthService.requestProfile(accessToken);

            if (isBlank(profile.getSocialId()) || isBlank(profile.getEmail()) || isBlank(profile.getName())) {
                redirectLoginWithError(request, response);
                return;
            }

            MemberDTO member = memberService.loginByNaver(profile.getSocialId());

            if (member != null) {
                session.setAttribute("loginMember", member);
                response.sendRedirect(request.getContextPath() + "/main.do");
                return;
            }

            session.setAttribute("naverProfile", profile);
            response.sendRedirect(request.getContextPath() + "/join.do");
        } catch (RuntimeException e) {
            redirectLoginWithError(request, response);
        }
    }

    private void redirectLoginWithError(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.getSession().setAttribute("naverLoginError", "네이버 로그인 처리 중 오류가 발생했습니다.");
        response.sendRedirect(request.getContextPath() + "/login.do");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

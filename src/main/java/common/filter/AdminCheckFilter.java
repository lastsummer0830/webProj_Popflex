package common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import member.service.MemberService;

@WebFilter("/admin/*")
public class AdminCheckFilter implements Filter {

    private final MemberService memberService = new MemberService();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.do");
            return;
        }

        MemberDTO refreshedMember = memberService.getMember(loginMember.getMemberId());
        if (refreshedMember == null || !refreshedMember.isActive()) {
            session.invalidate();
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.do");
            return;
        }

        session.setAttribute("loginMember", refreshedMember);
        loginMember = refreshedMember;

        if (!loginMember.isAdmin()) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/main.do");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

package member.controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import member.service.NaverOAuthService;

@WebServlet("/member/naverLogin.do")
public class NaverLoginServlet extends HttpServlet {

    private final NaverOAuthService naverOAuthService = new NaverOAuthService();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String state = createState();
        request.getSession().setAttribute("naverOAuthState", state);

        response.sendRedirect(naverOAuthService.buildAuthorizeUrl(state));
    }

    private String createState() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

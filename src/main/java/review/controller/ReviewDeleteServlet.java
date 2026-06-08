package review.controller;

import review.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import member.dto.MemberDTO;

import java.io.IOException;

@WebServlet("/review/delete.do")
public class ReviewDeleteServlet extends HttpServlet {

    private final ReviewService service = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        //1.로그인 확인
        HttpSession session = req.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        String returnUrl = req.getParameter("returnUrl");
        //2.reviewId 받기
        String reviewIdParam = req.getParameter("reviewId");

        if (reviewIdParam == null || reviewIdParam.trim().isEmpty()) {
            redirectBack(req, resp, returnUrl, "/review/myList.do");
            return;
        }

        try {
            //3.서비스 호출 (+본인확인)
            int reviewId = Integer.parseInt(reviewIdParam);
            service.deleteReview(reviewId, loginMember.getMemberId());
            redirectBack(req, resp, returnUrl, "/review/myList.do");
        } catch (Exception e) {
            e.printStackTrace();
            redirectBack(req, resp, addError(returnUrl, "delete"), "/review/myList.do");
        }
    }

    private void redirectBack(HttpServletRequest req, HttpServletResponse resp, String returnUrl, String fallback)
            throws IOException {
        if (isSafeReturnUrl(returnUrl)) {
            resp.sendRedirect(req.getContextPath() + returnUrl);
        } else {
            resp.sendRedirect(req.getContextPath() + fallback);
        }
    }

    private boolean isSafeReturnUrl(String returnUrl) {
        return returnUrl != null
                && returnUrl.startsWith("/")
                && !returnUrl.startsWith("//")
                && !returnUrl.contains("\\");
    }

    private String addError(String returnUrl, String value) {
        if (!isSafeReturnUrl(returnUrl)) {
            return returnUrl;
        }
        return returnUrl + (returnUrl.contains("?") ? "&" : "?") + "reviewError=" + value;
    }
}

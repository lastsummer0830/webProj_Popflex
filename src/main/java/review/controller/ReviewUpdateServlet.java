package review.controller;

import review.dto.ReviewDTO;
import review.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import member.dto.MemberDTO;

import java.io.IOException;

@WebServlet("/review/update.do")
public class ReviewUpdateServlet extends HttpServlet {

    private ReviewService service = new ReviewService();

    //GET : 수정 폼 이동 (기존 데이터 불러오기)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //1.로그인 확인
        HttpSession session = req.getSession(false);
        MemberDTO loginMember = session == null          
        		? null         
        		: (MemberDTO) session.getAttribute("loginMember"); 
        
        if (loginMember == null) {      
        	resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        //2.reviewId 받기
        String reviewIdParam = req.getParameter("reviewId");
        if (reviewIdParam == null || reviewIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/review/myList.do");
            return;
        }

        int reviewId = Integer.parseInt(reviewIdParam);
        ReviewDTO dto = service.getReviewById(reviewId);

        // 리뷰가 없거나 본인 리뷰가 아니면 차단
        if (dto == null || dto.getMemberId() != loginMember.getMemberId()) {
            resp.sendRedirect(req.getContextPath() + "/review/myList.do");
            return;
        }

        req.setAttribute("review", dto);
        req.getRequestDispatcher("/WEB-INF/views/review/reviewUpdate.jsp").forward(req, resp);
    	
    }

    //POST : 수정 처리
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

        try {
            //2.폼 데이터 받기
            int reviewId = Integer.parseInt(req.getParameter("reviewId"));
            String freshYn = req.getParameter("freshYn");
            String publicYn = req.getParameter("publicYn");
            String content = req.getParameter("content");

            if (!"Y".equals(freshYn) && !"N".equals(freshYn)) {
                freshYn = "Y";
            }
            if (!"Y".equals(publicYn) && !"N".equals(publicYn)) {
                publicYn = "Y";
            }

            //3.DTO 세팅
            ReviewDTO dto = new ReviewDTO();
            dto.setReviewId(reviewId);
            dto.setMemberId(loginMember.getMemberId());
            dto.setFreshYn(freshYn);
            dto.setPublicYn(publicYn);
            dto.setContent(content);

            //4.서비스 호출(+본인확인)
            int result = service.updateReview(dto, loginMember.getMemberId());

            if (result == 1) {
                // 성공 → 내 리뷰 목록으로    
                redirectBack(req, resp, returnUrl, "/review/myList.do");
            } else {
                redirectBack(req, resp, addError(returnUrl, "update"), "/review/myList.do");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectBack(req, resp, addError(returnUrl, "update"), "/review/myList.do");
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

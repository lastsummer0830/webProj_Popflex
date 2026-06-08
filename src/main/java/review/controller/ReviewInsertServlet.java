package review.controller;

import review.dto.ReviewDTO;
import review.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import member.dto.MemberDTO;

import java.io.IOException;

@WebServlet("/review/insert.do")
public class ReviewInsertServlet extends HttpServlet {

    private ReviewService service = new ReviewService();

    //GET : 리뷰 작성 폼 이동
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //1.로그인 확인
        HttpSession session = req.getSession(false);
        MemberDTO loginMember = session == null           
        		? null          
        		: (MemberDTO) session.getAttribute("loginMember"); //"loginMember" + MemberDTO
        
        if (loginMember == null) {      
        	resp.sendRedirect(req.getContextPath() + "/login.do"); ///login.do      
        	return;
        }

        //2.moviedId 받기
        // 리뷰 작성할 영화 번호 (영화 상세페이지에서 넘어옴)      
        String movieIdParam = req.getParameter("movieId");
        if (movieIdParam == null || movieIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/main.do");
            return;
        }
        
        req.setAttribute("movieId", movieIdParam);

        req.getRequestDispatcher("/WEB-INF/views/review/reviewInsert.jsp").forward(req, resp);
    }

    //POST : 리뷰 등록 처리
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
            int movieId = Integer.parseInt(req.getParameter("movieId"));
            String freshYn = req.getParameter("freshYn");  // 'Y'=터졌다, 'N'=안터졌다  
            String publicYn = req.getParameter("publicYn");  // 'Y'=전체공개, 'N'=친구공개  
            String content = req.getParameter("content");

            if (!"Y".equals(freshYn) && !"N".equals(freshYn)) {
                freshYn = "Y";
            }
            if (!"Y".equals(publicYn) && !"N".equals(publicYn)) {
                publicYn = "Y";
            }

            //3.DTO 구성
            ReviewDTO dto = new ReviewDTO();
            dto.setMovieId(movieId);
            dto.setMemberId(loginMember.getMemberId());  // MemberDTO에서 꺼내기 
            dto.setFreshYn(freshYn);
            dto.setPublicYn(publicYn);
            dto.setContent(content);

            //4.서비스 호출
            int result = service.insertReview(dto);

            if (result > 0) {
                // 성공 → 해당 영화 상세페이지로 이동  
                redirectBack(req, resp, returnUrl, "/review/myList.do");
            } else {
                // 등록 실패 → 에러 메시지 전달 후 다시 폼으로
                redirectBack(req, resp, addError(returnUrl, "insert"), "/review/myList.do");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectBack(req, resp, addError(returnUrl, "insert"), "/review/myList.do");
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

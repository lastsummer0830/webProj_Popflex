package review.controller;

import member.dto.MemberDTO;
import review.dto.ReviewDTO;
import review.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 리뷰 수정 화면 이동
 * 명세서: GET /review/updateForm.do -> reviewUpdate.jsp
 * 작성자 본인만 가능
 */
@WebServlet("/review/updateForm.do")
public class ReviewUpdateFormServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        String reviewIdParam = req.getParameter("reviewId");
        if (reviewIdParam == null || reviewIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/review/myList.do");
            return;
        }

        try {
            int reviewId = Integer.parseInt(reviewIdParam.trim());
            ReviewDTO review = reviewService.getReviewById(reviewId);

            if (review == null || review.getMemberId() != loginMember.getMemberId()) {
                resp.sendRedirect(req.getContextPath() + "/review/myList.do");
                return;
            }

            req.setAttribute("review", review);
            req.getRequestDispatcher("/WEB-INF/views/review/reviewUpdate.jsp")
                    .forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/review/myList.do");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

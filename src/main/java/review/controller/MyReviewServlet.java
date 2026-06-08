package review.controller;

import friend.service.FriendService;
import member.dto.MemberDTO;
import review.dto.ReviewDTO;
import review.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/review/myList.do")
public class MyReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();
    private final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 로그인 확인
        HttpSession session = req.getSession(false);

        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        // 로그인 안 했으면 로그인 페이지로 이동
        if (loginMember == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        // 2. 볼 대상 memberId 결정
        // 파라미터가 있으면 다른 사람 리뷰, 없으면 내 리뷰
        String memberIdParam = req.getParameter("memberId");

        int targetMemberId;
        boolean isMyPage;

        if (memberIdParam == null || memberIdParam.trim().isEmpty()) {
            targetMemberId = loginMember.getMemberId();
            isMyPage = true;
        } else {
            try {
                targetMemberId = Integer.parseInt(memberIdParam.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/review/myList.do");
                return;
            }

            isMyPage = targetMemberId == loginMember.getMemberId();
        }

        // 3. 필터값
        // publicYn : null/"" = 전체, Y = 전체공개, N = 친구공개
        String publicYn = req.getParameter("publicYn");
        boolean isFriend = false;

        try {
            // 4. 리뷰 목록 조회
            List<ReviewDTO> reviewList = reviewService.getMyReviewList(targetMemberId);

            // 5. 다른 사람 리뷰 페이지일 때 공개 범위 처리
            if (!isMyPage) {
                isFriend = friendService.isFriend(loginMember.getMemberId(), targetMemberId);

                if (isFriend) {
                    // 친구면 전체공개 + 친구공개 둘 다 볼 수 있음
                    reviewList.removeIf(r ->
                            !"Y".equals(r.getPublicYn()) &&
                            !"N".equals(r.getPublicYn())
                    );
                } else {
                    // 친구가 아니면 전체공개만 볼 수 있음
                    reviewList.removeIf(r -> !"Y".equals(r.getPublicYn()));
                }
            }

            // 6. 셀렉트 필터 처리
            if (publicYn != null && !publicYn.trim().isEmpty()) {
                reviewList.removeIf(r -> !publicYn.equals(r.getPublicYn()));
            }

            req.setAttribute("reviewList", reviewList);
            req.setAttribute("publicYn", publicYn);
            req.setAttribute("isMyPage", isMyPage);
            req.setAttribute("isFriend", isFriend);
            req.setAttribute("targetMemberId", targetMemberId);

            req.getRequestDispatcher("/WEB-INF/views/review/myReview.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
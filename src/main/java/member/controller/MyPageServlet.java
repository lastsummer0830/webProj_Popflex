package member.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import member.service.MemberService;
import reservation.dto.ReservationDTO;
import reservation.service.ReservationService;
import review.dto.ReviewDTO;
import review.service.ReviewService;

@WebServlet("/member/mypage.do")
public class MyPageServlet extends HttpServlet {

    private final MemberService memberService = new MemberService();
    private final ReservationService reservationService = new ReservationService();
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        MemberDTO member = memberService.getMember(loginMember.getMemberId());

        if (member == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        moveFlash(session, request, "mypageMessage");
        moveFlash(session, request, "mypageError");
        session.setAttribute("loginMember", member);
        request.setAttribute("member", member);
        setReservationSummary(request, member.getMemberId());
        setReviewSummary(request, member.getMemberId());
        request.getRequestDispatcher("/WEB-INF/views/member/mypage.jsp")
               .forward(request, response);
    }

    private void setReservationSummary(HttpServletRequest request, int memberId) {
        try {
            ArrayList<ReservationDTO> reservationList =
                    reservationService.getReservationListByMember(memberId);
            request.setAttribute("reservationList", reservationList);
        } catch (SQLException e) {
            request.setAttribute("reservationLoadError", true);
        }
    }

    private void setReviewSummary(HttpServletRequest request, int memberId) {
        try {
            List<ReviewDTO> reviewList = reviewService.getMyReviewList(memberId);
            request.setAttribute("reviewList", reviewList);
        } catch (RuntimeException e) {
            request.setAttribute("reviewLoadError", true);
        }
    }

    private void moveFlash(HttpSession session, HttpServletRequest request, String key) {
        Object value = session.getAttribute(key);

        if (value != null) {
            request.setAttribute(key, value);
            session.removeAttribute(key);
        }
    }
}

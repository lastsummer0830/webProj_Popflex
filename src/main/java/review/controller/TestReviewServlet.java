package review.controller;  // ← 이 한 줄이 없었던 거예요!

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import review.dto.ReviewDTO;

@WebServlet("/test/review")
public class TestReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 🔧 임시 로그인 세션 세팅
        MemberDTO fakeLogin = new MemberDTO();
        fakeLogin.setMemberId(1);
        fakeLogin.setName("테스트유저");

        HttpSession session = req.getSession(true);
        session.setAttribute("loginMember", fakeLogin);

        // 🔧 임시 리뷰 데이터 세팅
        ReviewDTO fakeReview = new ReviewDTO();
        fakeReview.setReviewId(1);
        fakeReview.setMemberId(1);
        fakeReview.setMovieId(100);
        fakeReview.setMovieTitle("인터스텔라");
        fakeReview.setFreshYn("Y");
        fakeReview.setPublicYn("Y");
        fakeReview.setContent("우주 스케일이 어마어마했어요!");

        req.setAttribute("review", fakeReview);

        req.getRequestDispatcher(
            "/WEB-INF/views/review/reviewUpdate.jsp"
        ).forward(req, resp);
    }
}

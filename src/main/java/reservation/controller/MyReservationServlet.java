package reservation.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import reservation.dto.ReservationDTO;
import reservation.service.ReservationService;

@WebServlet("/reservation/myList.do")
// 내 예매 목록 컨트롤러
// 로그인 회원의 예매 내역을 조회해서 myReservation.jsp로 전달한다.
public class MyReservationServlet extends HttpServlet {

    private ReservationService reservationService = new ReservationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        // 본인 예매 목록만 보여줘야 하므로 로그인 회원을 확인한다.
        HttpSession session = req.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        try {
            // 영화명, 상영시간, 좌석 목록까지 포함된 예매 목록을 조회한다.
            ArrayList<ReservationDTO> reservationList =
                    reservationService.getReservationListByMember(loginMember.getMemberId());
            req.setAttribute("reservationList", reservationList);
            req.getRequestDispatcher("/WEB-INF/views/reservation/myReservation.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}

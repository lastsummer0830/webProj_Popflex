package reservation.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;
import reservation.dto.ReservationDTO;
import reservation.service.ReservationService;

@WebServlet("/reservation/detail.do")
// 예매 상세 컨트롤러
// reservationId를 받아 본인 예매인지 확인한 뒤 상세 화면으로 이동한다.
public class ReservationDetailServlet extends HttpServlet {

    private ReservationService reservationService = new ReservationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        // 상세 조회도 본인 예매만 허용한다.
        HttpSession session = req.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }

        String reservationIdParameter = req.getParameter("reservationId");
        if (reservationIdParameter == null || reservationIdParameter.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdParameter);
            // reservationId와 memberId를 함께 조건으로 걸어 다른 회원 예매 접근을 막는다.
            ReservationDTO reservation =
                    reservationService.getReservationDetail(reservationId, loginMember.getMemberId());

            if (reservation == null) {
                resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
                return;
            }

            req.setAttribute("reservation", reservation);
            req.getRequestDispatcher("/WEB-INF/views/reservation/reservationDetail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}

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

@WebServlet("/reservation/complete.do")
// 예매 완료 화면 컨트롤러
// 방금 생성된 reservationId를 받아 완료 화면에 표시할 예매 정보를 조회한다.
public class ReservationCompleteServlet extends HttpServlet {

    private ReservationService reservationService = new ReservationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        // 완료 화면도 본인 예매만 볼 수 있게 로그인 회원을 확인한다.
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
            // 완료 화면에 영화명, 상영시간, 좌석 정보까지 보여주기 위해 상세 조회를 사용한다.
            ReservationDTO reservation =
                    reservationService.getReservationDetail(reservationId, loginMember.getMemberId());

            if (reservation == null) {
                resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
                return;
            }

            req.setAttribute("reservation", reservation);
            req.getRequestDispatcher("/WEB-INF/views/reservation/reservationComplete.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}

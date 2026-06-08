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
import reservation.dto.SeatDTO;
import reservation.service.ReservationService;
import reservation.service.SeatService;

@WebServlet("/reservation/updateForm.do")
// 예매 변경 화면 컨트롤러
// 기존 예매 정보와 현재 선택 좌석을 조회해서 변경 JSP로 전달한다.
public class ReservationUpdateFormServlet extends HttpServlet {

    private ReservationService reservationService = new ReservationService();
    private SeatService seatService = new SeatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        // 변경 화면도 본인 예매만 접근할 수 있게 로그인 회원을 확인한다.
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
            int memberId = loginMember.getMemberId();
            // 변경 가능한 예매인지 확인하기 위해 상세 정보를 먼저 조회한다.
            ReservationDTO reservation = reservationService.getReservationDetail(reservationId, memberId);

            if (reservation == null || reservation.getStatus() != 'Y') {
                resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
                return;
            }

            // JSP에서 기존 좌석을 체크 상태로 표시하기 위해 seat_id 목록을 CSV 형태로 만든다.
            ArrayList<Integer> currentSeatIds = reservationService.getReservationSeatIds(reservationId, memberId);
            ArrayList<SeatDTO> seatList = seatService.getSeatListByScheduleId(reservation.getSchedule_id());
            StringBuilder currentSeatIdCsv = new StringBuilder(",");
            for (Integer seatId : currentSeatIds) {
                currentSeatIdCsv.append(seatId).append(",");
            }

            req.setAttribute("reservation", reservation);
            req.setAttribute("currentSeatIdCsv", currentSeatIdCsv.toString());
            req.setAttribute("seatList", seatList);
            req.getRequestDispatcher("/WEB-INF/views/reservation/reservationUpdate.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}

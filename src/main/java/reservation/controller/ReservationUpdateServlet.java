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
import reservation.service.ReservationService;

@WebServlet("/reservation/update.do")
// 예매 변경 처리 컨트롤러
// 변경 화면에서 선택한 seatId[]를 받아 기존 예매 좌석을 새 좌석으로 교체한다.
public class ReservationUpdateServlet extends HttpServlet {

    private ReservationService reservationService = new ReservationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        // 변경 처리는 로그인 회원의 본인 예매만 허용한다.
        HttpSession session = req.getSession(false);
        MemberDTO loginMember = session == null
                ? null
                : (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            resp.sendRedirect(req.getContextPath() + "/login.do");
            return;
        }
        int memberId = loginMember.getMemberId();

        String reservationIdParameter = req.getParameter("reservationId");
        if (reservationIdParameter == null || reservationIdParameter.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/reservation/myList.do");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdParameter);
            // 변경 화면의 체크박스 값을 정수 좌석 ID 목록으로 변환한다.
            String[] seatIdArray = req.getParameterValues("seatId");
            ArrayList<Integer> seatIds = new ArrayList<>();

            if (seatIdArray != null) {
                for (String seatId : seatIdArray) {
                    seatIds.add(Integer.parseInt(seatId));
                }
            }

            // Service에서 기존 좌석 삭제, 새 좌석 등록, headcount 갱신을 처리한다.
            int result = reservationService.updateReservation(
                    reservationId, memberId, seatIds);

            if (result > 0) {
                resp.sendRedirect(req.getContextPath() + "/reservation/myList.do?update=success");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/reservation/myList.do?update=fail");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/reservation/myList.do?update=fail");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}

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
import reservation.service.ReservationService;

@WebServlet("/reservation/cancel.do")
public class ReservationCancelServlet extends HttpServlet {

	private ReservationService reservationService = new ReservationService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html; charset=utf-8");

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

		int reservationId = Integer.parseInt(reservationIdParameter);
		int memberId = loginMember.getMemberId();

		try {
			int result = reservationService.cancelReservation(reservationId, memberId);

			if (result > 0) {
				resp.sendRedirect(req.getContextPath() + "/reservation/myList.do?cancel=success");
			} else {
				resp.sendRedirect(req.getContextPath() + "/reservation/myList.do?cancel=fail");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			resp.sendError(500);
		}
	}
}

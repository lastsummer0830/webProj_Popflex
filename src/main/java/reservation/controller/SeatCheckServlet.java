package reservation.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import reservation.service.SeatService;

@WebServlet("/seat/check.do")
// 좌석 중복 확인 JSON 컨트롤러
// 좌석 체크 시 해당 좌석이 아직 선택 가능한지 AJAX로 확인한다.
public class SeatCheckServlet extends HttpServlet {

    private SeatService seatService = new SeatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        JSONObject json = new JSONObject();

        try {
            // scheduleId와 seatId를 받아 현재 예매된 좌석 목록에 포함되는지 검사한다.
            int scheduleId = Integer.parseInt(req.getParameter("scheduleId"));
            int seatId = Integer.parseInt(req.getParameter("seatId"));

            ArrayList<Integer> reservedSeatIds = seatService.getReservedSeatIds(scheduleId);
            boolean reserved = reservedSeatIds.contains(seatId);

            json.put("available", !reserved);
            json.put("reserved", reserved);
            json.put("scheduleId", scheduleId);
            json.put("seatId", seatId);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("available", false);
            json.put("message", "scheduleId and seatId are required.");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("available", false);
            json.put("message", "Failed to check seat.");
        }

        resp.getWriter().write(json.toString());
    }
}

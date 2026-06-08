package reservation.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import reservation.dto.SeatDTO;
import reservation.service.SeatService;

@WebServlet("/seat/list.do")
// 좌석 목록 JSON 컨트롤러
// scheduleId 기준으로 전체 좌석과 이미 예매된 좌석 상태를 함께 반환한다.
public class SeatListServlet extends HttpServlet {

    private SeatService seatService = new SeatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        JSONObject json = new JSONObject();

        try {
            // AJAX 요청에서 전달된 상영 일정 번호를 확인한다.
            int scheduleId = Integer.parseInt(req.getParameter("scheduleId"));
            ArrayList<SeatDTO> seatList = seatService.getSeatListByScheduleId(scheduleId);

            if (seatList == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                json.put("success", false);
                json.put("message", "상영 일정이 존재하지 않습니다.");
                resp.getWriter().write(json.toString());
                return;
            }

            // SEAT 테이블의 전체 좌석과 해당 상영 일정의 예약 좌석을 조회한다.
            ArrayList<Integer> reservedSeatIds = seatService.getReservedSeatIds(scheduleId);
            JSONArray seats = new JSONArray();

            // JSP에서 바로 렌더링할 수 있도록 각 좌석별 예약 여부를 JSON으로 만든다.
            for (SeatDTO seat : seatList) {
                boolean reserved = reservedSeatIds.contains(seat.getSeat_id());
                JSONObject seatJson = new JSONObject();
                seatJson.put("seatId", seat.getSeat_id());
                seatJson.put("rowLabel", seat.getRow_label());
                seatJson.put("colNum", seat.getCol_num());
                seatJson.put("seatName", seat.getRow_label() + seat.getCol_num());
                seatJson.put("reserved", reserved);
                seatJson.put("available", !reserved);
                seats.put(seatJson);
            }

            json.put("success", true);
            json.put("scheduleId", scheduleId);
            json.put("seats", seats);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("success", false);
            json.put("message", "scheduleId가 필요합니다.");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("success", false);
            json.put("message", "좌석 목록 조회 중 오류가 발생했습니다.");
        }

        resp.getWriter().write(json.toString());
    }
}

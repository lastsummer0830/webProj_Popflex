package admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import admin.dto.AdminScreenDTO;
import admin.service.AdminService;

@WebServlet("/admin/screen/list.do")
public class AdminScreenListServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        try {
            List<AdminScreenDTO> screens = adminService.getScreenOptions();
            response.getWriter().write(toJson(screens));
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"상영관 목록 조회 중 오류가 발생했습니다.\"}");
        }
    }

    private String toJson(List<AdminScreenDTO> screens) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < screens.size(); i++) {
            AdminScreenDTO screen = screens.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                .append("\"screenId\":").append(screen.getScreenId()).append(",")
                .append("\"theaterId\":").append(screen.getTheaterId()).append(",")
                .append("\"theaterName\":\"").append(escapeJson(screen.getTheaterName())).append("\",")
                .append("\"location\":\"").append(escapeJson(screen.getLocation())).append("\",")
                .append("\"screenName\":\"").append(escapeJson(screen.getScreenName())).append("\",")
                .append("\"seatCount\":").append(screen.getSeatCount())
                .append("}");
        }

        json.append("]");
        return json.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            switch (ch) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(ch);
            }
        }

        return escaped.toString();
    }
}

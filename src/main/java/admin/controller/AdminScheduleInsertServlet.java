package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import admin.dto.AdminScheduleDTO;

@WebServlet("/admin/scheduleInsert.do")
public class AdminScheduleInsertServlet extends AdminServletSupport {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        AdminScheduleDTO schedule = buildSchedule(request);

        try {
            if (adminService.insertSchedule(schedule)) {
                setFlash(request, "adminMessage", "상영 정보가 등록되었습니다.");
                response.sendRedirect(request.getContextPath() + "/admin/scheduleList.do");
                return;
            }

            request.setAttribute("adminError", "영화 또는 상영관 정보를 확인하세요.");
        } catch (RuntimeException e) {
            request.setAttribute("adminError", "상영 정보 등록 중 오류가 발생했습니다. 입력값과 중복 일정을 확인하세요.");
        }

        forwardScheduleForm(request, response, schedule, "insert");
    }

    private AdminScheduleDTO buildSchedule(HttpServletRequest request) {
        return adminService.buildSchedule(null,
                request.getParameter("movieId"),
                request.getParameter("screenId"),
                request.getParameter("startTime"),
                request.getParameter("endTime"),
                request.getParameter("price"));
    }
}

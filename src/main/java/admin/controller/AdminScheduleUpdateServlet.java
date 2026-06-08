package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import admin.dto.AdminScheduleDTO;

@WebServlet("/admin/scheduleUpdate.do")
public class AdminScheduleUpdateServlet extends AdminServletSupport {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        AdminScheduleDTO schedule = buildSchedule(request);

        try {
            if (adminService.updateSchedule(schedule)) {
                setFlash(request, "adminMessage", "상영 정보가 수정되었습니다.");
                response.sendRedirect(request.getContextPath() + "/admin/scheduleList.do");
                return;
            }

            request.setAttribute("adminError", "수정할 상영 정보 또는 입력값을 확인하세요.");
        } catch (RuntimeException e) {
            request.setAttribute("adminError", "상영 정보 수정 중 오류가 발생했습니다. 입력값과 중복 일정을 확인하세요.");
        }

        forwardScheduleForm(request, response, schedule, "update");
    }

    private AdminScheduleDTO buildSchedule(HttpServletRequest request) {
        return adminService.buildSchedule(request.getParameter("scheduleId"),
                request.getParameter("movieId"),
                request.getParameter("screenId"),
                request.getParameter("startTime"),
                request.getParameter("endTime"),
                request.getParameter("price"));
    }
}

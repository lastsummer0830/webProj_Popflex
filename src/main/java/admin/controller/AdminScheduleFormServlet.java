package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import admin.dto.AdminScheduleDTO;

@WebServlet("/admin/scheduleForm.do")
public class AdminScheduleFormServlet extends AdminServletSupport {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        moveFlash(request);

        int scheduleId = parseInt(request.getParameter("scheduleId"), 0);
        AdminScheduleDTO schedule = null;
        String mode = "insert";

        if (scheduleId > 0) {
            schedule = adminService.getSchedule(scheduleId);
            mode = "update";

            if (schedule == null) {
                setFlash(request, "adminError", "상영 정보를 찾을 수 없습니다.");
                response.sendRedirect(request.getContextPath() + "/admin/scheduleList.do");
                return;
            }
        }

        forwardScheduleForm(request, response, schedule, mode);
    }
}

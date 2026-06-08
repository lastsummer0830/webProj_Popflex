package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/scheduleDelete.do")
public class AdminScheduleDeleteServlet extends AdminServletSupport {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int scheduleId = parseInt(request.getParameter("scheduleId"), 0);

        try {
            if (adminService.deleteSchedule(scheduleId)) {
                setFlash(request, "adminMessage", "상영 정보가 삭제되었습니다.");
            } else {
                setFlash(request, "adminError", "예약 내역이 있거나 존재하지 않는 상영은 삭제할 수 없습니다.");
            }
        } catch (RuntimeException e) {
            setFlash(request, "adminError", "상영 정보 삭제 중 오류가 발생했습니다.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/scheduleList.do");
    }
}

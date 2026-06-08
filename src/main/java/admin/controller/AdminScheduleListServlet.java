package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/scheduleList.do")
public class AdminScheduleListServlet extends AdminServletSupport {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        moveFlash(request);
        request.setAttribute("schedules", adminService.getScheduleList());
        request.getRequestDispatcher("/WEB-INF/views/admin/scheduleManage.jsp")
               .forward(request, response);
    }
}

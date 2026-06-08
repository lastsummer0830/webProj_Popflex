package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/main.do")
public class AdminMainServlet extends AdminServletSupport {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        moveFlash(request);
        request.setAttribute("dashboard", adminService.getDashboard());
        request.getRequestDispatcher("/WEB-INF/views/admin/adminMain.jsp")
               .forward(request, response);
    }
}

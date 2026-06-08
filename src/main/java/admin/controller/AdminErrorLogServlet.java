package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import admin.logging.ErrorLogStore;

@WebServlet("/admin/errorLogs.do")
public class AdminErrorLogServlet extends AdminServletSupport {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        moveFlash(request);
        request.setAttribute("errorLogs", ErrorLogStore.getLogs());
        request.getRequestDispatcher("/WEB-INF/views/admin/errorLogs.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ErrorLogStore.clear();
        setFlash(request, "adminMessage", "오류 로그를 비웠습니다.");
        response.sendRedirect(request.getContextPath() + "/admin/errorLogs.do");
    }
}

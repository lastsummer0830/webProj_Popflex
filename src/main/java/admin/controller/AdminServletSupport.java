package admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import admin.dto.AdminMovieDTO;
import admin.dto.AdminScheduleDTO;
import admin.dto.AdminScreenDTO;
import admin.service.AdminService;

public abstract class AdminServletSupport extends HttpServlet {

    protected final AdminService adminService = new AdminService();

    protected void moveFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        moveFlashValue(session, request, "adminMessage");
        moveFlashValue(session, request, "adminError");
    }

    protected void setFlash(HttpServletRequest request, String key, String value) {
        request.getSession().setAttribute(key, value);
    }

    protected void forwardScheduleForm(HttpServletRequest request, HttpServletResponse response,
            AdminScheduleDTO schedule, String mode) throws ServletException, IOException {
        List<AdminMovieDTO> movies = adminService.getMovieOptions();
        List<AdminScreenDTO> screens = adminService.getScreenOptions();
        request.setAttribute("movies", movies);
        request.setAttribute("screens", screens);
        request.setAttribute("schedule", schedule);
        request.setAttribute("mode", mode);
        request.getRequestDispatcher("/WEB-INF/views/admin/scheduleForm.jsp")
               .forward(request, response);
    }

    protected int parseInt(String value, int defaultValue) {
        return adminService.parseInt(value, defaultValue);
    }

    private void moveFlashValue(HttpSession session, HttpServletRequest request, String key) {
        Object value = session.getAttribute(key);
        if (value != null) {
            request.setAttribute(key, value);
            session.removeAttribute(key);
        }
    }
}

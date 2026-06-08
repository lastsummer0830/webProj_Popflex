package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/seatManage.do")
public class AdminSeatManageServlet extends AdminServletSupport {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int screenId = parseInt(request.getParameter("screenId"), 0);
        int rowCount = parseInt(request.getParameter("rowCount"), 5);
        int colCount = parseInt(request.getParameter("colCount"), 8);

        try {
            int created = adminService.createDefaultSeats(screenId, rowCount, colCount);

            if (created >= 0) {
                setFlash(request, "adminMessage", "좌석 생성이 완료되었습니다. 새로 추가된 좌석: " + created + "개");
            } else {
                setFlash(request, "adminError", "상영관 또는 좌석 범위를 확인하세요.");
            }
        } catch (RuntimeException e) {
            setFlash(request, "adminError", "좌석 생성 중 오류가 발생했습니다.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/scheduleForm.do");
    }
}

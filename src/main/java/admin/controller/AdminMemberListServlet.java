package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/memberList.do")
public class AdminMemberListServlet extends AdminServletSupport {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        moveFlash(request);

        String keyword = request.getParameter("keyword");
        request.setAttribute("keyword", keyword);
        request.setAttribute("members", adminService.getMemberList(keyword));
        request.getRequestDispatcher("/WEB-INF/views/admin/memberManage.jsp")
               .forward(request, response);
    }
}

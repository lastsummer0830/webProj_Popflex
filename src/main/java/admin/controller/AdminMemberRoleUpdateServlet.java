package admin.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/memberRoleUpdate.do")
public class AdminMemberRoleUpdateServlet extends AdminServletSupport {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int memberId = parseInt(request.getParameter("memberId"), 0);
        String keyword = request.getParameter("keyword");

        try {
            if (adminService.promoteMemberToAdmin(memberId)) {
                setFlash(request, "adminMessage", "회원에게 관리자 권한을 부여했습니다.");
            } else {
                setFlash(request, "adminError", "권한을 변경할 수 없는 회원입니다. 이미 관리자이거나 탈퇴 회원일 수 있습니다.");
            }
        } catch (RuntimeException e) {
            setFlash(request, "adminError", "관리자 권한 부여 중 오류가 발생했습니다.");
        }

        String redirectUrl = request.getContextPath() + "/admin/memberList.do";
        if (keyword != null && !keyword.trim().isEmpty()) {
            redirectUrl += "?keyword=" + java.net.URLEncoder.encode(keyword.trim(), "UTF-8");
        }
        response.sendRedirect(redirectUrl);
    }
}

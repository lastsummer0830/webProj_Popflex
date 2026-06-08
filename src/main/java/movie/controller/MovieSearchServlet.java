package movie.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import movie.dto.MovieSearchResultDTO;
import movie.service.MovieSearchService;

@WebServlet("/movie/search.do")
public class MovieSearchServlet extends HttpServlet {

    // 영화 검색 화면용 서비스
    private MovieSearchService movieSearchService = new MovieSearchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 요청 파라미터 한글 깨짐 방지
        req.setCharacterEncoding("UTF-8");

        // 응답 화면 한글 깨짐 방지
        resp.setContentType("text/html; charset=UTF-8");

        // 사용자가 입력한 검색어
        String query = req.getParameter("query");

        // 사용자가 요청한 페이지 번호
        int page = parsePage(req.getParameter("page"));

        // 검색 결과 화면에 필요한 데이터 묶음
        MovieSearchResultDTO result = movieSearchService.search(query, page);

        // JSP에서 사용할 검색 결과 데이터
        req.setAttribute("result", result);

        req.getRequestDispatcher("/WEB-INF/views/movie/movieList.jsp").forward(req, resp);
    }

    // page 파라미터를 int로 변환하는 메서드
    private int parsePage(String pageParam) {
        int page = 1;

        try {
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        if (page < 1) {
            page = 1;
        }

        return page;
    }
}
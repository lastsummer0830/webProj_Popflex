package screen.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import screen.dto.TheaterDTO;
import screen.service.TheaterService;

@WebServlet("/theater/list.do")
public class TheaterListServlet extends HttpServlet {

	private TheaterService service = new TheaterService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json; charset=UTF-8");

//		최종 응답 전체를 담을 json 객체 생성
		JSONObject json = new JSONObject();

		try {

//			service 메서드로 극장 목록
			ArrayList<TheaterDTO> theaterList = service.getTheaterList();

			JSONArray theaters = new JSONArray();

			for (TheaterDTO theater : theaterList) {
//				조회된 극장 1건을 담을  json 객체 생성
				JSONObject item = new JSONObject();

//				theaterDTO 1개에 들어있는 값을 json key-value 형태로 담음 
				item.put("theaterId", theater.getTheaterId());
				item.put("theaterName", theater.getTheaterName());
				item.put("location", theater.getLocation());

				theaters.put(item);
			}

//			조회 성공 상태, 요청한 극장목록 배열을 최종 json 객체에 담음
			json.put("success", true); // 요청 처리 성공 여부
			json.put("theaters", theaters); // 조회된 극장 목록 배열

		} catch (Exception e) {
//			예외 발생 시 500 상태코드와 실패 메시지 반환
//			500 띄우기 SC_INTERNAL_SERVER_ERROR : 
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			json.put("success", false);
			json.put("message", "극장 목록 조회 중 오류가 발생했습니다.");
		}

		// 최종 JSON 을 실제 응답으로 보내기 toString으로 문자열로 바꾸어
		resp.getWriter().write(json.toString());
	}
}

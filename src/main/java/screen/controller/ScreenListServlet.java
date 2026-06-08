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

import screen.dto.ScreenDTO;
import screen.service.ScreenService;

//screen/list.do 요청을 처리하는 Controller
@WebServlet("/screen/list.do")
public class ScreenListServlet extends HttpServlet {

	private ScreenService service = new ScreenService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json; charset=UTF-8");
		
		// 상영관 목록을 JSON 형태로 반환한다.
		JSONObject json = new JSONObject();
		
		try {
			// ScreenService를 통해 THEATER와 JOIN된 상영관 목록을 조회한다.
			ArrayList<ScreenDTO> screenList = service.getScreenList();
			
			// 상영관 목록 여러 건을 담을 JSONArray를 생성한다.
			JSONArray screens = new JSONArray();
			
			for(ScreenDTO screen : screenList) {
				// ScreenDTO 1개를 JSON 객체로 변환한다.
				JSONObject item = new JSONObject();
				
				// screenId, theaterId, screenName, theaterName을 JSON에 담는다.
				item.put("screenId", screen.getScreenId());
				item.put("theaterId", screen.getTheaterId());
				item.put("screenName", screen.getScreenName());
				item.put("theaterName", screen.getTheaterName());
				
				
				screens.put(item);
			}
			
			// 관리자 상영 등록 화면에서 상영관 선택 목록으로 사용할 수 있게 응답한다.
			
			json.put("success", true); // 요청 처리 성공 여부
			json.put("screens", screens); // 조회된 상영관 목록 배열
		
	} catch (Exception e ) {
		e.printStackTrace();
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		json.put("success", false);
		json.put("message", "상영관 목록 조회 중 오류가 발생했습니다.");
	}
		
	// 최종 JSON 을 실제 응답으로 보내기 toString으로 문자열로 바꾸어
	resp.getWriter().write(json.toString());
	
	
	}

	

}

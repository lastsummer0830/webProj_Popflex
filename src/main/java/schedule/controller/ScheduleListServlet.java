package schedule.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import schedule.dto.ScheduleDTO;
import schedule.service.ScheduleService;

@WebServlet("/schedule/list.do")
public class ScheduleListServlet extends HttpServlet{
	
	private ScheduleService service = new ScheduleService();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json; charset=UTF-8");
		
//		최종 응답 전체를 담을 json 객체 생성
		JSONObject json = new JSONObject();
		
		try {

//			movieId 파라미터를 문자열로 받음
			String movieIdParam = req.getParameter("movieId");
			
//			다른 기능에서 movie_id로 파라미터 보내서 if문으로 처리
			if(movieIdParam == null || movieIdParam.trim().isEmpty()) {
				movieIdParam = req.getParameter("movie_id");
			}
			if(movieIdParam == null || movieIdParam.trim().isEmpty()) {
				throw new NumberFormatException("movieId");
			}
			
//			movieId int이므로 형변환
			int movieId = Integer.parseInt(movieIdParam);
			
//			service 메서드로 상영일정 조회 후 scheduleList 에 담기
			ArrayList<ScheduleDTO> scheduleList = service.getScheduleListByMovieId(movieId);
			
//			상영 일정 여러 개를 담는 JSON 배열 schedules
			JSONArray schedules = new JSONArray();
			
			for(ScheduleDTO schedule : scheduleList) {
//				scheduleList 에서 꺼내온 상영일정 1건을 담을  json 객체 생성
				JSONObject item = new JSONObject();
				
//				scheduleDTO 1개에 들어있는 값을 json key-value 형태로 담음 
				item.put("scheduleId", schedule.getScheduleId());
				item.put("movieId", schedule.getMovieId());
				item.put("screenId", schedule.getScreenId());
				
				LocalDateTime startTime = schedule.getStartTime();
				LocalDateTime endTime = schedule.getEndTime();
				
				item.put("startTime", formatDateTime(startTime));
				item.put("endTime", formatDateTime(endTime));
				
				item.put("price", schedule.getPrice());
				
//				상영 일정 1건을 상영 일정 목록 배열에 추가
				schedules.put(item);
				
			}
			
//			조회 성공 상태, 요청한 movieId, 상영 일정 목록 배열을 최종 json 객체에 담음
			json.put("success", true);  		// 요청 처리 성공 여부
			json.put("movieId", movieId);		// 요청받은 영화 번호를 응답에도 표시
			json.put("schedules", schedules);	// 해당 영화의 상영 일정 목록 배열
			
		} catch (NumberFormatException e) {
//			movieId가 없거나 숫자가 아니면 400 BAD_REQUEST 상태 코드 반환
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			json.put("success", false);
			json.put("message", "movieId가 없거나 숫자여야 합니다.");
		}
//		최종 JSON 을 실제 응답으로 보내기 toString으로 문자열로 바꾸어 
		resp.getWriter().write(json.toString());
		
		
	}
//	LocalDateTime 값을 JSON 에 넣기 전에 "yyyy-MM-dd HH:mm" 형태의 문자열로 변환
	private String formatDateTime(LocalDateTime time) {
		if(time == null) {
			return "";
		}
		return time.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}
}

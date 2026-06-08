package schedule.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import schedule.dto.ScheduleDTO;
import schedule.service.ScheduleService;

@WebServlet("/schedule/detail.do")
public class ScheduleDetailServlet extends HttpServlet {

	private ScheduleService service = new ScheduleService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json; charset=UTF-8");
		
//		최종 응답 전체를 담을 json 객체 생성
		JSONObject json = new JSONObject();

		try {
		
//		scheduleId 파라미터를 문자열로 받음
			String scheduleParam = req.getParameter("scheduleId");

//		다른 기능에서 schedule_id로 보내는 것 처리
			if (scheduleParam == null || scheduleParam.trim().isEmpty()) {
				scheduleParam = req.getParameter("schedule_id");
			}
			
			if (scheduleParam == null || scheduleParam.trim().isEmpty()) {
				throw new NumberFormatException();
			}

//		scheduleId int로 형변환
			int scheduleId = Integer.parseInt(scheduleParam.trim());

//		service 메서드로 scheduleId에 해당하는 상영 일정 1건 조회
			ScheduleDTO oneSchedule = service.getScheduleById(scheduleId);

			if(oneSchedule == null) {
//				scheduleId에 해당하는 상영일정이 없으면 404 상태코드 반환
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				json.put("success", false);
				json.put("message", "해당 scheduleId의 상영 일정이 없습니다.");
			} else {
//				조회된 상영일정 1건을 담을  json 객체 생성
				JSONObject item = new JSONObject();

//				scheduleDTO 1개에 들어있는 값을 json key-value 형태로 담음 
				item.put("scheduleId", oneSchedule.getScheduleId());
				item.put("movieId", oneSchedule.getMovieId());
				item.put("screenId", oneSchedule.getScreenId());

				LocalDateTime startTime = oneSchedule.getStartTime();
				LocalDateTime endTime = oneSchedule.getEndTime();

				item.put("startTime", formatDateTime(startTime));
				item.put("endTime", formatDateTime(endTime));

				item.put("price", oneSchedule.getPrice());

//			조회 성공 상태, 요청한 scheduleId, 상영 일정 1건을 최종 json 객체에 담음
				json.put("success", true); // 요청 처리 성공 여부
				json.put("scheduleId", scheduleId); // 요청받은 상영 일정 번호를 응답에도 표시
				json.put("schedule", item); // 조회된 상영 일정 1건
				
			}
		} catch(NumberFormatException e) {
//			400 띄우기 sc bad request : 요청값이 숫자가 아니라 잘못되어 400
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			json.put("success", false);
			json.put("message", "scheduleId가 없거나 숫자여야 합니다.");
		}
			
		// 최종 JSON 을 실제 응답으로 보내기 toString으로 문자열로 바꾸어
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

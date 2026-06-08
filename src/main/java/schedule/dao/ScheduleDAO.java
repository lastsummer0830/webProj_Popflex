package schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import schedule.dto.ScheduleDTO;

// SCHEDULE 테이블 조회를 담당하는 DAO
// 예매 화면에서 필요한 상영 일정 단건/목록 조회 SQL을 관리한다.
public class ScheduleDAO {

	// db 에 있는 내용 ScheduleDTO 객체로 만들어 반환
	private ScheduleDTO mapRowToScheduleDTO(ResultSet rs) throws SQLException {
		ScheduleDTO dto = new ScheduleDTO();

		dto.setScheduleId(rs.getInt("schedule_id"));
		dto.setMovieId(rs.getInt("movie_id"));
		dto.setScreenId(rs.getInt("screen_id"));
		
		Timestamp startTimeStamp = rs.getTimestamp("start_time");
		if (startTimeStamp != null) {
			dto.setStartTime(startTimeStamp.toLocalDateTime());
		} else {
			dto.setStartTime(null);
		}
		
		Timestamp endTimeStamp = rs.getTimestamp("end_time");
		if (endTimeStamp != null) {
			dto.setEndTime(endTimeStamp.toLocalDateTime());
		} else {
			dto.setEndTime(null);
		}
		
			
		dto.setPrice(rs.getInt("price"));

		return dto;

	}

	// [상영정보 상세조회]
	// schedule_id로 상영 일정 1건을 조회한다.
	// 예매 등록 전에 실제 존재하는 상영 일정인지 확인할 때 사용한다.
	public ScheduleDTO getScheduleById(Connection con, int scheduleId) {
		ScheduleDTO dto = null;
		String sql = "select schedule_id, movie_id, screen_id, start_time, end_time, price from schedule where schedule_id = ?";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, scheduleId);
			rs = pst.executeQuery();

			if (rs.next()) {
				// ResultSet의 SCHEDULE 컬럼 값을 ScheduleDTO에 옮긴다.
				dto = mapRowToScheduleDTO(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pst != null)
					pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return dto;
	}

	// [상영 시간 조회]
	// movie_id로 해당 영화의 상영 일정 목록을 조회한다.
	// /reservation/form.do 화면에서 시간 선택 목록을 보여줄 때 사용한다.
	public ArrayList<ScheduleDTO> getScheduleListByMovieId(Connection con, int movieId) {
		ArrayList<ScheduleDTO> list = new ArrayList<>();
		String sql = "select schedule_id, movie_id, screen_id, start_time, end_time, price " + "from schedule "
				+ "where movie_id = ? " + "order by start_time asc";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, movieId);
			rs = pst.executeQuery();

			while (rs.next()) {
				// 조회된 각 상영 일정을 DTO로 만들어 목록에 추가한다.
				list.add(mapRowToScheduleDTO(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pst != null)
					pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return list;
	}

}

package schedule.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import common.DBUtil;
import schedule.dao.ScheduleDAO;
import schedule.dto.ScheduleDTO;

public class ScheduleService {

	ScheduleDAO dao = new ScheduleDAO();

//	상영 정보 상세 조회
	public ScheduleDTO getScheduleById(int scheduleId) {
		if (scheduleId <= 0) {
			return null;
		}
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			return dao.getScheduleById(con, scheduleId);
		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		} finally {
			DBUtil.close(con);
		}

	}

//	영화별 상영 시간 목록 조회용
	public ArrayList<ScheduleDTO> getScheduleListByMovieId(int movieId) {
		if (movieId <= 0) {
			return new ArrayList<ScheduleDTO>();
		}
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			return dao.getScheduleListByMovieId(con, movieId);
		} catch (SQLException e) {

			e.printStackTrace();
			return new ArrayList<ScheduleDTO>();
		} finally {
			DBUtil.close(con);
		}

	}

}

package reservation.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import common.DBUtil;
import reservation.dao.SeatDAO;
import reservation.dto.SeatDTO;

// 좌석 조회와 좌석 중복 확인 로직을 담당하는 서비스
// 컨트롤러와 ReservationService가 SeatDAO를 직접 호출하지 않게 분리한다.
public class SeatService {

	private SeatDAO seatDAO = new SeatDAO();

	// 전체 좌석 목록을 조회한다.
	public ArrayList<SeatDTO> getSeatList() throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return seatDAO.getSeatList(con);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	public ArrayList<SeatDTO> getSeatList(int screenId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return seatDAO.getSeatList(con, screenId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	public ArrayList<SeatDTO> getSeatListByScheduleId(int scheduleId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return seatDAO.getSeatListByScheduleId(con, scheduleId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	public ArrayList<SeatDTO> getSeatListByScheduleId(Connection con, int scheduleId) {
		return seatDAO.getSeatListByScheduleId(con, scheduleId);
	}

	// 특정 상영 일정에서 이미 예매된 좌석 ID 목록을 조회한다.
	public ArrayList<Integer> getReservedSeatIds(int scheduleId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return seatDAO.getReservedSeatIds(con, scheduleId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	// 이미 열린 트랜잭션 커넥션으로 예매된 좌석 ID 목록을 조회한다.
	public ArrayList<Integer> getReservedSeatIds(Connection con, int scheduleId) {
		return seatDAO.getReservedSeatIds(con, scheduleId);
	}

	// 예매 변경 시 현재 예매를 제외한 예매 좌석 ID 목록을 조회한다.
	public ArrayList<Integer> getReservedSeatIdsExceptReservation(
			Connection con, int scheduleId, int reservationId) {
		return seatDAO.getReservedSeatIdsExceptReservation(con, scheduleId, reservationId);
	}
}

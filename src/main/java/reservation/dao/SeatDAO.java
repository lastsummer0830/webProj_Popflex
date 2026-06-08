package reservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import reservation.dto.SeatDTO;

// SEAT 테이블과 예매된 좌석 조회를 담당하는 DAO
// 좌석 목록 조회, 기본 좌석 생성, 예매된 좌석 ID 조회를 처리한다.
public class SeatDAO {

	private static final int DEFAULT_ROW_COUNT = 7;
	private static final int DEFAULT_COL_COUNT = 14;

	// SEAT 테이블의 전체 좌석 목록을 조회한다.
	// 좌석 데이터가 없으면 기본 좌석 A~E, 1~8을 생성한 뒤 다시 조회한다.
	public ArrayList<SeatDTO> getSeatList(Connection con) {
		ArrayList<SeatDTO> list = selectSeatList(con);

		if (list.size() == 0) {
			insertDefaultSeats(con);
			list = selectSeatList(con);
		}

		return list;
	}

	public ArrayList<SeatDTO> getSeatList(Connection con, int screenId) {
		ArrayList<SeatDTO> list = new ArrayList<>();
		String sql = "select seat_id, row_label, col_num from seat where screen_id = ? order by row_label, col_num";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, screenId);
			rs = pst.executeQuery();

			while (rs.next()) {
				SeatDTO dto = new SeatDTO();
				dto.setSeat_id(rs.getInt("seat_id"));
				dto.setRow_label(rs.getString("row_label"));
				dto.setCol_num(rs.getInt("col_num"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (list.size() == 0) {
			insertDefaultSeats(con, screenId);
			list = selectSeatList(con, screenId);
		}

		return list;
	}

	public ArrayList<SeatDTO> getSeatListByScheduleId(Connection con, int scheduleId) {
		ArrayList<SeatDTO> list = selectSeatListByScheduleId(con, scheduleId);
		int screenId;

		if (list.size() == 0) {
			screenId = getScreenIdByScheduleId(con, scheduleId);
			if (screenId > 0) {
				insertDefaultSeats(con, screenId);
				list = selectSeatListByScheduleId(con, scheduleId);
			}
		}

		return list;
	}

	private ArrayList<SeatDTO> selectSeatListByScheduleId(Connection con, int scheduleId) {
		ArrayList<SeatDTO> list = new ArrayList<>();
		String sql = "select se.seat_id, se.row_label, se.col_num "
				+ "from schedule s "
				+ "join seat se on s.screen_id = se.screen_id "
				+ "where s.schedule_id = ? "
				+ "order by se.row_label, se.col_num";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, scheduleId);
			rs = pst.executeQuery();

			while (rs.next()) {
				SeatDTO dto = new SeatDTO();
				dto.setSeat_id(rs.getInt("seat_id"));
				dto.setRow_label(rs.getString("row_label"));
				dto.setCol_num(rs.getInt("col_num"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// SEAT 테이블에서 좌석 정보를 실제로 조회하는 내부 메서드
	private ArrayList<SeatDTO> selectSeatList(Connection con) {
		ArrayList<SeatDTO> list = new ArrayList<>();
		String sql = "select seat_id, row_label, col_num from seat order by row_label, col_num";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				SeatDTO dto = new SeatDTO();
				dto.setSeat_id(rs.getInt("seat_id"));
				dto.setRow_label(rs.getString("row_label"));
				dto.setCol_num(rs.getInt("col_num"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private ArrayList<SeatDTO> selectSeatList(Connection con, int screenId) {
		ArrayList<SeatDTO> list = new ArrayList<>();
		String sql = "select seat_id, row_label, col_num from seat where screen_id = ? order by row_label, col_num";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, screenId);
			rs = pst.executeQuery();

			while (rs.next()) {
				SeatDTO dto = new SeatDTO();
				dto.setSeat_id(rs.getInt("seat_id"));
				dto.setRow_label(rs.getString("row_label"));
				dto.setCol_num(rs.getInt("col_num"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private int getScreenIdByScheduleId(Connection con, int scheduleId) {
		String sql = "select screen_id from schedule where schedule_id = ?";
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, scheduleId);
			rs = pst.executeQuery();

			if (rs.next()) {
				return rs.getInt("screen_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	// 테스트와 초기 실행을 위해 기본 좌석 A~E, 1~8을 생성한다.
	private void insertDefaultSeats(Connection con) {
		String sql = "insert into seat(row_label, col_num) values(?, ?)";
		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement(sql);
			for (char row = 'A'; row <= 'E'; row++) {
				for (int col = 1; col <= 8; col++) {
					pst.setString(1, String.valueOf(row));
					pst.setInt(2, col);
					pst.addBatch();
				}
			}
			pst.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertDefaultSeats(Connection con, int screenId) {
		String sql = "insert into seat(screen_id, row_label, col_num) values(?, ?, ?)";
		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement(sql);
			for (char row = 'A'; row < 'A' + DEFAULT_ROW_COUNT; row++) {
				for (int col = 1; col <= DEFAULT_COL_COUNT; col++) {
					pst.setInt(1, screenId);
					pst.setString(2, String.valueOf(row));
					pst.setInt(3, col);
					pst.addBatch();
				}
			}
			pst.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 특정 상영 일정에서 이미 예매된 seat_id 목록을 조회한다.
	public ArrayList<Integer> getReservedSeatIds(Connection con, int scheduleId) {
		ArrayList<Integer> list = new ArrayList<>();
		String sql = "select rs.seat_id from reservation_seat rs "
				+ "join reservation r on rs.reservation_id = r.reservation_id "
				+ "where rs.schedule_id = ? and r.status = 'Y'";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, scheduleId);
			rs = pst.executeQuery();

			while (rs.next()) {
				list.add(rs.getInt("seat_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// 예매 변경 시 자기 예매의 기존 좌석은 제외하고 예매된 seat_id 목록을 조회한다.
	public ArrayList<Integer> getReservedSeatIdsExceptReservation(Connection con, int scheduleId, int reservationId) {
		ArrayList<Integer> list = new ArrayList<>();
		String sql = "select rs.seat_id from reservation_seat rs "
				+ "join reservation r on rs.reservation_id = r.reservation_id "
				+ "where rs.schedule_id = ? and r.status = 'Y' and r.reservation_id <> ?";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, scheduleId);
			pst.setInt(2, reservationId);
			rs = pst.executeQuery();

			while (rs.next()) {
				list.add(rs.getInt("seat_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}

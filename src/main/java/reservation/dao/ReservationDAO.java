package reservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import reservation.dto.ReservationDTO;

// RESERVATION, RESERVATION_SEAT 테이블에 직접 접근하는 DAO
// SQL 실행과 ResultSet 데이터를 ReservationDTO로 옮기는 역할을 담당한다.
public class ReservationDAO {

	// reservation 테이블에 예매 기본 정보를 저장하고 생성된 reservation_id를 반환한다.
	public int insertReservation(Connection con, ReservationDTO rvdto) {
		int reservationId = 0;
		String sql = "insert into reservation (member_id, schedule_id, headcount, status, total_price) "
				+ "values(?, ?, ?, 'Y', (select price * ? from schedule where schedule_id = ?))";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql, new String[] {"reservation_id"});
			pst.setInt(1, rvdto.getMember_id());
			pst.setInt(2, rvdto.getSchedule_id());
			pst.setInt(3, rvdto.getHeadcount());
			pst.setInt(4, rvdto.getHeadcount());
			pst.setInt(5, rvdto.getSchedule_id());

			pst.executeUpdate();
			rs = pst.getGeneratedKeys();

			if (rs.next()) {
				reservationId = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return reservationId;
	}

	// 회원의 예매 목록을 영화명, 상영시간, 좌석명까지 조인해서 조회한다.
	public ArrayList<ReservationDTO> getReservationListByMember(Connection con, int memberId) {
		ArrayList<ReservationDTO> list = new ArrayList<>();
		String sql = "select r.reservation_id, r.member_id, r.schedule_id, r.headcount, r.status, r.reserved_at, "
				+ "m.title as movie_title, s.start_time, s.end_time, s.price, "
				+ "t.theater_name, sc.screen_name, "
				+ "listagg(se.row_label || se.col_num, ', ') within group (order by se.row_label, se.col_num) as seat_names "
				+ "from reservation r "
				+ "join schedule s on r.schedule_id = s.schedule_id "
				+ "join movie m on s.movie_id = m.movie_id "
				+ "join screen sc on s.screen_id = sc.screen_id "
				+ "join theater t on sc.theater_id = t.theater_id "
				+ "left join reservation_seat rs on r.reservation_id = rs.reservation_id "
				+ "left join seat se on rs.seat_id = se.seat_id "
				+ "where r.member_id = ? "
				+ "group by r.reservation_id, r.member_id, r.schedule_id, r.headcount, r.status, r.reserved_at, "
				+ "m.title, s.start_time, s.end_time, s.price, t.theater_name, sc.screen_name "
				+ "order by r.reserved_at desc";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, memberId);
			rs = pst.executeQuery();

			while (rs.next()) {
				ReservationDTO dto = new ReservationDTO();
				dto.setReservation_id(rs.getInt("reservation_id"));
				dto.setMember_id(rs.getInt("member_id"));
				dto.setSchedule_id(rs.getInt("schedule_id"));
				dto.setHeadcount(rs.getInt("headcount"));
				dto.setStatus(rs.getString("status").charAt(0));
				dto.setReserved_at(rs.getTimestamp("reserved_at"));
				dto.setMovieTitle(rs.getString("movie_title"));
				dto.setStartTime(rs.getTimestamp("start_time"));
				dto.setEndTime(rs.getTimestamp("end_time"));
				dto.setSeatNames(rs.getString("seat_names"));
				dto.setPrice(rs.getInt("price"));
				dto.setTheaterName(rs.getString("theater_name"));
				dto.setScreenName(rs.getString("screen_name"));

				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// 회원 본인의 활성 예매 1건을 조회한다.
	public ReservationDTO getReservationByIdAndMember(Connection con, int reservationId, int memberId) {
		ReservationDTO dto = null;
		String sql = "select reservation_id, member_id, schedule_id, headcount, status, reserved_at "
				+ "from reservation "
				+ "where reservation_id = ? and member_id = ? and status = 'Y'";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, reservationId);
			pst.setInt(2, memberId);
			rs = pst.executeQuery();

			if (rs.next()) {
				dto = new ReservationDTO();
				dto.setReservation_id(rs.getInt("reservation_id"));
				dto.setMember_id(rs.getInt("member_id"));
				dto.setSchedule_id(rs.getInt("schedule_id"));
				dto.setHeadcount(rs.getInt("headcount"));
				dto.setStatus(rs.getString("status").charAt(0));
				dto.setReserved_at(rs.getTimestamp("reserved_at"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dto;
	}

	// 예매 상세 화면에 필요한 영화명, 상영시간, 좌석명을 함께 조회한다.
	public ReservationDTO getReservationDetailByIdAndMember(Connection con, int reservationId, int memberId) {
		ReservationDTO dto = null;
		String sql = "select r.reservation_id, r.member_id, r.schedule_id, r.headcount, r.status, r.reserved_at, "
				+ "m.title as movie_title, s.start_time, s.end_time, s.price, "
				+ "t.theater_name, sc.screen_name, "
				+ "listagg(se.row_label || se.col_num, ', ') within group (order by se.row_label, se.col_num) as seat_names "
				+ "from reservation r "
				+ "join schedule s on r.schedule_id = s.schedule_id "
				+ "join movie m on s.movie_id = m.movie_id "
				+ "join screen sc on s.screen_id = sc.screen_id "
				+ "join theater t on sc.theater_id = t.theater_id "
				+ "left join reservation_seat rs on r.reservation_id = rs.reservation_id "
				+ "left join seat se on rs.seat_id = se.seat_id "
				+ "where r.reservation_id = ? and r.member_id = ? "
				+ "group by r.reservation_id, r.member_id, r.schedule_id, r.headcount, r.status, r.reserved_at, "
				+ "m.title, s.start_time, s.end_time, s.price, t.theater_name, sc.screen_name";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, reservationId);
			pst.setInt(2, memberId);
			rs = pst.executeQuery();

			if (rs.next()) {
				dto = new ReservationDTO();
				dto.setReservation_id(rs.getInt("reservation_id"));
				dto.setMember_id(rs.getInt("member_id"));
				dto.setSchedule_id(rs.getInt("schedule_id"));
				dto.setHeadcount(rs.getInt("headcount"));
				dto.setStatus(rs.getString("status").charAt(0));
				dto.setReserved_at(rs.getTimestamp("reserved_at"));
				dto.setMovieTitle(rs.getString("movie_title"));
				dto.setStartTime(rs.getTimestamp("start_time"));
				dto.setEndTime(rs.getTimestamp("end_time"));
				dto.setSeatNames(rs.getString("seat_names"));
				dto.setPrice(rs.getInt("price"));
				dto.setTheaterName(rs.getString("theater_name"));
				dto.setScreenName(rs.getString("screen_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dto;
	}

	// 좌석 변경 후 선택 좌석 수에 맞춰 headcount를 수정한다.
	public int updateReservationHeadcount(Connection con, int reservationId, int memberId, int headcount) {
		String sql = "update reservation set headcount = ? "
				+ "where reservation_id = ? and member_id = ? and status = 'Y'";
		PreparedStatement pst = null;
		int result = 0;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, headcount);
			pst.setInt(2, reservationId);
			pst.setInt(3, memberId);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	// 예매 상태를 취소(C)로 변경한다.
	public int cancelReservation(Connection con, int reservationId, int memberId) {
		String sql = "update reservation set status = 'C' where reservation_id = ? and member_id = ? and status = 'Y'";
		PreparedStatement pst = null;
		int result = 0;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, reservationId);
			pst.setInt(2, memberId);

			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
}

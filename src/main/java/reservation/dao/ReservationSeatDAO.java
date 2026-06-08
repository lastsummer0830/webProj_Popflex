package reservation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import reservation.dto.ReservationSeatDTO;

public class ReservationSeatDAO {

	public int insertReservationSeat(Connection con, ReservationSeatDTO dto) {
		String sql = "insert into reservation_seat(reservation_id, schedule_id, seat_id) values(?,?,?)";
		PreparedStatement pst = null;
		int result = 0;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, dto.getReservation_id());
			pst.setInt(2, dto.getSchedule_id());
			pst.setInt(3, dto.getSeat_id());

			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public int deleteReservationSeats(Connection con, int reservationId) {
		String sql = "delete from reservation_seat where reservation_id = ?";
		PreparedStatement pst = null;
		int result = 0;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, reservationId);
			result = pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<Integer> getSeatIdsByReservation(Connection con, int reservationId, int memberId) {
		ArrayList<Integer> list = new ArrayList<>();
		String sql = "select rs.seat_id "
				+ "from reservation_seat rs "
				+ "join reservation r on rs.reservation_id = r.reservation_id "
				+ "where rs.reservation_id = ? and r.member_id = ? and r.status = 'Y' "
				+ "order by rs.seat_id";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, reservationId);
			pst.setInt(2, memberId);
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

package reservation.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import common.DBUtil;
import movie.dao.MovieActorDAO;
import movie.dao.MovieDAO;
import movie.dao.MovieKeywordDAO;
import movie.dto.MovieActorDTO;
import movie.dto.MovieDTO;
import movie.dto.MovieKeywordDTO;
import reservation.dao.ReservationDAO;
import reservation.dao.ReservationSeatDAO;
import reservation.dto.ReservationDTO;
import reservation.dto.ReservationSeatDTO;
import reservation.dto.ReservationScheduleDTO;
import reservation.dto.ReservationTheaterDTO;
import reservation.dto.SeatDTO;
import schedule.dao.ScheduleDAO;
import schedule.dto.ScheduleDTO;

// 예매 비즈니스 로직과 트랜잭션 처리를 담당하는 서비스 클래스
public class ReservationService {
	private ReservationDAO reservationDAO = new ReservationDAO();
	private ReservationSeatDAO reservationSeatDAO = new ReservationSeatDAO();
	private MovieDAO movieDAO = new MovieDAO();
	private MovieActorDAO actorDAO = new MovieActorDAO();
	private MovieKeywordDAO keywordDAO = new MovieKeywordDAO();
	private ScheduleDAO scheduleDAO = new ScheduleDAO();
	private SeatService seatService = new SeatService();

	// schedule_id로 상영 일정 1건을 조회한다.
	public ScheduleDTO getScheduleById(int scheduleId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return scheduleDAO.getScheduleById(con, scheduleId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	// movie_id로 영화 1건을 조회한다.
	public MovieDTO getMovieById(int movieId) throws SQLException {
		if (movieId <= 0) {
			return null;
		}

		Connection con = null;

		try {
			con = DBUtil.getConnection();
			MovieDTO movie = movieDAO.getMovieById(con, movieId);
			fillActorAndKeyword(movie);
			return movie;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	private void fillActorAndKeyword(MovieDTO movie) {
		if (movie == null || movie.getMovieId() <= 0) {
			return;
		}

		ArrayList<MovieActorDTO> actorList = actorDAO.findActorsByMovieId(movie.getMovieId());
		ArrayList<MovieKeywordDTO> keywordList = keywordDAO.findKeywordsByMovieId(movie.getMovieId());

		movie.setActorNm(toActorNames(actorList));
		movie.setKeywords(toKeywordNames(keywordList));
	}

	private String toActorNames(ArrayList<MovieActorDTO> actorList) {
		if (actorList == null || actorList.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (MovieActorDTO actor : actorList) {
			if (actor == null || actor.getActorName() == null || actor.getActorName().trim().isEmpty()) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(actor.getActorName().trim());
		}

		return sb.toString();
	}

	private String toKeywordNames(ArrayList<MovieKeywordDTO> keywordList) {
		if (keywordList == null || keywordList.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (MovieKeywordDTO keyword : keywordList) {
			if (keyword == null || keyword.getKeywordName() == null || keyword.getKeywordName().trim().isEmpty()) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(keyword.getKeywordName().trim());
		}

		return sb.toString();
	}

	// 선택한 영화의 전체 상영 일정 목록을 조회한다.
	public ArrayList<ScheduleDTO> getScheduleListByMovieId(int movieId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return scheduleDAO.getScheduleListByMovieId(con, movieId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	public void ensureDefaultSchedulesForMovie(int movieId) throws SQLException {
		if (movieId <= 0) {
			return;
		}

		Connection con = null;

		try {
			con = DBUtil.getConnection();
			con.setAutoCommit(false);

			if (hasReservableScheduleForMovie(con, movieId)) {
				con.commit();
				return;
			}

			ArrayList<DefaultScreen> screens = getAllScreens(con);
			if (screens.isEmpty()) {
				con.commit();
				return;
			}

			int runtimeMinutes = getMovieRuntime(con, movieId);
			insertDefaultSchedules(con, movieId, screens, runtimeMinutes);
			con.commit();
		} catch (SQLException e) {
			if (con != null) {
				con.rollback();
			}
			throw e;
		} finally {
			if (con != null) {
				con.setAutoCommit(true);
				con.close();
			}
		}
	}

	public ArrayList<ReservationScheduleDTO> getReservationScheduleListByMovieId(int movieId) throws SQLException {
		ArrayList<ReservationScheduleDTO> list = new ArrayList<>();
		String sql = "select s.schedule_id, s.movie_id, s.screen_id, sc.theater_id, t.theater_name, sc.screen_name, "
				+ "s.start_time, s.end_time, s.price "
				+ "from schedule s "
				+ "join screen sc on s.screen_id = sc.screen_id "
				+ "join theater t on sc.theater_id = t.theater_id "
				+ "where s.movie_id = ? "
				+ "order by t.theater_name asc, sc.screen_name asc, s.start_time asc";

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			con = DBUtil.getConnection();
			pst = con.prepareStatement(sql);
			pst.setInt(1, movieId);
			rs = pst.executeQuery();

			while (rs.next()) {
				ReservationScheduleDTO dto = new ReservationScheduleDTO();
				dto.setScheduleId(rs.getInt("schedule_id"));
				dto.setMovieId(rs.getInt("movie_id"));
				dto.setScreenId(rs.getInt("screen_id"));
				dto.setTheaterId(rs.getInt("theater_id"));
				dto.setTheaterName(rs.getString("theater_name"));
				dto.setScreenName(rs.getString("screen_name"));
				dto.setPrice(rs.getInt("price"));

				Timestamp startTime = rs.getTimestamp("start_time");
				if (startTime != null) {
					dto.setStartTime(startTime.toLocalDateTime());
				}

				Timestamp endTime = rs.getTimestamp("end_time");
				if (endTime != null) {
					dto.setEndTime(endTime.toLocalDateTime());
				}

				list.add(dto);
			}
		} finally {
			DBUtil.close(rs, pst, con);
		}

		return list;
	}

	public ArrayList<ReservationTheaterDTO> getTheaterList() throws SQLException {
		ArrayList<ReservationTheaterDTO> list = new ArrayList<>();
		String sql = "select theater_id, theater_name, location from theater order by theater_name asc";

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			con = DBUtil.getConnection();
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				ReservationTheaterDTO dto = new ReservationTheaterDTO();
				dto.setTheaterId(rs.getInt("theater_id"));
				dto.setTheaterName(rs.getString("theater_name"));
				dto.setLocation(rs.getString("location"));
				list.add(dto);
			}
		} finally {
			DBUtil.close(rs, pst, con);
		}

		return list;
	}

	private boolean hasReservableScheduleForMovie(Connection con, int movieId) throws SQLException {
		String sql = "select count(*) "
				+ "from schedule s "
				+ "join screen sc on s.screen_id = sc.screen_id "
				+ "join theater t on sc.theater_id = t.theater_id "
				+ "where s.movie_id = ?";

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, movieId);

			try (ResultSet rs = pst.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	private ArrayList<DefaultScreen> getAllScreens(Connection con) throws SQLException {
		ArrayList<DefaultScreen> screens = new ArrayList<>();
		String sql = "select screen_id, screen_name from screen order by screen_id asc";

		try (PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				screens.add(new DefaultScreen(rs.getInt("screen_id"), rs.getString("screen_name")));
			}
		}

		return screens;
	}

	private int getMovieRuntime(Connection con, int movieId) throws SQLException {
		String sql = "select runtime from movie where movie_id = ?";

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, movieId);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					int runtime = rs.getInt("runtime");
					if (!rs.wasNull() && runtime > 0) {
						return runtime;
					}
				}
			}
		}

		return 120;
	}

	private int insertDefaultSchedules(Connection con, int movieId, ArrayList<DefaultScreen> screens, int runtimeMinutes)
			throws SQLException {
		String sql = "insert into schedule (movie_id, screen_id, start_time, end_time, price) values (?, ?, ?, ?, ?)";
		int price = 12000;
		int inserted = 0;
		LocalDate firstDate = findAvailableFirstDate(con);
		int runtime = runtimeMinutes > 0 ? runtimeMinutes : 120;
		int screenIndex;

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			for (int day = 0; day < 7; day++) {
				for (screenIndex = 0; screenIndex < screens.size(); screenIndex++) {
					DefaultScreen screen = screens.get(screenIndex);
					int[] startHours = getDefaultStartHours(screen.screenName, screenIndex);

					for (int startHour : startHours) {
						LocalDateTime startTime = firstDate.plusDays(day).atTime(startHour, 0);
						LocalDateTime endTime = startTime.plusMinutes(runtime);

						pst.setInt(1, movieId);
						pst.setInt(2, screen.screenId);
						pst.setTimestamp(3, Timestamp.valueOf(startTime));
						pst.setTimestamp(4, Timestamp.valueOf(endTime));
						pst.setInt(5, price);
						pst.addBatch();
					}
				}
			}

			int[] results = pst.executeBatch();
			for (int result : results) {
				if (result > 0) {
					inserted += result;
				}
			}
		}

		return inserted;
	}

	private LocalDate findAvailableFirstDate(Connection con) throws SQLException {
		LocalDate firstDate = LocalDate.now().plusDays(1);

		for (int week = 0; week < 52; week++) {
			if (!hasScheduleInDateRange(con, firstDate, firstDate.plusDays(7))) {
				return firstDate;
			}

			firstDate = firstDate.plusDays(7);
		}

		return firstDate;
	}

	private boolean hasScheduleInDateRange(Connection con, LocalDate startDate, LocalDate endDate) throws SQLException {
		String sql = "select count(*) from schedule where start_time >= ? and start_time < ?";

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
			pst.setTimestamp(2, Timestamp.valueOf(endDate.atStartOfDay()));

			try (ResultSet rs = pst.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	private int[] getDefaultStartHours(String screenName, int screenIndex) {
		String name = screenName == null ? "" : screenName;

		if (name.contains("1")) {
			return new int[] { 13, 14, 15 };
		}

		if (name.contains("2")) {
			return new int[] { 16, 17, 18 };
		}

		if (name.contains("3")) {
			return new int[] { 19, 20, 21 };
		}

		if (screenIndex == 0) {
			return new int[] { 13, 14, 15 };
		}

		if (screenIndex == 1) {
			return new int[] { 16, 17, 18 };
		}

		return new int[] { 19, 20, 21 };
	}

	private static class DefaultScreen {
		private final int screenId;
		private final String screenName;

		private DefaultScreen(int screenId, String screenName) {
			this.screenId = screenId;
			this.screenName = screenName;
		}
	}

	// 예매 기본 정보와 선택 좌석 정보를 하나의 트랜잭션으로 저장한다.
	public int reserve(int memberId, int scheduleId, ArrayList<Integer> seatIds) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			con.setAutoCommit(false);

			// 예매 저장 전에 상영 일정이 실제로 존재하는지 확인한다.
			ScheduleDTO schedule = scheduleDAO.getScheduleById(con, scheduleId);
			if (schedule == null) {
				con.rollback();
				return 0;
			}

			// 좌석은 최소 1개 이상 선택해야 한다.
			if (seatIds == null || seatIds.size() == 0) {
				con.rollback();
				return -1;
			}

			if (!allSeatsBelongToSchedule(con, scheduleId, seatIds)) {
				con.rollback();
				return -1;
			}

			// DB UNIQUE 제약에 걸리기 전에 이미 예매된 좌석인지 검사한다.
			ArrayList<Integer> reservedSeatIds = seatService.getReservedSeatIds(con, scheduleId);
			for (Integer seatId : seatIds) {
				if (reservedSeatIds.contains(seatId)) {
					con.rollback();
					return -2;
				}
			}

			// reservation 테이블에 예매 기본 정보를 저장한다.
			ReservationDTO reservationDTO = new ReservationDTO();
			reservationDTO.setMember_id(memberId);
			reservationDTO.setSchedule_id(scheduleId);
			reservationDTO.setHeadcount(seatIds.size());
			reservationDTO.setStatus('Y');

			int reservationId = reservationDAO.insertReservation(con, reservationDTO);
			if (reservationId == 0) {
				con.rollback();
				return -3;
			}

			// 선택한 좌석마다 reservation_seat 행을 저장한다.
			for (Integer seatId : seatIds) {
				ReservationSeatDTO reservationSeatDTO = new ReservationSeatDTO();
				reservationSeatDTO.setReservation_id(reservationId);
				reservationSeatDTO.setSchedule_id(scheduleId);
				reservationSeatDTO.setSeat_id(seatId);

				int result = reservationSeatDAO.insertReservationSeat(con, reservationSeatDTO);
				if (result == 0) {
					con.rollback();
					return -4;
				}
			}

			con.commit();
			return reservationId;
		} catch (SQLException e) {
			if (con != null) {
				con.rollback();
			}
			throw e;
		} finally {
			if (con != null) {
				con.setAutoCommit(true);
				con.close();
			}
		}
	}

	// 로그인 회원의 예매 목록을 조회한다.
	public ArrayList<ReservationDTO> getReservationListByMember(int memberId) throws SQLException {
		Connection con = null;
		ArrayList<ReservationDTO> list = null;

		try {
			con = DBUtil.getConnection();
			list = reservationDAO.getReservationListByMember(con, memberId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return list;
	}

	// 해당 회원에게 속한 예매 상세 1건만 조회한다.
	public ReservationDTO getReservationDetail(int reservationId, int memberId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return reservationDAO.getReservationDetailByIdAndMember(con, reservationId, memberId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	// 예매 변경 화면에서 기존 선택 좌석을 표시하기 위한 seat_id 목록을 조회한다.
	public ArrayList<Integer> getReservationSeatIds(int reservationId, int memberId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			return reservationSeatDAO.getSeatIdsByReservation(con, reservationId, memberId);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	// 활성 예매의 선택 좌석을 교체하고 인원수를 갱신한다.
	public int updateReservation(int reservationId, int memberId, ArrayList<Integer> seatIds) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			con.setAutoCommit(false);

			// 본인의 활성 예매만 변경할 수 있다.
			ReservationDTO reservation = reservationDAO.getReservationByIdAndMember(con, reservationId, memberId);
			if (reservation == null) {
				con.rollback();
				return 0;
			}

			if (seatIds == null || seatIds.size() == 0) {
				con.rollback();
				return -1;
			}

			if (!allSeatsBelongToSchedule(con, reservation.getSchedule_id(), seatIds)) {
				con.rollback();
				return -1;
			}

			// 현재 예매의 기존 좌석은 제외하고 다른 예매 좌석만 중복 검사한다.
			ArrayList<Integer> reservedSeatIds = seatService.getReservedSeatIdsExceptReservation(
					con, reservation.getSchedule_id(), reservationId);
			for (Integer seatId : seatIds) {
				if (reservedSeatIds.contains(seatId)) {
					con.rollback();
					return -2;
				}
			}

			// 기존 좌석 행을 삭제하고 새로 선택한 좌석 행을 다시 저장한다.
			reservationSeatDAO.deleteReservationSeats(con, reservationId);
			for (Integer seatId : seatIds) {
				ReservationSeatDTO reservationSeatDTO = new ReservationSeatDTO();
				reservationSeatDTO.setReservation_id(reservationId);
				reservationSeatDTO.setSchedule_id(reservation.getSchedule_id());
				reservationSeatDTO.setSeat_id(seatId);

				int result = reservationSeatDAO.insertReservationSeat(con, reservationSeatDTO);
				if (result == 0) {
					con.rollback();
					return -3;
				}
			}

			int updateResult = reservationDAO.updateReservationHeadcount(
					con, reservationId, memberId, seatIds.size());
			if (updateResult == 0) {
				con.rollback();
				return -4;
			}

			con.commit();
			return updateResult;
		} catch (SQLException e) {
			if (con != null) {
				con.rollback();
			}
			throw e;
		} finally {
			if (con != null) {
				con.setAutoCommit(true);
				con.close();
			}
		}
	}

	// 활성 예매를 취소하고 좌석을 다시 예매 가능 상태로 풀어준다.
	public int cancelReservation(int reservationId, int memberId) throws SQLException {
		Connection con = null;

		try {
			con = DBUtil.getConnection();
			con.setAutoCommit(false);

			// 본인의 활성 예매만 취소할 수 있다.
			ReservationDTO reservation = reservationDAO.getReservationByIdAndMember(con, reservationId, memberId);
			if (reservation == null) {
				con.rollback();
				return 0;
			}

			// 같은 좌석을 다시 예매할 수 있도록 reservation_seat 행을 먼저 삭제한다.
			reservationSeatDAO.deleteReservationSeats(con, reservationId);

			int result = reservationDAO.cancelReservation(con, reservationId, memberId);
			if (result > 0) {
				con.commit();
			} else {
				con.rollback();
			}

			return result;
		} catch (SQLException e) {
			if (con != null) {
				con.rollback();
			}
			throw e;
		} finally {
			if (con != null) {
				con.setAutoCommit(true);
				con.close();
			}
		}
	}

	private boolean allSeatsBelongToSchedule(Connection con, int scheduleId, ArrayList<Integer> seatIds) {
		ArrayList<SeatDTO> seats = seatService.getSeatListByScheduleId(con, scheduleId);
		ArrayList<Integer> validSeatIds = new ArrayList<>();

		for (SeatDTO seat : seats) {
			validSeatIds.add(seat.getSeat_id());
		}

		for (Integer seatId : seatIds) {
			if (seatId == null || !validSeatIds.contains(seatId)) {
				return false;
			}
		}

		return true;
	}
}

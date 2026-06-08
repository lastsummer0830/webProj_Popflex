package admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import admin.dto.AdminDashboardDTO;
import admin.dto.AdminMemberDTO;
import admin.dto.AdminMovieDTO;
import admin.dto.AdminScheduleDTO;
import admin.dto.AdminScreenDTO;

public class AdminDAO {

    public AdminDashboardDTO selectDashboard(Connection conn) throws SQLException {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setMemberCount(safeCount(conn, "SELECT COUNT(*) FROM MEMBER WHERE IS_USE = 'Y'", "MEMBER", dto));
        dto.setMovieCount(safeCount(conn, "SELECT COUNT(*) FROM MOVIE", "MOVIE", dto));
        dto.setScheduleCount(safeCount(conn, "SELECT COUNT(*) FROM SCHEDULE", "SCHEDULE", dto));
        dto.setReservationCount(safeCount(conn,
                "SELECT COUNT(*) FROM RESERVATION WHERE STATUS = 'Y'", "RESERVATION", dto));
        return dto;
    }

    public List<AdminMovieDTO> selectMovieOptions(Connection conn) throws SQLException {
        String sql = "SELECT MOVIE_ID, TITLE, RUNTIME "
                + "FROM MOVIE "
                + "ORDER BY TITLE";
        List<AdminMovieDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                AdminMovieDTO dto = new AdminMovieDTO();
                dto.setMovieId(rs.getInt("MOVIE_ID"));
                dto.setTitle(rs.getString("TITLE"));
                dto.setRuntime(rs.getString("RUNTIME"));
                list.add(dto);
            }
        }

        return list;
    }

    public List<AdminMemberDTO> selectMemberList(Connection conn, String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MEMBER_ID, USER_ID, NAME, EMAIL, ROLE, IS_USE, SOCIAL_TYPE, CREATED_AT ")
           .append("FROM MEMBER ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE LOWER(USER_ID) LIKE ? ")
               .append("OR LOWER(NAME) LIKE ? ")
               .append("OR LOWER(EMAIL) LIKE ? ");
        }

        sql.append("ORDER BY CASE WHEN ROLE = 'A' THEN 0 ELSE 1 END, MEMBER_ID DESC");

        List<AdminMemberDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (hasKeyword) {
                String searchValue = "%" + keyword.trim().toLowerCase() + "%";
                pstmt.setString(1, searchValue);
                pstmt.setString(2, searchValue);
                pstmt.setString(3, searchValue);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMember(rs));
                }
            }
        }

        return list;
    }

    public int updateMemberRoleToAdmin(Connection conn, int memberId) throws SQLException {
        String sql = "UPDATE MEMBER "
                + "SET ROLE = 'A' "
                + "WHERE MEMBER_ID = ? "
                + "AND IS_USE = 'Y' "
                + "AND (ROLE IS NULL OR ROLE <> 'A')";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate();
        }
    }

    public boolean existsActiveMember(Connection conn, int memberId) throws SQLException {
        return exists(conn, "SELECT COUNT(*) FROM MEMBER WHERE MEMBER_ID = ? AND IS_USE = 'Y'", memberId);
    }

    public List<AdminScreenDTO> selectScreenOptions(Connection conn) throws SQLException {
        String sql = "SELECT sc.SCREEN_ID, sc.THEATER_ID, th.THEATER_NAME, th.LOCATION, "
                + "sc.SCREEN_NAME, COUNT(se.SEAT_ID) AS SEAT_COUNT "
                + "FROM SCREEN sc "
                + "JOIN THEATER th ON sc.THEATER_ID = th.THEATER_ID "
                + "LEFT JOIN SEAT se ON sc.SCREEN_ID = se.SCREEN_ID "
                + "GROUP BY sc.SCREEN_ID, sc.THEATER_ID, th.THEATER_NAME, th.LOCATION, sc.SCREEN_NAME "
                + "ORDER BY th.THEATER_NAME, sc.SCREEN_NAME";
        List<AdminScreenDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapScreen(rs));
            }
        }

        return list;
    }

    public List<AdminScheduleDTO> selectScheduleList(Connection conn) throws SQLException {
        String sql = "SELECT s.SCHEDULE_ID, s.MOVIE_ID, m.TITLE, m.POSTER_URL, "
                + "s.SCREEN_ID, th.THEATER_NAME, sc.SCREEN_NAME, "
                + "s.START_TIME, s.END_TIME, s.PRICE, "
                + "(SELECT COUNT(*) FROM RESERVATION r WHERE r.SCHEDULE_ID = s.SCHEDULE_ID) AS RESERVATION_COUNT "
                + "FROM SCHEDULE s "
                + "JOIN MOVIE m ON s.MOVIE_ID = m.MOVIE_ID "
                + "JOIN SCREEN sc ON s.SCREEN_ID = sc.SCREEN_ID "
                + "JOIN THEATER th ON sc.THEATER_ID = th.THEATER_ID "
                + "ORDER BY s.START_TIME DESC, s.SCHEDULE_ID DESC";
        List<AdminScheduleDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapSchedule(rs));
            }
        }

        return list;
    }

    public AdminScheduleDTO selectScheduleById(Connection conn, int scheduleId) throws SQLException {
        String sql = "SELECT s.SCHEDULE_ID, s.MOVIE_ID, m.TITLE, m.POSTER_URL, "
                + "s.SCREEN_ID, th.THEATER_NAME, sc.SCREEN_NAME, "
                + "s.START_TIME, s.END_TIME, s.PRICE, "
                + "(SELECT COUNT(*) FROM RESERVATION r WHERE r.SCHEDULE_ID = s.SCHEDULE_ID) AS RESERVATION_COUNT "
                + "FROM SCHEDULE s "
                + "JOIN MOVIE m ON s.MOVIE_ID = m.MOVIE_ID "
                + "JOIN SCREEN sc ON s.SCREEN_ID = sc.SCREEN_ID "
                + "JOIN THEATER th ON sc.THEATER_ID = th.THEATER_ID "
                + "WHERE s.SCHEDULE_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapSchedule(rs);
                }
            }
        }

        return null;
    }

    public int insertSchedule(Connection conn, AdminScheduleDTO dto) throws SQLException {
        String sql = "INSERT INTO SCHEDULE (MOVIE_ID, SCREEN_ID, START_TIME, END_TIME, PRICE) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setScheduleParams(pstmt, dto, false);
            return pstmt.executeUpdate();
        }
    }

    public int updateSchedule(Connection conn, AdminScheduleDTO dto) throws SQLException {
        String sql = "UPDATE SCHEDULE "
                + "SET MOVIE_ID = ?, SCREEN_ID = ?, START_TIME = ?, END_TIME = ?, PRICE = ? "
                + "WHERE SCHEDULE_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setScheduleParams(pstmt, dto, true);
            return pstmt.executeUpdate();
        }
    }

    public int deleteSchedule(Connection conn, int scheduleId) throws SQLException {
        String sql = "DELETE FROM SCHEDULE WHERE SCHEDULE_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            return pstmt.executeUpdate();
        }
    }

    public int countReservationsBySchedule(Connection conn, int scheduleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RESERVATION WHERE SCHEDULE_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public boolean existsMovie(Connection conn, int movieId) throws SQLException {
        return exists(conn, "SELECT COUNT(*) FROM MOVIE WHERE MOVIE_ID = ?", movieId);
    }

    public boolean existsScreen(Connection conn, int screenId) throws SQLException {
        return exists(conn, "SELECT COUNT(*) FROM SCREEN WHERE SCREEN_ID = ?", screenId);
    }

    public int mergeDefaultSeats(Connection conn, int screenId, int rowCount, int colCount) throws SQLException {
        String sql = "MERGE INTO SEAT s "
                + "USING (SELECT ? AS SCREEN_ID, ? AS ROW_LABEL, ? AS COL_NUM FROM dual) src "
                + "ON (s.SCREEN_ID = src.SCREEN_ID AND s.ROW_LABEL = src.ROW_LABEL AND s.COL_NUM = src.COL_NUM) "
                + "WHEN NOT MATCHED THEN "
                + "INSERT (SCREEN_ID, ROW_LABEL, COL_NUM) VALUES (src.SCREEN_ID, src.ROW_LABEL, src.COL_NUM)";
        int changed = 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                String rowLabel = String.valueOf((char) ('A' + rowIndex));

                for (int col = 1; col <= colCount; col++) {
                    pstmt.setInt(1, screenId);
                    pstmt.setString(2, rowLabel);
                    pstmt.setInt(3, col);
                    pstmt.addBatch();
                }
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result > 0) {
                    changed += result;
                }
            }
        }

        return changed;
    }

    private void setScheduleParams(PreparedStatement pstmt, AdminScheduleDTO dto, boolean includeId)
            throws SQLException {
        pstmt.setInt(1, dto.getMovieId());
        pstmt.setInt(2, dto.getScreenId());
        pstmt.setTimestamp(3, dto.getStartTime());

        Timestamp endTime = dto.getEndTime();
        if (endTime == null) {
            pstmt.setNull(4, java.sql.Types.TIMESTAMP);
        } else {
            pstmt.setTimestamp(4, endTime);
        }

        pstmt.setInt(5, dto.getPrice());

        if (includeId) {
            pstmt.setInt(6, dto.getScheduleId());
        }
    }

    private boolean exists(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private int count(Connection conn, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    private int safeCount(Connection conn, String sql, String tableName, AdminDashboardDTO dashboard)
            throws SQLException {
        try {
            return count(conn, sql);
        } catch (SQLException e) {
            if (e.getErrorCode() == 942) {
                dashboard.addMissingTable(tableName);
                return 0;
            }

            throw e;
        }
    }

    private AdminScreenDTO mapScreen(ResultSet rs) throws SQLException {
        AdminScreenDTO dto = new AdminScreenDTO();
        dto.setScreenId(rs.getInt("SCREEN_ID"));
        dto.setTheaterId(rs.getInt("THEATER_ID"));
        dto.setTheaterName(rs.getString("THEATER_NAME"));
        dto.setLocation(rs.getString("LOCATION"));
        dto.setScreenName(rs.getString("SCREEN_NAME"));
        dto.setSeatCount(rs.getInt("SEAT_COUNT"));
        return dto;
    }

    private AdminMemberDTO mapMember(ResultSet rs) throws SQLException {
        AdminMemberDTO dto = new AdminMemberDTO();
        dto.setMemberId(rs.getInt("MEMBER_ID"));
        dto.setUserId(rs.getString("USER_ID"));
        dto.setName(rs.getString("NAME"));
        dto.setEmail(rs.getString("EMAIL"));
        dto.setRole(rs.getString("ROLE"));
        dto.setIsUse(rs.getString("IS_USE"));
        dto.setSocialType(rs.getString("SOCIAL_TYPE"));
        dto.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        return dto;
    }

    private AdminScheduleDTO mapSchedule(ResultSet rs) throws SQLException {
        AdminScheduleDTO dto = new AdminScheduleDTO();
        dto.setScheduleId(rs.getInt("SCHEDULE_ID"));
        dto.setMovieId(rs.getInt("MOVIE_ID"));
        dto.setMovieTitle(rs.getString("TITLE"));
        dto.setPosterUrl(rs.getString("POSTER_URL"));
        dto.setScreenId(rs.getInt("SCREEN_ID"));
        dto.setTheaterName(rs.getString("THEATER_NAME"));
        dto.setScreenName(rs.getString("SCREEN_NAME"));
        dto.setStartTime(rs.getTimestamp("START_TIME"));
        dto.setEndTime(rs.getTimestamp("END_TIME"));
        dto.setPrice(rs.getInt("PRICE"));
        dto.setReservationCount(rs.getInt("RESERVATION_COUNT"));
        return dto;
    }
}

package admin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import admin.dao.AdminDAO;
import admin.dto.AdminDashboardDTO;
import admin.dto.AdminMemberDTO;
import admin.dto.AdminMovieDTO;
import admin.dto.AdminScheduleDTO;
import admin.dto.AdminScreenDTO;
import common.DBUtil;

public class AdminService {

    private static final int DEFAULT_PRICE = 12000;
    private static final int MAX_SEAT_ROWS = 12;
    private static final int MAX_SEAT_COLS = 30;

    private final AdminDAO adminDAO = new AdminDAO();

    public AdminDashboardDTO getDashboard() {
        try (Connection conn = DBUtil.getConnection()) {
            return adminDAO.selectDashboard(conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load admin dashboard.", e);
        }
    }

    public List<AdminMovieDTO> getMovieOptions() {
        try (Connection conn = DBUtil.getConnection()) {
            return adminDAO.selectMovieOptions(conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load movie options.", e);
        }
    }

    public List<AdminMemberDTO> getMemberList(String keyword) {
        try (Connection conn = DBUtil.getConnection()) {
            return adminDAO.selectMemberList(conn, keyword);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load member list.", e);
        }
    }

    public boolean promoteMemberToAdmin(int memberId) {
        if (memberId <= 0) {
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if (!adminDAO.existsActiveMember(conn, memberId)) {
                return false;
            }

            return adminDAO.updateMemberRoleToAdmin(conn, memberId) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to promote member to admin.", e);
        }
    }

    public List<AdminScreenDTO> getScreenOptions() {
        try (Connection conn = DBUtil.getConnection()) {
            return adminDAO.selectScreenOptions(conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load screen options.", e);
        }
    }

    public List<AdminScheduleDTO> getScheduleList() {
        try (Connection conn = DBUtil.getConnection()) {
            return adminDAO.selectScheduleList(conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load schedule list.", e);
        }
    }

    public AdminScheduleDTO getSchedule(int scheduleId) {
        if (scheduleId <= 0) {
            return null;
        }

        try (Connection conn = DBUtil.getConnection()) {
            return adminDAO.selectScheduleById(conn, scheduleId);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load schedule.", e);
        }
    }

    public boolean insertSchedule(AdminScheduleDTO dto) {
        validateSchedule(dto);

        try (Connection conn = DBUtil.getConnection()) {
            if (!adminDAO.existsMovie(conn, dto.getMovieId())
                    || !adminDAO.existsScreen(conn, dto.getScreenId())) {
                return false;
            }

            return adminDAO.insertSchedule(conn, dto) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert schedule.", e);
        }
    }

    public boolean updateSchedule(AdminScheduleDTO dto) {
        validateSchedule(dto);

        if (dto.getScheduleId() <= 0) {
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if (adminDAO.selectScheduleById(conn, dto.getScheduleId()) == null
                    || !adminDAO.existsMovie(conn, dto.getMovieId())
                    || !adminDAO.existsScreen(conn, dto.getScreenId())) {
                return false;
            }

            return adminDAO.updateSchedule(conn, dto) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update schedule.", e);
        }
    }

    public boolean deleteSchedule(int scheduleId) {
        if (scheduleId <= 0) {
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if (adminDAO.countReservationsBySchedule(conn, scheduleId) > 0) {
                return false;
            }

            return adminDAO.deleteSchedule(conn, scheduleId) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete schedule.", e);
        }
    }

    public int createDefaultSeats(int screenId, int rowCount, int colCount) {
        if (screenId <= 0) {
            return -1;
        }

        if (rowCount <= 0) {
            rowCount = 5;
        }

        if (colCount <= 0) {
            colCount = 8;
        }

        if (rowCount > MAX_SEAT_ROWS || colCount > MAX_SEAT_COLS) {
            return -1;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if (!adminDAO.existsScreen(conn, screenId)) {
                return -1;
            }

            return adminDAO.mergeDefaultSeats(conn, screenId, rowCount, colCount);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create seats.", e);
        }
    }

    public AdminScheduleDTO buildSchedule(String scheduleId, String movieId, String screenId,
            String startTime, String endTime, String price) {
        AdminScheduleDTO dto = new AdminScheduleDTO();
        dto.setScheduleId(parseInt(scheduleId, 0));
        dto.setMovieId(parseInt(movieId, 0));
        dto.setScreenId(parseInt(screenId, 0));
        dto.setStartTime(parseDateTime(startTime));
        dto.setEndTime(parseDateTime(endTime));
        dto.setPrice(parseInt(price, DEFAULT_PRICE));
        return dto;
    }

    public int parseInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void validateSchedule(AdminScheduleDTO dto) {
        if (dto == null || dto.getMovieId() <= 0 || dto.getScreenId() <= 0
                || dto.getStartTime() == null || dto.getPrice() < 0) {
            throw new IllegalArgumentException("Invalid schedule input.");
        }

        Timestamp endTime = dto.getEndTime();
        if (endTime != null && !dto.getStartTime().before(endTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }

    private Timestamp parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().replace('T', ' ');
        if (normalized.length() == 16) {
            normalized += ":00";
        }

        try {
            return Timestamp.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

package admin.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminDashboardDTO {

    private int memberCount;
    private int movieCount;
    private int scheduleCount;
    private int reservationCount;
    private final List<String> missingTables = new ArrayList<>();

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getMovieCount() {
        return movieCount;
    }

    public void setMovieCount(int movieCount) {
        this.movieCount = movieCount;
    }

    public int getScheduleCount() {
        return scheduleCount;
    }

    public void setScheduleCount(int scheduleCount) {
        this.scheduleCount = scheduleCount;
    }

    public int getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(int reservationCount) {
        this.reservationCount = reservationCount;
    }

    public List<String> getMissingTables() {
        return Collections.unmodifiableList(missingTables);
    }

    public void addMissingTable(String tableName) {
        if (tableName != null && !missingTables.contains(tableName)) {
            missingTables.add(tableName);
        }
    }

    public boolean isDbReady() {
        return missingTables.isEmpty();
    }
}

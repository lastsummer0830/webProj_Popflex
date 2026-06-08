package reservation.dto;

import java.sql.Timestamp;

// RESERVATION 테이블 기본 정보와 목록/상세 화면에 필요한 조인 결과를 담는 DTO
public class ReservationDTO {
	// RESERVATION 테이블 컬럼
	int reservation_id;
	int member_id;
	int schedule_id;
	int headcount;
	char status;
	Timestamp reserved_at;

	// 목록/상세 화면 표시용 조인 컬럼
	String movieTitle;
	Timestamp startTime;
	Timestamp endTime;
	String seatNames;
	int price;
	String theaterName;
	String screenName;
	public ReservationDTO(int reservation_id, int member_id, int schedule_id, int headcount, char status,
			Timestamp reserved_at) {
		super();
		this.reservation_id = reservation_id;
		this.member_id = member_id;
		this.schedule_id = schedule_id;
		this.headcount = headcount;
		this.status = status;
		this.reserved_at = reserved_at;
	}
	public ReservationDTO() {

	}
	@Override
	public String toString() {
		return "ReservationDTO [reservation_id=" + reservation_id + ", member_id=" + member_id + ", schedule_id="
				+ schedule_id + ", headcount=" + headcount + ", status=" + status + ", reserved_at=" + reserved_at
				+ "]";
	}
	
	public int getReservation_id() {
		return reservation_id;
	}
	public void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}
	public int getMember_id() {
		return member_id;
	}
	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}
	public int getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(int schedule_id) {
		this.schedule_id = schedule_id;
	}
	public int getHeadcount() {
		return headcount;
	}
	public void setHeadcount(int headcount) {
		this.headcount = headcount;
	}
	public char getStatus() {
		return status;
	}
	public String getStatusText() {
		return String.valueOf(status);
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public Timestamp getReserved_at() {
		return reserved_at;
	}
	public void setReserved_at(Timestamp reserved_at) {
		this.reserved_at = reserved_at;
	}
	public String getMovieTitle() {
		return movieTitle;
	}
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public String getSeatNames() {
		return seatNames;
	}
	public void setSeatNames(String seatNames) {
		this.seatNames = seatNames;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getTotalPrice() {
		return price * headcount;
	}
	public String getTheaterName() {
		return theaterName;
	}
	public void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
}

package reservation.dto;

// RESERVATION_SEAT 테이블 한 행을 담는 DTO
// 하나의 예매가 어떤 상영 일정의 어떤 좌석을 잡았는지 표현한다.
public class ReservationSeatDTO {
	int reservation_seat_id;
	int reservation_id;
	int schedule_id;
	int seat_id;
	public ReservationSeatDTO(int reservation_seat_id, int reservation_id, int schedule_id, int seat_id) {
		super();
		this.reservation_seat_id = reservation_seat_id;
		this.reservation_id = reservation_id;
		this.schedule_id = schedule_id;
		this.seat_id = seat_id;
	}
	public ReservationSeatDTO() {

	}
	@Override
	public String toString() {
		return "ReservationSeatDTO [reservation_seat_id=" + reservation_seat_id + ", reservation_id=" + reservation_id
				+ ", schedule_id=" + schedule_id + ", seat_id=" + seat_id + "]";
	}
	public int getReservation_seat_id() {
		return reservation_seat_id;
	}
	public void setReservation_seat_id(int reservation_seat_id) {
		this.reservation_seat_id = reservation_seat_id;
	}
	public int getReservation_id() {
		return reservation_id;
	}
	public void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}
	public int getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(int schedule_id) {
		this.schedule_id = schedule_id;
	}
	public int getSeat_id() {
		return seat_id;
	}
	public void setSeat_id(int seat_id) {
		this.seat_id = seat_id;
	}
	
}

package reservation.dto;

// SEAT 테이블 한 행을 담는 DTO
// 좌석 ID와 화면 표시용 행/열 정보를 가진다.
public class SeatDTO {
	
	int seat_id;
	String row_label;
	int col_num;
	
	public SeatDTO(int seat_id, String row_label, int col_num) {
		super();
		this.seat_id = seat_id;
		this.row_label = row_label;
		this.col_num = col_num;
	}
	public SeatDTO() {

	}
	
	@Override
	public String toString() {
		return "SeatDTO [seat_id=" + seat_id + ", row_label=" + row_label + ", col_num="
				+ col_num + "]";
	}
	
	public int getSeat_id() {
		return seat_id;
	}
	public void setSeat_id(int seat_id) {
		this.seat_id = seat_id;
	}
	public String getRow_label() {
		return row_label;
	}
	public void setRow_label(String row_label) {
		this.row_label = row_label;
	}
	public int getCol_num() {
		return col_num;
	}
	public void setCol_num(int col_num) {
		this.col_num = col_num;
	}
}

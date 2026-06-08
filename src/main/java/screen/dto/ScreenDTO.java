package screen.dto;

public class ScreenDTO {
	private int screenId;		//pk, identity 		상영관 id
	private int theaterId;		// fk -> theater	극장 fk
	private String screenName;	// not null			상영관명
	private String theaterName;	// screen 컬럼은 아니고 theater 와 join해서 조회한 극장명
	
	public ScreenDTO() {
		
	}

	public ScreenDTO(int screenId, int theaterId, String screenName, String theaterName) {
		
		this.screenId = screenId;
		this.theaterId = theaterId;
		this.screenName = screenName;
		this.theaterName = theaterName;
	}

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public int getTheaterId() {
		return theaterId;
	}

	public void setTheaterId(int theaterId) {
		this.theaterId = theaterId;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getTheaterName() {
		return theaterName;
	}

	public void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}

	@Override
	public String toString() {
		return "ScreenDTO [screenId=" + screenId + ", theaterId=" + theaterId + ", screenName=" + screenName
				+ ", theaterName=" + theaterName + "]";
	}
	
	
	

}

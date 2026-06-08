package movie.dto;

public class MovieKeywordDTO {
	
	private int movieKeywordId;	// 키워드 테이블 pk
	private int movieId;		// 영화 테이블 fk
	private String keywordName; // 키워드명
	
	public MovieKeywordDTO() {
		
	}
	public MovieKeywordDTO(int movieKeywordId, int movieId, String keywordName) {
		
		this.movieKeywordId = movieKeywordId;
		this.movieId = movieId;
		this.keywordName = keywordName;
	}
	
	public int getMovieKeywordId() {
		return movieKeywordId;
	}
	public void setMovieKeywordId(int movieKeywordId) {
		this.movieKeywordId = movieKeywordId;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public String getKeywordName() {
		return keywordName;
	}
	public void setKeywordName(String keywordName) {
		this.keywordName = keywordName;
	}
	
	@Override
	public String toString() {
		return "MovieKeywordDTO [movieKeywordId=" + movieKeywordId + ", movieId=" + movieId + ", keywordName="
				+ keywordName + "]";
	}
	
}

package movie.dto;

public class MovieActorDTO {
	
	private int movieActorId;   // MOVIE_ACTOR_ID, 배우 행 고유번호
	private int movieId;        // MOVIE_ID, 영화 FK
	private String actorName;   // ACTOR_NAME, 배우명
	private int sortOrder;      // SORT_ORDER, 출력 순서
	
	public MovieActorDTO() {
		
	}

	public MovieActorDTO(int movieActorId, int movieId, String actorName, int sortOrder) {
		
		this.movieActorId = movieActorId;
		this.movieId = movieId;
		this.actorName = actorName;
		this.sortOrder = sortOrder;
	}

	public int getMovieActorId() {
		return movieActorId;
	}

	public void setMovieActorId(int movieActorId) {
		this.movieActorId = movieActorId;
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "MovieActorDTO [movieActorId=" + movieActorId + ", movieId=" + movieId + ", actorName=" + actorName
				+ ", sortOrder=" + sortOrder + "]";
	}
	
	

}

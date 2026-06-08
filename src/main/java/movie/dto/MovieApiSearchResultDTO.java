package movie.dto;

import java.util.ArrayList;

public class MovieApiSearchResultDTO {

    // API에서 파싱한 영화 목록
    private ArrayList<MovieDTO> movies;

    // API에서 추출한 전체 검색 결과 개수
    private int totalCount;

    public MovieApiSearchResultDTO() {
        this.movies = new ArrayList<MovieDTO>();
    }

    public MovieApiSearchResultDTO(ArrayList<MovieDTO> movies, int totalCount) {
        this.movies = movies;
        this.totalCount = totalCount;
    }

    public ArrayList<MovieDTO> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<MovieDTO> movies) {
        this.movies = movies;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
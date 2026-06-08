package movie.dto;

import java.util.ArrayList;

import common.paging.PagingDTO;

public class MovieSearchResultDTO {

    // 사용자가 입력한 검색어
    private String query;

    // 검색어 존재 여부
    private boolean hasQuery;

    // 검색 결과 존재 여부
    private boolean hasResult;

    // JSP 영화 카드 출력용 목록
    private ArrayList<MovieListItemDTO> movieItems;

    // 페이징 정보
    private PagingDTO paging;

    public MovieSearchResultDTO() {
        this.movieItems = new ArrayList<MovieListItemDTO>();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isHasQuery() {
        return hasQuery;
    }

    public void setHasQuery(boolean hasQuery) {
        this.hasQuery = hasQuery;
    }

    public boolean isHasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }

    public ArrayList<MovieListItemDTO> getMovieItems() {
        return movieItems;
    }

    public void setMovieItems(ArrayList<MovieListItemDTO> movieItems) {
        this.movieItems = movieItems;
    }

    public PagingDTO getPaging() {
        return paging;
    }

    public void setPaging(PagingDTO paging) {
        this.paging = paging;
    }
}
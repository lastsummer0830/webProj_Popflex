package movie.service;

import java.util.ArrayList;

import common.paging.PagingDTO;
import movie.dto.MovieApiSearchResultDTO;
import movie.dto.MovieDTO;
import movie.dto.MovieListItemDTO;
import movie.dto.MovieSearchResultDTO;

public class MovieSearchService {

	// 한 페이지에 보여줄 영화 개수
	private static final int PAGE_SIZE = 12;

	// 한 번에 보여줄 페이지 번호 개수
	private static final int PAGE_BLOCK_SIZE = 5;

	// KMDb API 호출 전담 서비스
	private MovieApiService movieApiService = new MovieApiService();

	private MovieService movieService = new MovieService();

	// 1. 검색어 정리
	// 2. 검색어 없으면 빈 결과
	// 3. DB 먼저 검색
	// 4. DB에 있으면 DB 결과 반환
	// 5. DB에 없으면 API 호출
	// 6. API 결과 DB 저장
	// 7. API 결과 반환

	// 영화 검색 화면에 필요한 최종 결과를 만드는 메서드
	public MovieSearchResultDTO search(String query, int currentPage) {
		MovieSearchResultDTO result = new MovieSearchResultDTO();

		query = nvl(query).trim();

		result.setQuery(query);
		result.setHasQuery(!query.isEmpty());

		if (currentPage < 1) {
			currentPage = 1;
		}

		// 검색어가 없으면 빈 결과 반환
		if (query.isEmpty()) {

			PagingDTO emptyPaging = new PagingDTO(currentPage, PAGE_SIZE, PAGE_BLOCK_SIZE, 0);

			result.setMovieItems(new ArrayList<MovieListItemDTO>());
			result.setPaging(emptyPaging);
			result.setHasResult(false);

			return result;
		}

		/*
		 * 1. 먼저 DB에서 검색한다. 예: 예전에 "괴물" 검색 결과로 "도쿄!"가 DB에 저장되어 있다면, 나중에 "도쿄"를 검색했을 때
		 * API를 먼저 호출하지 않고 DB 결과를 보여준다.
		 */
		ArrayList<MovieDTO> savedMovies = movieService.searchSavedMoviesByTitle(query);

		if (savedMovies != null && !savedMovies.isEmpty()) {
			PagingDTO paging = new PagingDTO(currentPage, PAGE_SIZE, PAGE_BLOCK_SIZE, savedMovies.size());

			// DB 결과도 한 페이지에 12개만 보여주기 위해 자른다.
			ArrayList<MovieDTO> pageMovies = sliceMovies(savedMovies, currentPage);

			ArrayList<MovieListItemDTO> movieItems = toMovieListItems(pageMovies);

			result.setMovieItems(movieItems);
			result.setPaging(paging);
			result.setHasResult(!movieItems.isEmpty());

			return result;
		}

		/*
		 * 2. DB에 검색 결과가 없을 때만 KMDb API를 호출한다.
		 */

		MovieApiSearchResultDTO apiResult = movieApiService.searchMovies(query, currentPage, PAGE_SIZE);

//		api 장애, 파싱 실패 시 검색화면이 500으로 터지는 거 방지
		if(apiResult == null || apiResult.getMovies() == null) {
			PagingDTO paging = new PagingDTO(currentPage, PAGE_SIZE, PAGE_BLOCK_SIZE, 0);
			result.setMovieItems(new ArrayList<>());
			result.setPaging(paging);
			result.setHasResult(false);
			return result;
		}
		PagingDTO paging = new PagingDTO(currentPage, PAGE_SIZE, PAGE_BLOCK_SIZE, apiResult.getTotalCount());

		if (paging.getTotalPage() > 0 && currentPage > paging.getTotalPage()) {
		    int lastPage = paging.getTotalPage();

		    apiResult = movieApiService.searchMovies(query, lastPage, PAGE_SIZE);

		    if (apiResult == null || apiResult.getMovies() == null) {
		        paging = new PagingDTO(lastPage, PAGE_SIZE, PAGE_BLOCK_SIZE, 0);
		        result.setMovieItems(new ArrayList<>());
		        result.setPaging(paging);
		        result.setHasResult(false);
		        return result;
		    }

		    paging = new PagingDTO(lastPage, PAGE_SIZE, PAGE_BLOCK_SIZE, apiResult.getTotalCount());
		}

		/*
		 * 3. API에서 받은 검색 결과를 DB에 저장한다. 이미 저장된 영화는 saveMovieIfNotExists() 안에서 중복 저장하지
		 * 않는다.
		 */
		for (MovieDTO movie : apiResult.getMovies()) {
			movieService.saveMovieIfNotExists(movie);
		}

		ArrayList<MovieListItemDTO> movieItems = toMovieListItems(apiResult.getMovies());

		result.setMovieItems(movieItems);
		result.setPaging(paging);
		result.setHasResult(!movieItems.isEmpty());

		return result;
	}

	// MovieDTO 목록을 movieList.jsp 출력용 DTO 목록으로 변환하는 메서드
	private ArrayList<MovieListItemDTO> toMovieListItems(ArrayList<MovieDTO> movies) {
		ArrayList<MovieListItemDTO> items = new ArrayList<MovieListItemDTO>();

		if (movies == null) {
			return items;
		}

		for (MovieDTO movie : movies) {
			items.add(toMovieListItem(movie));
		}

		return items;
	}

	// DB 검색 결과도 현재 페이지에 해당하는 12개만 잘라서 반환하는 메서드
	private ArrayList<MovieDTO> sliceMovies(ArrayList<MovieDTO> movies, int currentPage) {
		ArrayList<MovieDTO> pageMovies = new ArrayList<MovieDTO>();

		if (movies == null || movies.isEmpty()) {
			return pageMovies;
		}

		int startIndex = (currentPage - 1) * PAGE_SIZE;
		int endIndex = Math.min(startIndex + PAGE_SIZE, movies.size());

		if (startIndex >= movies.size()) {
			return pageMovies;
		}

		for (int i = startIndex; i < endIndex; i++) {
			pageMovies.add(movies.get(i));
		}

		return pageMovies;
	}

	// MovieDTO 1개를 movieList.jsp 출력용 DTO 1개로 변환하는 메서드
	private MovieListItemDTO toMovieListItem(MovieDTO movie) {
		MovieListItemDTO item = new MovieListItemDTO();

		item.setKmdbMovieId(nvl(movie.getKmdbMovieId()));
		item.setKmdbMovieSeq(nvl(movie.getKmdbMovieSeq()));
		item.setTitle(nvl(movie.getTitle()));
		item.setDirectorNm(nvl(movie.getDirectorNm()));
		item.setDisplayReleaseDate(formatReleaseDate(movie.getReleaseDate()));
		item.setPosterUrl(nvl(movie.getPosterUrl()));

		return item;
	}

	// null 문자열을 빈 문자열로 바꾸는 메서드
	private String nvl(String value) {
		return value == null ? "" : value;
	}

	// 화면 출력용 개봉일을 만드는 메서드
	private String formatReleaseDate(String releaseDate) {
		releaseDate = nvl(releaseDate).trim();

		if (releaseDate.length() == 8) {
			return releaseDate.substring(0, 4) + "." + releaseDate.substring(4, 6) + "." + releaseDate.substring(6, 8);
		}

		return releaseDate;
	}

}
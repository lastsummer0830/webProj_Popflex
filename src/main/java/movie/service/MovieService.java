package movie.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import common.DBUtil;
import movie.dao.MovieActorDAO;
import movie.dao.MovieDAO;
import movie.dao.MovieKeywordDAO;
import movie.dto.MovieActorDTO;
import movie.dto.MovieDTO;
import movie.dto.MovieKeywordDTO;

public class MovieService {

	private MovieApiService apiService = new MovieApiService();
	private MovieActorDAO actorDAO = new MovieActorDAO();
	private MovieKeywordDAO keywordDAO = new MovieKeywordDAO();
	private MovieDAO movieDAO = new MovieDAO();

    // 1. 영화 저장
    // 새 영화라고 확정된 경우 사용
    // 반환값: 저장 성공 시 MOVIE_ID, 실패 시 0
    public int saveMovie(MovieDTO movie) {
        return saveMovieIfNotExists(movie);
    }

    // 3. 중복 저장 방지 저장
    // 이미 있으면 기존 MOVIE_ID 반환
    // 없으면 새로 저장 후 새 MOVIE_ID 반환
    // 반환값: MOVIE_ID, 실패 시 0
    public int saveMovieIfNotExists(MovieDTO movie) {

    if (movie == null) {
        return 0;
    }

    if (movie.getKmdbMovieId() == null || movie.getKmdbMovieId().trim().isEmpty()
            || movie.getKmdbMovieSeq() == null || movie.getKmdbMovieSeq().trim().isEmpty()) {
        return 0;
    }

    Connection con = null;

    try {
        con = DBUtil.getConnection();

        // 여기부터 직접 commit하지 않겠다는 뜻
        con.setAutoCommit(false);

        // 1. 같은 KMDb movieId + movieSeq가 이미 있는지 확인
        MovieDTO savedMovie = movieDAO.findByKmdbIdAndSeq(
                con,
                movie.getKmdbMovieId(),
                movie.getKmdbMovieSeq()
        );

        if (savedMovie != null) {
            con.commit();
            return savedMovie.getMovieId();
        }

        // 2. MOVIE 저장
        int movieId = movieDAO.insertMovieAndReturnId(con, movie);

        if (movieId <= 0) {
            throw new SQLException("MOVIE 저장 실패");
        }

        // 3. MOVIE_ACTOR 저장
        actorDAO.insertActors(con, movieId, movie.getActorNm());

        // 4. MOVIE_KEYWORD 저장
        keywordDAO.insertKeywords(con, movieId, movie.getKeywords());

        // 5. 전부 성공하면 commit
        con.commit();

        return movieId;

    } catch (SQLException e) {
        e.printStackTrace();

        // 중간에 하나라도 실패하면 전부 취소
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }

        return 0;

    } finally {
        if (con != null) {
            try {
                // 커넥션 풀/다른 코드 영향 방지용
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
    
    // KMDb 식별자 기준 영화 조회
    public MovieDTO findByKmdbIdAndSeq(String kmdbMovieId, String kmdbMovieSeq) {
        return movieDAO.findByKmdbIdAndSeq(kmdbMovieId, kmdbMovieSeq);
    }
    
    // DB에 저장된 영화 중 제목으로 검색
    public ArrayList<MovieDTO> searchSavedMoviesByTitle(String query) {
        return movieDAO.searchByTitle(query);
    }
    
    
    // 영화 상세 조회
    // DB에 있으면 DB에서 조회
    // DB에 없으면 KMDb API에서 조회 후 MOVIE에 저장하고 다시 조회
    public MovieDTO getOrSaveMovieDetail(String kmdbMovieId, String kmdbMovieSeq) {
     if (kmdbMovieId == null || kmdbMovieId.trim().isEmpty()
             || kmdbMovieSeq == null || kmdbMovieSeq.trim().isEmpty()) {
         return null;
     }

     kmdbMovieId = kmdbMovieId.trim();
     kmdbMovieSeq = kmdbMovieSeq.trim();

     // 1. 이미 DB에 저장된 영화인지 확인
     MovieDTO savedMovie = movieDAO.findByKmdbIdAndSeq(kmdbMovieId, kmdbMovieSeq);

     // 2. 이미 있으면 MOVIE 조회 후 배우/키워드도 채워서 반환
     if (savedMovie != null) {
    	 MovieDTO movie = movieDAO.findByMovieId(savedMovie.getMovieId());
    	 fillActorAndKeyword(movie);
         return movie;
     }

     // 3. DB에 없으면 API에서 상세 조회
     MovieDTO apiMovie = apiService.findMovieDetail(kmdbMovieId, kmdbMovieSeq);
     
     if (apiMovie == null) {
         return null;
     }

     // 4. MOVIE 저장 후 movie_id 확보
     int movieId = saveMovieIfNotExists(apiMovie);

     if (movieId == 0) {
         return null;
     }

     // 5. MOVIE_ID 기준으로 다시 조회
     MovieDTO movie = movieDAO.findByMovieId(movieId);
     fillActorAndKeyword(movie);

     return movie;
    }
    
    // MOVIE_ACTOR, MOVIE_KEYWORD 테이블에서 조회한 값을 MovieDTO의 문자열 필드에 채우는 메서드
    private void fillActorAndKeyword(MovieDTO movie) {
    	if(movie == null || movie.getMovieId() <= 0) {
    		return;
    	}
    	
    	ArrayList<MovieActorDTO> actorList = actorDAO.findActorsByMovieId(movie.getMovieId());
    	ArrayList<MovieKeywordDTO> keywordList = keywordDAO.findKeywordsByMovieId(movie.getMovieId());

        movie.setActorNm(toActorNames(actorList));
        movie.setKeywords(toKeywordNames(keywordList));
    	
    }
    
    // 배우 DTO 목록을 "배우1, 배우2, 배우3" 문자열로 변환
    private String toActorNames(ArrayList<MovieActorDTO> actorList) {
        if (actorList == null || actorList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (MovieActorDTO actor : actorList) {
            if (actor == null || actor.getActorName() == null || actor.getActorName().trim().isEmpty()) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(actor.getActorName().trim());
        }

        return sb.toString();
    }
    
 // 키워드 DTO 목록을 "키워드1, 키워드2, 키워드3" 문자열로 변환
    private String toKeywordNames(ArrayList<MovieKeywordDTO> keywordList) {
        if (keywordList == null || keywordList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (MovieKeywordDTO keyword : keywordList) {
            if (keyword == null || keyword.getKeywordName() == null || keyword.getKeywordName().trim().isEmpty()) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(keyword.getKeywordName().trim());
        }

        return sb.toString();
    }
    
  
}
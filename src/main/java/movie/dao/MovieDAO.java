package movie.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import common.DBUtil;
import movie.dto.MovieDTO;

public class MovieDAO {

	public Connection dbcon() {
		try {
			return DBUtil.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("db 연결 실패: DBUtil의 URL/USER/PASSWORD를 확인하세요.");
		}
	}

	// 문자열 날짜 yyyyMMdd -> java.sql.Date 변환
	private Date toSqlDate(String releaseDate) {
		if (releaseDate == null || releaseDate.trim().isEmpty()) {
			return null;
		}

		try {
			// KMDb 날짜가 20251024 형태라고 가정
			if (releaseDate.length() == 8) {
				String yyyy = releaseDate.substring(0, 4);
				String mm = releaseDate.substring(4, 6);
				String dd = releaseDate.substring(6, 8);
				return Date.valueOf(yyyy + "-" + mm + "-" + dd);
			}

			// 이미 2025-10-24 형태면 그대로 처리
			if (releaseDate.length() == 10) {
				return Date.valueOf(releaseDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// runtime String -> int 변환
	private Integer toRuntimeNumber(String runtime) {
		if (runtime == null || runtime.trim().isEmpty()) {
			return null;
		}

		try {
			return Integer.parseInt(runtime.replaceAll("[^0-9]", ""));
		} catch (Exception e) {
			return null;
		}
	}

	// KMDb movieId + movieSeq 기준으로 이미 저장된 영화인지 조회
	public MovieDTO findByKmdbIdAndSeq(String kmdbMovieId, String kmdbMovieSeq) {
		Connection con = dbcon();
		PreparedStatement pst = null;
		ResultSet rs = null;

		MovieDTO movie = null;

		String sql = "SELECT MOVIE_ID, KMDB_MOVIE_ID, KMDB_MOVIE_SEQ, DOCID, TITLE, "
				+ "DIRECTOR_NAME, COMPANY, PLOT, RUNTIME, RATING, GENRE, RATING_GRADE, "
				+ "RELEASE_DATE, POSTER_URL, VOD_URL " + "FROM MOVIE " + "WHERE KMDB_MOVIE_ID = ? "
				+ "AND KMDB_MOVIE_SEQ = ?";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, kmdbMovieId);
			pst.setString(2, kmdbMovieSeq);

			rs = pst.executeQuery();

			if (rs.next()) {
				movie = new MovieDTO();

				movie.setMovieId(rs.getInt("MOVIE_ID"));
				movie.setKmdbMovieId(rs.getString("KMDB_MOVIE_ID"));
				movie.setKmdbMovieSeq(rs.getString("KMDB_MOVIE_SEQ"));
				movie.setDocid(rs.getString("DOCID"));
				movie.setTitle(rs.getString("TITLE"));
				movie.setDirectorNm(rs.getString("DIRECTOR_NAME"));
				movie.setCompany(rs.getString("COMPANY"));
				movie.setPlot(rs.getString("PLOT"));
				movie.setRating(rs.getString("RATING"));
				movie.setGenre(rs.getString("GENRE"));
				movie.setRatingGrade(rs.getString("RATING_GRADE"));

				int runtime = rs.getInt("RUNTIME");
				if (!rs.wasNull()) {
					movie.setRuntime(String.valueOf(runtime));
				}

				Date releaseDate = rs.getDate("RELEASE_DATE");
				if (releaseDate != null) {
					movie.setReleaseDate(String.valueOf(releaseDate));
				}

				movie.setPosterUrl(rs.getString("POSTER_URL"));
				movie.setVodUrl(rs.getString("VOD_URL"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, pst, con);
		}

		return movie;
	}

	// 영화 DB 저장 후 생성된 MOVIE_ID 반환
	public int insertMovieAndReturnId(MovieDTO movie) {

		if (movie == null) {
			return 0;
		}

		if (movie.getKmdbMovieId() == null || movie.getKmdbMovieId().trim().isEmpty() || movie.getKmdbMovieSeq() == null
				|| movie.getKmdbMovieSeq().trim().isEmpty()) {
			return 0;
		}

		Connection con = dbcon();
		PreparedStatement pst = null;
		ResultSet rs = null;

		int movieId = 0;

		String sql = "INSERT INTO MOVIE (" + "KMDB_MOVIE_ID, KMDB_MOVIE_SEQ, DOCID, TITLE, DIRECTOR_NAME, COMPANY, "
				+ "PLOT, RUNTIME, RATING, GENRE, RATING_GRADE, RELEASE_DATE, " + "POSTER_URL, VOD_URL" + ") VALUES ("
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" + ")";

		try {
			String[] columns = { "MOVIE_ID" };
			pst = con.prepareStatement(sql, columns);

			pst.setString(1, movie.getKmdbMovieId());
			pst.setString(2, movie.getKmdbMovieSeq());
			pst.setString(3, movie.getDocid());
			pst.setString(4, movie.getTitle());
			pst.setString(5, movie.getDirectorNm());
			pst.setString(6, movie.getCompany());
			pst.setString(7, movie.getPlot());

			Integer runtime = toRuntimeNumber(movie.getRuntime());
			if (runtime == null) {
				pst.setNull(8, java.sql.Types.NUMERIC);
			} else {
				pst.setInt(8, runtime);
			}

			pst.setString(9, movie.getRating());
			pst.setString(10, movie.getGenre());
			pst.setString(11, movie.getRatingGrade());

			Date releaseDate = toSqlDate(movie.getReleaseDate());
			if (releaseDate == null) {
				pst.setNull(12, java.sql.Types.DATE);
			} else {
				pst.setDate(12, releaseDate);
			}

			pst.setString(13, movie.getPosterUrl());
			pst.setString(14, movie.getVodUrl());

			int result = pst.executeUpdate();

			if (result > 0) {
				rs = pst.getGeneratedKeys();

				if (rs.next()) {
					movieId = rs.getInt(1);
					movie.setMovieId(movieId);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, pst, con);
		}

		return movieId;
	}

	// DB movie_id 기준으로 영화 상세정보 조회
	public MovieDTO findByMovieId(int movieId) {
		Connection con = dbcon();
		PreparedStatement pst = null;
		ResultSet rs = null;

		MovieDTO movie = null;

		String sql = "SELECT MOVIE_ID, KMDB_MOVIE_ID, KMDB_MOVIE_SEQ, DOCID, TITLE, "
				+ "DIRECTOR_NAME, COMPANY, PLOT, RUNTIME, RATING, GENRE, RATING_GRADE, "
				+ "RELEASE_DATE, POSTER_URL, VOD_URL " + "FROM MOVIE " + "WHERE MOVIE_ID = ?";

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, movieId);

			rs = pst.executeQuery();

			if (rs.next()) {
				movie = new MovieDTO();

				movie.setMovieId(rs.getInt("MOVIE_ID"));
				movie.setKmdbMovieId(rs.getString("KMDB_MOVIE_ID"));
				movie.setKmdbMovieSeq(rs.getString("KMDB_MOVIE_SEQ"));
				movie.setDocid(rs.getString("DOCID"));
				movie.setTitle(rs.getString("TITLE"));
				movie.setDirectorNm(rs.getString("DIRECTOR_NAME"));
				movie.setCompany(rs.getString("COMPANY"));
				movie.setPlot(rs.getString("PLOT"));

				int runtime = rs.getInt("RUNTIME");
				if (!rs.wasNull()) {
					movie.setRuntime(String.valueOf(runtime));
				}

				movie.setRating(rs.getString("RATING"));
				movie.setGenre(rs.getString("GENRE"));
				movie.setRatingGrade(rs.getString("RATING_GRADE"));

				Date releaseDate = rs.getDate("RELEASE_DATE");
				if (releaseDate != null) {
					movie.setReleaseDate(String.valueOf(releaseDate));
				}

				movie.setPosterUrl(rs.getString("POSTER_URL"));
				movie.setVodUrl(rs.getString("VOD_URL"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, pst, con);
		}

		return movie;
	}

	// 외부에서 받은 Connection으로 MOVIE_ID 기준 영화 조회
	// 주의: con은 호출한 쪽에서 관리하므로 여기서 닫지 않는다.
	public MovieDTO getMovieById(Connection con, int movieId) {
		MovieDTO dto = null;

		String sql = "SELECT MOVIE_ID, KMDB_MOVIE_ID, KMDB_MOVIE_SEQ, DOCID, TITLE, "
				+ "DIRECTOR_NAME, COMPANY, PLOT, RUNTIME, RATING, GENRE, RATING_GRADE, "
				+ "RELEASE_DATE, POSTER_URL, VOD_URL " + "FROM MOVIE " + "WHERE MOVIE_ID = ?";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, movieId);
			rs = pst.executeQuery();

			if (rs.next()) {
				dto = new MovieDTO();

				dto.setMovieId(rs.getInt("MOVIE_ID"));
				dto.setKmdbMovieId(rs.getString("KMDB_MOVIE_ID"));
				dto.setKmdbMovieSeq(rs.getString("KMDB_MOVIE_SEQ"));
				dto.setDocid(rs.getString("DOCID"));
				dto.setTitle(rs.getString("TITLE"));
				dto.setDirectorNm(rs.getString("DIRECTOR_NAME"));
				dto.setCompany(rs.getString("COMPANY"));
				dto.setPlot(rs.getString("PLOT"));

				int runtime = rs.getInt("RUNTIME");
				if (!rs.wasNull()) {
					dto.setRuntime(String.valueOf(runtime));
				}

				dto.setRating(rs.getString("RATING"));
				dto.setGenre(rs.getString("GENRE"));
				dto.setRatingGrade(rs.getString("RATING_GRADE"));

				Date releaseDate = rs.getDate("RELEASE_DATE");
				if (releaseDate != null) {
					dto.setReleaseDate(String.valueOf(releaseDate));
				}

				dto.setPosterUrl(rs.getString("POSTER_URL"));
				dto.setVodUrl(rs.getString("VOD_URL"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pst != null)
					pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return dto;
	}

	public ArrayList<MovieDTO> searchByTitle(String query) {
		ArrayList<MovieDTO> list = new ArrayList<>();

		if (query == null || query.trim().isEmpty()) {
			return list;
		}

		Connection con = dbcon();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT MOVIE_ID, KMDB_MOVIE_ID, KMDB_MOVIE_SEQ, DOCID, TITLE, "
				+ "DIRECTOR_NAME, COMPANY, PLOT, RUNTIME, RATING, GENRE, RATING_GRADE, "
				+ "RELEASE_DATE, POSTER_URL, VOD_URL " + "FROM MOVIE "
				+ "WHERE REPLACE(TITLE, ' ', '') LIKE '%' || REPLACE(?, ' ', '') || '%' " + "ORDER BY MOVIE_ID DESC";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, query.trim());

			rs = pst.executeQuery();

			while (rs.next()) {
				MovieDTO movie = new MovieDTO();

				movie.setMovieId(rs.getInt("MOVIE_ID"));
				movie.setKmdbMovieId(rs.getString("KMDB_MOVIE_ID"));
				movie.setKmdbMovieSeq(rs.getString("KMDB_MOVIE_SEQ"));
				movie.setDocid(rs.getString("DOCID"));
				movie.setTitle(rs.getString("TITLE"));
				movie.setDirectorNm(rs.getString("DIRECTOR_NAME"));
				movie.setCompany(rs.getString("COMPANY"));
				movie.setPlot(rs.getString("PLOT"));

				int runtime = rs.getInt("RUNTIME");
				if (!rs.wasNull()) {
					movie.setRuntime(String.valueOf(runtime));
				}

				movie.setRating(rs.getString("RATING"));
				movie.setGenre(rs.getString("GENRE"));
				movie.setRatingGrade(rs.getString("RATING_GRADE"));

				Date releaseDate = rs.getDate("RELEASE_DATE");
				if (releaseDate != null) {
					movie.setReleaseDate(String.valueOf(releaseDate));
				}

				movie.setPosterUrl(rs.getString("POSTER_URL"));
				movie.setVodUrl(rs.getString("VOD_URL"));

				list.add(movie);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(rs, pst, con);
		}

		return list;
	}

	// 트랜잭션용: 외부에서 받은 Connection으로 Movie 저장
	// 주의 : con은 여기서 닫지 않음
	public int insertMovieAndReturnId(Connection con, MovieDTO movie) throws SQLException{
		
		if(movie == null) {
			return 0;
		}
		
		if(movie.getKmdbMovieId() == null || movie.getKmdbMovieId().trim().isEmpty() 
			|| movie.getKmdbMovieSeq() == null || movie.getKmdbMovieSeq().trim().isEmpty()){
			return 0;
		}
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		int movieId = 0;
		
		String sql = "INSERT INTO MOVIE("
				+ "KMDB_MOVIE_ID, KMDB_MOVIE_SEQ, DOCID, TITLE, DIRECTOR_NAME, COMPANY, "
				+ "PLOT, RUNTIME, RATING, GENRE, RATING_GRADE, RELEASE_DATE, "
				+ "POSTER_URL, VOD_URL) "
				+ "VALUES ("
				+ "?, ?, ?, ?, ?, "
				+ "?, ?, ?, ?, ?, "
				+ "?, ?, ?, ?) ";
		
		try {
			String[] columns = {"MOVIE_ID"};
			pst = con.prepareStatement(sql, columns);
			
			pst.setString(1, movie.getKmdbMovieId());
			pst.setString(2, movie.getKmdbMovieSeq());
			pst.setString(3, movie.getDocid());
			pst.setString(4, movie.getTitle());
			pst.setString(5, movie.getDirectorNm());
			pst.setString(6, movie.getCompany());
			pst.setString(7, movie.getPlot());
			
			Integer runtime = toRuntimeNumber(movie.getRuntime());
			if(runtime == null) {
				pst.setNull(8, java.sql.Types.NUMERIC);
			} else {
				pst.setInt(8, runtime);
			}
			
			pst.setString(9, movie.getRating());
			pst.setString(10, movie.getGenre());
			pst.setString(11, movie.getRatingGrade());
			
			Date releaseDate = toSqlDate(movie.getReleaseDate());
			if(releaseDate == null) {
				pst.setNull(12, java.sql.Types.DATE);
			} else{
				pst.setDate(12,releaseDate);
			}
			
			pst.setString(13, movie.getPosterUrl());
			pst.setString(14,  movie.getVodUrl());
			
			int result = pst.executeUpdate();
			
			if(result >0) {
				rs = pst.getGeneratedKeys();
				
				if(rs.next()) {
					movieId = rs.getInt(1);
					movie.setMovieId(movieId);
				}
			}
					
		} finally {
			if(rs != null) rs.close();
			if(pst != null) pst.close();
		}
		
		return movieId;
		
	}
	
	// 트랜잭션용: 외부 Connection으로 KMDb movieId + movieSeq 중복 조회
	public MovieDTO findByKmdbIdAndSeq(Connection con, String kmdbMovieId, String kmdbMovieSeq) throws SQLException {
	    PreparedStatement pst = null;
	    ResultSet rs = null;

	    MovieDTO movie = null;

	    String sql = "SELECT MOVIE_ID, KMDB_MOVIE_ID, KMDB_MOVIE_SEQ, DOCID, TITLE, "
	            + "DIRECTOR_NAME, COMPANY, PLOT, RUNTIME, RATING, GENRE, RATING_GRADE, "
	            + "RELEASE_DATE, POSTER_URL, VOD_URL "
	            + "FROM MOVIE "
	            + "WHERE KMDB_MOVIE_ID = ? "
	            + "AND KMDB_MOVIE_SEQ = ?";

	    try {
	        pst = con.prepareStatement(sql);
	        pst.setString(1, kmdbMovieId);
	        pst.setString(2, kmdbMovieSeq);

	        rs = pst.executeQuery();

	        if (rs.next()) {
	            movie = new MovieDTO();

	            movie.setMovieId(rs.getInt("MOVIE_ID"));
	            movie.setKmdbMovieId(rs.getString("KMDB_MOVIE_ID"));
	            movie.setKmdbMovieSeq(rs.getString("KMDB_MOVIE_SEQ"));
	            movie.setDocid(rs.getString("DOCID"));
	            movie.setTitle(rs.getString("TITLE"));
	            movie.setDirectorNm(rs.getString("DIRECTOR_NAME"));
	            movie.setCompany(rs.getString("COMPANY"));
	            movie.setPlot(rs.getString("PLOT"));
	            movie.setRating(rs.getString("RATING"));
	            movie.setGenre(rs.getString("GENRE"));
	            movie.setRatingGrade(rs.getString("RATING_GRADE"));

	            int runtime = rs.getInt("RUNTIME");
	            if (!rs.wasNull()) {
	                movie.setRuntime(String.valueOf(runtime));
	            }

	            Date releaseDate = rs.getDate("RELEASE_DATE");
	            if (releaseDate != null) {
	                movie.setReleaseDate(String.valueOf(releaseDate));
	            }

	            movie.setPosterUrl(rs.getString("POSTER_URL"));
	            movie.setVodUrl(rs.getString("VOD_URL"));
	        }

	    } finally {
	        if (rs != null) rs.close();
	        if (pst != null) pst.close();
	    }

	    return movie;
	}

	private void close(ResultSet rs, PreparedStatement pst, Connection con) {
		DBUtil.close(rs, pst, con);
	}
}
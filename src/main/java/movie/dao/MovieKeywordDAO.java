package movie.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import common.DBUtil;
import movie.dto.MovieKeywordDTO;

public class MovieKeywordDAO {

	public Connection dbcon() {
		try {
			return DBUtil.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("db 연결 실패: DBUtil의 URL/USER/PASSWORD를 확인하세요.");
		}
	}

	public int insertKeywords(int movieId, String keywords) {

		if (movieId <= 0) {
			return 0;
		}
		if (keywords == null || keywords.trim().isEmpty()) {
			return 0;
		}

		Connection con = dbcon();
		PreparedStatement pst = null;

		int insertCount = 0;

		String sql = "insert into MOVIE_KEYWORD (movie_id, keyword_name) values (?,?)";

		try {
			String[] keywordArr = keywords.split("\\s*,\\s*");

//    		순서를 유지하면서 중복 제거
			Set<String> keywordSet = new LinkedHashSet<>();

			for (String keyword : keywordArr) {
				keyword = keyword.trim();

				if (keyword.isEmpty()) {
					continue;
				}

				keywordSet.add(keyword);
			}

			pst = con.prepareStatement(sql);

			for (String keyword : keywordSet) {

				pst.setInt(1, movieId);
				pst.setString(2, keyword);

				int result = pst.executeUpdate();

				if (result > 0) {
					insertCount++;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, pst, con);
		}
		return insertCount;

	}

	public ArrayList<MovieKeywordDTO> findKeywordsByMovieId(int movieId) {

		ArrayList<MovieKeywordDTO> keywordList = new ArrayList<>();

		if (movieId <= 0) {
			return keywordList;
		}

		Connection con = dbcon();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT MOVIE_KEYWORD_ID, MOVIE_ID, KEYWORD_NAME " + "FROM MOVIE_KEYWORD " + "WHERE MOVIE_ID = ? "
				+ "ORDER BY MOVIE_KEYWORD_ID ASC";

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, movieId);

			rs = pst.executeQuery();

			while (rs.next()) {
				MovieKeywordDTO keyword = new MovieKeywordDTO();

				keyword.setMovieKeywordId(rs.getInt("MOVIE_KEYWORD_ID"));
				keyword.setMovieId(rs.getInt("MOVIE_ID"));
				keyword.setKeywordName(rs.getString("KEYWORD_NAME"));

				keywordList.add(keyword);

			}
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			close(rs, pst, con);
		}
		return keywordList;
	}
	
	// 트랜잭션용: 외부 Connection으로 키워드 저장
	// 주의: con은 여기서 닫지 않는다.
	public int insertKeywords(Connection con, int movieId, String keywords) throws SQLException {

	    if (movieId <= 0) {
	        return 0;
	    }

	    if (keywords == null || keywords.trim().isEmpty()) {
	        return 0;
	    }

	    PreparedStatement pst = null;
	    int insertCount = 0;

	    String sql = "insert into MOVIE_KEYWORD (movie_id, keyword_name) values (?,?)";

	    try {
	        String[] keywordArr = keywords.split("\\s*,\\s*");

	        Set<String> keywordSet = new LinkedHashSet<>();

	        for (String keyword : keywordArr) {
	            keyword = keyword.trim();

	            if (keyword.isEmpty()) {
	                continue;
	            }

	            keywordSet.add(keyword);
	        }

	        pst = con.prepareStatement(sql);

	        for (String keyword : keywordSet) {
	            pst.setInt(1, movieId);
	            pst.setString(2, keyword);

	            int result = pst.executeUpdate();

	            if (result > 0) {
	                insertCount++;
	            }
	        }

	    } finally {
	        if (pst != null) {
	            pst.close();
	        }
	    }

	    return insertCount;
	}

	private void close(ResultSet rs, PreparedStatement pst, Connection con) {
		DBUtil.close(rs, pst, con);
	}

}

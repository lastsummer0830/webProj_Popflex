package movie.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import common.DBUtil;
import movie.dto.MovieActorDTO;

public class MovieActorDAO {

	public Connection dbcon() {
		try {
			return DBUtil.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("db 연결 실패: DBUtil의 URL/USER/PASSWORD를 확인하세요.");
		}
	}

	// 배우 여러 명 저장
	// actorNm 예시: "최민식, 김고은, 유해진"
	public int insertActors(int movieId, String actorNm) {

		if (movieId <= 0) {
			return 0;
		}

		if (actorNm == null || actorNm.trim().isEmpty()) {
			return 0;
		}

		Connection con = dbcon();
		PreparedStatement pst = null;

		int insertCount = 0;

		String sql = "INSERT INTO MOVIE_ACTOR(MOVIE_ID, ACTOR_NAME, SORT_ORDER) " + "VALUES(?, ?, ?)";

		try {
			// 콤마 기준 분리
			String[] actors = actorNm.split("\\s*,\\s*");

//          순서를 유지하면서 중복 제거
			Set<String> actorSet = new LinkedHashSet<>();

			for (String actorName : actors) {
				actorName = actorName.trim();

				if (actorName.isEmpty()) {
					continue;
				}

				actorSet.add(actorName);
			}

			pst = con.prepareStatement(sql);

			int sortOrder = 1;

			for (String actorName : actorSet) {

				pst.setInt(1, movieId);
				pst.setString(2, actorName);
				pst.setInt(3, sortOrder);

				int result = pst.executeUpdate();

				if (result > 0) {
					insertCount++;
					sortOrder++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, pst, con);
		}

		return insertCount;
	}

	// movie_id 기준 배우 목록 조회
	public ArrayList<MovieActorDTO> findActorsByMovieId(int movieId) {
		ArrayList<MovieActorDTO> actorList = new ArrayList<>();

		if (movieId <= 0) {
			return actorList;
		}

		Connection con = dbcon();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT MOVIE_ACTOR_ID, MOVIE_ID, ACTOR_NAME, SORT_ORDER " + "FROM MOVIE_ACTOR "
				+ "WHERE MOVIE_ID = ? " + "ORDER BY SORT_ORDER ASC";

		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, movieId);

			rs = pst.executeQuery();

			while (rs.next()) {
				MovieActorDTO actor = new MovieActorDTO();

				actor.setMovieActorId(rs.getInt("MOVIE_ACTOR_ID"));
				actor.setMovieId(rs.getInt("MOVIE_ID"));
				actor.setActorName(rs.getString("ACTOR_NAME"));
				actor.setSortOrder(rs.getInt("SORT_ORDER"));

				actorList.add(actor);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, pst, con);
		}

		return actorList;
	}

	private void close(ResultSet rs, PreparedStatement pst, Connection con) {
		DBUtil.close(rs, pst, con);
	}
	
	// 트랜잭션용: 외부 Connection으로 배우 저장
	// 주의: con은 여기서 닫지 않는다.
	public int insertActors(Connection con, int movieId, String actorNm) throws SQLException {

	    if (movieId <= 0) {
	        return 0;
	    }

	    if (actorNm == null || actorNm.trim().isEmpty()) {
	        return 0;
	    }

	    PreparedStatement pst = null;
	    int insertCount = 0;

	    String sql = "INSERT INTO MOVIE_ACTOR(MOVIE_ID, ACTOR_NAME, SORT_ORDER) "
	            + "VALUES(?, ?, ?)";

	    try {
	        String[] actors = actorNm.split("\\s*,\\s*");

	        Set<String> actorSet = new LinkedHashSet<>();

	        for (String actorName : actors) {
	            actorName = actorName.trim();

	            if (actorName.isEmpty()) {
	                continue;
	            }

	            actorSet.add(actorName);
	        }

	        pst = con.prepareStatement(sql);

	        int sortOrder = 1;

	        for (String actorName : actorSet) {
	            pst.setInt(1, movieId);
	            pst.setString(2, actorName);
	            pst.setInt(3, sortOrder);

	            int result = pst.executeUpdate();

	            if (result > 0) {
	                insertCount++;
	                sortOrder++;
	            }
	        }

	    } finally {
	        if (pst != null) {
	            pst.close();
	        }
	    }

	    return insertCount;
	}

}
package screen.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import screen.dto.TheaterDTO;

// theater 테이블 조회를 담당하는 dao
// 극장 선택 목록과 상영관 필터 기능에서 사용 가능
public class TheaterDAO {

//	ResultSet 현재 행 1개를 TheaterDTO 객체로 변환
	private TheaterDTO mapRowToTheaterDTO(ResultSet rs) throws SQLException {
		TheaterDTO dto = new TheaterDTO();

		dto.setTheaterId(rs.getInt("theater_id"));
		dto.setTheaterName(rs.getString("theater_name"));
		dto.setLocation(rs.getString("location"));

		return dto;
	}

	/*
	 * [극장 목록 조회] theater 테이블의 전체 극장 목록을 조회한다. theater/list.do json 응답을 만들 때 사용한다.
	 * 관리자 상영 등록 화면에서 극장 선택 목록으로 사용할 수 있다.
	 */
	public ArrayList<TheaterDTO> getTheaterList(Connection con) {
		ArrayList<TheaterDTO> list = new ArrayList<TheaterDTO>();
		String sql = "select theater_id, theater_name, location "
				+ "from theater "
				+ "order by theater_name asc";

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();

			while (rs.next()) {
				list.add(mapRowToTheaterDTO(rs));
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
		return list;

	}
	
/*
 * [극장 상세 조회]
 * theater_id 기준으로 극장 1건을 조회한다.
 * 존재하지 않는 theaterId가 들어왔는지 검증 시 사용 가능	
*/
	public TheaterDTO getTheaterById(Connection con, int theaterId) {
		TheaterDTO dto = null;
		String sql = "select theater_id, theater_name, location "
				+ "from theater "
				+ "where theater_id = ?";
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		
		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1,theaterId);
			rs = pst.executeQuery();
			
			if(rs.next()) {
				dto = mapRowToTheaterDTO(rs);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pst != null) pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dto;
		

	}

}

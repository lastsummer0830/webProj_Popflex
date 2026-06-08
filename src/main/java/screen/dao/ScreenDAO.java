package screen.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import screen.dto.ScreenDTO;


// SCREEN 테이블 조회를 담당하는 DAO
// 상영관 목록 조회 시 THEATER 테이블과 JOIN 해서 극장명도 함께 조회한다.
public class ScreenDAO {
	
//	ResultSet의 현재 행 1개를 ScreenDTO 객체로 변환
	private ScreenDTO mapRowToScreenDTO(ResultSet rs) throws SQLException {
		ScreenDTO dto = new ScreenDTO();
		
		dto.setScreenId(rs.getInt("screen_id"));
		dto.setTheaterId(rs.getInt("theater_id"));
		dto.setScreenName(rs.getString("screen_name"));
		dto.setTheaterName(rs.getString("theater_name"));
		
		return dto;
	}
	
//	[상영관 목록 조회]
//	SCREEN 과 THEATER 를 JOIN 해서 전체 상영관 목록을 조회한다.
//  /screen/list.do에서 JSON 응답을 만들 때 사용한다.
//	관리자 상영등록화면, 상영관 선택 기능에서 사용 할 수 있다.
	
	public ArrayList<ScreenDTO> getScreenList(Connection con){
		ArrayList<ScreenDTO> list = new ArrayList<>();
		String sql = "select s.screen_id screen_id, t.theater_id theater_id, s.screen_name screen_name, t.theater_name theater_name "
				+ "from screen s "
				+ "join theater t "
				+ "on s.theater_id = t.theater_id "
				+ "order by t.theater_name asc, s.screen_name asc";
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				list.add(mapRowToScreenDTO(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
				try {
					if(rs!= null) rs.close();
					if(pst!= null) pst.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return list;
	}
	
//	[상영관 상세 조회]
//	screen_id 기준으로 상영관 1건을 조회한다.
//	특정 screenId가 실제로 존재하는 지 확인할 때 사용 가능
	public ScreenDTO getScreenById(Connection con, int screenId) {
		
		ScreenDTO dto = null;
		String sql = "select s.screen_id screen_id, t.theater_id theater_id, s.screen_name screen_name, t.theater_name theater_name "
				+ "from screen s "
				+ "join theater t "
				+ "on s.theater_id = t.theater_id "
				+ "where s.screen_id = ?";
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, screenId);
			rs = pst.executeQuery();
			
			if(rs.next()) {
				dto = mapRowToScreenDTO(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
				try {
					if(rs != null) rs.close();
					if(pst!= null) pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return dto;
	}
}

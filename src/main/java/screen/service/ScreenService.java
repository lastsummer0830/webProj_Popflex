package screen.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import common.DBUtil;
import screen.dao.ScreenDAO;
import screen.dto.ScreenDTO;

public class ScreenService {
	
	private ScreenDAO dao = new ScreenDAO();
	
/*	전체 상영관 목록을 조회한다.
 *	screen과 theater join한 결과를 가져온다. 
 *  screenlistServlet 에서 /screen/list.do json 응답을 만들 때 사용한다.
 *  관리자 상영 등록 화면에서도 상영관 선택 목록으로 사용할 수 있다. 
 */ 
	public ArrayList<ScreenDTO> getScreenList() {
		
		Connection con = null;
		
		try {
			con = DBUtil.getConnection();
			return dao.getScreenList(con);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<ScreenDTO>();
			
		} finally {
			DBUtil.close(con);
		}
	}
	
	/*
	 * screenId 기준으로 상영관 1건을 조회한다.
	 * 해당 screenId가 실제로 존재하는지 확인할 때 사용할 수 있다.
	 * 
	 */
	
	public ScreenDTO getScreenById(int screenId) {
		Connection con = null;
		
		try {
			con = DBUtil.getConnection();
			return dao.getScreenById(con, screenId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			DBUtil.close(con);
		}
	}
	
	

}

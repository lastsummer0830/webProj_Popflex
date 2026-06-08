package screen.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import common.DBUtil;
import screen.dao.TheaterDAO;
import screen.dto.TheaterDTO;

// 극장 조회 기능의 비즈니스 로직을 담당하는 service
// db 연결 생성과 종료는 service 에서 처리하고, 실제 sql은 theater dao 에서 처리 
public class TheaterService {
	
	private TheaterDAO dao = new TheaterDAO();

//	[극장 목록 조회]
/*	dao에서 전체 극장 목록을 가져와 controller에 반환한다.
 *  조회 실패 시 빈 목록 또는 null 처리 기준을 정해 반환한다.
 */
	public ArrayList<TheaterDTO> getTheaterList(){
		Connection con = null;
		
		try {
			con = DBUtil.getConnection();
			return dao.getTheaterList(con);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<TheaterDTO>();
		} finally {
			DBUtil.close(con);
		}
	}
	
	//	[극장 상세 조회]
	/* 	theater_id가 0 이하면 잘못된 요청으로 보고, null 반환 
	 * 	유효한 theaterId이면 dao를 통해 극장 1건 조회한다.
	 */
	public TheaterDTO getTheaterById(int theaterId) {
		
		if(theaterId <= 0 ) {
			return null;
		}
		
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			return dao.getTheaterById(con, theaterId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			DBUtil.close(con);
		}
	}
}

package friend.service;

import friend.dao.FriendDAO;
import friend.dto.FriendDTO;
import member.dto.MemberDTO;

import java.sql.SQLException;
import java.util.List;

public class FriendService {

    private final FriendDAO friendDAO = new FriendDAO();
    

    //친구 목록 조회
    public List<FriendDTO> getFriendList(int myId) throws SQLException {
        return friendDAO.getFriendList(myId);
    }

    //친구 추가 ───────────────────────────────────────────────
    // 반환 코드:
    //  1 = 성공
    // -1 = 자기 자신
    // -2 = 이미 친구
    // -3 = 존재하지 않는 회원
    public int addFriend(int myId, String targetUserId) throws SQLException {
        if (targetUserId == null || targetUserId.trim().isEmpty()) {
            return -3;
        }

        // 본인 제외 검색
        MemberDTO target = friendDAO.findMemberByUserId(targetUserId.trim());

        if (target == null) {
            return -3; // 존재하지 않는 회원
        }

        if (target.getMemberId() == myId) {
            return -1; // 자기 자신
        }

        // 중복 친구 체크
        if (friendDAO.isFriend(myId, target.getMemberId())) {
            return -2;
        }

        friendDAO.insertFriend(myId, target.getMemberId());
        return 1;
    }


    //친구 삭제
    public boolean deleteFriend(int myId, int targetId) throws SQLException {
        return friendDAO.deleteFriend(myId, targetId) > 0;
    }

    //친구 여부 확인 (리뷰 연동용)
    public boolean isFriend(int myId, int targetId) throws SQLException {
        if (myId == targetId) {
            return true;
        }
        return friendDAO.isFriend(myId, targetId);
    }

    /*
    // ── AJAX 회원 검색 (친구 추가용) ────────────────────────────
    public MemberDTO searchMember(String userId, int myId) throws SQLException {
        return friendDAO.searchMemberByUserId(userId, myId);
    }*/

    //userId로 회원 조회 (프로필 조회용)
    //MemberService 대신 FriendDAO.findMemberByUserId() 사용
    public MemberDTO findByUserId(String userId) throws SQLException {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        return friendDAO.findMemberByUserId(userId.trim());
    }

    //memberId로 회원 조회 (프로필 조회용)
    //MemberService 대신 FriendDAO.findMemberByMemberId() 사용
    public MemberDTO findByMemberId(int memberId) throws SQLException {
        return friendDAO.findMemberByMemberId(memberId);
    }
}

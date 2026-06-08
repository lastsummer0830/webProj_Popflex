package friend.dao;

import friend.dto.FriendDTO;
import member.dto.MemberDTO;
import common.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    //친구 목록 조회
    public List<FriendDTO> getFriendList(int myId) throws SQLException {
        List<FriendDTO> list = new ArrayList<>();

        String sql =
                "SELECT f.friend_id, f.member_a_id, f.member_b_id, f.created_at, " +
                "       m.user_id AS friend_user_id, m.name AS friend_name " +
                "FROM FRIEND f " +
                "JOIN MEMBER m ON m.member_id = CASE " +
                "    WHEN f.member_a_id = ? THEN f.member_b_id " +
                "    ELSE f.member_a_id " +
                "END " +
                "WHERE (f.member_a_id = ? OR f.member_b_id = ?) " +
                "  AND m.is_use = 'Y' " +
                "ORDER BY f.created_at DESC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, myId);
            ps.setInt(2, myId);
            ps.setInt(3, myId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FriendDTO dto = new FriendDTO();
                    dto.setFriendId(rs.getInt("friend_id"));
                    dto.setMemberAId(rs.getInt("member_a_id"));
                    dto.setMemberBId(rs.getInt("member_b_id"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setFriendUserId(rs.getString("friend_user_id"));
                    dto.setFriendName(rs.getString("friend_name"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    //친구 추가
    public int insertFriend(int myId, int targetId) throws SQLException {
    	int memberAId = Math.min(myId, targetId);
        int memberBId = Math.max(myId, targetId);

        String sql = "INSERT INTO FRIEND (member_a_id, member_b_id) VALUES (?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberAId);
            ps.setInt(2, memberBId);
            return ps.executeUpdate();
        }
    }

    //친구 삭제
    public int deleteFriend(int myId, int targetId) throws SQLException {
    	int memberAId = Math.min(myId, targetId);
        int memberBId = Math.max(myId, targetId);

        String sql = "DELETE FROM FRIEND WHERE member_a_id = ? AND member_b_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberAId);
            ps.setInt(2, memberBId);
            return ps.executeUpdate();
        }
    }

    //친구 여부 확인
    public boolean isFriend(int myId, int targetId) throws SQLException {
    	int memberAId = Math.min(myId, targetId);
        int memberBId = Math.max(myId, targetId);

        String sql =
            "SELECT COUNT(*) " +
            "FROM FRIEND " +
            "WHERE member_a_id = ? AND member_b_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberAId);
            ps.setInt(2, memberBId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    //회원 검색 (본인 제외)
    public MemberDTO findMemberByUserId(String userId) throws SQLException {
        String sql =
            "SELECT member_id, user_id, name " +
            "FROM MEMBER " +
            "WHERE user_id = ? AND is_use = 'Y'";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MemberDTO dto = new MemberDTO();
                    dto.setMemberId(rs.getInt("member_id"));
                    dto.setUserId(rs.getString("user_id"));
                    dto.setName(rs.getString("name"));
                    return dto;
                }
            }
        }

        return null;
    }

    //본인 포함 전체 검색 (자기 자신 체크 + 프로필 조회용)
    public MemberDTO findMemberByMemberId(int memberId) throws SQLException {
        String sql =
            "SELECT member_id, user_id, name " +
            "FROM MEMBER " +
            "WHERE member_id = ? AND is_use = 'Y'";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MemberDTO dto = new MemberDTO();
                    dto.setMemberId(rs.getInt("member_id"));
                    dto.setUserId(rs.getString("user_id"));
                    dto.setName(rs.getString("name"));
                    return dto;
                }
            }
        }

        return null;
    }

    /* ✅ NEW: memberId로 회원 조회 (프로필 조회용)
    public MemberDTO findMemberByMemberId(int memberId) throws SQLException {

        String sql =
            "SELECT member_id, user_id, name " +
            "FROM MEMBER " +
            "WHERE member_id = ? AND is_use = 'Y'";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MemberDTO dto = new MemberDTO();
                    dto.setMemberId(rs.getInt("member_id"));
                    dto.setUserId(rs.getString("user_id"));
                    dto.setName(rs.getString("name"));
                    return dto;
                }
            }
        }
        return null;
    }
    */
}

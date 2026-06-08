package member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import member.dto.MemberDTO;

public class MemberDAO {

    public int insertMember(Connection conn, MemberDTO member) throws SQLException {
        String sql = "INSERT INTO MEMBER ("
                + "USER_ID, PASSWORD, NAME, EMAIL, ROLE, IS_USE, SOCIAL_TYPE"
                + ") VALUES ("
                + "?, ?, ?, ?, 'U', 'Y', 'LOCAL'"
                + ")";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getUserId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getEmail());

            return pstmt.executeUpdate();
        }
    }

    public int insertNaverMember(Connection conn, MemberDTO member) throws SQLException {
        String sql = "INSERT INTO MEMBER ("
                + "USER_ID, PASSWORD, NAME, EMAIL, ROLE, IS_USE, SOCIAL_TYPE, SOCIAL_ID"
                + ") VALUES ("
                + "?, ?, ?, ?, 'U', 'Y', 'NAVER', ?"
                + ")";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getUserId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getSocialId());

            return pstmt.executeUpdate();
        }
    }

    public MemberDTO selectByUserId(Connection conn, String userId) throws SQLException {
        String sql = "SELECT MEMBER_ID, USER_ID, PASSWORD, NAME, EMAIL, ROLE, IS_USE, "
                + "SOCIAL_TYPE, SOCIAL_ID, CREATED_AT "
                + "FROM MEMBER "
                + "WHERE USER_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapMemberWithPassword(rs);
                }
            }
        }

        return null;
    }

    public MemberDTO selectBySocial(Connection conn, String socialType, String socialId) throws SQLException {
        String sql = "SELECT MEMBER_ID, USER_ID, PASSWORD, NAME, EMAIL, ROLE, IS_USE, "
                + "SOCIAL_TYPE, SOCIAL_ID, CREATED_AT "
                + "FROM MEMBER "
                + "WHERE SOCIAL_TYPE = ? "
                + "AND SOCIAL_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, socialType);
            pstmt.setString(2, socialId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapMemberWithPassword(rs);
                }
            }
        }

        return null;
    }

    public MemberDTO selectByMemberId(Connection conn, int memberId) throws SQLException {
        String sql = "SELECT MEMBER_ID, USER_ID, NAME, EMAIL, ROLE, IS_USE, "
                + "SOCIAL_TYPE, SOCIAL_ID, CREATED_AT "
                + "FROM MEMBER "
                + "WHERE MEMBER_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapMemberWithoutPassword(rs);
                }
            }
        }

        return null;
    }

    public int countByUserId(Connection conn, String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MEMBER WHERE USER_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public int countByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MEMBER WHERE EMAIL = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public int updateMember(Connection conn, MemberDTO member) throws SQLException {
        String sql = "UPDATE MEMBER "
                + "SET NAME = ?, EMAIL = ? "
                + "WHERE MEMBER_ID = ? "
                + "AND IS_USE = 'Y'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setInt(3, member.getMemberId());

            return pstmt.executeUpdate();
        }
    }

    public int deactivateMember(Connection conn, int memberId) throws SQLException {
        String sql = "UPDATE MEMBER "
                + "SET IS_USE = 'N' "
                + "WHERE MEMBER_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate();
        }
    }

    private MemberDTO mapMemberWithPassword(ResultSet rs) throws SQLException {
        MemberDTO member = mapMemberWithoutPassword(rs);
        member.setPassword(rs.getString("PASSWORD"));
        return member;
    }

    private MemberDTO mapMemberWithoutPassword(ResultSet rs) throws SQLException {
        MemberDTO member = new MemberDTO();
        member.setMemberId(rs.getInt("MEMBER_ID"));
        member.setUserId(rs.getString("USER_ID"));
        member.setName(rs.getString("NAME"));
        member.setEmail(rs.getString("EMAIL"));
        member.setRole(rs.getString("ROLE"));
        member.setIsUse(rs.getString("IS_USE"));
        member.setSocialType(rs.getString("SOCIAL_TYPE"));
        member.setSocialId(rs.getString("SOCIAL_ID"));
        member.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        return member;
    }
}

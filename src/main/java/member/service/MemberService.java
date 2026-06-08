package member.service;

import java.sql.Connection;
import java.sql.SQLException;

import common.DBUtil;
import common.util.Sha256Util;
import member.dao.MemberDAO;
import member.dto.MemberDTO;
import member.dto.NaverProfileDTO;

public class MemberService {

    private static final String SOCIAL_PASSWORD_DUMMY = "SOCIAL_LOGIN_DUMMY_PASSWORD";

    private final MemberDAO memberDAO = new MemberDAO();

    public boolean join(MemberDTO member) {
        if (member == null) {
            return false;
        }

        String userId = trim(member.getUserId());
        String password = trim(member.getPassword());
        String name = trim(member.getName());
        String email = trim(member.getEmail());

        if (isBlank(userId) || isBlank(password) || isBlank(name) || isBlank(email)) {
            return false;
        }

        if (userId.length() > 30 || password.length() < 4) {
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if (memberDAO.countByUserId(conn, userId) > 0) {
                return false;
            }

            if (memberDAO.countByEmail(conn, email) > 0) {
                return false;
            }

            member.setUserId(userId);
            member.setPassword(Sha256Util.sha256(password));
            member.setName(name);
            member.setEmail(email);

            return memberDAO.insertMember(conn, member) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to join member.", e);
        }
    }

    public MemberDTO login(String userId, String rawPassword) {
        userId = trim(userId);
        rawPassword = trim(rawPassword);

        if (isBlank(userId) || isBlank(rawPassword)) {
            return null;
        }

        try (Connection conn = DBUtil.getConnection()) {
            MemberDTO member = memberDAO.selectByUserId(conn, userId);

            if (member == null || !member.isActive()) {
                return null;
            }

            if (!Sha256Util.matches(rawPassword, member.getPassword())) {
                return null;
            }

            member.setPassword(null);
            return member;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to login.", e);
        }
    }

    public MemberDTO loginByNaver(String socialId) {
        socialId = trim(socialId);

        if (isBlank(socialId)) {
            return null;
        }

        try (Connection conn = DBUtil.getConnection()) {
            MemberDTO member = memberDAO.selectBySocial(conn, "NAVER", socialId);

            if (member == null || !member.isActive()) {
                return null;
            }

            member.setPassword(null);
            return member;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to login by naver.", e);
        }
    }

    public boolean joinNaver(String userId, NaverProfileDTO profile) {
        userId = trim(userId);

        if (profile == null) {
            return false;
        }

        String socialId = trim(profile.getSocialId());
        String name = trim(profile.getName());
        String email = trim(profile.getEmail());

        if (isBlank(userId) || isBlank(socialId) || isBlank(name) || isBlank(email)) {
            return false;
        }

        if (userId.length() > 30) {
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            if (memberDAO.countByUserId(conn, userId) > 0) {
                return false;
            }

            if (memberDAO.countByEmail(conn, email) > 0) {
                return false;
            }

            if (memberDAO.selectBySocial(conn, "NAVER", socialId) != null) {
                return false;
            }

            MemberDTO member = new MemberDTO();
            member.setUserId(userId);
            member.setPassword(Sha256Util.sha256(SOCIAL_PASSWORD_DUMMY + ":" + socialId));
            member.setName(name);
            member.setEmail(email);
            member.setSocialType("NAVER");
            member.setSocialId(socialId);

            return memberDAO.insertNaverMember(conn, member) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to join naver member.", e);
        }
    }

    public boolean isDuplicatedUserId(String userId) {
        userId = trim(userId);

        if (isBlank(userId)) {
            return true;
        }

        try (Connection conn = DBUtil.getConnection()) {
            return memberDAO.countByUserId(conn, userId) > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check user id.", e);
        }
    }

    public MemberDTO getMember(int memberId) {
        if (memberId <= 0) {
            return null;
        }

        try (Connection conn = DBUtil.getConnection()) {
            return memberDAO.selectByMemberId(conn, memberId);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to get member.", e);
        }
    }

    public boolean updateMember(MemberDTO member) {
        if (member == null || member.getMemberId() <= 0) {
            return false;
        }

        String name = trim(member.getName());
        String email = trim(member.getEmail());

        if (isBlank(name) || isBlank(email)) {
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            MemberDTO savedMember = memberDAO.selectByMemberId(conn, member.getMemberId());

            if (savedMember == null || !savedMember.isActive()) {
                return false;
            }

            if (!email.equals(savedMember.getEmail()) && memberDAO.countByEmail(conn, email) > 0) {
                return false;
            }

            member.setName(name);
            member.setEmail(email);

            return memberDAO.updateMember(conn, member) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update member.", e);
        }
    }

    public boolean deactivateMember(int memberId) {
        if (memberId <= 0) {
            return false;
        }

        try (Connection conn = DBUtil.getConnection()) {
            return memberDAO.deactivateMember(conn, memberId) == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to deactivate member.", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

package friend.dto;

import java.sql.Timestamp;

public class FriendDTO {

    private int friendId;
    private int memberAId;
    private int memberBId;
    private Timestamp createdAt;

    // 조회용 추가 필드 (JOIN 결과)
    private String friendUserId;   
    private String friendName;

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getMemberAId() {
        return memberAId;
    }

    public void setMemberAId(int memberAId) {
        this.memberAId = memberAId;
    }

    public int getMemberBId() {
        return memberBId;
    }

    public void setMemberBId(int memberBId) {
        this.memberBId = memberBId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}

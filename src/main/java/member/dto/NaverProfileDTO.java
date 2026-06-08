package member.dto;

import java.io.Serializable;

public class NaverProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String socialId;
    private String email;
    private String name;

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

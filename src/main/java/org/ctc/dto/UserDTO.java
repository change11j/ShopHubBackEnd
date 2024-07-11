package org.ctc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ctc.entity.Image;

import java.util.Date;
@Data
@AllArgsConstructor
public class UserDTO {
    private Integer userId;

    private String userName;

    private Image image;

    private String token;

    private String avatarUrl;

    public UserDTO(Integer userId, String userName, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }

    public UserDTO( String userName,Integer userId, String token, Image image, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.image = image;
        this.token = token;
        this.avatarUrl = avatarUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

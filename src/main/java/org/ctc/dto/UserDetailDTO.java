package org.ctc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

import java.util.Date;

public class UserDetailDTO {
    private Integer userId;

    private String userName;

    private Integer sex;

    private String mail;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")

    private Date birthday;

    private String userImage;


    public UserDetailDTO() {}

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getUserImage() {return userImage;}

    public void setUserImage(String userImage) {this.userImage = userImage;}
}


package org.ctc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ctc.entity.Image;

import java.util.Date;

@Data
public class UserProfileDTO {
    private Integer userId;

    private String userName;

    private Integer sex;

    private String mail;

    private Date birthday;

    private String token;

    private String filePath;

    private String idNum;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Integer userId, String userName, Integer sex, String mail, Date birthday, String token) {
        this.userId = userId;
        this.userName = userName;
        this.sex = sex;
        this.mail = mail;
        this.birthday = birthday;
        this.token = token;
    }

    public UserProfileDTO(Integer userId, String mail, Date birthday, Integer sex, String userName,String filePath) {
        this.userId = userId;
        this.mail = mail;
        this.birthday = birthday;
        this.sex = sex;
        this.userName = userName;
        this.filePath=filePath;
    }

    public UserProfileDTO(Integer userId, String mail, Date birthday, Integer sex, String userName) {
        this.userId = userId;
        this.mail = mail;
        this.birthday = birthday;
        this.sex = sex;
        this.userName = userName;
    }

    public UserProfileDTO(Integer userId, String userName, Integer sex, String mail, Date birthday, String token, String filePath, String idNum) {
        this.userId = userId;
        this.userName = userName;
        this.sex = sex;
        this.mail = mail;
        this.birthday = birthday;
        this.token = token;
        this.filePath = filePath;
        this.idNum = idNum;
    }
}

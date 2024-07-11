package org.ctc.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String userName;

    private String secret;

    private Integer sex = 3;

    private String mail;

    private Date birthday = new Date();

    private Integer isOpenGoogle;

    private Date rDate;

    private boolean mailNoti = true;
    private boolean orderNoti = true;
    private boolean discountNoti = true;
    private boolean valid = true;

    @Lob
    @Column(name = "user_image", columnDefinition = "MEDIUMTEXT")
    private String userImage;

    @Lob
    @Column(name = "seller_image", columnDefinition = "MEDIUMTEXT")
    private String sellerImage;

    private String idNum;

    private String householdCounty;

    private String householdArea;

    private String householdAddress;

    private String sellerName;
    private String phone;
    @Column(columnDefinition = "LONGTEXT")
    private String sellerDisc;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSellerImage() {
        return sellerImage;
    }

    public void setSellerImage(String sellerImage) {
        this.sellerImage = sellerImage;
    }



    public void setSellerDisc(String sellerDisc) {
        this.sellerDisc = sellerDisc;
    }







    // Getters and Setters

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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getIsOpenGoogle() {
        return isOpenGoogle;
    }

    public void setIsOpenGoogle(Integer isOpenGoogle) {
        this.isOpenGoogle = isOpenGoogle;
    }

    public Boolean getMailNoti() {
        return this.mailNoti;
    }

    public void setMailNoti(Boolean mailNoti) {
        this.mailNoti = mailNoti;
    }

    public Boolean getOrderNoti() {
        return this.orderNoti;
    }

    public void setOrderNoti(Boolean orderNoti) {
        this.orderNoti = orderNoti;
    }

    public Boolean getDiscountNoti() {
        return this.discountNoti;
    }

    public void setDiscountNoti(Boolean discountNoti) {
        this.discountNoti = discountNoti;
    }

    public Boolean getValid() {
        return this.valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getUserImage() {return userImage;}

    public void setUserImage(String userImage) {this.userImage = userImage;}



    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getHouseholdCounty() {
        return householdCounty;
    }

    public void setHouseholdCounty(String householdCounty) {
        this.householdCounty = householdCounty;
    }

    public String getHouseholdArea() {
        return householdArea;
    }

    public void setHouseholdArea(String householdArea) {
        this.householdArea = householdArea;
    }

    public String getHouseholdAddress() {
        return householdAddress;
    }

    public void setHouseholdAddress(String householdAddress) {
        this.householdAddress = householdAddress;
    }

    public Date getrDate() {
        return rDate;
    }

    public void setrDate(Date rDate) {
        this.rDate = rDate;
    }

    public boolean isMailNoti() {
        return mailNoti;
    }

    public void setMailNoti(boolean mailNoti) {
        this.mailNoti = mailNoti;
    }

    public boolean isOrderNoti() {
        return orderNoti;
    }

    public void setOrderNoti(boolean orderNoti) {
        this.orderNoti = orderNoti;
    }

    public boolean isDiscountNoti() {
        return discountNoti;
    }

    public void setDiscountNoti(boolean discountNoti) {
        this.discountNoti = discountNoti;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }



    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Users(String sellerName) {
        this.sellerName = sellerName;
    }

    public Users() {
    }    public Date getRDate() {
        return rDate;
    }

    public void setRDate(Date rDate) {
        this.rDate = rDate;
    }

    public String getSellerName() {
        return sellerName;
    }


    public String getSellerDisc() {
        return sellerDisc;
    }

}

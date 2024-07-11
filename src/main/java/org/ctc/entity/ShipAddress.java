package org.ctc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ShipAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipId;
//    private Integer Id;
    private String recipientName;
    private String recipientPhone;
    private String address;

    private String shopName;   //店鋪名稱
    private Integer shipType;   //類型 0 : 宅配  1:7-11 2:family mart

    private Integer userId;

    public Integer getShipId() {
        return shipId;
    }

    public void setShipId(Integer shipId) {
        this.shipId = shipId;
    }

//    public Integer getId() {
//        return Id;
//    }
//
//    public void setId(Integer userId) {
//        this.Id = userId;
//    }


    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getShipType() {
        return shipType;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShipType(Integer shipType) {
        this.shipType = shipType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ShipAddress{" +
                "shipId=" + shipId +
                ", recipientName='" + recipientName + '\'' +
                ", recipientPhone='" + recipientPhone + '\'' +
                ", address='" + address + '\'' +
                ", shopName='" + shopName + '\'' +
                ", shipType=" + shipType +
                ", userId=" + userId +
                '}';
    }
}

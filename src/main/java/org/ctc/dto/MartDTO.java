package org.ctc.dto;

public class MartDTO {
    private Integer shipId;

    private String recipientName;

    private String recipientPhone;

    private String address;

    private String shopName;   //店鋪名稱

    private Integer shipType;   //1:7-11 2:family mart

    private Integer userId;


    public Integer getShipId() {return shipId;}

    public void setShipId(Integer shipId) {this.shipId = shipId;}

    public String getRecipientName() {return recipientName;}

    public void setRecipientName(String recipientName) {this.recipientName = recipientName;}

    public String getRecipientPhone() {return recipientPhone;}

    public void setRecipientPhone(String recipientPhone) {this.recipientPhone = recipientPhone;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public String getShopName() {return shopName;}

    public void setShopName(String shopName) {this.shopName = shopName;}

    public Integer getShipType() {return shipType;}

    public void setShipType(Integer shipType) {this.shipType = shipType;}

    public Integer getUserId() {return userId;}

    public void setUserId(Integer userId) {this.userId = userId;}
}

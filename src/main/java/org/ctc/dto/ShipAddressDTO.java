package org.ctc.dto;

public class ShipAddressDTO {
    private Integer shipId;
    private Integer Id;
    private String RecipientName;
    private String RecipientPhone;
    private String address;
    private Integer shipType;

    public ShipAddressDTO() {
    }

    public Integer getShipId() {
        return shipId;
    }

    public void setShipId(Integer shipId) {
        this.shipId = shipId;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getRecipientName() {
        return RecipientName;
    }

    public void setRecipientName(String recipientName) {
        RecipientName = recipientName;
    }

    public String getRecipientPhone() {
        return RecipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        RecipientPhone = recipientPhone;
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

    public void setShipType(Integer shipType) {
        this.shipType = shipType;
    }

    public ShipAddressDTO(Integer shipId, Integer Id, String RecipientName, String RecipientPhone, String address, Integer shipType) {
        this.shipId = shipId;
        this.Id = Id;
        this.RecipientName = RecipientName;
        this.RecipientPhone = RecipientPhone;
        this.address = address;
        this.shipType = shipType;
    }
}



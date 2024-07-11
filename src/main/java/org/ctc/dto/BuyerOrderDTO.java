package org.ctc.dto;

import lombok.Data;

import java.util.List;
@Data
public class BuyerOrderDTO {
    private String ecpayId;
    private Integer sellerId;
    private String userName;
    private String userImage;
    private Integer orderStatus;
    private Integer shipMethod;
    private List<BuyerOrderDetailDTO> products;

    public BuyerOrderDTO(String ecpayId, Integer sellerId, String userName, String userImage, Integer orderStatus, Integer shipMethod, List<BuyerOrderDetailDTO> products) {
        this.ecpayId = ecpayId;
        this.sellerId = sellerId;
        this.userName = userName;
        this.userImage = userImage;
        this.orderStatus = orderStatus;
        this.shipMethod = shipMethod;
        this.products = products;
    }
}

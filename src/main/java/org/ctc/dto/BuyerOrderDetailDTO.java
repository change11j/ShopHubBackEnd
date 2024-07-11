package org.ctc.dto;

import lombok.Data;

import java.util.Base64;

@Data
public class BuyerOrderDetailDTO {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private String spec;
    private String specName;
    private Integer price;
    private Integer quantity;
    private String ecpayId;
    private Integer sellerId;
    private String sellerName;
    private String sellerImage;
    private Integer orderStatus;
    private Integer shipMethod;

    public BuyerOrderDetailDTO(
            Integer productId,
            String productName,
            String imageUrl,
            String spec,
            String specName,
            Integer price,
            Integer quantity,
            String ecpayId,
            Integer sellerId,
            String sellerName,
            String sellerImage,
            Integer orderStatus,
            Integer shipMethod
    ) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.spec = spec;
        this.specName = specName;
        this.price = price;
        this.quantity = quantity;
        this.ecpayId = ecpayId;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerImage = sellerImage;
        this.orderStatus = orderStatus;
        this.shipMethod = shipMethod;
    }

}

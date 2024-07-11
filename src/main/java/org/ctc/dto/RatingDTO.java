package org.ctc.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@Data
@AllArgsConstructor
public class RatingDTO {
    private Integer ratingID;
    private Integer buyerId;
    private Date ratingDate;
    private String comment;
    private Integer productRating;
    private Integer sellerRating;
    private Integer shippingRating;
    private String buyerName;
    private String buyerAvatar;
    private String spec1Name;
    private String spec2Name;
    private String  ecpayId;
    public RatingDTO() {
    }

    public RatingDTO(Integer ratingID, Integer buyerId, Date ratingDate, String comment, Integer productRating, Integer sellerRating, Integer shippingRating, String buyerName, String buyerAvatar, String spec1Name, String spec2Name) {
        this.ratingID = ratingID;
        this.buyerId = buyerId;
        this.ratingDate = ratingDate;
        this.comment = comment;
        this.productRating = productRating;
        this.sellerRating = sellerRating;
        this.shippingRating = shippingRating;
        this.buyerName = buyerName;
        this.buyerAvatar = buyerAvatar;
        this.spec1Name = spec1Name;
        this.spec2Name = spec2Name;
    }
}

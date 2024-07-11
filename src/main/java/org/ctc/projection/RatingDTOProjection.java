package org.ctc.projection;

import java.util.Date;

public interface RatingDTOProjection {
    Integer getRatingID();
    Integer getBuyerId();
    Date getRatingDate();
    String getComment();
    Integer getProductRating();
    Integer getSellerRating();
    Integer getShippingRating();
    String getBuyerName();
    String getBuyerAvatar();
    String getSpec1Name();
    String getSpec2Name();

}

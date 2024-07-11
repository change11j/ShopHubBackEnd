package org.ctc.projection;

import org.apache.catalina.User;
import org.ctc.entity.*;
import org.springframework.beans.factory.annotation.Value;

public interface ProdDetailProjection {
    Product getProduct();
    Category getCategory();
    Image getImage();
    ProdSpec getProdSpec();
    Users getUser();
    OrderDetail getOrderDetail();
    Rating getRating();

    @Value("#{target.user.userId}")
    Integer getUserId();

    @Value("#{target.user.userName}")
    String getUsername();

    @Value("#{@imageUtil.generateImageFile('u', target.user.userId, target.userImage.imageId, target.userImage.extension)}")
    String getAvatarUrl();

    @Value("#{@imageUtil.generateImageFile('p', target.product.productId, target.image.imageId, target.image.extension)}")
    String getImgUrl();
}

package org.ctc.projection;

import org.ctc.entity.Category;
import org.ctc.entity.Image;
import org.ctc.entity.ProdSpec;
import org.ctc.entity.Product;
import org.springframework.beans.factory.annotation.Value;

public interface CategorySearchProjection {
    Product getProduct();
    Category getCategory();
    Image getImage();
    ProdSpec getProdSpec();
    @Value("#{@imageUtil.generateImageFile('p', target.product.productId, target.image.imageId, target.image.extension)}")
    String getImgUrl();
}

package org.ctc.projection;

import org.ctc.entity.Image;
import org.ctc.entity.ProdSpec;
import org.ctc.entity.Product;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface AddedProductDTOProjection {
    Product getProduct();
    List<ProdSpec> getProdSpecs();
    List<Image> getImageList();

}

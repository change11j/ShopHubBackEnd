package org.ctc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ctc.entity.*;

import java.util.List;

@Data
@AllArgsConstructor
public class ProdDetail {
    private Product product;
    private Category category;
    private List<Image> images;
    private List<ProdSpec> prodSpecs;
    private SellerDTO sellerDTO;
    private List<String> imgUrls;
    private List<OrderDetail> orderDetails;
    private List<Rating> ratings;

    public ProdDetail() {
    }
}

package org.ctc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ctc.entity.Image;
import org.ctc.entity.Category;
import org.ctc.entity.Product;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductWithCateNImgDTO {
    private Product product;
    private Category category;
    private Image images;
    private String imgUrls;

    public ProductWithCateNImgDTO() {
    }
}

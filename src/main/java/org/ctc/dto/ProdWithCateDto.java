package org.ctc.dto;

import org.ctc.entity.Category;
import org.ctc.entity.Product;

public class ProdWithCateDto {
    private Product product;
    private Category category;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

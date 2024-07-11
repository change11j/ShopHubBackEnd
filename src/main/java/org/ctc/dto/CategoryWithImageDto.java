package org.ctc.dto;

import org.ctc.entity.Category;

public class CategoryWithImageDto {
    private Category category;
    private String imgUrl;



    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}

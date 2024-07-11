package org.ctc.dto;

import org.ctc.entity.Category;
import org.ctc.entity.Image;
import org.ctc.entity.ProdSpec;
import org.ctc.entity.Product;
import org.ctc.entity.Users;

public class ProductDetailDTO {
    private Product product;
    private ProdSpec spec;
    private Image image;
    private Category category;
    private Users users;


    public ProductDetailDTO(Product product, Category category, Image image, ProdSpec spec, Users users) {
        this.product = product;
        this.category = category;
        this.image = image;
        this.spec = spec;
        this.users = users;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProdSpec getSpec() {
        return spec;
    }

    public void setSpec(ProdSpec spec) {
        this.spec = spec;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}

package org.ctc.dto.ecpay;

import lombok.Data;
import org.ctc.entity.CartItem;
import org.ctc.entity.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CartDTO implements Serializable {
    private Integer cartItemId;

    private ProductDTO product;

    private String pImgURL;

    private Integer amount;

    private Integer prdSpecId;

    public CartDTO(Integer cartItemId, ProductDTO product, Integer amount,Integer prdSpecId) {
        this.cartItemId = cartItemId;
        this.product = product;
        this.amount = amount;
        this.prdSpecId=prdSpecId;
    }

    public CartDTO(Integer cartItemId, ProductDTO product, Integer amount) {
        this.cartItemId = cartItemId;
        this.product = product;
        this.amount = amount;
    }
    public CartDTO(Integer cartItemId, ProductDTO product, Integer amount,String pImgURL) {
        this.cartItemId = cartItemId;
        this.product = product;
        this.amount = amount;
        this.pImgURL=pImgURL;
    }

    public CartDTO() {
    }
}

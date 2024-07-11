package org.ctc.dto.ecpay;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.ctc.Enum.ProductStatus;
import org.ctc.entity.ProdSpec;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ProductDTO {

    private Integer productId;

    private Integer sellerId;

    private String sellerName;

    private Double discount;

    private Integer stock;

    private String productName;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private String description1;

    private String description2;

    @CreationTimestamp
    private LocalDateTime listingDate;

    private Integer categoryId;

    @Column(columnDefinition = "LONGTEXT")
    private String productDetail;

    private List<ProdSpec> prodSpecList;
    public ProductDTO(Integer productId, Integer sellerId, Double discount, Integer stock, String productName,
                      Integer price, ProductStatus productStatus, String description1, String description2,
                      LocalDateTime listingDate, Integer categoryId, String productDetail) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.discount = discount;
        this.stock = stock;
        this.productName = productName;
        this.price = price;
        this.productStatus = productStatus;
        this.description1 = description1;
        this.description2 = description2;
        this.listingDate = listingDate;
        this.categoryId = categoryId;
        this.productDetail = productDetail;
    }
    public ProductDTO(Integer productId, Integer sellerId, Double discount, Integer stock, String productName,
                      Integer price, ProductStatus productStatus, String description1, String description2,
                      LocalDateTime listingDate, Integer categoryId, String productDetail,String sellerName) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.discount = discount;
        this.stock = stock;
        this.productName = productName;
        this.price = price;
        this.productStatus = productStatus;
        this.description1 = description1;
        this.description2 = description2;
        this.listingDate = listingDate;
        this.categoryId = categoryId;
        this.productDetail = productDetail;
        this.sellerName=sellerName;
    }
    public ProductDTO() {
    }
}

package org.ctc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingID;
    private String  ecpayId;
    private Integer buyerId;
    private Date ratingDate;
    @Column(columnDefinition = "LONGTEXT")
    private String comment;
    private Integer productRating;
    private Integer sellerRating;
    private Integer shippingRating;


    public Rating() {
    }
}

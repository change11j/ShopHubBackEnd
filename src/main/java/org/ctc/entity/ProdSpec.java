package org.ctc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class ProdSpec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prodSpecId;
    private String spec1;
    @Column(name = "spec1_name")
    private String spec1Name;
    @Column(columnDefinition = "LONGTEXT")
    private String specBase64;
    private String spec2;
    @Column(name = "spec2_name")
    private String spec2Name;

    private Integer price;
    private Integer stock;
    private Integer productId;
    public ProdSpec() {
    }
}

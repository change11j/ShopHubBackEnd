package org.ctc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ctc.entity.Image;
import org.ctc.entity.ProdSpec;
import org.ctc.entity.Product;

import java.util.List;

@Data
@AllArgsConstructor
public class Pcard {
    private Product product;
    private List<ProdSpec> prodSpecs;
    private List<String> imgUrls;
    public Pcard() {
    }
}

package org.ctc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ctc.entity.Category;
import org.ctc.entity.Image;
import org.ctc.entity.ProdSpec;
import org.ctc.entity.Product;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductSearch {
    private Product product;
    private Category category;
    private List<ProdSpec> prodSpecs;
    private List<Image> images;
    private List<String> imgUrls;

    public ProductSearch() {
    }
}

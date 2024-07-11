package org.ctc.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class SellerInfoDTO {

    private Integer userId;
    private String sellerName;
    private String sellerDisc;
    private String sellerImage;


}

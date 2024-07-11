package org.ctc.dto.ecpay;

import lombok.Data;

import java.util.List;

@Data
public class EcPayReq {
    List<CartGroup> cartGroupList;

    private Integer userId;
}

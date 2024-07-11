package org.ctc.dto.ecpay;

import lombok.Data;
import org.ctc.dto.OrderDetailDTO;
import org.ctc.entity.OrderDetail;

import java.util.List;

@Data
public class OrderTotalDTO {

    private List<OrderDetail> orderDetailList;

    private Integer totalPrice;

    public OrderTotalDTO(List<OrderDetail> orderDetailList, Integer totalPrice) {
        this.orderDetailList = orderDetailList;
        this.totalPrice = totalPrice;
    }
}

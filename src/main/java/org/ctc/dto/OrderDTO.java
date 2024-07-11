package org.ctc.dto;

import lombok.Data;

import java.util.List;
@Data
public class OrderDTO{
        private String ecpayId;
        private Integer buyerId;
        private String userName;
        private String userImage;
        private Integer orderStatus;
        private Integer shipMethod;
        private List<OrderDetailDTO> products;

        public OrderDTO(String ecpayId, Integer buyerId, String userName, String userImage, Integer orderStatus, Integer shipMethod, List<OrderDetailDTO> products) {
                this.ecpayId = ecpayId;
                this.buyerId = buyerId;
                this.userName = userName;
                this.userImage = userImage;
                this.orderStatus = orderStatus;
                this.shipMethod = shipMethod;
                this.products = products;
        }
}

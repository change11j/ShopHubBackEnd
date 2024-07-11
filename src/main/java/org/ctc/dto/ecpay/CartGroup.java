package org.ctc.dto.ecpay;

import lombok.Data;
import org.ctc.entity.ShipAddress;

import java.io.Serializable;
import java.util.List;

//傳送給前端的資料，需先處理
@Data
public class CartGroup implements Serializable {

    private Integer sellerId;

    private String sellerName;

    private List<Integer> smIdList;

    private List<CartDTO> cartDTOList;

    private ShipAddress shipAddress;



    public CartGroup(Integer sellerId,List<CartDTO> cartDTOList){
        this.sellerId=sellerId;
        this.cartDTOList=cartDTOList;
    }
    public CartGroup() {

    }
}

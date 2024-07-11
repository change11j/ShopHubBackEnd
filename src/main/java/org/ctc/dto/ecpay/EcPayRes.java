package org.ctc.dto.ecpay;

import lombok.Data;

@Data
public class EcPayRes {

    private String merchantTradeNo; //我發送的ID

    private String tradeNo;  //綠界ID

    private String tradeAmt; //交易金額

    private String paymentDate; //付款時間

    private String rtnCode;  //回傳代號 1 為成功 他為失敗

    private String paymentType; //付款方式

    private String tradeDate; //訂單成立時間




}

package org.ctc.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ctc.dto.Result;
import org.ctc.dto.ecpay.*;
import org.ctc.ecpay.AioCheckOutALL;
import org.ctc.service.EcpayService;
import org.ctc.service.OrderDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.ctc.costant.Constance.SHOP;
import static org.ctc.ecpay.EcpayFunction.genCheckMacValue;
@RequestMapping("/ecpay")
@Controller
public class EcpayController {

    private Logger log = LoggerFactory.getLogger(EcpayController.class);

    @Value("${ecpay.merchantid}")
    private String merchantId;

    @Value("${ecpay.hashkey}")
    private String hashkey;

    @Value("${ecpay.hashv}")
    private String hashv;

    @Value("${ecpay.tradeno}")
    private String tradeNo;

    @Value("${ecpay.redirect.succuri}")
    private String succPayRedirect;

    @Value("${ecpay.redirect.failuri}")
    private String failPayRedirect;

    @Value("${ecpay.returnurl}")
    private String ecpayReturnRUL;

    private EcpayService ecpayService;

    private OrderDetailService orderDetailService;


    public EcpayController(EcpayService ecpayService,OrderDetailService orderDetailService){
        this.ecpayService=ecpayService;
        this.orderDetailService=orderDetailService;
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public void method(HttpServletResponse httpServletResponse, @RequestBody EcPayReq ecPayReq) throws IOException {
         //接收前端傳進來的商品 為一個陣列，裡面會再根據sellerId 放進同一個陣列 [{"sellerId":1,"shipId":1, "items": [{"productId":1,...各種產品屬性 },{第二個}... ]  ],[{"sellerId":2, "item": [{"productId":2,...各種產品屬性 },{第二個}... ]  ]

        String ecpayUrl = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";
        UUID uuid = UUID.randomUUID();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<CartGroup> cartGroupList=ecPayReq.getCartGroupList();


        String uuidString = uuid.toString().replace("-", "");
        String shortUuid = uuidString.substring(0, 16); //簡寫的訂單編號
       // String merchantTradeNo = SHOP+tradeNo+UUID.randomUUID().toString();   //特店訂單編號
        String merchantTradeDate = sdf.format(new Date());; //特店交易時間
        Integer totalPrice =0;
        StringBuffer itemStringBuffer = new StringBuffer();
        for(CartGroup cartGroup:cartGroupList){

            List<CartDTO> cartDTOList=cartGroup.getCartDTOList();
            for(CartDTO cartDTO:cartDTOList){
                itemStringBuffer.append(cartDTO.getProduct().getProductName());
                itemStringBuffer.append("#");
                totalPrice+=cartDTO.getProduct().getPrice();
            }
        }

        String paymentType = "aio"; //交易類型 固定為aio  物件預設已有此值
        String totalAmount = String.valueOf(totalPrice); //交易金額
        String tradeDesc = "付款事項"; //交易描述 不能接受特殊字元
        String itemName = itemStringBuffer.toString(); //商品名稱 若有多筆商品用#隔開
        String returnURL = ecpayReturnRUL; //付款完成通知回傳網址

        String choosePayment = "ALL"; //選擇預設付款方式 物件預設已有此值
        String orderResultURL=ecpayReturnRUL;
       // String orderResultURL = "yourOrderResultURL";   // Client端回傳付款結果網址 若與[ClientBackURL]同時設定，將會以此參數為主。
        Integer encryptType=1; //物件預設已有此值
        Integer userId = 1;
        ecpayService.storeOrder(shortUuid,ecPayReq);
        //設定需要的值
        AioCheckOutALL aioCheckOutALL = new AioCheckOutALL();
        aioCheckOutALL.setMerchantID(merchantId);
        aioCheckOutALL.setMerchantTradeDate(merchantTradeDate);
        aioCheckOutALL.setTotalAmount(totalAmount);
        aioCheckOutALL.setTradeDesc(tradeDesc);
        aioCheckOutALL.setItemName(itemName);
        aioCheckOutALL.setReturnURL(returnURL);
        aioCheckOutALL.setMerchantTradeNo(shortUuid);
       // aioCheckOutALL.setClientBackURL(returnURL);
        aioCheckOutALL.setOrderResultURL(orderResultURL);

        log.info(" uuid : {} , trade start",shortUuid);
        //官網範例

        String checkVal = genCheckMacValue(hashkey, hashv, aioCheckOutALL); //檢查碼



        // 開新視窗
        httpServletResponse.setContentType("text/html;charset=UTF-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.println("<html>");
        out.println("<body >");
        out.println("<h1>正在連線至付款頁面</h1>");
        out.println("<form id='ecpayForm' action='" + ecpayUrl + "' method='post'>");
        out.println("<input type='hidden' name='MerchantID' value='" + merchantId + "'>");
        out.println("<input type='hidden' name='MerchantTradeNo' value='" + shortUuid + "'>");
        out.println("<input type='hidden' name='MerchantTradeDate' value='" + merchantTradeDate + "'>");
        out.println("<input type='hidden' name='PaymentType' value='aio'>");
        out.println("<input type='hidden' name='TotalAmount' value='" + totalAmount + "'>");
        out.println("<input type='hidden' name='TradeDesc' value='" + tradeDesc + "'>");
        out.println("<input type='hidden' name='ItemName' value='" + itemName + "'>");
        out.println("<input type='hidden' name='ReturnURL' value='" + returnURL + "'>");
        out.println("<input type='hidden' name='ChoosePayment' value='ALL'>");
        out.println("<input type='hidden' name='EncryptType' value='1'>");
        out.println("<input type='hidden' name='CheckMacValue' value='"+checkVal+"'>");
       // out.println("<input type='hidden' name='ClientBackURL' value='"+returnURL+"'>");
        out.println("<input type='hidden' name='OrderResultURL' value='"+orderResultURL+"'>");
        out.println("</form>");
        out.println("<script>");
        out.println("document.getElementById('ecpayForm').submit();");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");


    }


    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());

    }
    @ResponseBody
    @PostMapping("/getOrderDetail")
    public void getOrderDetail(@ModelAttribute EcPayRes ecPayRes,HttpServletResponse response) throws IOException {
        log.info(" uuid : {} , after trade",ecPayRes.getMerchantTradeNo());


        if("1".equals(ecPayRes.getRtnCode())){
            response.sendRedirect(succPayRedirect+"?ecpay="+ecPayRes.getMerchantTradeNo());
            System.out.println(ecPayRes);
            EcPayReq ecPayReq= ecpayService.retrieveOrder(ecPayRes.getMerchantTradeNo());
            System.out.println(ecPayReq);
            orderDetailService.generateOrder(ecPayReq.getCartGroupList(),ecPayRes.getMerchantTradeNo(),ecPayRes.getPaymentType(),ecPayReq.getUserId());
        }else {
            response.sendRedirect(failPayRedirect+"?ecpay="+ecPayRes.getMerchantTradeNo());
        }


    }
    @ResponseBody
    @PostMapping("/getTotalOrderDetail")
    public Result getOrderDetailBy(@RequestParam String ecpayId) throws IOException {
        log.info(" uuid  , after trade");

//        String successUrl = succPayRedirect;
//        response.sendRedirect(successUrl+"?ecpay="+ecPayRes.getTradeNo());
//        System.out.println(ecPayRes);
//        EcPayReq ecPayReq= ecpayService.retrieveOrder(ecPayRes.getMerchantTradeNo());
//        System.out.println(ecPayReq);
//        orderDetailService.getOrderDetailByEcpay(ecPayReq.getCartGroupList(),ecPayRes.getMerchantTradeNo(),ecPayRes.getPaymentType(),ecPayReq.getUserId());
           return orderDetailService.getOrderDetailByEcpay(ecpayId);
    }



}

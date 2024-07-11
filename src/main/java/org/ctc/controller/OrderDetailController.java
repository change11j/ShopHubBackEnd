package org.ctc.controller;

import org.ctc.dto.Result;
import org.ctc.service.OrderDetailService;
import org.ctc.util.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.ctc.costant.Constance.SUCCESS;

@RestController
@RequestMapping("/order")
public class OrderDetailController {
    private OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService){
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/seller/getOrderDetail/{id}")
    public Result getOrderDetail(@PathVariable Integer id){
        return orderDetailService.getOrderDetailsBySellerId(id);
    }

    @PutMapping("/seller/{ecpayId}/status")
    public Result updateOrderStatus(@PathVariable String ecpayId,@RequestParam Integer buyerId,@RequestParam Integer orderStatus){
        try{
            int updatedRows=orderDetailService.updateOrderStatusByEcpayId(ecpayId,buyerId,orderStatus);

            if(updatedRows>0){
                return new Result(SUCCESS,"Order status updated successfully");
            }else{
                return new Result(404,"Failed to update order status");
            }
        }catch (IllegalArgumentException e){
            return new Result(404,e.getMessage());
        }
    }

    @GetMapping("/seller/incomeData")
    public Result getTotal(@RequestParam int sellerId){
        return new Result(SUCCESS,orderDetailService.getTotalAmounts(sellerId));
    }

    @GetMapping("/seller/totalByDateRange")
    public Result getTotalByDateRange(
            @RequestParam int sellerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) Date endDate){
        return new Result(SUCCESS, orderDetailService.calculateTotalByDateRange(sellerId, startDate, endDate));
    }

    @GetMapping("/seller/getIncomeOrders")
    public Result getIncomeOrders(@RequestParam int sellerId,
                                  @RequestParam(required = false)  String startDate,
                                  @RequestParam(required = false)  String endDate) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date startDateFormed=simpleDateFormat.parse(startDate);
//        Date endDateFormed=simpleDateFormat.parse(endDate);

        Date startDateFormed=null;
        Date endDateFormed=null;

        if(startDate!=null && !startDate.trim().isEmpty()){
            try {
                startDateFormed=simpleDateFormat.parse(startDate);
            }catch (ParseException e){
                return new Result(400,"Invalid start date format");
            }
        }
        if(endDate!=null && !endDate.trim().isEmpty()){
            try {
                endDateFormed=simpleDateFormat.parse(endDate);
            }catch (ParseException e){
                return new Result(400,"Invalid start date format");
            }
        }


        Date currentDate=new Date();
        if(endDateFormed!=null && startDateFormed.after(currentDate)){
            return new Result(400,"End date cannot be in the future.");
        }


        if(startDateFormed==null){
            startDateFormed= DateUtils.getMinDate();
        }

        if(endDateFormed==null){
            endDateFormed=DateUtils.getMaxDate();
        }

        return new Result(SUCCESS,orderDetailService.getIncomeOrders(sellerId,startDateFormed,endDateFormed));
    }
    //買方
    @GetMapping("/buyer/getOrderDetail/{id}")
    public Result getOrderDetailsByBuyerId(@PathVariable Integer id){
        return orderDetailService.getOrderDetailsByBuyerId(id);
    }
    //買方取消訂單
    @PutMapping("/buyer/{userId}/cancelOrder")
    public Result cancelOrder(
            @PathVariable Integer userId,
            @RequestParam String ecpayId,
            @RequestParam Integer buyerId) {
        // 確認 userId 是否為 buyerId
        if (!orderDetailService.isUserBuyer(userId, buyerId)) {
            return new Result(403, "用戶無權取消此訂單。");
        }

        int updatedRows = orderDetailService.cancelOrder(ecpayId, buyerId);

        if (updatedRows > 0) {
            return new Result(SUCCESS, "取消成功");
        } else {
            return new Result(404, "取消失敗");
        }
    }
    //買方完成訂單
    @PutMapping("/buyer/{userId}/RatingOrder")
    public Result ratingOrder(
            @PathVariable Integer userId,
            @RequestParam String ecpayId,
            @RequestParam Integer buyerId) {
        // 確認 userId 是否為 buyerId
        if (!orderDetailService.isUserBuyer(userId, buyerId)) {
            return new Result(403, "無法更改訂單狀態");
        }
        int updatedRows = orderDetailService.ratingOrder(ecpayId, buyerId);

        if (updatedRows > 0) {
            return new Result(SUCCESS, "完成訂單成功");
        } else {
            return new Result(404, "完成訂單失敗");
        }
    }
}

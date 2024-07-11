package org.ctc.service;

import org.ctc.costant.Constance;
import org.ctc.dao.CartItemDao;
import org.ctc.dao.OrderDetailDao;
import org.ctc.dto.*;
import org.ctc.util.DateUtils;
import org.ctc.dto.ecpay.CartDTO;
import org.ctc.dto.ecpay.CartGroup;
import org.ctc.dto.ecpay.OrderTotalDTO;
import org.ctc.dto.ecpay.ProductDTO;
import org.ctc.entity.OrderDetail;
import org.ctc.dto.BuyerOrderDetailDTO;
import org.ctc.dto.BuyerOrderDTO;
import org.ctc.entity.ProdSpec;
import org.ctc.entity.ShipAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

import static org.ctc.costant.Constance.*;

@Service
public class OrderDetailService {
    private final OrderDetailDao orderDetailDao;

    private final CartItemDao cartItemDao;



    @Autowired
    public OrderDetailService(OrderDetailDao orderDetailDao,CartItemDao cartItemDao){
        this.orderDetailDao = orderDetailDao;
        this.cartItemDao=cartItemDao;
    }


    public Result getOrderDetailsBySellerId(Integer sellerId){
    List<OrderDetailDTO> orderDetails=orderDetailDao.findAllOrderDetailBySellerId(sellerId);


    //users url
    Map<Integer, String> userImageUrls = orderDetails.stream()
            .map(OrderDetailDTO::getBuyerId)
            .distinct()
            .collect(Collectors.toMap(
                    userId -> userId,
                    userId ->{
                        String url=orderDetailDao.findUserImageUrlByUserId(userId);
                        return url !=null? url:"";
                    }

            ));

    //products url
    Map<Integer,String> productImageUrls=orderDetails.stream()
            .map(OrderDetailDTO::getProductId)
            .distinct()
            .collect(Collectors.toMap(
                    productId->productId,
                    productId->{
                            String url=orderDetailDao.findProductImageUrlByProductId(productId);
                            return url !=null? url:"";
                    }
            ));

    //combine
            List<OrderDetailDTO> updatedOrderDetails=orderDetails.stream()
                    .map(detail -> new OrderDetailDTO(
                            detail.getProductId(),
                            detail.getProductName(),
                            productImageUrls.get(detail.getProductId()),
                            detail.getSpec(),
                            detail.getSpecName(),
                            detail.getPrice(),
                            detail.getQuantity(),
                            detail.getEcpayId(),
                            detail.getBuyerId(),
                            detail.getUserName(),
                            detail.getOrderStatus(),
                            detail.getShipMethod()
            )).collect(Collectors.toList());

    Map<String, List<OrderDetailDTO>> groupedByEcpayId = updatedOrderDetails.stream()
            .collect(Collectors.groupingBy(OrderDetailDTO::getEcpayId));

    List<OrderDTO> result=groupedByEcpayId.entrySet().stream()
            .map(entry->{
                String ecpayId=entry.getKey();
                List<OrderDetailDTO> products=entry.getValue();
                OrderDetailDTO firstProduct=products.get(0);
                Integer buyerId=firstProduct.getBuyerId();
                String userName=firstProduct.getUserName();
                Integer orderStatus=firstProduct.getOrderStatus();
                Integer shipMethod=firstProduct.getShipMethod();
                String userImageUrl=userImageUrls.get(buyerId);

                return new OrderDTO(ecpayId,buyerId, userName,userImageUrl, orderStatus, shipMethod, products);
            }).collect(Collectors.toList());

        return new Result(SUCCESS,result);
    }

    public int updateOrderStatusByEcpayId(String ecpayId,Integer buyerId,Integer newOrderStatus){
        List<Integer> currentOrderStatuses=orderDetailDao.findOrderStatusByEcpayIdAndBuyerId(ecpayId,buyerId);

        if(currentOrderStatuses.isEmpty()){
            throw new IllegalArgumentException("Invalid ecpayId or buyerId :" + ecpayId +","+buyerId);
        }

        for(Integer currentOrderStatus:currentOrderStatuses){
            if(!isValidOrderStatusTransition(currentOrderStatus,newOrderStatus)){
                throw new IllegalArgumentException("Invalid status transition from "+ currentOrderStatus + "to" +newOrderStatus);
            }
        }
            return orderDetailDao.updateOrderStatusByEcpayIdAndBuyerId(ecpayId,buyerId,newOrderStatus);

        }

    private boolean isValidOrderStatusTransition(Integer currentStatus, Integer newStatus) {
        return (currentStatus == Constance.PENDING_SHIPMENT && newStatus == Constance.IN_TRANSIT) ||
                (currentStatus == Constance.IN_TRANSIT && newStatus == Constance.COMPLETED);
    }




    public Integer calculateTotalByDateRange(int sellerId, Date startDate, Date endDate){
        return orderDetailDao.findTotalByDateRange(sellerId,startDate,endDate);
    }

    public TotalAmountDTO getTotalAmounts(int sellerId){
        Integer totalIncome=orderDetailDao.findTotalIncome(sellerId);

        Date startOfWeek= DateUtils.getStartOfWeek();
        Date endOfWeek=DateUtils.getEndOfWeek();
        Integer weeklyTotal=calculateTotalByDateRange(sellerId,startOfWeek,endOfWeek);

        Date startOfMonth= DateUtils.getStartOfMonth();
        Date endOfMonth=DateUtils.getEndOfMonth();
        Integer monthlyTotal=calculateTotalByDateRange(sellerId,startOfMonth,endOfMonth);

        return new TotalAmountDTO(totalIncome,weeklyTotal,monthlyTotal);
    }

    public List<IncomeOrdersDTO> getIncomeOrders(int sellerId,Date startDate, Date endDate){
        return orderDetailDao.findAllIncomeByOrders(sellerId,startDate,endDate);
    }




    public void generateOrder(List<CartGroup> cartGroupList,String ecpayId,String paymentType,Integer buyerId){


        for(CartGroup cartGroup:cartGroupList){
            ShipAddress shipAddress=cartGroup.getShipAddress();
            Integer sellerId = cartGroup.getSellerId();

            List<CartDTO> cartDTOList=cartGroup.getCartDTOList();
            for(CartDTO cartDTO:cartDTOList){
                ProductDTO thisPrd =cartDTO.getProduct();
                OrderDetail orderDetail = new OrderDetail();
                Integer prdSpecId = cartDTO.getPrdSpecId();
                if(prdSpecId == null){
                    orderDetail.setProductName(thisPrd.getProductName());
                }else {

                    orderDetail.setSpecId(prdSpecId);
                    Optional<ProdSpec> thisPrdSpecOpt = thisPrd.getProdSpecList().stream().filter(item->item.getProdSpecId() == prdSpecId).findFirst();
                    if(thisPrdSpecOpt.isPresent()){
                        ProdSpec prodSpec=thisPrdSpecOpt.get();
                        StringBuffer prdNameBuff = new StringBuffer();
                        prdNameBuff.append(thisPrd.getProductName());
                        prdNameBuff.append(" (");
                        prdNameBuff.append(prodSpec.getSpec1());
                        prdNameBuff.append(":");
                        prdNameBuff.append(prodSpec.getSpec1Name());
                        if(prodSpec.getSpec2()!=null){
                            prdNameBuff.append(",");
                            prdNameBuff.append(prodSpec.getSpec2());
                            prdNameBuff.append(":");
                            prdNameBuff.append(prodSpec.getSpec2Name());
                            prdNameBuff.append(")");
                        }

                        orderDetail.setProductName(prdNameBuff.toString());
                    }
                }
                orderDetail.setOrderDate(new Date());
                orderDetail.setAddress(shipAddress.getAddress());
                orderDetail.setShopName(shipAddress.getShopName());
                orderDetail.setOdId(UUID.randomUUID().toString());
                orderDetail.setEcpayId(ecpayId);
                orderDetail.setPayMethod(paymentType);
                orderDetail.setPrice(thisPrd.getPrice());
                orderDetail.setQuantity(cartDTO.getAmount());
                orderDetail.setSellerId(sellerId);
                orderDetail.setShipMethod(shipAddress.getShipType());
                orderDetail.setBuyerId(buyerId);
                orderDetail.setOrderStatus(PENDING_SHIPMENT);

                orderDetail.setProductId(thisPrd.getProductId());
                orderDetailDao.save(orderDetail);
                cartItemDao.deleteById(cartDTO.getCartItemId());
            }
        }


    }


    public Result getOrderDetailByEcpay(String ecpayId){
        System.out.println(ecpayId);
        List<OrderDetail> orderDetailList=orderDetailDao.findByEcpayId(ecpayId);
        System.out.println(orderDetailList);
        Integer total=0;
        for(OrderDetail orderDetail:orderDetailList){
            total+=(orderDetail.getQuantity()*orderDetail.getPrice());
        }

        return new Result<>(SUCCESS,new OrderTotalDTO(orderDetailList,total));
    }

    //買方OrderDetail===================================================================
    public Result getOrderDetailsByBuyerId(Integer buyerId) {
        List<BuyerOrderDetailDTO> orderDetails = orderDetailDao.findAllOrderDetailByBuyerId(buyerId);

            //products url
            Map<Integer,String> productImageUrls=orderDetails.stream()
                    .map(BuyerOrderDetailDTO::getProductId)
                    .distinct()
                    .collect(Collectors.toMap(
                            productId->productId,
                            productId->{
                                String url=orderDetailDao.findProductImageUrlByProductId(productId);
                                return url !=null? url:"";
                            }
                    ));

        //combine
        List<BuyerOrderDetailDTO> updatedOrderDetails=orderDetails.stream()
                .map(detail -> new BuyerOrderDetailDTO(
                        detail.getProductId(),
                        detail.getProductName(),
                        productImageUrls.get(detail.getProductId()),
                        detail.getSpec(),
                        detail.getSpecName(),
                        detail.getPrice(),
                        detail.getQuantity(),
                        detail.getEcpayId(),
                        detail.getSellerId(),
                        detail.getSellerName(),
                        detail.getSellerImage(),
                        detail.getOrderStatus(),
                        detail.getShipMethod()
                )).collect(Collectors.toList());

        Map<String, List<BuyerOrderDetailDTO>> groupedByEcpayId = updatedOrderDetails.stream()
                .collect(Collectors.groupingBy(BuyerOrderDetailDTO::getEcpayId));

        List<BuyerOrderDTO> result=groupedByEcpayId.entrySet().stream()
                .map(entry->{
                    String ecpayId=entry.getKey();
                    List<BuyerOrderDetailDTO> products=entry.getValue();
                    BuyerOrderDetailDTO firstProduct=products.get(0);
                    Integer sellerId=firstProduct.getSellerId();
                    String userName=firstProduct.getSellerName();
                    Integer orderStatus=firstProduct.getOrderStatus();
                    Integer shipMethod=firstProduct.getShipMethod();
                    String sellerImage=firstProduct.getSellerImage();

                    return new BuyerOrderDTO(ecpayId, sellerId, userName, sellerImage, orderStatus, shipMethod, products);
                }).collect(Collectors.toList());

        return new Result(SUCCESS,result);
    }

    public boolean isUserBuyer(Integer userId, Integer buyerId) {
        return userId.equals(buyerId);
    }

    // 取消訂單的方法
    public int cancelOrder(String ecpayId, Integer buyerId) {
        // 更新訂單狀態為取消 (3)
        return orderDetailDao.cancelOrderStatusByEcpayIdAndBuyerId(ecpayId, buyerId, 5);
    }

    // 改變訂單為待評價
    public int ratingOrder(String ecpayId, Integer buyerId) {
        return orderDetailDao.cancelOrderStatusByEcpayIdAndBuyerId(ecpayId, buyerId, 3);
    }


}

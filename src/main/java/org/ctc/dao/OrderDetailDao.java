package org.ctc.dao;

import org.ctc.dto.BuyerOrderDetailDTO;
import org.ctc.dto.IncomeOrdersDTO;
import org.ctc.dto.OrderDetailDTO;
import org.ctc.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailDao extends JpaRepository<OrderDetail,Integer> {
        @Query(
            "SELECT new org.ctc.dto.OrderDetailDTO(o.productId,o.productName,null, o.spec, o.specName, o.price, o.quantity,o.ecpayId,o.buyerId,buyer.userName, o.orderStatus, o.shipMethod) " +
                    "FROM OrderDetail o " +
                    "JOIN Users buyer ON buyer.userId = o.buyerId "+
                    "WHERE o.sellerId= :sellerId"
        )
        List<OrderDetailDTO> findAllOrderDetailBySellerId(@Param("sellerId") Integer sellerId);


        @Query("SELECT CONCAT('http://54.199.192.205/shopHub/', 'shop',i.sourceType, i.sourceId, '-', i.imageId, '.', i.extension) " +
                "FROM Image i " +
                "WHERE i.sourceId = :userId AND i.sourceType = 'u'")
        String findUserImageUrlByUserId(@Param("userId") Integer userId);

        @Query("SELECT CONCAT('http://54.199.192.205/shopHub/', 'shop',i.sourceType, i.sourceId, '-', i.imageId, '.', i.extension) " +
                "FROM Image i " +
                "WHERE i.sourceId = :productId AND i.sourceType = 'p'")
        String findProductImageUrlByProductId(@Param("productId") Integer productId);


        @Query("SELECT o.orderStatus FROM OrderDetail o WHERE o.ecpayId=:ecpayId AND o.buyerId=:buyerId")
        List<Integer> findOrderStatusByEcpayIdAndBuyerId(@Param("ecpayId") String ecpayId,@Param("buyerId") Integer buyerId);

        @Modifying
        @Transactional
        @Query("UPDATE OrderDetail o SET o.orderStatus= :orderStatus WHERE o.ecpayId= :ecpayId AND o.buyerId= :buyerId")
        int updateOrderStatusByEcpayIdAndBuyerId(@Param("ecpayId") String ecpayId,@Param("buyerId") Integer buyerId,@Param("orderStatus") Integer orderStatus);

        List<OrderDetail> findByEcpayId(@Param("ecpayId") String ecpayId);


        @Query("SELECT SUM(od.price*od.quantity) FROM OrderDetail od"+
                " WHERE od.sellerId= :sellerId "+
                "AND od.orderStatus=2"
        )
        Integer findTotalIncome(@Param("sellerId") int sellerId);

        @Query("SELECT SUM(od.price*od.quantity) FROM OrderDetail od"+
                " WHERE od.sellerId=:sellerId "+
                "AND od.orderStatus=2 "+
                "AND od.orderDate BETWEEN :startDate AND :endDate"
        )
        Integer findTotalByDateRange(@Param("sellerId") int sellerId,
                                        @Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate);

        @Query("SELECT new org.ctc.dto.IncomeOrdersDTO(od.ecpayId,od.orderDate,od.orderStatus,od.payMethod,SUM(od.price*od.quantity))" +
                "FROM OrderDetail od "+
                "WHERE od.sellerId= :sellerId "+
                "AND od.orderStatus=2 "+
                "AND (:startDate IS NULL OR od.orderDate >= :startDate )"+
                "AND (:endDate IS NULL OR od.orderDate <= :endDate)"+
                "GROUP BY od.ecpayId,od.orderDate, od.orderStatus, od.payMethod"
        )
        List<IncomeOrdersDTO> findAllIncomeByOrders(@Param("sellerId") int sellerId,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate
                                                    );

        //買家 , u.sellerImage
        @Query(
                "SELECT new org.ctc.dto.BuyerOrderDetailDTO(o.productId,o.productName, null, o.spec, o.specName, o.price, o.quantity, o.ecpayId, o.sellerId,  u.sellerName,u.sellerImage, o.orderStatus, o.shipMethod) " +
                        "FROM OrderDetail o " +
                        "JOIN Users u ON u.userId = o.sellerId "+
                        "WHERE o.buyerId= :buyerId"
        )
        List<BuyerOrderDetailDTO> findAllOrderDetailByBuyerId(@Param("buyerId") Integer buyerId);

        @Query("SELECT o.orderStatus FROM OrderDetail o WHERE o.ecpayId=:ecpayId AND o.sellerId=:sellerId")
        List<Integer> findOrderStatusByEcpayIdAndSellerId(@Param("ecpayId") String ecpayId,@Param("sellerId") Integer sellerId);

        @Modifying
        @Transactional
        @Query("UPDATE OrderDetail o SET o.orderStatus = :orderStatus WHERE o.ecpayId = :ecpayId AND o.buyerId = :buyerId")
        int cancelOrderStatusByEcpayIdAndBuyerId(@Param("ecpayId") String ecpayId, @Param("buyerId") Integer buyerId, @Param("orderStatus") Integer orderStatus);

        @Modifying
        @Transactional
        @Query("UPDATE OrderDetail o SET o.orderStatus = :orderStatus WHERE o.ecpayId = :ecpayId AND o.buyerId = :buyerId")
        int changeOrderStatusToRating(@Param("ecpayId") String ecpayId, @Param("buyerId") Integer buyerId, @Param("orderStatus") Integer orderStatus);
}

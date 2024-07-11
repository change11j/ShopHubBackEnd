package org.ctc.service;

import org.ctc.dao.OrderDetailDao;
import org.ctc.dao.RatingDao;
import org.ctc.dto.RatingDTO;
import org.ctc.entity.OrderDetail;
import org.ctc.entity.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingDao ratingDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    public Rating createRating(RatingDTO ratingDTO){
        Rating rating = new Rating();
        rating.setEcpayId(ratingDTO.getEcpayId());
        rating.setBuyerId(ratingDTO.getBuyerId());
        rating.setProductRating(ratingDTO.getProductRating());
        rating.setSellerRating(ratingDTO.getSellerRating());
        rating.setShippingRating(ratingDTO.getShippingRating());
        rating.setRatingDate(new Date( ));
        rating.setComment(ratingDTO.getComment());

        Rating savedRating = ratingDao.save(rating);

        // 使用ecpayId更新OrderDetail的orderStatus
        List<OrderDetail> orderDetails = orderDetailDao.findByEcpayId(ratingDTO.getEcpayId());
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrderStatus(4); // 设置新的orderStatus
            orderDetailDao.save(orderDetail); // 保存更新后的OrderDetail
        }

        return savedRating;
    }

}

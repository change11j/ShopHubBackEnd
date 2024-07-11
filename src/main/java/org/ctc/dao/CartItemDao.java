package org.ctc.dao;

import org.ctc.dto.ecpay.CartDTO;
import org.ctc.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemDao extends JpaRepository<CartItem,Integer> {

    @Query("SELECT new org.ctc.dto.ecpay.CartDTO(ci.cartItemId, " +
            "new org.ctc.dto.ecpay.ProductDTO(p.productId, p.sellerId, p.discount, p.stock, p.productName, " +
            "p.price, p.productStatus, p.description1, p.description2, p.listingDate, p.categoryId, p.productDetail, " +
            "(SELECT u.sellerName FROM Users u WHERE u.userId = p.sellerId)), " +
            "ci.amount, ci.prdSpecId) " +
            "FROM CartItem ci LEFT JOIN Product p ON ci.productId = p.productId " +
            "WHERE ci.userId = :userId")
public List<CartDTO> getCartItemByUserId(Integer userId);

}

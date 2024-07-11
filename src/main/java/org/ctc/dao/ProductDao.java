package org.ctc.dao;

import org.ctc.dto.AddedProductDTO;
import org.ctc.dto.ProductDetailDTO;
import org.ctc.dto.RatingDTO;
import org.ctc.entity.Product;
import org.ctc.projection.AddedProductDTOProjection;
import org.ctc.projection.CategorySearchProjection;
import org.ctc.projection.ProdDetailProjection;
import org.ctc.projection.RatingDTOProjection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductDao extends JpaRepository<Product,Integer> , JpaSpecificationExecutor<Product> {
    Optional<Product> findById(Integer id);
    Product findByProductName(String name);
    @Query("SELECT p ,ps,od , i " +
            "FROM Product p " +
            "LEFT JOIN ProdSpec ps ON p.productId = ps.productId " +
            "LEFT JOIN OrderDetail od on od.productId = p.productId " +
            "LEFT JOIN Image i ON p.productId = i.sourceId and i.sourceType = 'p' " +
            "WHERE p.sellerId = :sellerId ")
    List<Object[]> findAddedProductDTOBySellerId(Integer sellerId);

    @Query(value = "select p, " +
            "c.categoryName " +
            "from Product p, Category c " +
            "where p.categoryId = c.categoryId " +
            "and p.productId in :productIds")
    List<Object[]> findProductsWithCategoryByIds(List<Integer> productIds);
//    *search方法
    @Query(value = "select p , c ,ps, " +
            "i from Product p " +
            "left join Category c " +
            "on p.categoryId = c.categoryId " +
            "left join ProdSpec ps on p.productId = ps.productId " +
            "left join Image i on i.sourceId=p.productId and i.sourceType='p' " +
            "where p.productName like %:name% " +
            "or c.categoryName like %:name% " +
            "or p.description1 like %:name% " +
            "or p.description2 like %:name%")
    List<Object[]> searchProductsByName(String name);
    @Query(value = "select p as product, c as category, ps as prodSpec ,i as image " +
            "from Product p " +
            "left join Category c on p.categoryId=c.categoryId " +
            "left join ProdSpec as ps on p.productId = ps.productId " +
            "left join Image i on i.sourceId=p.productId and i.sourceType='p'" +
            "where c.categoryName= :cateName")
    List<CategorySearchProjection> searchCategoryProduct(String cateName);
    @Query(value = "select p as product,ps as prodSpec,i as image " +
            "from Product p " +
            "left join ProdSpec ps on p.productId= ps.productId " +
            "left join Image i on i.sourceId=p.productId and i.sourceType='p' " +
            "where p.productId in :productIds")
    List<Object[]> findPcardByProductIds(List<Integer> productIds);


    @Query("select new org.ctc.dto.ProductDetailDTO(p, c, i, ps, u) " +
            "from Product p " +
            "left join Category c on p.categoryId = c.categoryId " +
            "left join Image i on p.productId = i.sourceId " +
            "left join ProdSpec ps on ps.productId = p.productId " +
            "left join Users u on u.userId = p.sellerId " +
            "where p.productId = :productId and i.sourceType = 'p'")
    List<ProductDetailDTO> findProdCateImgSpecUserDtoByPId(Integer productId);

    @Query("select p as product, c as category, i as image, ps as prodSpec, " +
            "u as user ,ui as userImage ,od as orderDetail ,r as rating " +
            "from Product p " +
            "left join Category c on p.categoryId = c.categoryId " +
            "left join Image i on p.productId = i.sourceId and i.sourceType = 'p'" +
            "left join ProdSpec ps on ps.productId = p.productId " +
            "left join Users u on u.userId = p.sellerId " +
            "left join Image ui on u.userId = ui.sourceId and ui.sourceType = 'u' " +
            "left join OrderDetail od on od.productId = p.productId " +
            "left join Rating r on od.buyerId = r.buyerId " +
            "where p.productId = :productId ")
    List<ProdDetailProjection> findProductDetailsByProductId(Integer productId);
    @Query("select new org.ctc.dto.RatingDTO(r.ratingID ," +
            "r.buyerId,r.ratingDate,r.comment,r.productRating,r.sellerRating," +
            "r.shippingRating,u.userName,u.userImage,ps.spec1Name,ps.spec2Name) " +
            "from OrderDetail od " +
            "left join Rating r on od.buyerId=r.buyerId " +
            "left join Users u on u.userId=r.buyerId " +
            "left join ProdSpec ps on od.specId=ps.prodSpecId " +
            "where od.productId = :productId")
    List<RatingDTO>findRatingByProductId(@Param("productId") Integer productId);

    List<Product> findProductsBySellerId(@Param("sellerId") Integer sellerId);
}

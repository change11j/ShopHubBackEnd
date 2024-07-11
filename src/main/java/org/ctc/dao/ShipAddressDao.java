package org.ctc.dao;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.ctc.entity.ShipAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipAddressDao extends JpaRepository<ShipAddress,Integer> {


    public List<ShipAddress> findByUserId(Integer userId);

    List<ShipAddress> findByUserIdAndShipTypeIn(Integer userId, List<Integer> shipTypes);
    // 確認這個方法是否正確定義
    void deleteByUserIdAndShipId(Integer userId, Integer shipId);

    // 添加一個查找方法來檢查條件是否匹配
    Optional<ShipAddress> findByUserIdAndShipId(Integer userId, Integer shipId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ShipAddress sa WHERE sa.userId = :userId AND sa.shipId = :shipId")
    void deleteByUserIdAndShipIdJPQL(@Param("userId") Integer userId, @Param("shipId") Integer shipId);
}

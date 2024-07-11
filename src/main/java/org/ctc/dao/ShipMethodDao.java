package org.ctc.dao;

import org.ctc.entity.ShipMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipMethodDao extends JpaRepository<ShipMethod,Integer> {
    @Query("SELECT sm.shipMethod from ShipMethod sm where sm.sellerId = :sellerId")
    public List<Integer> getUserShipTypes(Integer sellerId);

}

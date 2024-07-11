package org.ctc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ShipMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer smId;

    private Integer sellerId;

    private Integer shipMethod;

    public ShipMethod(Integer shipMethod) {
        this.shipMethod = shipMethod;
    }

    public ShipMethod() {
    }

    public ShipMethod(Integer smId, Integer sellerId, Integer shipMethod) {
        this.smId = smId;
        this.sellerId = sellerId;
        this.shipMethod = shipMethod;
    }
}

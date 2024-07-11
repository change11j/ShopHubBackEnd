package org.ctc.service;

import org.ctc.dto.MartDTO;
import org.ctc.dto.ShipAddressDTO;
import org.ctc.dto.Result;
import org.ctc.entity.ShipAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.ctc.dao.ShipAddressDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.ctc.costant.Constance.*;

@Service
public class ShipAddressService {
    private ShipAddressDao shipAddressDao;

    public ShipAddressService(ShipAddressDao shipAddressDao) {
        this.shipAddressDao = shipAddressDao;
    }

    public Result createAddress(ShipAddress shipAddress){
        ShipAddress save = null;
            save = shipAddressDao.save(shipAddress);

        return new Result(SUCCESS,save);
    }
    public Result getAddress(Integer id) {
        List<ShipAddress> addressOptional = shipAddressDao.findByUserId(id);
            return new Result(SUCCESS, addressOptional);

    }

    public Result updateAddress(Integer id, ShipAddress shipAddress) {
        Optional<ShipAddress> addressOptional = shipAddressDao.findById(id);
        if (addressOptional.isPresent()) {
            ShipAddress existingShipAddress = addressOptional.get();
            BeanUtils.copyProperties(shipAddress, existingShipAddress, "id");
            ShipAddress updatedShipAddress = shipAddressDao.save(existingShipAddress);
            return new Result(SUCCESS, updatedShipAddress);
        } else {
            return new Result(ADDRESS_NOT_EXIST, null);
        }
    }

    public Result deleteAddress(Integer id) {
        Optional<ShipAddress> addressOptional = shipAddressDao.findById(id);
        if (addressOptional.isPresent()) {
            shipAddressDao.deleteById(id);
            return new Result(SUCCESS, null);
        } else {
            return new Result(ADDRESS_NOT_EXIST, null);
        }
    }

    public ShipAddress saveMartData(MartDTO martDTO) {
        ShipAddress shipAddress = new ShipAddress();

        shipAddress.setUserId(martDTO.getUserId());
        shipAddress.setRecipientName(martDTO.getRecipientName());
        shipAddress.setRecipientPhone(martDTO.getRecipientPhone());
        shipAddress.setShopName(martDTO.getShopName());
        shipAddress.setAddress(martDTO.getAddress());
        shipAddress.setShipType(martDTO.getShipType());

        ShipAddress savedShipAddress = shipAddressDao.save(shipAddress);

        return savedShipAddress;
    }

    public List<ShipAddress> getAddressesByUserIdAndShipType(Integer userId, List<Integer> shipTypes) {
        return shipAddressDao.findByUserIdAndShipTypeIn(userId, shipTypes);
    }

    @Transactional
    public Result deleteAddress(Integer userId, Integer shipId) {
        try {
            // 檢查記錄是否存在
            Optional<ShipAddress> shipAddressOptional = shipAddressDao.findByUserIdAndShipId(userId, shipId);
            if (shipAddressOptional.isPresent()) {
                // 使用自定義的 JPQL 方法來刪除記錄
                shipAddressDao.deleteByUserIdAndShipIdJPQL(userId, shipId);
                return new Result(SUCCESS);
            } else {
                return new Result(USER_NOT_EXIST);
            }
        } catch (Exception e) {
            return new Result(ADDRESS_NOT_EXIST);
        }
    }
}
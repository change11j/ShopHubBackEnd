package org.ctc.controller;

import org.ctc.dto.MartDTO;
import org.ctc.dto.Result;
import org.ctc.entity.ShipAddress;
import org.ctc.service.ShipAddressService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    private ShipAddressService shipAddressService;

    public AddressController(ShipAddressService shipAddressService) {
        this.shipAddressService = shipAddressService;
    }

    @PostMapping("/createAddress")
    public Result createAddress(@RequestBody ShipAddress shipAddress) {
        System.out.println(shipAddress);
        return shipAddressService.createAddress(shipAddress);
    }

    @GetMapping("/getAddress/{id}")
    public Result getAddress(@PathVariable Integer id) {
        return shipAddressService.getAddress(id);
    }

    @PutMapping("/updateAddress/{id}")
    public Result updateAddress(@PathVariable Integer id, @RequestBody ShipAddress shipAddress) {
        return shipAddressService.updateAddress(id, shipAddress);
    }

    @DeleteMapping("/deleteAddress/{id}")
    public Result deleteAddress(@PathVariable Integer id) {
        return shipAddressService.deleteAddress(id);
    }

    @PostMapping("/savemart")
    public ShipAddress saveMartData(@RequestBody MartDTO martDTO) {
        return shipAddressService.saveMartData(martDTO);
    }

    @GetMapping("/getmart")
    public List<ShipAddress> getAddressesByUserIdAndShipType(@RequestParam Integer userId) {
        List<Integer> shipTypes = Arrays.asList(1, 2); // 1 for 7-11, 2 for Family Mart
        return shipAddressService.getAddressesByUserIdAndShipType(userId, shipTypes);
    }

    @DeleteMapping("/deletemart")
    public Result deleteShipAddress(
            @RequestParam Integer userId,
            @RequestParam Integer shipId
    ) {
        return shipAddressService.deleteAddress(userId, shipId);
    }
}

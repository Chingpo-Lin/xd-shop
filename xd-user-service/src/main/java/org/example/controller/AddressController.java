package org.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.mapper.AddressMapper;
import org.example.model.AddressDO;
import org.example.service.AddressService;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * address controller
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
@Api(tags="receive address")
@RestController
@RequestMapping("/api/address/v1/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @ApiOperation("find address detail by id")
    @GetMapping("find/{address_id}")
    public Object detail(
            @ApiParam(value = "address id", required = true)
            @PathVariable("address_id") Long addressId) {
        AddressDO addressDO = addressService.detail(addressId);

        return JsonData.buildSuccess(addressDO);
    }
}


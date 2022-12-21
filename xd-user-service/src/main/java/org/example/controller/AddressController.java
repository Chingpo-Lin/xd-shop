package org.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.enums.BizCodeEnum;
import org.example.exception.BizException;
import org.example.mapper.AddressMapper;
import org.example.model.AddressDO;
import org.example.request.AddressAddRequest;
import org.example.service.AddressService;
import org.example.utils.JsonData;
import org.example.vo.AddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * address controller
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
@Api(tags="receive address module")
@RestController
@RequestMapping("/api/address/v1/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @ApiOperation("add receive address")
    @PostMapping("add")
    public JsonData add(@ApiParam("address object") @RequestBody AddressAddRequest addressAddRequest) {
        int rows = addressService.add(addressAddRequest);
        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildError("insert error");
    }

    @ApiOperation("find address detail by id")
    @GetMapping("find/{address_id}")
    public Object detail(
            @ApiParam(value = "address id", required = true)
            @PathVariable("address_id") Long addressId) {
        AddressVO addressVO = addressService.detail(addressId);
//        if (addressId == 1) {
//            throw new BizException(-1, "test");
//        }
        return addressVO == null ? JsonData.buildResult(BizCodeEnum.ADDRESS_NOT_EXIST) : JsonData.buildSuccess(addressVO);
    }

    @DeleteMapping("/del/{address_id}")
    public JsonData del(
            @ApiParam(value = "address id", required = true)
            @PathVariable("address_id") int addressId) {

        int rows = addressService.del(addressId);

        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildResult(BizCodeEnum.ADDRESS_DEL_FAIL);
    }

    /**
     * find all address of current user
     * @return
     */
    @ApiOperation("list all current user address")
    @GetMapping("/list")
    public JsonData listAllUserAddress() {
        List<AddressVO> addressVOList = addressService.listAllUserAddress();
        return JsonData.buildSuccess(addressVOList);
    }
}


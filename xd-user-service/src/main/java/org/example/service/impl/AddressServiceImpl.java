package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.AddressStatusEnum;
import org.example.interceptor.LoginInterceptor;
import org.example.mapper.AddressMapper;
import org.example.model.AddressDO;
import org.example.model.LoginUser;
import org.example.request.AddressAddRequest;
import org.example.service.AddressService;
import org.example.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public AddressVO detail(Long id) {
        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id", id));
        if (addressDO == null) {
            return null;
        }
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(addressDO, addressVO);

        return addressVO;
    }

    /**
     * add address
     *
     * @param addressAddRequest
     * @return
     */
    @Override
    public int add(AddressAddRequest addressAddRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        AddressDO addressDO = new AddressDO();
        addressDO.setCreateTime(new Date());
        addressDO.setUserId(loginUser.getId());
        BeanUtils.copyProperties(addressAddRequest, addressDO);

        // if has default address
        if (addressDO.getDefaultStatus() == AddressStatusEnum.DEFAULT_STATUS.getStatus()) {
            // check db if there is default
            AddressDO defaultAddressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>()
                    .eq("user_id", loginUser.getId())
                    .eq("default_status", AddressStatusEnum.DEFAULT_STATUS.getStatus()));
            if (defaultAddressDO != null) {
                // change last default to not default
                defaultAddressDO.setDefaultStatus(AddressStatusEnum.COMMON_STATUS.getStatus());
                addressMapper.update(defaultAddressDO,
                        new QueryWrapper<AddressDO>().eq("id", defaultAddressDO.getId()));
            }
        }

        int rows = addressMapper.insert(addressDO);
        log.info("add receive address, data={}", addressDO);
        return rows;
    }

    /**
     * delete address by id
     * @param addressId
     * @return
     */
    @Override
    public int del(int addressId) {

        addressMapper.delete(new QueryWrapper<AddressDO>().eq("id", addressId));

        return 0;
    }

}

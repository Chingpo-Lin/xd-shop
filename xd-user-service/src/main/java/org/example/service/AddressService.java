package org.example.service;

import org.example.model.AddressDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.request.AddressAddRequest;
import org.example.vo.AddressVO;

/**
 * <p>
 * address service interface
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
public interface AddressService {

    AddressVO detail(Long id);

    /**
     * add address
     * @param addressAddRequest
     * @return
     */
    int add(AddressAddRequest addressAddRequest);

    /**
     * delete address by id
     * @param addressId
     * @return
     */
    int del(int addressId);
}

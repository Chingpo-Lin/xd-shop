package org.example.service.impl;

import org.example.model.AddressDO;
import org.example.mapper.AddressMapper;
import org.example.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * address service impl
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressDO> implements AddressService {

}

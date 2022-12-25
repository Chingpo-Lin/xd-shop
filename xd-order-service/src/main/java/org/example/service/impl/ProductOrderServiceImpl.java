package org.example.service.impl;

import org.example.model.ProductOrderDO;
import org.example.mapper.ProductOrderMapper;
import org.example.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bob
 * @since 2022-12-25
 */
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrderDO> implements ProductOrderService {

}

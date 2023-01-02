package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.model.ProductOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bob
 * @since 2022-12-25
 */
public interface ProductOrderMapper extends BaseMapper<ProductOrderDO> {

    /**
     * update order state
     * @param outTradeNo
     * @param newState
     * @param state
     */
    void updateOrderPayState(@Param("outTradeNo") String outTradeNo,
                             @Param("newState") String newState,
                             @Param("oldState") String state);
}

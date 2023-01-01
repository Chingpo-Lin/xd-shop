package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.model.ProductOrderItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bob
 * @since 2022-12-25
 */
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItemDO> {

    /**
     * insert order item
     * @param list
     */
    void insertBatch(@Param("orderItemList") List<ProductOrderItemDO> list);
}

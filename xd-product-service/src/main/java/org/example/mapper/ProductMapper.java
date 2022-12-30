package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.model.ProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bob
 * @since 2022-12-24
 */
public interface ProductMapper extends BaseMapper<ProductDO> {

    /**
     * lock product stock
     * @param productId
     * @param buyNum
     * @return
     */
    int lockProductStock(@Param("productId") long productId,
                         @Param("buyNum")int buyNum);
}

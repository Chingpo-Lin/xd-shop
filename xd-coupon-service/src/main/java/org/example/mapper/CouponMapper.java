package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.model.CouponDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */
public interface CouponMapper extends BaseMapper<CouponDO> {

    /**
     * deduct stock
     * @param couponId
     * @return
     */
    int reduceStock(@Param("couponId") long couponId);
}

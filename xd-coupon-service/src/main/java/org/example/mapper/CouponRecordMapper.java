package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.model.CouponRecordDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */
public interface CouponRecordMapper extends BaseMapper<CouponRecordDO> {

    /**
     * update coupon use record
     * @param userId
     * @param useState
     * @param lockCouponRecordIds
     * @return
     */
    int lockUseStateBatch(
            @Param("userId") Long userId,
            @Param("useState") String useState,
            @Param("lockCouponRecordIds") List<Long> lockCouponRecordIds);

    /**
     * update coupon use state
     * @param couponRecordId
     * @param name
     */
    void updateState(
            @Param("couponRecordId") Long couponRecordId,
            @Param("name") String name);
}

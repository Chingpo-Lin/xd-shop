package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.model.CouponTaskDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bob
 * @since 2022-12-28
 */
public interface CouponTaskMapper extends BaseMapper<CouponTaskDO> {

    /**
     * insert batch
     * @param couponTaskDOList
     * @return
     */
    int insertBatch(@Param("couponTaskList") List<CouponTaskDO> couponTaskDOList);
}

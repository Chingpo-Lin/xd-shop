package org.example.service;

import org.example.model.CouponDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */
public interface CouponService {

    /**
     * pagination for coupon
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> pageCouponActivity(int page, int size);
}

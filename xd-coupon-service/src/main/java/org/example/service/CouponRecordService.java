package org.example.service;

import org.example.model.CouponRecordDO;
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
public interface CouponRecordService {

    /**
     * paging
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> page(int page, int size);

}

package org.example.service;

import org.example.model.CouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.request.LockCouponRecordRequest;
import org.example.utils.JsonData;
import org.example.vo.CouponRecordVO;

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

    /**
     * find detail by record id
     * @param recordId
     * @return
     */
    CouponRecordVO findById(long recordId);

    /**
     * lock coupon
     * @param recordRequest
     * @return
     */
    JsonData lockCouponRecords(LockCouponRecordRequest recordRequest);
}

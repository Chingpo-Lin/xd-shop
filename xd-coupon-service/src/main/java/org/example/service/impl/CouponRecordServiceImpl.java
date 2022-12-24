package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.interceptor.LoginInterceptor;
import org.example.model.CouponRecordDO;
import org.example.mapper.CouponRecordMapper;
import org.example.model.LoginUser;
import org.example.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo.CouponRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */
@Service
@Slf4j
public class CouponRecordServiceImpl implements CouponRecordService {

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    /**
     * paging
     * @param page
     * @param size
     * @return
     */
    @Override
    public Map<String, Object> page(int page, int size) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        // encapsulate page info
        Page<CouponRecordDO> pageInfo = new Page<CouponRecordDO>(page, size);

        IPage<CouponRecordDO> recordDOIPage = couponRecordMapper.selectPage(pageInfo,
                new QueryWrapper<CouponRecordDO>().eq("user_id", loginUser.getId())
                        .orderByDesc("create_time"));

        Map<String, Object> pageMap = new HashMap<>(3);

        pageMap.put("total_record", recordDOIPage.getTotal());
        pageMap.put("total_page", recordDOIPage.getPages());
        pageMap.put("current_data", recordDOIPage.getRecords().stream()
                .map(obj -> beanProcess(obj)).collect(Collectors.toList()));

        return pageMap;
    }

    private CouponRecordVO beanProcess(CouponRecordDO couponRecordDO) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecordDO, couponRecordVO);
        return couponRecordVO;
    }
}

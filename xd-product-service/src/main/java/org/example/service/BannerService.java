package org.example.service;

import org.example.model.BannerDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.vo.BannerVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bob
 * @since 2022-12-24
 */
public interface BannerService {

    List<BannerVO> list();

}

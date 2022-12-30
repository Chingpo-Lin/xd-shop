package org.example.service;

import org.example.model.ProductDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.request.LockProductRequest;
import org.example.utils.JsonData;
import org.example.vo.ProductVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bob
 * @since 2022-12-24
 */
public interface ProductService {

    /**
     * paging product list
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> page(int page, int size);

    /**
     * find product detail by id
     * @param productId
     * @return
     */
    ProductVO findProductById(long productId);

    /**
     * find product by ids
     * @param productIdList
     * @return
     */
    List<ProductVO> findProductByIdBatch(List<Long> productIdList);

    /**
     * lock product stock
     * @param lockProductRequest
     * @return
     */
    JsonData lockProductStock(LockProductRequest lockProductRequest);
}

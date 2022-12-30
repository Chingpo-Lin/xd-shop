package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BizCodeEnum;
import org.example.enums.StockTaskStateEnum;
import org.example.exception.BizException;
import org.example.mapper.ProductTaskMapper;
import org.example.model.ProductDO;
import org.example.mapper.ProductMapper;
import org.example.model.ProductTaskDO;
import org.example.request.LockProductRequest;
import org.example.request.OrderItemRequest;
import org.example.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.utils.JsonData;
import org.example.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bob
 * @since 2022-12-24
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTaskMapper productTaskMapper;

    /**
     * paging
     * @param page
     * @param size
     * @return
     */
    @Override
    public Map<String, Object> page(int page, int size) {

        Page<ProductDO> pageInfo = new Page<>(page, size);

        IPage<ProductDO> productDOIPage = productMapper.selectPage(pageInfo, null);

        Map<String, Object> pageResult = new HashMap<>(3);
        pageResult.put("total_record", productDOIPage.getTotal());
        pageResult.put("total_page", productDOIPage.getPages());
        pageResult.put("current_data", productDOIPage.getRecords()
                .stream().map(obj -> beanProcess(obj)).collect(Collectors.toList()));

        return pageResult;
    }

    /**
     * find product detail by id
     * @param productId
     * @return
     */
    @Override
    public ProductVO findProductById(long productId) {

        ProductDO productDO = productMapper.selectById(productId);
        return beanProcess(productDO);
    }

    @Override
    public List<ProductVO> findProductByIdBatch(List<Long> productIdList) {

        List<ProductDO> productDOList = productMapper.selectList(new QueryWrapper<ProductDO>()
                .in("id", productIdList));

        List<ProductVO> productVOList = productDOList.stream()
                .map(obj -> beanProcess(obj)).collect(Collectors.toList());
        return productVOList;
    }

    /**
     * lock product stock
     *
     * 1. loop all products, lock all product buy num
     * 2. send delayed msg after each lock
     * @param lockProductRequest
     * @return
     */
    @Override
    public JsonData lockProductStock(LockProductRequest lockProductRequest) {
        String outTradeNo = lockProductRequest.getOrderOutTradeNo();
        List<OrderItemRequest> itemList = lockProductRequest.getOrderItemList();

        // use stream get all ids and add to list
        List<Long> productIdList = itemList.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());

        // find batch
        List<ProductVO> productVOList = this.findProductByIdBatch(productIdList);

        Map<Long, ProductVO> productMap = productVOList.stream()
                .collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        for (OrderItemRequest item: itemList) {
            int rows = productMapper.lockProductStock(item.getProductId(), item.getBuyNum());
            if (rows != 1) {
                throw new BizException(BizCodeEnum.PRODUCT_LOCK_FAIL);
            } else {
                // insert product task
                ProductVO productVO = productMap.get(item.getProductId());
                ProductTaskDO productTaskDO = new ProductTaskDO();
                productTaskDO.setBuyNum(item.getBuyNum());
                productTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
                productTaskDO.setProductId(item.getProductId());
                productTaskDO.setProductName(productVO.getTitle());
                productTaskDO.setOutTradeNo(outTradeNo);
                productTaskMapper.insert(productTaskDO);

                // send mq delay msg, introduce product stock TODO
            }
        }
        return JsonData.buildSuccess();
    }

    private ProductVO beanProcess(ProductDO productDO) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);
        productVO.setStock(productDO.getStock() - productDO.getLockStock());
        return productVO;
    }

}

package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.config.RabbitMQConfig;
import org.example.enums.BizCodeEnum;
import org.example.enums.ProductOrderStateEnum;
import org.example.enums.StockTaskStateEnum;
import org.example.exception.BizException;

import org.example.feign.ProductOrderFeignService;
import org.example.mapper.ProductTaskMapper;
import org.example.model.ProductDO;
import org.example.mapper.ProductMapper;
import org.example.model.ProductMessage;
import org.example.model.ProductTaskDO;
import org.example.request.LockProductRequest;
import org.example.request.OrderItemRequest;
import org.example.service.ProductService;
import org.example.utils.JsonData;
import org.example.vo.ProductVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private ProductOrderFeignService productOrderFeignService;

    /**
     * paging
     * @param page
     * @param size
     * @return
     */
    @Override
    @Cacheable(value = {"product"}, key = "#root.methodName + #page + '_' + #size")
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
                log.info("product stock insert task successfully:{}", productTaskDO);

                // send mq delay msg, introduce product stock
                ProductMessage productMessage = new ProductMessage();
                productMessage.setOutTradeNo(outTradeNo);
                productMessage.setTaskId(productTaskDO.getId());

                rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),
                        rabbitMQConfig.getStockReleaseDelayRoutingKey(), productMessage);

                log.info("product stock lock msg send successfully:{}", productMessage);

            }
        }
        return JsonData.buildSuccess();
    }

    /**
     * release product stock
     * @param productMessage
     * @return
     */
    @Override
    public boolean releaseProductStock(ProductMessage productMessage) {
        ProductTaskDO productTaskDO = productTaskMapper.selectOne(new QueryWrapper<ProductTaskDO>()
                .eq("id", productMessage.getTaskId()));

        if (productTaskDO == null) {
            log.warn("task not exist:{}", productMessage);
        }

        // handle only lock
        if (productTaskDO.getLockState().equalsIgnoreCase(StockTaskStateEnum.LOCK.name())) {
            // query order state
            log.info("enter here:{}", productMessage.getOutTradeNo());
            JsonData jsonData = productOrderFeignService.queryProductOrderState(productMessage.getOutTradeNo());
            log.info("get data:{}", jsonData);

            if (jsonData.getCode() == 0) {
                String state = jsonData.getData().toString();

                if (state.equalsIgnoreCase(ProductOrderStateEnum.NEW.name())) {
                    log.warn("order is new, requeue, msg={}", productMessage);
                    return false;
                }

                if (state.equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
                    productTaskDO.setLockState(StockTaskStateEnum.FINISH.name());
                    productTaskMapper.update(productTaskDO, new QueryWrapper<ProductTaskDO>().eq("id", productMessage.getTaskId()));
                    log.warn("order is payed, change stock to FINISH, msg={}", productMessage);
                    return true;
                }
            }

            log.warn("order is cancel or not exist, change task to CANCEL, renew stock, message:{}", productMessage);
            productTaskDO.setLockState(StockTaskStateEnum.CANCEL.name());
            productTaskMapper.update(productTaskDO, new QueryWrapper<ProductTaskDO>()
                    .eq("id", productMessage.getTaskId()));

            // recover stock, lock stock - current buy num
            productMapper.unlockProductStock(productTaskDO.getProductId(), productTaskDO.getBuyNum());
;
            return true;
        } else {
            // not lock
            log.warn("order not lock, state={}, msg={}", productTaskDO.getLockState(), productMessage);
        }
        return true;
    }

    private ProductVO beanProcess(ProductDO productDO) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);
        productVO.setStock(productDO.getStock() - productDO.getLockStock());
        return productVO;
    }

}

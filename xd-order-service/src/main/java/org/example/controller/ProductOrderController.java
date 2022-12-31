package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.example.enums.BizCodeEnum;
import org.example.enums.ProductOrderPayTypeEnum;
import org.example.request.ConfirmOrderRequest;
import org.example.service.ProductOrderService;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bob
 * @since 2022-12-25
 */
@Api("order module")
@RestController
@Slf4j
@RequestMapping("/api/order/v1")
public class ProductOrderController {

    @Autowired
    private ProductOrderService productOrderService;

    /**
     * query order state
     *
     * this api will not be intercepted, can add key for privacy
     * @param outTradeNo
     * @return
     */
    @ApiOperation("query order state")
    @GetMapping("query_state")
    public JsonData queryProductOrderState(
            @ApiParam("order number")
            @RequestParam("out_trade_no") String outTradeNo) {
        log.info("query here with out trade no:{}", outTradeNo);
        String state = productOrderService.queryProductOrderState(outTradeNo);
        log.info("query here with state:{}", state);
        return StringUtils.isEmpty(state) ? JsonData.buildResult(BizCodeEnum.ORDER_NOT_EXIST) : JsonData.buildSuccess(state);
    }

    @ApiOperation("confirm order")
    @PostMapping("confirm")
    public void confirmOrder(
            @ApiParam("confirm order request object")
            @RequestBody ConfirmOrderRequest confirmOrderRequest,
            HttpServletResponse response) {
        JsonData jsonData = productOrderService.confirmOrder(confirmOrderRequest);

        if (jsonData.getCode() == 0) {
            // different client has different payment jump logic
            String client = confirmOrderRequest.getClientType();
            String payType = confirmOrderRequest.getPayType();

            if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.CREDIT.name())) {
                log.info("pay with credit", confirmOrderRequest.toString());
            } else if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.DEBIT.name())) {
                log.info("pay with debit", confirmOrderRequest.toString());
            }

            log.info("create order success:{}", confirmOrderRequest.toString());
            writeData(response, jsonData);
        } else {
            log.error("create order fail:{}", jsonData.toString());
        }
    }

    private void writeData(HttpServletResponse response, JsonData jsonData) {
        try {
            response.setContentType("test/html;charset=UTF8");
            response.getWriter().write(jsonData.getData().toString());
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            log.error("html error:{}", e);
        }
    }
}


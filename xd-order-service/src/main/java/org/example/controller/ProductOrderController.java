package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.ProductOrderPayTypeEnum;
import org.example.request.ConfirmOrderRequest;
import org.example.service.ProductOrderService;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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


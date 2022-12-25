package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.service.ProductService;
import org.example.utils.JsonData;
import org.example.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bob
 * @since 2022-12-24
 */
@RestController
@Api("Product Module")
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("paging product list")
    @GetMapping("page")
    public JsonData pageProductList(
            @ApiParam(value = "current page") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "count in each page") @RequestParam(value = "size", defaultValue = "10") int size) {

        Map<String, Object> pageResult = productService.page(page, size);
        return JsonData.buildSuccess(pageResult);
    }

    @ApiOperation("product detail")
    @GetMapping("detail/{product_id}")
    public JsonData detail(
            @ApiParam("product id")
            @PathVariable("product_id") long productId) {

        ProductVO productVO = productService.findProductById(productId);
        return JsonData.buildSuccess(productVO);
    }
}


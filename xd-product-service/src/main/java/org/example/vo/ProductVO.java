package org.example.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bob
 * @since 2022-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductVO {

    private Long id;

    /**
     * title
     */
    private String title;

    /**
     * cover image
     */
    @JsonProperty("cover_img")
    private String coverImg;

    /**
     * detail
     */
    private String detail;

    /**
     * old price
     */
    @JsonProperty("old_price")
    private BigDecimal oldPrice;

    /**
     * new price
     */
    private BigDecimal price;

    /**
     * stock
     */
    private Integer stock;

}

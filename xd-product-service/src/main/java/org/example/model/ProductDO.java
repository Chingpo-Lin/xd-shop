package org.example.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("product")
public class ProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * title
     */
    private String title;

    /**
     * cover image
     */
    private String coverImg;

    /**
     * detail
     */
    private String detail;

    /**
     * old price
     */
    private BigDecimal oldPrice;

    /**
     * new price
     */
    private BigDecimal price;

    /**
     * stock
     */
    private Integer stock;

    /**
     * create time
     */
    private Date createTime;

    /**
     * lock stock
     */
    private Integer lockStock;


}

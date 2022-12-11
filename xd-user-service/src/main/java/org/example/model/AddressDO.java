package org.example.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * Address model
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("address")
public class AddressDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * if default address:0->no;1->yes
     */
    private Integer defaultStatus;

    /**
     * receive name
     */
    private String receiveName;

    /**
     * receive phone
     */
    private String phone;

    /**
     * province
     */
    private String province;

    /**
     * city
     */
    private String city;

    /**
     * region
     */
    private String region;

    /**
     * detail address
     */
    private String detailAddress;

    private Date createTime;

}

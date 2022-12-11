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
 * User model
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * password
     */
    private String pwd;

    /**
     * avatar
     */
    private String headImg;

    /**
     * sign
     */
    private String slogan;

    /**
     * 0 is female，1 is male
     */
    private Integer sex;

    /**
     * point
     */
    private Integer points;

    private Date createTime;

    /**
     * email
     */
    private String mail;

    /**
     * secret for privacy
     */
    private String secret;
}

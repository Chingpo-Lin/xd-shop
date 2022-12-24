package org.example.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bob
 * @since 2022-12-24
 */
@Data
public class BannerVO {

    /**
     * banner id
     */
    private Integer id;

    /**
     * image
     */
    private String img;

    /**
     * jump url
     */
    private String url;

    /**
     * importance
     */
    private Integer weight;


}

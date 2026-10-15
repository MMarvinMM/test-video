package com.chinatower.device.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 租户物联设备关系表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-08
 */
@Data
public class OrgIotDevRelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表id
     */
    private Long orgIotDevRelId;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 组织id
     */
    private String orgId;
    private String orgName;

    private String deviceCode;

    /**
     * 设备类型
     */
    private Integer categoryCode;
}

package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@Getter
@Setter
@TableName("vm_organ_device_rel_t")
public class OrganIotDevRelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 组织id
     */
    private String deptId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除状态：0正常 1删除
     */
    private String deleteFlag;

    /**
     * 类型
     */
    private Integer deviceType;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;



}

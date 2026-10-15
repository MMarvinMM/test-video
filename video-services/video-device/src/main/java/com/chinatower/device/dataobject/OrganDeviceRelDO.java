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
 * 组织设备关系表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Getter
@Setter
@TableName("vm_organ_device_rel_t")
public class OrganDeviceRelDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String tenantId;

    private String deptId;
    
    private String deviceCode;
    
    /**
     * 创建人
     */
    private String creator;
    
    /**
     * 创建时间
     */
    private Date creatorDate;
    
    /**
     * 修改人
     */
    private String modifier;
    
    /**
     * 修改时间
     */
    private Date modifierDate;
    
    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

}

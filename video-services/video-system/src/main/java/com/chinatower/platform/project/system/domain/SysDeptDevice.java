package com.chinatower.platform.project.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("vm_organ_device_rel_t")
public class SysDeptDevice {

    private Long id;

    private String tenantId;

    private String deptId;

    private String deviceCode;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String deleteFlag;

    private Integer deviceType;

    private String appVerCode;

    private String industryCode;

}

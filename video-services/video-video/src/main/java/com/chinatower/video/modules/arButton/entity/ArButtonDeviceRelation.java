package com.chinatower.video.modules.arButton.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: video-platform
 * @ClassName: ArButtonDeviceRelation
 * @description: ar大屏配置按钮与设备的关系
 * @author: fengty
 * @create: 2024-05-28 17:59
 */

@Data
@TableName("vm_arbutton_device_relation")
public class ArButtonDeviceRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    @TableField("device_code")
    private String deviceCode;

    @TableField("button_id")
    private Long buttonId;

    @TableField("tenant_id")
    private String tenantId;

    @TableField("industry_code")
    private String industryCode;

    @TableField("accept_node")
    private String acceptNode;

    private int enableStatus;

    private int count;

    // 用于计算距离配置个数上线还差几个
    private int varCount;
}

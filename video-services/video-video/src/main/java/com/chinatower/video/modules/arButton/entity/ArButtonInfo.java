package com.chinatower.video.modules.arButton.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.video.modules.constants.IndErrorMsgConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: ArButtonInfo
 * @description: AR大屏配置按钮实体类
 * @author: fengty
 * @create: 2024-04-07 10:44
 */

@Data
@TableName("vm_ar_button_info")
public class ArButtonInfo implements Serializable {

    // 按钮ID
    @TableId("id")
    private Long id;
    // 按钮名称
    @NotBlank(message = IndErrorMsgConstants.AR_BUTTON_NAME_NULL)
    @TableField("button_name")
    private String buttonName;
    // 使用状态：1-启用，2-停用
    @TableField("enable_status")
    private Integer enableStatus;
    // 创建时间
    @TableField("create_time")
    private String createTime;
    // 最后修改时间
    @TableField("update_time")
    private String updateTime;
    // 启用或停用时间
    private String turnTime;
    // 创建人
    @TableField("creator")
    private String creator;
    // 修改人
    @TableField("modifier")
    private String modifier;
    // 删除标志：0-正常，1-删除
    @TableField("delete_flag")
    private int deleteFlag;
    // 租户ID
    @TableField("tenant_id")
    private String tenantId;
    // 行业编码
    @TableField("industry_code")
    private String industryCode;
    // 行业应用版本编码
    @TableField("app_ver_code")
    private String appVerCode;
    // 受理节点
    @TableField("accept_node")
    private String acceptNode;

    // 应用范围 1：当前摄像机， 2：指定摄像机， 3：全部摄像机
    @TableField("device_range")
    private Integer deviceRange;

    @TableField(exist = false)
    private List<String> deviceCodes;

    @TableField(exist = false)
    private List<Long> ids;

    @NotEmpty(message = IndErrorMsgConstants.AR_ATTACH_EMPTY)
    @TableField(exist = false)
    private List<ArButtonAttachInfo> attachList;

    @TableField(exist = false)
    private List<ArButtonAttachInfo> deleteAttachList;

    @TableField(exist = false)
    private int pageSize;

    @TableField(exist = false)
    private int pageNum;

    @TableField(exist = false)
    private String orderColumn;

    @TableField(exist = false)
    private String createTimeStart;

    @TableField(exist = false)
    private String createTimeEnd;

    @TableField(exist = false)
    private String updateTimeStart;

    @TableField(exist = false)
    private String updateTimeEnd;

    @Pattern(regexp = "asc|desc",message = "排序规则不合法")
    private String ascOrDesc;

    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return super.toString();
        }
    }
}

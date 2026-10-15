package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.device.entity.dto.VmHornTaskContentRelDO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 广播任务管理实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HornTaskConfVO extends BaseDataScopeEntity {

    /**
     * 返回编码
     */
    @ApiModelProperty("主键id")
    private Long id;

    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    /**
     * 任务类型 0-音频 1-文本
     */
    @ApiModelProperty("任务类型 0-音频 1-文本")
    @NotNull(message = "任务类型不能为空")
    private Integer taskType;

    /**
     * 任务音量
     */
    @ApiModelProperty("任务音量")
    @NotBlank(message = "任务音量不能为空")
    private String taskVolumn;

    /**
     * 任务优先级
     */
    @ApiModelProperty("任务优先级")
    @NotNull(message = "任务优先级不能为空")
    @Min(value = 0, message = "任务优先级不能小于0")
    private Integer taskPriority;

    /**
     * 操作类型 0-未操作 1-开始 2-结束
     */
    @ApiModelProperty("操作类型 0-未操作 1-开始 2-结束")
    private Integer operatorType;

    /**
     * 启动状态 0-未启动 1-启动中
     */
    @ApiModelProperty("启动状态 0-未启动 1-启动中")
    private Integer startupStatus;

    /**
     * 执行状态 0-未执行 1-执行中
     */
    @ApiModelProperty("执行状态 0-未执行 1-执行中")
    private Integer executStatus;

    /**
     * 操作人
     */
    @ApiModelProperty("操作人")
    private String operator;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date operateTime;

    /**
     * 分页每页数量
     */
    @ApiModelProperty("分页每页数量")
    private Integer pageSize;

    /**
     * 分页页码
     */
    @ApiModelProperty("分页页码")
    private Integer pageNum;

    /**
     * 任务类型
     */
    @ApiModelProperty("任务类型")
    private String taskTypeName;

    /**
     * 操作类型
     */
    @ApiModelProperty("操作类型")
    private String operatorTypeName;

    /**
     * 开始状态
     */
    @ApiModelProperty("开始状态")
    private String startupStatusName;

    /**
     * 执行状态
     */
    @ApiModelProperty("执行状态")
    private String executStatusName;

    /**
     * 查询创建开始时间
     */
    @ApiModelProperty("查询创建开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeBegin;

    /**
     * 查询创建结束时间
     */
    @ApiModelProperty("查询创建结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeEnd;

    /**
     * 查询修改开始时间
     */
    @ApiModelProperty("查询修改开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeBegin;

    /**
     * 查询修改结束时间
     */
    @ApiModelProperty("查询修改结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeEnd;

    /**
     * 精确查找任务名称
     */
    @ApiModelProperty("精确查找任务名称")
    private String accurateTaskName;

    /**
     * 关联的内容id集合
     */
    @ApiModelProperty("关联的内容id集合")
    private List<VmHornTaskContentRelDO> vmHornTaskContentRelList;

    /**
     * 关联的设备编码集合
     */
    @ApiModelProperty("关联的设备编码集合")
    private List<String> deviceCodeList;

    /**
     * 策略集合
     */
    @ApiModelProperty("策略集合")
    private HornTaskStrategyConfVO strategyConf;

    /**
     * 对应广播平台任务id
     */
    @ApiModelProperty("对应广播平台任务id")
    private String hornTaskId;

    /**
     * 操作类型：0-立即播放 1-创建任务 2-全选
     */
    @ApiModelProperty("操作类型：0-立即播放 1-创建任务 2-全选")
    private Integer hornOptType;

    /**
     * 行业名称
     */
    @ApiModelProperty("行业名称")
    private String industryName;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String tenantName;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private String orderColumn;

    /**
     * 排序类型 asc:升序  desc:降序
     */
    @ApiModelProperty("排序类型 asc:升序  desc:降序")
    private String sort;

    /**
     * 文件id
     */
    @ApiModelProperty("用于临时存储构建调用文本任务相关接口时，创建文本返回的文件id")
    private String fileId;
}

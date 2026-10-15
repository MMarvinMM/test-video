package com.chinatower.emergency.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Digital implements Serializable {

    private String digitalId;

    @TableField(exist = false)
    private String eventCode;
    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件等级，1：特别重大，2：重大，3：较大，4：一般
     */
    private Integer eventGradeDict;



    /**
     * 事件类型大类
     */
    private Integer eventTypeLargeCategory;

    /**
     * 事件类型中类
     */
    private Integer eventTypeMiddleCategory;

    /**
     * 事件类型小类
     */
    private Integer eventTypeSmallCategory;

    /**
     * 事件描述
     */
    private String eventDescribe;

    /**
     * 附件
     */
    private String files;

    /**
     * 文件
     */
    private List<EmergencyAttachVO> attachList;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private String tenantId;

    private String tenantName;

    /**
     * 行业编码
     */
    private String industryCode;

    private String appVerCode;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标记，0：正常，1：删除
     */
    private Integer deleteFlag;


    /**
     * 死亡(含失踪人数)人数  json字段
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private RangeUnit deathCount;

    /**
     * 紧急转移/救助人数  json字段
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private RangeUnit emergencyCount;

    /**
     * 倒塌数量 json字段
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private RangeUnit collapsesCount;

    /**
     * 生活困难救助人员  json字段
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private RangeUnit difficultCount;

    /**
     * 其他
     */
    private String other;

    /**
     * 接入节点
     */
    private String acceptNode;


    /**
     * 指挥官
     */
    private String commander;

    private String commanderName;

    /**
     * 指挥部
     */
    private List<DigitalDeptUserTreeVO> deptTree;

    /**
     * 计划
     */
    private DigitalPlan digitalPlan;

    /**
     * 资源ids
     */
    private List<String> resourceList;

    /**
     * 是否全部返回 true-是 false-否
     */
    private Boolean isAllReturn;

    private String industryName;
}

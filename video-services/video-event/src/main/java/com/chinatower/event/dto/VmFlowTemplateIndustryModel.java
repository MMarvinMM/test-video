package com.chinatower.event.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.common.core.entity.BasePlatformEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 告警流程模板关联行业应用实体对象
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class VmFlowTemplateIndustryModel extends BasePlatformEntity implements Serializable {
    private static final long serialVersionUID = -1334520687434433624L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 模板id
     */
    private Long templateId;
    /**
     * 行业应用版本
     */
    private String appVerCode;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 状态： 0默认不是初始化模板 1默认初始化模板
     */
    private String status;
    /**
     * 告警来源
     */
    private String warningSource;

}

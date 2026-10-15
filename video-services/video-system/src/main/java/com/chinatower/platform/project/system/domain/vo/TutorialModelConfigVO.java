package com.chinatower.platform.project.system.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


/**
 * 所属模块配置
 * @author liuxt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TutorialModelConfigVO extends PageDTO {

    /**
     * 主键id
     */
    @TableId(value = "model_id", type = IdType.AUTO)
    private Long modelId;

    /**
     * 所属模块名称
     */
    @NotBlank(message = "所属模块名称不能为空")
    @Length(max = 10,message = "所属模块名称限制最多支持输入10个字")
    private String modelName;

}



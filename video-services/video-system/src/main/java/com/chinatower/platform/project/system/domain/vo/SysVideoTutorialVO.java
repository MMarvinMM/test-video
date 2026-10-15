package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysVideoTutorialVO extends PageDTO {

    /**
     * 教程ID
     */
    private String tutorialId;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 视频教程名称
     */
    @NotBlank(message = "视频教程名称不能为空")
    @Length(max = 30,message = "服务范围限制最多支持输入30个字")
    private String tutorailTitle;

    /**
     * 可见范围：1 只铁塔可见 2 只客户可见 3铁塔客户均可见
     */
    private String visibility;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 创建时间开始日期
     */
    private String createTimeStart;

    /**
     * 创建时间结束日期
     */
    private String createTimeEnd;

    /**
     * 修改时间开始日期
     */
    private String updateTimeStart;

    /**
     * 修改时间结束日期
     */
    private String updateTimeEnd;

    /**
     * 所属模块id
     */
//    @NotNull(message = "所属模块不能为空")
    private Long modelId;

    /**
     * 租户是否可见（0：不可见 1：可见）
     */
    private Integer tenantIsLook;

    /**
     * 铁塔用户是否可见（0：不可见 1：可见）
     */
    private Integer towerIsLook;


    /**
     * 所属模块名称
     */
    private String modelName;

    /**
     * 行业名称
     */
    private String industryName;

    /**
     * 行业编码列表
     */
    private List<String> industryCodeList;


}

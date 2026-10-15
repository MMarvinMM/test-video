package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author WANGZIC
 * @description GetNoticeReqVo
 * @date 22/04/14 17:27
 */
@Data
public class GetNoticeReqVo extends BaseDataScopeEntity {

    @NotBlank(message = "用户编码不能为空")
    @ApiModelProperty(value = "用户编码", required = true, example = "100001")
    private String userId;

    @NotBlank(message = "用户部门编码不能为空")
    @ApiModelProperty(value = "用户部门编码", required = true, example = "100001")
    private String deptId;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;


    /** 公告类型 */
    private String noticeType;

    private Integer pageNum;

    private Integer pageSize;

    private Integer ifRead;

}

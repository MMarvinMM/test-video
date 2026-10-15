package com.chinatower.file.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 通知公告表 sys_notice
 *
 * @author iwhalecloud
 */
@Data
public class SysNotice extends PageDTO {
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long noticeId;

    /** 公告标题 */
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    private String noticeTitle;

    /** 内容类型*/
    @NotBlank(message = "内容类型不能为空")
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态 */
    private String status;

    /** 发布时间 */
    private Date publishTime;

    /** 生效时间 */
    private Date validTime;

    /** 失效时间 */
    private Date inValidTime;

    /** 接收终端 0全部 1PC端 2APP端*/
    private String receiveEnd;

    /** 是否全体接收 */
    private String ifAllReceive;

    /** 接收部门 */
    private String receiveDepts;

    /** 接收角色 */
    private String receiveRoles;

    /** 接收人 */
    private String receiveUsers;

    private String remark;

    private String noticeBanner;

    private String deptId;

    /** 租户编码  */
    private String tenantId;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endTime;

    private String ifRead;

    private String userId;

    private String tenantName;

    @ApiModelProperty(value = "省份id")
    @TableField(exist = false)
    private String provinceId;

    @TableField(exist = false)
    @ApiModelProperty(value = "地市id")
    private String cityId;

    /** 附件url */
    private String fileUrl;

}


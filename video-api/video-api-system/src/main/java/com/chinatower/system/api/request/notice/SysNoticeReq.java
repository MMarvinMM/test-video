package com.chinatower.system.api.request.notice;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.utils.StringUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class SysNoticeReq extends BaseDataScopeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    private String noticeTitle;

    /**
     * 公告类型
     */
    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态
     */
    private String status;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 生效时间
     */
    private Date validTime;

    /**
     * 失效时间
     */
    private Date inValidTime;

    /**
     * 接收端 0全部 1PC端 2APP端
     */
    private String receiveEnd;

    /**
     * 是否全体接收
     */
    private String ifAllReceive;

    /**
     * 接收部门
     */
    private String receiveDepts;

    /**
     * 接收角色
     */
    private String receiveRoles;

    /**
     * 接收人
     */
    private String receiveUsers;

    /**
     * 备注
     */
    private String remark;

    /**
     * 公告图
     */
    private String noticeBanner;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 租户编码
     */
    private String tenantId;

    private String beginTime;

    private String endTime;

    private Integer pageNum;
    private Integer pageSize;
    private String orderByColumn;

    @Pattern(regexp = "asc|desc", message = "排序规则不合法")
    private String isAsc;

    public String getOrderBy() {
        return StringUtils.isEmpty(this.orderByColumn) ? "" : StringUtils.toUnderScoreCase(this.orderByColumn) + " " + this.isAsc;
    }

}

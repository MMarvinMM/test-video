package com.chinatower.platform.project.system.domain;

import com.chinatower.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 参数配置表 sys_config
 *
 * @author iwhalecloud
 */
@Data
public class SysSso
{
    private static final long serialVersionUID = 1L;

    /** 参数主键 */
    @Excel(name = "主键")
    private String ssoId;

    /** 租户id */
    @Excel(name = "租户id")
    private String tenantId;

    /** 租户名称*/
    @Excel(name = "租户名称")
    private String tenantName;

    /** 来源地址 */
    @Excel(name = "来源地址")
    private String address;

    /** appkey */
    @Excel(name = "appkey")
    private String appkey;

    /** secretkey */
    @Excel(name = "secretkey")
    private String secretkey;


    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    /** 创建开始时间 格式：yyyy-MM-dd*/
    private String createBeginTime;

    /** 创建结束时间 格式：yyyy-MM-dd*/
    private String createEndTime;

    /** 更新开始时间 格式：yyyy-MM-dd*/
    private String updateBeginTime;

    /** 更新结束时间 格式：yyyy-MM-dd*/
    private String updateEndTime;

    private List<SysSsoUser> sysSsoUser;

    private Integer pageNum;

    private Integer pageSize;

    private String appCode;//第三方平台唯一编码

    private String prop;
    private String order;

}

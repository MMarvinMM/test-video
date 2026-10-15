package com.chinatower.manage.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_localization_industry_ver_conf")
public class LocalizationSysIndustryVerConf implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String industryCode;
    private String appVerCode;
    private String appVerName;
    private String appVerDesc;
    private String creator;
    private String modifier;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    private String status;
    private String deleteFlag;
    /** 受理节点 */
    private String acceptNode;
    /** 0-未同步 1-同步成功 2-同步失败 */
    private String syncStatus;
    /** 同步时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date syncTime;
}

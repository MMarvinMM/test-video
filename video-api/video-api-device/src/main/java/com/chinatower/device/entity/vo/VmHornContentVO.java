package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author willem
 * @date 2024/10/9
 * @description 喇叭内容管理配置
 */
@Data
public class VmHornContentVO {
    /**
     * 内容id
     */
    private Long id;
    /**
     * 内容标题
     */
    private String contentTitle;
    /**
     * 内容类型  0-音频 1-文本
     */
    private Integer contentType;
    /**
     * 内容信息
     */
    private String contentValue;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件时长
     */
    private String fileDuration;

    /**
     * 文件类型 0-其他格式 1-pcm格式
     */
    private Integer fileType;

    /**
     * 文件时长 展示格式 xx时xx分xx秒
     */
    private String fileDurationStr;
    /**
     * 审核状态 0-未审核 1-审核通过 2-审核不通过
     */
    private String auditStatus;
    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date auditTime;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    /**
     * 删除标识 0-未删除 1-已删除
     */
    private Integer deleteFlag;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 修改人
     */
    private String modifier;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 音频文件上传后的url的地址
     */
    private String fileUrl;

    /**
     * 通用上传的url地址
     */
    private String baseFileUrl;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 租户编码
     */
    private String tenantId;
    /**
     * 应用版本号
     */
    private String appVerCode;
    /**
     * 喇叭平台上传后的资源id
     */
    private String resourceId;
    /**
     * 区域编码
     */
    private String regionCode;
    /**
     * 语速
     */
    private Integer speechRate;
    /**
     * 语调音高
     */
    private Integer tonePitch;
    /**
     * 音量
     */
    private Integer volume;
    /**
     * 引擎类型
     */
    private String xunfeiEnt = "intp65";
    /**
     * 发音人
     */
    private String xunfeiVcn;
    /**
     * 接入节点
     */
    private String accessNode;
    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 要删除的内容id集合
     */
    private List<String> ids;

    /**
     * 是否能操作 0-不能操作 1-能操作
     */
    private String canOperate;
}

package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.Date;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 13:18
 */
@Data
public class AnnotationMapReq extends PageDTO {
    private String id;
    private String type;
    private String geometry;
    private String properties;
    private String query;
    private String queryType;
    private String queryName; // 查询名称
    private String annotationName; // 标注名称
    private String accessNode; // 接入节点
    private String acceptNode; // 接收节点
    private Date createTime; // 创建时间
    private String createUser; // 创建用户
    private Date updateTime; // 更新时间
    private String updateUser; // 更新用户
    private String IndustryCode; // 行业编码
}

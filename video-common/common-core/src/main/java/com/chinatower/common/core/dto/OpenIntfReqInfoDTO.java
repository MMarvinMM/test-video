package com.chinatower.common.core.dto;

import lombok.Data;

/**
 * @author tianhao
 */
@Data
public class OpenIntfReqInfoDTO {

    private String paramId;
    //开放平台接口id
    private String openIntfId;

    //字段描述/参数描述
    private String fieldDesc;

    //字段名称/参数名称
    private String fieldName;

    //类型长度
    private String fieldType;

    //字段说明
    private String fieldRemark;

    //内容示例
    private String contentExample;

    //是否必填
    private String required;

    //排序
    private Integer sort;

    //类型: 1:请求头参数，2：请求路径入参，3：请求参数，4：请求内容，5：响应内容
    private String type;
}

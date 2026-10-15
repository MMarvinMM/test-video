package com.chinatower.common.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author tianhao
 */
@Data
public class OpenIntfInfoDTO {

    //id
    private Long intfId;
    //开放平台接口id
    private String openIntfId;

    //请求路径
    private String requestUrl;

    //请求类型
    private String requestType;

    //消费方
    private String consumer;

    //服务方
    private String provider;

    //服务编码
    private String serviceCode;

    //服务名称
    private String serviceName;

    //服务描述
    private String serviceDesc;

    //请求报文示例
    private String requestExample;

    //响应报文示例
    private String responseExample;

    //创建人
    private String createStaff;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    //修改人
    private String updateStaff;

    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

}

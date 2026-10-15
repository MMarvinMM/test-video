package com.chinatower.manage.dto.open;

import com.chinatower.manage.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tianhao
 */
@Data
public class OpenIntfInfoModel extends BaseModel implements Serializable {

    //接口id
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
}

package com.chinatower.common.core.vo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class RequestHeaderVo implements Serializable {
  private String requestTime;
    private String token;
    private String msgId;
    /**
     * 请求外部接口目标地址,必选
     */
    @NotNull
    private String targetUrl;

    /**
     *能开方法版本号
     */
    private String methodVersion;


    private String targetPlat;


    /**
     * get post 默认post
     */
    private String requestType;



    public String getMsgId() {
        return msgId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTargetPlat() {
        return targetPlat;
    }

    public void setTargetPlat(String targetPlat) {
        this.targetPlat = targetPlat;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getMethodVersion() {
        return methodVersion;
    }

    public void setMethodVersion(String methodVersion) {
        this.methodVersion = methodVersion;
    }

    @Override
    public String toString() {
        return "RequestHeaderVo{" +
                "requestTime='" + requestTime + '\'' +
                ", token='" + token + '\'' +
                ", msgId='" + msgId + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", methodVersion='" + methodVersion + '\'' +
                ", targetPlat='" + targetPlat + '\'' +
                ", requestType='" + requestType + '\'' +
                '}';
    }
}


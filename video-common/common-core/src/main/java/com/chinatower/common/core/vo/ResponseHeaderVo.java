package com.chinatower.common.core.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class ResponseHeaderVo implements Serializable {
    private String respDesc;
    private String respCode;
    private String requestSeq;
    private String responseTime;
    private String sysNeedAuth;
    private String authId;
    private String appId;



    public String getAuthId() {
        return this.authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getRespDesc() {
        return this.respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public String getRespCode() {
        return this.respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRequestSeq() {
        return this.requestSeq;
    }

    public void setRequestSeq(String requestSeq) {
        this.requestSeq = requestSeq;
    }

    public String getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getSysNeedAuth() {
        return this.sysNeedAuth;
    }

    public void setSysNeedAuth(String sysNeedAuth) {
        this.sysNeedAuth = sysNeedAuth;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}


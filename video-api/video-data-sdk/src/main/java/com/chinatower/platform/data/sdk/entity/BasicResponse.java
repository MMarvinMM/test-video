package com.chinatower.platform.data.sdk.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;

@Data
@SuppressWarnings("all")
public class BasicResponse<T> implements Serializable {

    private String errorCode;
    private String errMsg;
    private T responseBody;

    public static BasicResponse createWithError(String errorCode, String... errMsg) {
        BasicResponse response = new BasicResponse();
        response.setErrorCode(errorCode);
        if (errMsg != null) {
            response.setErrMsg(StringUtils.join(errMsg, "\n"));
        }
        return response;
    }
}

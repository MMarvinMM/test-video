package com.chinatower.platform.project.system.domain;

import lombok.Data;

@Data
public class LanXinBaseResponse<T> {
    private Integer errCode;
    private String errMsg;
    private T data;

    public boolean isSuccess() {
        return errCode != null && errCode == 0;
    }
}
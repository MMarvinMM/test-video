package com.chinatower.platform.modules.screen.service;

import lombok.Data;

@Data
public class ScreenTokenRefreshResult {

    private int successCount;

    private int failCount;

    private int skipCount;

    private String message;

    public static ScreenTokenRefreshResult loginFailed() {
        ScreenTokenRefreshResult result = new ScreenTokenRefreshResult();
        result.setMessage("sdshbt 登录失败，请检查 Nacos 凭证配置");
        result.setFailCount(1);
        return result;
    }

    public static ScreenTokenRefreshResult empty() {
        ScreenTokenRefreshResult result = new ScreenTokenRefreshResult();
        result.setMessage("无 redirectType=sdshbt 的大屏配置");
        return result;
    }

    public boolean isSuccess() {
        return failCount == 0;
    }
}

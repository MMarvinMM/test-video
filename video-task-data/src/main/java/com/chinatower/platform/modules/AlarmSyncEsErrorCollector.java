package com.chinatower.platform.modules;

/**
 * 属地告警同步写入 ES 异常收集器
 */
public class AlarmSyncEsErrorCollector {

    private Exception error;

    /**
     * 记录 ES 写入异常，保留首次异常信息
     */
    public synchronized void recordError(Exception error) {
        if (error != null && this.error == null) {
            this.error = error;
        }
    }

    public boolean hasError() {
        return error != null;
    }

    public Exception getError() {
        return error;
    }
}

package com.chinatower.sync.consumer.constants;



import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对接广播平台枚举类
 */
public interface HornTaskEnum {

    /*
     * 广播平台任务状态
     */
    @Getter
    @AllArgsConstructor
    enum TaskStatus {
        TASK_STATUS_1("等待播发", 1),
        TASK_STATUS_2("播发中", 2),
        TASK_STATUS_3("播发成功", 3),
        TASK_STATUS_4("播发失败", 4),
        TASK_STATUS_5("播发取消", 5);
        private final String hornTaskStatusName;
        private final Integer hornTaskStatus;
    }


    @Getter
    @AllArgsConstructor
    enum ExecutStatus {
        EXECUT_STATUS_0("未执行", 0),
        EXECUT_STATUS_1("执行中", 1),
        EXECUT_STATUS_2("部分执行中", 2);
        private final String executStatusName;
        private final Integer executStatusValue;
    }

    @Getter
    @AllArgsConstructor
    enum FileStatus {
        FILE_STATUS_SUCCEED("上传成功", "SUCCEED"),
        FILE_STATUS_FAILED("上传失败", "FAILED");
        private final String fileStatusName;
        private final String fileStatusValue;
    }

}

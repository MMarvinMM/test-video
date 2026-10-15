package com.chinatower.device.enums;



import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 对接广播平台枚举类
 */
public interface HornCommonEnum {

    /**
     * 广播类型枚举
     */
    @Getter
    @AllArgsConstructor
    enum BroadcastType {
        BROADCAST_0("AUDIO_TIMING", "音频定时任务", 0, "AUDIO"),
        BROADCAST_1("TEXT_TIMING", "文本定时任务", 1, "TEXT");

        @ApiModelProperty(value = "广播类型")
        private final String broadcastType;
        @ApiModelProperty(value = "广播类型描述")
        private final String broadcastName;
        @ApiModelProperty(value = "对应任务类型 0-音频 1-文本")
        private final Integer taskType;
        @ApiModelProperty(value = "内容类型")
        private final String contentType;

        public static Stream<BroadcastType> stream() {
            return Stream.of(BroadcastType.values());
        }

        public static BroadcastType getEnum(Integer taskType) {
            return BroadcastType.stream().filter(broadcastType -> taskType.equals(broadcastType.getTaskType())).findFirst().orElse(null);
        }

    }

    /**
     * 启动策略枚举 0-单次 1-循环
     */
    @Getter
    @AllArgsConstructor
    enum StartupPolicy {
        STARTUP_POLICY_0("单次", 0),
        STARTUP_POLICY_1("循环", 1),
        //立即播放为手动添加 页面没有这个选项
        STARTUP_POLICY_2("立即播放", 2);

        private final String startupPolicyName;
        private final Integer startupPolicyValue;
    }

    /**
     * 启动策略枚举 0-单次 1-循环
     */
    @Getter
    @AllArgsConstructor
    enum JobType {
        JOB_TYPE_0("一次性", "ONCE", 0),
        JOB_TYPE_1("周循环", "WEEK", 1),
        //立即播放为手动添加 页面没有这个选项
        JOB_TYPE_2("手动", "HAND", 2);

        private final String jobTypeName;
        private final String jobTypeValue;
        //对应StartupPolicy的startupPolicyValue 3是立即播放
        private final Integer jobTypeRel;

        public static Stream<JobType> stream() {
            return Stream.of(JobType.values());
        }

        public static JobType getEnum(Integer jobTypeRel) {
            return JobType.stream().filter(jobType -> jobTypeRel.equals(jobType.getJobTypeRel())).findFirst().orElse(null);
        }
    }

    /**
     * 操作类型 0-未操作 1-开始 2-结束
     */
    @Getter
    @AllArgsConstructor
    enum OperatorType {
        OPERATOR_TYPE_0("未操作", 0),
        OPERATOR_TYPE_1("开始", 1),
        OPERATOR_TYPE_2("结束", 2);

        private final String operatorTypeName;
        private final Integer operatorTypeValue;
    }

    @Getter
    @AllArgsConstructor
    enum StartupStatus {
        STARTUP_STATUS_0("未启动", 0),
        STARTUP_STATUS_1("启动中", 1);
        private final String startupStatusName;
        private final Integer startupStatusValue;
    }

    @Getter
    @AllArgsConstructor
    enum ExecutStatus {
        EXECUT_STATUS_STOP("手动停止", -1),
        EXECUT_STATUS_0("未执行", 0),
        EXECUT_STATUS_1("执行中", 1);
        private final String executStatusName;
        private final Integer executStatusValue;
    }

    /**
     * 操作类型 0-未操作 1-开始 2-结束
     */
    @Getter
    @AllArgsConstructor
    enum OptType {
        OPT_TYPE_0("立即播放", 0),
        OPT_TYPE_1("创建任务", 1),
        OPT_TYPE_2("全选", 2);

        private final String optTypeName;
        private final Integer optTypeValue;
    }

    /**
     *  ("播放策略 0-播放次数 1-持续时长 2-播放时间段")
     */
    @Getter
    @AllArgsConstructor
    enum PlayPolicy {
        PLAY_POLICY_0("播放次数", 0),
        PLAY_POLICY_1("持续时长", 1),
        PLAY_POLICY_2("播放时间段", 2);

        private final String playPolicyName;
        private final Integer playPolicyValue;
    }
}

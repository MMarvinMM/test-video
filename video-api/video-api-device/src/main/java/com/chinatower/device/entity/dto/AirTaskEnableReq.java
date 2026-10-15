package com.chinatower.device.entity.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author cuisj
 * @Date 2024/11/5 20:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirTaskEnableReq {


    /**
     * 计划id
     */
    private String planId;

    /**
     * 是否启用 0开启 1关闭
     */
    private String isItEnabled;

    /**
     * 启用类型 0计划 1任务
     */
    private String enabledType;

    /**
     *飞行任务id集合
     */
    private List<String> taskRecordIdList;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     *
     * */
    private List<enableTask> enableTaskList;


    private String isSystem;


    private String platFormCode;

    @Data
    public static class enableTask{

        /**
         * 主键id
         */
        private Integer id;

        /**
         * 飞行任务id
         */
        private String taskRecordId;

        /**
         * 执行状态  0待执行 1执行成功 2执行失败 3执行中
         */
        private String status;

    }
}

package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 场景告警树状结构VO（用于列表查询）
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SceneAlarmTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID（主键）
     */
    private Long id;

    /**
     * 场景id（字符串）
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 告警类型列表
     */
    private List<AlarmTypeItem> alarmTypeList;

    /**
     * 告警类型项
     */
    @Getter
    @Setter
    public static class AlarmTypeItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 关系表主键ID
         */
        private Long id;

        /**
         * 告警类型编码
         */
        private String alarmType;

        /**
         * 告警类型名称
         */
        private String alarmTypeName;
    }
}



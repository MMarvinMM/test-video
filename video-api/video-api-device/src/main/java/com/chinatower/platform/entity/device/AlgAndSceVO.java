package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AlgorithmAreaVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgAndSceVO extends BaseDataScopeEntity {
    /** 通道编码 */
    String channelCode;

    /** 设备编码 */
    String deviceCode;


    /** 位置类型：1通道，2预置位 */
    Integer posType;

    /**
     * 算法编号
     * 算法任务新增时，必填
     * @required true
     */
    String algorithmCode;

    /** 算法名称
     * 算法任务新增时，必填
     * @required true
     */
    String algorithmName;

    /** 算法配置信息 */
    List<AlgAndSceConfigVO> configList;

    /** 场景配置信息 */
    List<AlgAndSceSceVO> sceneList;
}

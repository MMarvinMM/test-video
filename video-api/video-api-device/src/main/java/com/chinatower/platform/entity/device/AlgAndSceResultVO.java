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
public class AlgAndSceResultVO extends BaseDataScopeEntity {

    /**
     * 模型类型 0:小模型 1：大模型
     * @required true
     */
    Integer modelType;

    /** 算法/场景列表 */
    List<AlgAndSceVO> List;

}

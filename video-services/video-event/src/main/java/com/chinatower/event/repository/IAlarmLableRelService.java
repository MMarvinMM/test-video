package com.chinatower.event.repository;


import com.chinatower.event.entity.dto.AddLableRelationDTO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.TradAddLableDto;

import java.util.List;

/**
 * 告警类型标签关联业务接口
 * @author yhb
 * @version 1.0
 * @date 2022/12/29 11:22
 */
public interface IAlarmLableRelService {

    /**
     * 告警对应标签查询
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getLableByWaringOrderId(String waringOrderId);

    /**
     * 告警类型关联应标签保存
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    void saveLableRelation(AddLableRelationDTO addTagRelationDTO);

    /**
     * 行业告警关联应标签保存
     *
     */
    void tradeSaveLableRelation(TradAddLableDto tradAddLableDto);

    /**
     * 告警类型关联应标签保存
     *
     * @return
     * @author cuisj
     * @date 2024/07/23
     * @version
     */
    void addNewTagRelation(AddLableRelationDTO addTagRelationDTO);
}

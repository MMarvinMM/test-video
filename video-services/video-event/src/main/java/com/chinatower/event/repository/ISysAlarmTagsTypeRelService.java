package com.chinatower.event.repository;


import com.chinatower.event.dto.AddTagRelationDTO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;

import java.util.List;

/**
 * 告警类型标签关联业务接口
 * @author chenmf
 * @version 1.0
 * @date 2022/4/26 11:22
 */
public interface ISysAlarmTagsTypeRelService {
    
    /**
     * 告警类型对应标签查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    List<AlarmTagsTypeRelDTO> getTagsByAlarmType(String alarmTypeId);
    
    /**
     * 告警类型关联应标签保存
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    void saveTagRelation(AddTagRelationDTO addTagRelationDTO);

    /**
     * 告警类型关联应标签批量保存
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    void saveTagRelationBatch(List<AddTagRelationDTO> addTagRelationDTO);
}

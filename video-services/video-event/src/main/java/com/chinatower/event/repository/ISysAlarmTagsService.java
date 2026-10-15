package com.chinatower.event.repository;


import com.chinatower.event.dto.AddTagDTO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.dto.DeleteTagDTO;
import com.chinatower.common.core.dto.ModifyTagDTO;

import java.util.List;

/**
 * 告警类型标签业务接口
 * @author chenmf
 * @version 1.0
 * @date 2022/4/26 11:22
 */
public interface ISysAlarmTagsService {

    /**
     * 告警类型标签新增
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    void addTag(AddTagDTO addTagDTO);

    /**
     * 告警类型标签修改
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    void modifyTag(ModifyTagDTO modifyTagDTO);

    /**
     * 告警类型标签删除
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    void deleteTag(DeleteTagDTO deleteTagDTO);

    /**
     * 获取全部告警类型标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    List<AlarmTagsTypeRelDTO> getAllTagsByTenantId();

    /**
     * 获取告警类型为可见的告警类型标签
     *
     * @return
     * @author tianhao
     * @date 2022/11/11
     * @version
     */
    List<AlarmTagsTypeRelDTO> getVisibleTags();

}

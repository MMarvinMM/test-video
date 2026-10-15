package com.chinatower.event.repository;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.event.dataobject.AlarmLableDO;
import com.chinatower.event.dataobject.AlarmLableRelDO;
import com.chinatower.event.entity.dto.AddLableRelationDTO;
import com.chinatower.event.entity.dto.AlarmLableRelDTO;

import java.util.List;

/**
 * <p>
 * 告警类型标签关联表 服务类
 * </p>
 *
 * @author yhb
 * @since 2022-12-29
 */
public interface IAlarmLableRelRepository extends IService<AlarmLableRelDO> {

    /**
     * 告警对应标签查询
     *
     * @return
     * @author yhb
     * @date 2022-12-29
     * @version
     */
    List<AlarmLableRelDTO> getTagsByAlarmType(String waringOrderId);

    /**
     * 当前标签已关联告警数量
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    int selectLableRelCount(Long lableId);

    /**
     * 修改告警关联标签表中的标签名
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    void modifyRelTagName(AlarmLableDO alarmLableDO);

    /**
     * 删除关联标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    void deleteLableRel(String waringOrderId);

    /**
     * 删除关联标签
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    void deleteLableRelBatch(AddLableRelationDTO addLableRelationDTO);

    /**
     * 查询标签id对应的告警
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    List<AlarmLableRelDTO> getAlarmLableByIds(List<String> idList,String waringOrderId);
}

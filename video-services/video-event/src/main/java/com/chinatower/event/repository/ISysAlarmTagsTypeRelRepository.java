package com.chinatower.event.repository;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.event.dataobject.SysAlarmTagsDO;
import com.chinatower.event.dataobject.SysAlarmTagsTypeRelDO;
import com.chinatower.event.entity.dto.AlarmTagsTypeRelDTO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * <p>
 * 告警类型标签关联表 服务类
 * </p>
 *
 * @author chenmf
 * @since 2022-04-26
 */
public interface ISysAlarmTagsTypeRelRepository extends IService<SysAlarmTagsTypeRelDO> {
    
    /**
     * 告警类型对应标签查询
     *
     * @return
     * @author chenmf
     * @date 2022/4/26
     * @version
     */
    List<AlarmTagsTypeRelDTO> getTagsByAlarmType(String alarmTypeId,List<String> alarmTypeIdList, String tenantId, String industryCode);

    /**
     * 根据告警类型编码查询告警类型标签关联信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param typeValueList 告警类型编码列表
     * @return 告警类型标签信息
     */
    List<AlarmTypeTagInfoVO> selectRelateTagInfoByAlarmType(String tenantId, String industryCode,
                                                            List<String> typeValueList, String dataScope);


    /**
     * 根据客户信息查询告警类型标签信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @return 告警类型标签信息
     */
    List<AlarmTypeTagInfoVO> selectTagInfoByTenant(String tenantId, String industryCode);
    
    /**
     * 当前标签已关联标签数量
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    int selectTagRelCount(Long tagId, String tenantId);
    
    /**
     * 修改关联标签表中的标签名
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    void modifyRelTagName(SysAlarmTagsDO sysAlarmTagsDO);
    
    /**
     * 删除关联标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    void deleteTagsTypeRel(String alarmTypeId, String tenantId, String industryCode);

    /**
     * 批量删除关联标签
     *
     * @return
     * @author chenmf
     * @date 2022/4/27
     * @version
     */
    void deleteTagsTypeRelBatch(List<String> alarmTypeId, String tenantId, String industryCode);
}

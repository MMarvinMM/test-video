package com.chinatower.device.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.RadarAlarmBlackWhiteListDO;
import com.chinatower.device.dto.RegionTemplateRelDTO;
import com.chinatower.device.entity.vo.RadarAlarmBlackWhiteVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 雷达告警模板配置
 */
public interface RadarAlarmBlackWhiteListMapper extends BaseMapper<RadarAlarmBlackWhiteListDO> {

    /**
     * 根据模板id查询雷达白名单信息
     * @param templateId 模板id
     * @param listType 名单类型 1-白名单 2-黑名单
     * @return 白名单信息
     */
    List<RadarAlarmBlackWhiteVO> queryByTemplateId(@Param("templateId") Long templateId, @Param("listType") Integer listType);


    int deleteWhileBlackListByTempId(@Param("templateId") Long templateId);



    /**
     * 根据模板ID删除关联白名单信息
     * @param dto
     * @return
     */
    int deleteTemplateWhiteRelInfo(@Param("param") RegionTemplateRelDTO dto);
}

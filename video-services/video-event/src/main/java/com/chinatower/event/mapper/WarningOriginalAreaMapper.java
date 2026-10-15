package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.WarningOriginalAreaDO;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;

import java.util.List;

/**
 * <p>
 * 告警事件原始图片区域表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
public interface WarningOriginalAreaMapper extends BaseMapper<WarningOriginalAreaDO> {

    /**
     * 批量保存告警原始图片区域
     *
     * @param list
     * @return
     */
    int insertOriginalAreas(List<AlarmOriginalAreaVO> list);
}

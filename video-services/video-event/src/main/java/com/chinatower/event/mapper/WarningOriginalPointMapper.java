package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.WarningOriginalPointDO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;

import java.util.List;

/**
 * <p>
 * 告警事件原始图片告警点表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
public interface WarningOriginalPointMapper extends BaseMapper<WarningOriginalPointDO> {


    /**
     * 批量保存告警原始图片区域点
     *
     * @param list
     * @return
     */
    int insertOriginalPoints(List<AlarmOriginalPointVO> list);
}

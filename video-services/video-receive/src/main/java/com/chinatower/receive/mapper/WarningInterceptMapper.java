package com.chinatower.receive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.dto.WarningInterceptDO;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 17:48
 */
public interface WarningInterceptMapper extends BaseMapper<WarningInterceptDO> {

    /**
     * 批量保存压制数据
     * @param list
     * @return
     */
    int insertInterceptAlarm(List<ReceiveAlarmVO> list);
}

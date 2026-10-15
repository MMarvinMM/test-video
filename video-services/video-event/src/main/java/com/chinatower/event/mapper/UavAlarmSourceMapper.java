package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.UavAlarmSourceVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 无人机告警来源
 * @Author lize
 * @Date 2024/11/4 21:04
 */
@Mapper
public interface UavAlarmSourceMapper {

    @DS(EbikeDataBaseConstant.BASE)
    UavAlarmSourceVo findUavAlarmSourceByDeviceCode(String deviceCode);

    @DS(EbikeDataBaseConstant.BASE)
    String getUavDevcieAlias(OrderInfoVO req);

}

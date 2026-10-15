package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface HornDeviceMapper {
    List<HornDeviceVo> queryHornDeviceList(@Param("param") HornDeviceDTO hornDeviceDTO);
}

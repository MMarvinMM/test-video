package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.RadarDeviceRelDO;
import com.chinatower.device.dto.RadarDeviceRelDTO;
import com.chinatower.device.dto.RadarDeviceRelVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RadarDeviceRelMapper extends BaseMapper<RadarDeviceRelDO> {

    List<RadarDeviceRelVO> getDeviceListByRadar(RadarDeviceRelDTO radarDeviceRelDTO);

    int saveRelBatch(List<RadarDeviceRelDO> radarDeviceRelDOList);
}

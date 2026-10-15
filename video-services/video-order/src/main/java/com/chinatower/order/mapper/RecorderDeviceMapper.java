package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.entity.dto.BasicDeviceDTO;
import com.chinatower.order.domain.RecorderDeviceDO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
@Mapper
public interface RecorderDeviceMapper extends BaseMapper<RecorderDeviceDO> {
    
    /**
     * 功能描述 
     * @param: areaCode
     * @return com.chinatower.order.dto.BasicDeviceDTO
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    BasicDeviceDTO queryOrderDeviceAreaInfo(String areaCode);

    @MapKey("deviceCode")
    Map<String, BasicDeviceDTO> queryByDeviceCodes(Collection<String> deviceCodeList);
}

package com.chinatower.video.modules.device.mapper;

import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.video.modules.device.entity.DeviceDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: DeviceMapper
 * @description: 设备数据传输层接口
 * @author: fengty
 * @create: 2024-06-06 10:56
 */
@Repository
public interface DeviceMapper {
    List<DeviceDTO> getMonitorList(DeviceDTO deviceDTO);
}

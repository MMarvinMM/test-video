package com.chinatower.platform.service;

import com.chinatower.platform.dto.RadarAlarmBlackWhiteExportDTO;
import com.chinatower.platform.entity.RadarAlarmBlackWhiteExportVO;

import java.util.List;

public interface RadarAlarmConfigExportService {

    /**
     * 获取雷达告警配置黑白名单导出信息
     * @param param 参数
     * @return 黑白名单信息
     */
    List<RadarAlarmBlackWhiteExportVO> getRadarAlarmBlackWhiteExportList(RadarAlarmBlackWhiteExportDTO param);
}

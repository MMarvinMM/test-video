package com.chinatower.platform.service.impl;

import com.chinatower.platform.dto.RadarAlarmBlackWhiteExportDTO;
import com.chinatower.platform.entity.RadarAlarmBlackWhiteExportVO;
import com.chinatower.platform.mapper.RadarAlarmConfigExportMapper;
import com.chinatower.platform.service.RadarAlarmConfigExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RadarAlarmConfigExportServiceImpl implements RadarAlarmConfigExportService {

    @Autowired
    private RadarAlarmConfigExportMapper radarAlarmConfigExportMapper;

    @Override
    public List<RadarAlarmBlackWhiteExportVO> getRadarAlarmBlackWhiteExportList(RadarAlarmBlackWhiteExportDTO param) {
        return radarAlarmConfigExportMapper.getRadarAlarmBlackWhiteExportList(param.getTemplateId(), param.getListType());
    }
}





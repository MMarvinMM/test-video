package com.chinatower.platform.controller;


import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.dto.RadarAlarmBlackWhiteExportDTO;
import com.chinatower.platform.entity.RadarAlarmBlackWhiteExportVO;
import com.chinatower.platform.service.RadarAlarmConfigExportService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * video-export
 * 雷达告警模板导出
 */
@Slf4j
@RestController
@RequestMapping("/radarAlarmConfig")
public class RadarAlarmConfigExportController extends TowerBaseController {
    @Resource
    private RadarAlarmConfigExportService radarAlarmConfigExportService;

    /**
     * 雷达告警模板黑/白名单导出
     * @param param
     * @param response
     */
    @ApiOperation(value = "雷达告警模板黑/白名单导出")
    @PostMapping("/list/export")
    public void export(@RequestBody RadarAlarmBlackWhiteExportDTO param, HttpServletResponse response) {
        if (param == null || StringUtils.isEmpty(param.getTemplateId()) || param.getListType() == null) {
            throw new CustomException("必填参数不能为空");
        }
        try {
            List<RadarAlarmBlackWhiteExportVO> list = radarAlarmConfigExportService.getRadarAlarmBlackWhiteExportList(param);
            byte[] data =new ExcelUtil<>(RadarAlarmBlackWhiteExportVO.class).exportBytes(list);
            FileUtils.download(response, data);
        } catch (Exception e) {
            log.error("导出雷达告警名单列表异常：", e);
        }

    }

}

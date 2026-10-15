package com.chinatower.platform.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.device.entity.dto.AirExportDto;
import com.chinatower.device.entity.dto.StatisticalAnalysisReq;
import com.chinatower.event.api.model.DownLoadUrlsReq;
import com.chinatower.platform.service.AirExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author WLF
 * @description
 * @date 2024/11/6 12:33
 */
@RestController
@Slf4j
@RequestMapping(value = "/air/task")
public class AirExportController {
    @Resource
    private AirExportService airExportService;

    /**
     * 通过地址下载 飞行成果图片/视频下载
     *
     * @param req
     * @return
     */
    @PostMapping("/downloadByUrls")
    public void downloadByUrls(HttpServletResponse response, HttpServletRequest request, @RequestBody DownLoadUrlsReq req) {
        log.info("downloadByUrls飞行成果图片/视频入参:{}", JSON.toJSONString(req));
        try {
            airExportService.downloadByUrls(response, request, req);
        } catch (Exception e) {
            log.error("downloadImgsAndVideos-下载图片/视频报错:" + e);
        }
    }

//    /**
//     * 飞行成果下载压测demo
//     */
//    @PostMapping("/downloadByUrlsDemo")
//    public void downloadByUrlsDemo(HttpServletResponse response, HttpServletRequest request) {
//        log.info("downloadByUrlsDemo");
//        try {
//            airExportService.downloadByUrlsDemo(response, request);
//        } catch (Exception e) {
//            log.error("downloadByUrlsDemo-下载图片/视频报错:" + e);
//        }
//    }

    /**
     * 获取
     */
    @PostMapping("/downloadKmz")
    public void downloadKmz(@RequestBody AirExportDto airEditDto, HttpServletResponse response) {
        log.info("downloadKmz入参:{}", JSON.toJSONString(airEditDto.getKmzUrl()));
        response.setContentType("application/vnd.google-earth.kmz");
        try {
            airExportService.downloadKmz(airEditDto.getKmzUrl(), response);
        } catch (Exception e) {
            log.error("downloadKmz结果:" + e);
        }
    }

    @PostMapping("/exportStatisticalAnalysis")
    public void exportStatisticalAnalysis(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq,
                                          HttpServletResponse response) {
        log.info("airStatistics-exportStatisticalAnalysis-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        airExportService.exportStatisticalAnalysis(statisticalAnalysisReq, response);
    }
}

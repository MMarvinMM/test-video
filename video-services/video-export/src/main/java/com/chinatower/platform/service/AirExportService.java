package com.chinatower.platform.service;

import com.chinatower.device.entity.dto.StatisticalAnalysisReq;
import com.chinatower.event.api.model.DownLoadUrlsReq;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author WLF
 * @description
 * @date 2024/11/6 14:06
 */
public interface AirExportService {

    /**
     * 通过地址下载
     *
     * @param response
     * @param request
     * @param req
     */
    void downloadByUrls(HttpServletResponse response, HttpServletRequest request, DownLoadUrlsReq req);

    /**
     * @param kmzUrl
     */
    void downloadKmz(String kmzUrl, HttpServletResponse response);

    /**
     * 通过地址下载
     *
     * @param response
     * @param request
     */
    void downloadByUrlsDemo(HttpServletResponse response, HttpServletRequest request);

    void exportStatisticalAnalysis(StatisticalAnalysisReq statisticalAnalysisReq, HttpServletResponse response);
}

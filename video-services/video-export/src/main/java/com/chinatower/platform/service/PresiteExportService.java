package com.chinatower.platform.service;

import com.chinatower.platform.entity.PresiteVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: video-platform
 * @ClassName: PresiteExportService
 * @description: 预置位相关导出业务服务层
 * @author: fengty
 * @create: 2024-07-10 13:40
 */
public interface PresiteExportService {
    void downloadPresiteImg(HttpServletResponse response, HttpServletRequest request, PresiteVO presiteVO);
}

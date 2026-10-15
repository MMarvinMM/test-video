package com.chinatower.gis.modules.gis.service.impl;

import com.chinatower.common.core.utils.ServletUtils;
import com.chinatower.gis.config.GeoserverProperties;
import com.chinatower.gis.modules.gis.service.GeoserverService;
import com.chinatower.platform.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

@Slf4j
@Service
@RefreshScope
public class GeoserverServiceImpl implements GeoserverService {

    @Resource
    GeoserverProperties geoserverProperties;

    private static final String S = "/";

    @Override
    public void terrain(String x, String y, String z) {
        File file = new File(geoserverProperties.getDir() + S + z + S + x + S + y + ".terrain");
        if (!file.isFile() || !file.exists()) {
            throw new CustomException("数据不存在");
        }
        try {
            HttpServletResponse response = ServletUtils.getResponse();
            FileInputStream fis = new FileInputStream(file);
            response.setHeader("Content-Type", "multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error("读取文件流程错误", e);
        }

    }
}

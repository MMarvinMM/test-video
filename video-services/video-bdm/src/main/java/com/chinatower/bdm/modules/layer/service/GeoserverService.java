package com.chinatower.bdm.modules.layer.service;

import org.springframework.core.io.Resource;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.GeoserverWfsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface GeoserverService {

    /**
     * 转发wfs 接口
     */
    Result<Object> wfs(GeoserverWfsDTO dto);

    /**
     * 上传样式文件
     */
    Result<Object> uploadStyle(MultipartFile file, String workSpaceName);

    /**
     * 上传tiff 文件
     */
    Result<Object> uploadLayerTiff(MultipartFile file,String workSpace);

    /**
     * 快捷发布图层
     */
    Result<Object> releaseLayers(List<Map<String,String>> maps);

    /**
     * 图层样式sdl
     */
    ResponseEntity<Resource> layerSld(String styleName, String workSpace, HttpServletResponse response);

    Result shpTest(MultipartFile file);
}

package com.chinatower.bdm.modules.layer.controller;

import org.springframework.core.io.Resource;
import com.chinatower.bdm.modules.layer.service.GeoserverService;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.GeoserverWfsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * video-bdm
 * @desription: 转发geoserver 地址
 * @data 创建时间：2023/12/19 13:41
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(tags = "geoserver")
@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeoserverController {

    private final GeoserverService geoserverService;


    /**
     * @return wfs 接口转发
     */
    @ApiOperation(value = "geoserver wfs")
    @PostMapping("/queryWfs")
    public Result<Object> wfs(@RequestBody GeoserverWfsDTO dto){

        return geoserverService.wfs(dto);
    }


    /**
     * @return wfs 接口转发
     */
    @ApiOperation(value = "上传样式文件")
    @PostMapping("/uploadStyle")
    public Result<Object> uploadStyle(MultipartFile file, String workSpaceName){

        return geoserverService.uploadStyle(file,workSpaceName);
    }

    /**
     * @return wfs 接口转发
     */
    @ApiOperation(value = "上传tiff文件")
    @PostMapping("/uploadLayerTiff")
    public Result<Object> uploadLayerTiff(MultipartFile file,String workSpace){

        return geoserverService.uploadLayerTiff(file,workSpace);
    }

    /**
     * @return wfs 接口转发
     */
    @ApiOperation(value = "发布图层")
    @PostMapping("/releaseLayers")
    public Result<Object> releaseLayers(@RequestBody List<Map<String,String>> maps){

        return geoserverService.releaseLayers(maps);
    }

    /**
     * @return  获得图层样式
     */
    @ApiOperation(value = "获得图层样式")
    @GetMapping("/styleSld")
    public ResponseEntity<Resource> layerSld(String styleName, String workSpace, HttpServletResponse response){
        return geoserverService.layerSld(styleName,workSpace,response);
    }


    /**
     * @return wfs 接口转发
     */
    @ApiOperation(value = "解析 shp")
    @PostMapping("/shpTest")
    public Result shpTest(MultipartFile file){
        return geoserverService.shpTest(file);
    }
}

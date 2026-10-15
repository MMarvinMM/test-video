package com.chinatower.bdm.modules.layer.controller;

import com.chinatower.bdm.api.entity.GeoGroupVO;
import com.chinatower.bdm.api.entity.GeoVO;
import com.chinatower.bdm.modules.layer.service.GeoService;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * video-bdm
 * @desription: geoserver 相关接口
 * @data 创建时间：2024/7/29 13:58
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(tags = "geoserver")
@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoService geoService;

    /**
     * @return 上传样式文件
     */
    @ApiOperation(value = "上传样式文件")
    @PostMapping("/uploadGeoStyle")
    public Result<Void> uploadGeoStyle(MultipartFile file){
        geoService.uploadGeoStyle(file);
        return Result.success("上传成功");
    }

    /**
     * @return 删除样式文件
     */
    @ApiOperation(value = "删除样式文件")
    @PostMapping("/deleteGeoStyle")
    public Result<Void> deleteGeoStyle(@RequestBody GeoVO vo){
        geoService.deleteGeoStyle(vo.getStyleName());
        return Result.success("删除成功");
    }

    /**
     * @return 修改图层样式
     */
    @ApiOperation(value = "修改图层样式")
    @PostMapping("/modifyLayerStyle")
    public Result<Void> modifyLayerStyle(@RequestBody GeoVO vo){
        geoService.modifyLayerStyle(vo);
        return Result.success("修改图层样式");
    }

    /**
     * @return 上传SHP文件
     */
    @ApiOperation(value = "上传SHP文件")
    @PostMapping("/publishShp")
    public Result<Void> publishShp(MultipartFile file,String styleName){
        geoService.publishShp(file,styleName);
        return Result.success("上传成功");
    }

    /**
     * @return 上传Tif文件
     */
    @ApiOperation(value = "上传Tif文件")
    @PostMapping("/publishTif")
    public Result<Void> publishTif(MultipartFile file,String styleName){
        geoService.publishTif(file,styleName);
        return Result.success("发布成功");
    }

    /**
     * @return 发布PostGis图层
     */
    @ApiOperation(value = "发布PostGis图层")
    @PostMapping("/publishPostGis")
    public Result<Void> publishPostGis(@RequestBody GeoVO vo){
        geoService.publishPostGis(vo);
        return Result.success("发布成功");
    }

    /**
     * @return 发布图层组
     */
    @ApiOperation(value = "发布图层组")
    @PostMapping("/publishLayerGroup")
    public Result<Void> publishLayerGroup(@RequestBody GeoGroupVO vo){
        geoService.publishLayerGroup(vo);
        return Result.success("发布成功");
    }


    /**
     * @return 修改图层组
     */
    @ApiOperation(value = "修改图层组")
    @PostMapping("/modifyLayerGroup")
    public Result<Void> modifyLayerGroup(@RequestBody GeoGroupVO vo){
        geoService.modifyLayerGroup(vo);
        return Result.success("修改成功");
    }
}

package com.chinatower.bdm.modules.grid.controller;

import com.chinatower.bdm.modules.grid.config.GeoServer;
import com.chinatower.bdm.modules.grid.dao.IServerDTO;
import com.chinatower.bdm.modules.grid.dao.Wgs84DTO;
import com.chinatower.bdm.modules.grid.service.WgsAnalysisService;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * video-bdm
 * @desription: 网格解析
 * @data 创建时间：2023/2/17 10:32
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(value = "网格解析-Api",tags = "网格解析")
@Slf4j
@RestController
@RequestMapping("/wgs")
public class WgsController {

   @Resource
   WgsAnalysisService wgsAnalysisService;

    /**
     * 解析网格处理
     * @return
     */
    @ApiOperation(value = "解析网格处理")
    @GetMapping("/analysisGrid")
    public Result analysisGrid(String fileUrl,String fileName){
        return wgsAnalysisService.analysisGrid(fileUrl,fileName,GeoServer.CHINATOWER_STYLE,null,false);
    }
}

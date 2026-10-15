package com.chinatower.platform.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.WaterLevelInfo;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.service.IWaterLevelService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WaterLevelController
 * @description: 水位球信息导出
 * @author: fengty
 * @create: 2024-12-13 15:43
 */
@RestController
@Slf4j
public class WaterLevelController {
    @Autowired
    private IWaterLevelService waterLevelService;

    private static final int EXCEL_EXPORT_LIMIT = 5000;

    @PostMapping("/video/device/queryWaterLevelExportList")
    @ApiOperation("导出水位球信息")
    public Result<String> exportWaterLevel(@RequestBody WaterLevelInfo waterLevelRequestVO, HttpServletResponse response) {
        log.info("导出水位球数据,参数:{}",waterLevelRequestVO);
        List<WaterLevelInfo> waterLevelInfoList =  waterLevelService.exportWaterLevel(waterLevelRequestVO);
        int total = waterLevelInfoList.size();
        if(total > EXCEL_EXPORT_LIMIT){
            return Result.fail("数据量过多，请按查询条件导出!");
        } else {
            byte[] data =new ExcelUtil<>(WaterLevelInfo.class).exportBytes(waterLevelInfoList);
            FileUtils.download(response, data);
            return Result.success("导出水位球数据成功!");
        }
    }
}

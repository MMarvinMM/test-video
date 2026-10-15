package com.chinatower.platform.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import com.chinatower.platform.service.IVideoCheckpointDeviceMgmtInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 *
 * video-export
 * @Author：zl
 * @Description：卡口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__卡口管理
 */
@RestController
@Slf4j
@Api(value = "卡口设备相关信息查询模块",tags = "卡口设备相关信息查询模块")
public class CheckpointDeviceMgmtController extends TowerBaseController {
    
    @Autowired
    private IVideoCheckpointDeviceMgmtInfoService videoCheckpointDeviceMgmtInfoService;

    private static final int EXCEL_EXPORT_LIMIT = 5000;


    /**
     *
     * 导出卡口设备数据
     * 功能描述：导出卡口设备数据  增加industryCode appVerCode
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @param response
     * @return Result<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @ApiOperation(value = "导出卡口设备数据")
    @PostMapping("/video/device/queryCheckpointDeviceExportList")
    public Result<String> queryCheckpointDeviceExportList(@RequestBody CheckpointDeviceInfoVO checkpointDeviceInfoVO, HttpServletResponse response) {
        List<CheckpointDeviceExportVO> list = videoCheckpointDeviceMgmtInfoService.queryCheckpointDeviceExportList(checkpointDeviceInfoVO);
        int total = list.size();
        if(total > EXCEL_EXPORT_LIMIT){
            return Result.fail("数据量过多，请按查询条件导出!");
        } else {
            byte[] data =new ExcelUtil<>(CheckpointDeviceExportVO.class).exportBytes(list);
            FileUtils.download(response, data);
            return Result.success("导出卡口设备数据成功!");
        }
    }
}

package com.chinatower.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.api.IHornDeviceService;
import com.chinatower.device.api.IReorderDeviceService;
import com.chinatower.device.api.IVideoDeviceMgmtService;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.*;
import com.chinatower.platform.entity.device.RecorderDeviceDTO;
import com.chinatower.platform.entity.device.RecorderDeviceVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * video-export
 * 功能说明: device模块导出功能类
 *
 * @author lyy
 * @date 2022-12-20 14:20
 * @requestID
 */
@RestController
@Slf4j
@RequestMapping(value = "/device")
public class DeviceExportController extends TowerBaseController {

    @Resource
    private IHornDeviceService iHornDeviceService;

    @Resource
    private IVideoDeviceMgmtService iVideoDeviceMgmtService;

    @Resource
    private IReorderDeviceService recorderDeviceService;


    /**
     * 摄像机管理导出
     * @param deviceInfoVO
     * @param response
     */
    @PostMapping("/exportCameraDeviceList")
    public void exportCameraDeviceList(@RequestBody DeviceInfoVO deviceInfoVO, HttpServletResponse response) {
        try {
            deviceInfoVO.setExport(true);
            com.chinatower.common.core.web.page.TableDataInfo dataInfo = iVideoDeviceMgmtService.queryDeviceInfoPage(deviceInfoVO);
            List<CameraDeviceVoExport> exportList = new ArrayList<>();
            if(!dataInfo.getRows().isEmpty()){
                for(Object cameraDevice : dataInfo.getRows()){
                    CameraDeviceVoExport deviceVoExport = new CameraDeviceVoExport();
                    BeanUtil.copyProperties(cameraDevice, deviceVoExport);
                    if(!CollectionUtils.isEmpty(deviceVoExport.getLabelNameList())){
                        deviceVoExport.setLabelName(String.join(Constants.COMMA, deviceVoExport.getLabelNameList()));
                    }
                    exportList.add(deviceVoExport);
                }
            }
            byte[] data = new ExcelUtil<>(CameraDeviceVoExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出摄像机管理列表异常：", e);
        }
    }

    /**
     *
     * 喇叭管理列表导出
     * 功能描述 喇叭管理列表导出
     *
     * @param
     * @param response
     * @author lyy
     * @date 2023/12/20 14:23
     * @version:
     */
    @PostMapping("/exportHornDeviceList")
    public void exportHornDeviceList(@RequestBody HornDeviceDTO hornDeviceDTO, HttpServletResponse response) {
        try {
            hornDeviceDTO.setExport(true);
            Result<PageInfo<HornDeviceVo>> result = iHornDeviceService.queryHornDevicePage(hornDeviceDTO);
            PageInfo<HornDeviceVo> pageInfo = result.getData();
            List<HornDeviceVo> hornDeviceList = pageInfo.getList();
            List<HornDeviceVoExport> exportList = new ArrayList<>();
            hornDeviceList.forEach(hornDevice -> {
                HornDeviceVoExport deviceVoExport = new HornDeviceVoExport();
                BeanUtil.copyProperties(hornDevice, deviceVoExport);
                exportList.add(deviceVoExport);
            });
            byte[] data = new ExcelUtil<>(HornDeviceVoExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出喇叭管理列表异常：", e);
        }
    }

    /**
     *
     * 无人机管理列表导出
     * 功能描述 无人机管理列表导出
     *
     * @param
     * @param response
     * @author lyy
     * @date 2023/12/20 14:23
     * @version:
     */
    @PostMapping("/exportUavDeviceList")
    public void exportUavDeviceList(@RequestBody UavDeviceDTO uavDeviceDTO, HttpServletResponse response) {
        try {
            uavDeviceDTO.setExport(true);
            TableDataInfo uavResult = iHornDeviceService.qryUavDeviceListPage(uavDeviceDTO);
            List<UavDeviceVoExport> exportList = new ArrayList<>();
            if(!uavResult.getRows().isEmpty()){
                for(Object uavDevice : uavResult.getRows()){
                    UavDeviceVoExport deviceVoExport = new UavDeviceVoExport();
                    BeanUtil.copyProperties(uavDevice, deviceVoExport);
                    exportList.add(deviceVoExport);
                }
            }
            byte[] data = new ExcelUtil<>(UavDeviceVoExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出无人机管理列表异常：", e);
        }
    }

    /**
     *
     * 物联设备管理列表导出
     * 功能描述 物联设备管理列表导出
     *
     * @param
     * @param response
     * @author lyy
     * @date 2023/12/20 14:23
     * @version:
     */
    @PostMapping("/exportIotDeviceList")
    public void exportIotDeviceList(@RequestBody IotDeviceVO iotDeviceVO, HttpServletResponse response) {
        try {
            iotDeviceVO.setExport(true);
            TableDataInfo iotResult = iHornDeviceService.queryIotDevicePage(iotDeviceVO);
            List<IotDeviceVoExport> exportList = new ArrayList<>();
            if(!iotResult.getRows().isEmpty()){
                for(Object iotDevice : iotResult.getRows()){
                    IotDeviceVoExport deviceVoExport = new IotDeviceVoExport();
                    BeanUtil.copyProperties(iotDevice, deviceVoExport);
                    deviceVoExport.setHasLinkConf(!StringUtils.isEmpty(deviceVoExport.getHasLinkConf()) && Constants.ONE.equals(deviceVoExport.getHasLinkConf()) ? "是" : "否");
                    exportList.add(deviceVoExport);
                }
            }
            byte[] data = new ExcelUtil<>(IotDeviceVoExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出物联设备管理列表异常：", e);
        }
    }

    /**
     *
     * 雷达管理列表导出
     * 功能描述 雷达管理列表导出
     *
     * @param
     * @param response
     * @author lyy
     * @date 2023/12/20 14:23
     * @version:
     */
    @PostMapping("/exportRadarDeviceList")
    public void exportRadarDeviceList(@RequestBody IotDeviceVO iotDeviceVO, HttpServletResponse response) {
        try {
            iotDeviceVO.setExport(true);
            TableDataInfo radarResult = iHornDeviceService.queryRadarListPage(iotDeviceVO);
            List<RadarDeviceVoExport> exportList = new ArrayList<>();
            if(!radarResult.getRows().isEmpty()){
                for(Object radarDevice : radarResult.getRows()){
                    RadarDeviceVoExport deviceVoExport = new RadarDeviceVoExport();
                    BeanUtil.copyProperties(radarDevice, deviceVoExport);
                    exportList.add(deviceVoExport);
                }
            }
            byte[] data = new ExcelUtil<>(RadarDeviceVoExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出雷达管理列表异常：", e);
        }
    }


    /**
     * 执法记录仪管理导出
     * @param recorderDeviceVO
     * @param response
     */
    @PostMapping("/exportRecorderDeviceList")
    public void exportRecorderDeviceList(@RequestBody RecorderDeviceVO recorderDeviceVO, HttpServletResponse response) {
        try {
            Result<List<RecorderDeviceDTO>> result = recorderDeviceService.getList(recorderDeviceVO);
            byte[] data = new ExcelUtil<>(RecorderDeviceDTO.class).exportBytes(result.getData());
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出无人机管理列表异常：", e);
        }
    }
}

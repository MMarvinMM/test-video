package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.factory.HornDeviceFallbackFactory;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.TalkReq;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.VmHornAuditionRequestDTO;
import com.chinatower.device.entity.dto.VmHornContentConfDTO;
import com.chinatower.device.entity.dto.VmHornContentConfRequestListDTO;
import com.chinatower.device.entity.dto.VmHornContentRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "hornDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = HornDeviceFallbackFactory.class)
public interface IHornDeviceServiceMigrate {


    @PostMapping("/hornDevice/getHornMonitorTree")
    Result<List<DeviceTreeVO>> getHornMonitorTree(@RequestBody HornDeviceDTO hornDeviceDTO);


    @PostMapping("/hornDevice/getHornMonitorOrgTree")
    Result<List<DeviceTreeVO>> getHornMonitorOrgTree(@RequestBody HornDeviceDTO hornDeviceDTO);


    @PostMapping("/hornDevice/startTalk")
    Result<LockResultVo> startTalk(@RequestBody TalkReq talkReq);


    @PostMapping("/hornDevice/stopTalk")
    Result<String> stopTalk(@RequestBody TalkReq talkReq);


    @PostMapping("/hornDevice/queryHornDeviceDetail")
    Result<HornDeviceVo> queryHornDeviceDetail(@RequestBody HornDeviceDTO hornDeviceDTO);


    @PostMapping("/hornDevice/queryHornForArea")
    Result<List<HornDeviceVo>> queryHornForArea(@RequestBody HornDeviceDTO hornDeviceDTO);


    @PostMapping("/hornDevice/queryHornForSite")
    Result<List<HornDeviceVo>> queryHornForSite(@RequestBody HornDeviceDTO hornDeviceDTO);


    @PostMapping("/hornDevice/querySiteDevTree")
    Result<List<HornDeviceVo>> querySiteDevTree(@RequestBody HornDeviceDTO hornDeviceDTO);


    @PostMapping("/hornDevice/queryHornDevicePage")
    Result<PageInfo<HornDeviceVo>> queryHornDevicePage(@RequestBody HornDeviceDTO hornDeviceDTO);


    @PostMapping("/uav/device/qryUavDeviceListPage")
    TableDataInfo qryUavDeviceListPage(@RequestBody UavDeviceDTO uavDeviceDTO);


    @PostMapping("/iotdevice/queryIotDevicePage")
    TableDataInfo queryIotDevicePage(@RequestBody IotDeviceVO deviceInfoVO);


    @PostMapping("/radar/queryRadarListPage")
    TableDataInfo queryRadarListPage(@RequestBody IotDeviceVO deviceInfoVO);

    @PostMapping("/hornTask/device/callHornTaskPlay")
    Result<String> callHornTaskPlay(@RequestBody List<String> fileIdList);

    @PostMapping("/horn/content/add")
    Result<String> addHornContent(@RequestBody VmHornContentVO param);

    @PostMapping("/horn/content/update")
    Result<String> updateHornContent(@RequestBody VmHornContentVO param);

    @PostMapping("/horn/content/audit")
    Result<String> auditHornContent(@RequestBody VmHornContentVO param);

    @PostMapping("/horn/content/delete")
    Result<String> deleteHornContent(@RequestBody List<String> ids);

    @PostMapping("/hornTask/device/addHornTask")
    Result<String> addHornTask(@RequestBody HornTaskConfVO param);

    @PostMapping("/hornTask/device/addAndStartHornTask")
    Result<String> addAndStartHornTask(@RequestBody HornTaskConfVO param);

    @PostMapping("/hornTask/device/addHornTaskV2")
    Result<String> addHornTaskV2(@RequestBody HornTaskConfVO param);

    @PostMapping("/hornTask/device/deleteHornTask/v2")
    Result<String> deleteHornTaskV2(@RequestBody HornTaskConfVO hornTaskConfVO);

    @PostMapping("/horn/content/list")
    Result<PageInfo<VmHornContentConfDTO>> list(@RequestBody VmHornContentConfRequestListDTO param);

    @PostMapping("/horn/content/audition")
    Result<HornAuditionResultVO> audition(@RequestBody VmHornAuditionRequestDTO param);

    @PostMapping("/hornTask/device/list")
    TableDataInfo getList(@RequestBody HornTaskConfVO hornTaskConfVO);

    @PostMapping("/hornTask/device/startHornTask")
    Result<String> startTask(@RequestBody HornTaskConfVO hornTaskConfVO);

    @PostMapping("/hornTask/device/playRightNow")
    Result<String> playRightNow(@RequestBody HornTaskConfVO hornTaskConfVO);

    @PostMapping("/hornTask/device/stopHornTask")
    Result<String> stopHornTask(@RequestBody HornTaskConfVO hornTaskConfVO);

    @PostMapping("/hornTask/device/deleteHornTask")
    Result<String> deleteHornTask(@RequestBody HornTaskConfVO hornTaskConfVO);

    @PostMapping("/horn/content/getListByContentType")
    Result<PageInfo<VmHornContentConfDTO>> getListByContentType(@RequestBody VmHornContentRequestDTO param);

    @PostMapping("/hornTask/device/syncTaskInfoToAlgorithmMongo")
    Result<String> syncTaskInfoToAlgorithmMongo(@RequestBody Long taskId);

    @PostMapping("/iotdevice/queryIotRadarDeviceList")
    Result<List<IotDeviceVO> > queryIotRadarDeviceList(@RequestBody IotDeviceVO deviceInfoVO);
}

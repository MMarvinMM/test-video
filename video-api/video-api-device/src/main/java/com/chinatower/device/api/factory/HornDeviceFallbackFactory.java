package com.chinatower.device.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.deprecated.IHornDeviceServiceMigrate;
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
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HornDeviceFallbackFactory implements FallbackFactory<IHornDeviceServiceMigrate> {

    @Override
    public IHornDeviceServiceMigrate create(Throwable throwable) {
        return new IHornDeviceServiceMigrate() {


            @Override
            public Result<List<DeviceTreeVO>> getHornMonitorTree(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("查询设备所属的行政区域树形失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getHornMonitorOrgTree(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("查询行政组织喇叭设备树失败:" + throwable.getMessage());
            }

            @Override
            public Result<LockResultVo> startTalk(TalkReq talkReq) {
                return Result.fail("实时喊话-开始喊话失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> stopTalk(TalkReq talkReq) {
                return Result.fail("实时喊话-停止失败:" + throwable.getMessage());
            }

            @Override
            public Result<HornDeviceVo> queryHornDeviceDetail(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("查询喇叭设备详情失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<HornDeviceVo>> queryHornForArea(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("根据区域编码查询喇叭列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<HornDeviceVo>> queryHornForSite(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("根据站址编码查询喇叭列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<HornDeviceVo>> querySiteDevTree(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("喇叭站址分布查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<HornDeviceVo>> queryHornDevicePage(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("查询喇叭列表失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo qryUavDeviceListPage(UavDeviceDTO uavDeviceDTO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo queryIotDevicePage(IotDeviceVO deviceInfoVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo queryRadarListPage(IotDeviceVO deviceInfoVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<String> callHornTaskPlay(List<String> fileIdList) {
                return Result.fail(":" + throwable.getMessage());
            }

            @Override
            public Result<String> addHornContent(VmHornContentVO param) {
                return Result.fail("新增广播内容失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> updateHornContent(VmHornContentVO param) {
                return Result.fail("更新广播内容失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> auditHornContent(VmHornContentVO param) {
                return Result.fail("审核广播内容失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> deleteHornContent(List<String> ids) {
                return Result.fail("删除广播内容失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> addHornTask(HornTaskConfVO param) {
                return Result.fail("新增广播任务失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> addAndStartHornTask(HornTaskConfVO param) {
                return Result.fail("新增启动广播任务失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> addHornTaskV2(HornTaskConfVO param) {
                return Result.fail("新增广播任务V2失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> deleteHornTaskV2(HornTaskConfVO hornTaskConfVO) {
                return Result.fail("删除任务V2失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<VmHornContentConfDTO>> list(VmHornContentConfRequestListDTO param) {
                return Result.fail("获取内容列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<HornAuditionResultVO> audition(VmHornAuditionRequestDTO param) {
                return Result.fail("试听内容失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getList(HornTaskConfVO hornTaskConfVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<String> startTask(HornTaskConfVO hornTaskConfVO) {
                return Result.fail("开始任务失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> playRightNow(HornTaskConfVO hornTaskConfVO) {
                return Result.fail("立即播放失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> stopHornTask(HornTaskConfVO hornTaskConfVO) {
                return Result.fail("停止任务失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> deleteHornTask(HornTaskConfVO hornTaskConfVO) {
                return Result.fail("删除任务失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<VmHornContentConfDTO>> getListByContentType(VmHornContentRequestDTO param) {
                return Result.fail("根据内容类型及查询条件获取广播内容分页结果失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> syncTaskInfoToAlgorithmMongo(Long taskId) {
                return Result.fail("同步广播任务到mongo失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceVO>> queryIotRadarDeviceList(IotDeviceVO deviceInfoVO) {
                return Result.fail("查询物联、雷达设备失败:" + throwable.getMessage());
            }
        };
    }
}

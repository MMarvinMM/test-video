package com.chinatower.device.api.factory;


import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.IVideoCheckpointDeviceMgmtService;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备服务降级处理
 *
 * @author lc
 */
@Component
public class VideoCheckpointDeviceFallbackFactory implements FallbackFactory<IVideoCheckpointDeviceMgmtService> {

    @Override
    public IVideoCheckpointDeviceMgmtService create(Throwable throwable) {

        return new IVideoCheckpointDeviceMgmtService() {
            @Override
            public TableDataInfo queryCheckpointDeviceInfoPage(CheckpointDeviceInfoVO checkpointDeviceInfoVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<String> insertCheckpointDeviceInfo(List<String> channelCodeList) {
                return Result.fail("新增卡口设备失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> deleteCheckpointDeviceInfoBatch(List<String> ids) {
                return Result.fail("删除卡口设备失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TreeSelect>> getCheckpointDeviceTree() {
                return Result.fail("查询卡口设备通道树失败:" + throwable.getMessage());
            }
        };
    }
}

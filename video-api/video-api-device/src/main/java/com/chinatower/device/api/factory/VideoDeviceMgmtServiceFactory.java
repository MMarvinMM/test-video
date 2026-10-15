package com.chinatower.device.api.factory;


import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IVideoDeviceMgmtServiceMigrate;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备服务降级处理
 *
 * @author chenmf
 */
@Component
public class VideoDeviceMgmtServiceFactory implements FallbackFactory<IVideoDeviceMgmtServiceMigrate> {

    private static final Logger log = LoggerFactory.getLogger(VideoDeviceMgmtServiceFactory.class);

    @Override
    public IVideoDeviceMgmtServiceMigrate create(Throwable throwable) {

        return new IVideoDeviceMgmtServiceMigrate() {
            @Override
            public Result<String> addDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO) {
                return Result.fail("添加客户设备标签失败:" + throwable.getMessage());
            }

            public TableDataInfo queryDeviceInfoPage(DeviceInfoVO deviceInfoVO) {
                return null;
            }

            @Override
            public Result<List<ChannelInfoVO>> listChannelInfoForCustDev(DeviceInfoVO deviceInfoVO) {
                return Result.fail("查询客户设备下通道信息失败:" + throwable.getMessage());
            }

            @Override
            public  Result<List<DeviceAreaTreeVO>> queryAreaTree(DeviceReqDTO deviceReqDTO){
                return Result.fail("查询设备所属的行政区域树形失败:" + throwable.getMessage());
            }

            @Override
            public  Result<DeviceInfoVO> getDeviceInfo(DeviceInfoVO deviceInfoVO){
                return Result.fail("根据条件查询设备信息失败:" + throwable.getMessage());
            }


            @Override
            public  Result<String> updateDeviceInfo(DeviceInfoVO deviceInfoVO){
                return Result.fail("更新设备相关信息失败:" + throwable.getMessage());
            }

            @Override
            public  Result<List<DeviceTenantInfoVO>> getDeviceCustList(DeviceInfoVO deviceInfoVO){
                return Result.fail("查询设备下的客户信息列表失败:" + throwable.getMessage());
            }

            @Override
            public  Result<Boolean> checkDeviceName(DeviceInfoVO deviceInfoVO){
                return Result.fail("设备编辑时校验设备名称是否重复失败:" + throwable.getMessage());
            }

            @Override
            public  Result<List<TenantDeviceLabelVO>> queryDeviceLabels(TenantDeviceLabelVO tenantDeviceLabelVO){
                return Result.fail("查询客户设备标签失败:" + throwable.getMessage());
            }
            @Override
            public  Result<List<TenantDeviceLabelVO>> queryTenantLabelList(TenantDeviceLabelVO tenantDeviceLabelVO){
                return Result.fail("查询租户所属标签失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> addDeviceLabelRel(TenantDeviceLabelVO tenantDeviceLabelVO){
                return Result.fail("添加客户设备标签关系失败:" + throwable.getMessage());
            }
            @Override
            public Result<String> updateDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO){
                return Result.fail("修改客户设备标签失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> relieveDeviceLabelRel(TenantDeviceLabelVO tenantDeviceLabelVO){
                return Result.fail("解除客户设备标签关系失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> deleteDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO){
                return Result.fail("删除客户设备标签失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode(TenantDeviceLabelRelVO param) {
                return Result.fail("获取设备标签失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceLabelInfoVO>> queryDeviceLabelInfoByCondition(DeviceLabelReq param) {
                return Result.fail("获取设备标签sdk失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<String>> getDeviceImage(DeviceInfoVO deviceInfoVO) {
                return Result.fail("获取摄像机图片失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceInfoVO>> queryOrderDeviceByDevices(DeviceInfoVO deviceInfoVO) {
                return Result.fail("获取客户订购信息失败:" + throwable.getMessage());
            }

        };
    }
}

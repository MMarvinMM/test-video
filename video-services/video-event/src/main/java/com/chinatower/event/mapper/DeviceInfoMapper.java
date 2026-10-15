package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.event.vo.entity.DeviceTypeInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceLabelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS(EbikeDataBaseConstant.BASE)
public interface DeviceInfoMapper  {

    List<String> getDeptIds(String orgCode);

    List<DeviceInfoVO> queryDeviceInfoList(@Param("param") DeviceInfoVO deviceInfoVO);

    List<DeviceTypeInfoVO> getDeviceTypeList(@Param("param") DeviceInfoVO deviceInfoVO);

    List<TenantDeviceLabelVO> selectLabelInfoByDeviceCode(@Param("deviceCodeList") List<String> deviceCodeList,
                                                          @Param("tenantId") String tenantId,
                                                          @Param("industryCode") String industryCode);

}

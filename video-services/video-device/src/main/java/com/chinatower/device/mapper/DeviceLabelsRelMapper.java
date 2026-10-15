package com.chinatower.device.mapper;

import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelsRelVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceLabelDetailVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceLabelRelVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceLabelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备标签关系DAO
 * @author chmod
 * @version 1.0
 * @date 2022/1/5 15:13
 */
public interface DeviceLabelsRelMapper {
    /**
     * 添加
     * @param deviceLabelsRelVO
     * @return
     */
    int addDeviceLabelsRel(DeviceLabelsRelVO deviceLabelsRelVO);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteDeviceLabelsRel(Long id);

    /**
     * 查询
     * @param tenantDeviceLabelVO
     * @return
     */
    int countDeviceLabelsRel(TenantDeviceLabelVO tenantDeviceLabelVO);

    int deleteBatch(String tenantId,String deviceCode,String industryCode);

    int insertBatch(List<TenantDeviceLabelVO> list);

    int addDeviceLabelRelBatch(@Param("list") List<TenantDeviceLabelVO> list);

    int deleteDeviceLabelRelByDeviceCode(@Param("deviceCode") String deviceCode,
                                         @Param("tenantId") String tenantId,
                                         @Param("industryCode") String industryCode);

    List<Long> selectRelLabelIdByDeviceCode(@Param("deviceCode") String deviceCode,
                                            @Param("tenantId") String tenantId,
                                            @Param("industryCode") String industryCode);

    List<TenantDeviceLabelVO> selectLabelInfoByDeviceCode(@Param("deviceCodeList") List<String> deviceCodeList,
                                                          @Param("tenantId") String tenantId,
                                                          @Param("industryCode") String industryCode);
}

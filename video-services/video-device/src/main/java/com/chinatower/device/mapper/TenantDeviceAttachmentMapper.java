package com.chinatower.device.mapper;

import com.chinatower.device.dto.DeviceAttachmentFileUrlVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceAttachmentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TenantDeviceAttachmentMapper {

    /**
     * 批量插入设备附件信息
     * @param list 设备附件信息
     * @return 结果
     */
    int batchInsert(@Param("list") List<TenantDeviceAttachmentVO> list);

    /**
     * 根据设备编码删除设备附件信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param deviceCode 设备编码
     * @param deviceType 设备类型
     * @return 影响行数
     */
    int deleteByDeviceCode(@Param("tenantId")String tenantId,
                           @Param("industryCode")String industryCode,
                           @Param("deviceCode")String deviceCode,
                           @Param("deviceType")Integer deviceType);

    /**
     * 根据设备编码查询设备附件地址信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param deviceCode 设备编码
     * @param deviceType 设备类型
     * @return 附件信息
     */
    List<String> selectFileUrlByDeviceCode(@Param("tenantId")String tenantId,
                                                      @Param("industryCode")String industryCode,
                                                      @Param("deviceCode")String deviceCode,
                                                      @Param("deviceType")Integer deviceType);

    /**
     * 根据设备编码查询设备附件地址信息并排序，优先展示视频，再展示图片
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param deviceCode 设备编码
     * @param deviceType 设备类型
     * @return 附件信息
     */
    List<String> selectFileUrlByDeviceCodeOrder(@Param("tenantId")String tenantId,
                                                      @Param("industryCode")String industryCode,
                                                      @Param("deviceCode")String deviceCode,
                                                      @Param("deviceType")Integer deviceType);

    /**
     * 根据设备编码查询设备附件地址信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param deviceCodeList 设备编码
     * @param deviceType 设备类型
     * @return 附件信息
     */
    List<DeviceAttachmentFileUrlVO> selectFileUrlByDeviceCodeList(@Param("tenantId") String tenantId,
                                                                  @Param("industryCode") String industryCode,
                                                                  @Param("list") List<String> deviceCodeList,
                                                                  @Param("deviceType") Integer deviceType);
}

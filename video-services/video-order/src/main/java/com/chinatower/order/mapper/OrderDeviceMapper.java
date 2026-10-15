package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderDeviceDO;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.order.FindDeviceCodeVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.entity.order.QueryOrderRelationshipVO;
import com.chinatower.system.api.model.DeviceTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName OrderDeviceMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderDeviceMapper extends BaseMapper<OrderDeviceDO> {
    
    /**
     * 查询客户下有效的设备id
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    List<String> getValidDev(@Param("customerCode") String customerCode);
    
    /**
     * 查询客户下有效的设备通道id列表
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    List<String> getValidChn(@Param("customerCode") String customerCode);
    
    /**
     * 查询对应订单设备信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/3
     * @version
     */
    List<OrderDevicesVO> getDeviceInfoByList(@Param("orderCodeList")List<String> orderCodeList, @Param("packageCodeList")List<String> packageCodeList);
    
    /**
     * 根据用户编号查询用户订购的行业版本下下的设备编号
     *
     * @return
     * @author chenmf
     * @date 2022/3/5
     * @version
     */
    List<String> findDeviceCodeByUser(FindDeviceCodeVO findDeviceCodeVO);
    
    
    /**
     * 获取有效的订单编码
     *
     * @return
     * @author chenmf
     * @date 2022/5/30
     * @version
     */
    List<VideoOrderResp> getOrderCode(@Param("tenantId")String tenantId, @Param("deviceCode")String deviceCode,
                                      @Param("channelCode")String channelCode, @Param("industryCode")String industryCode);

    /**
     * 根据订单号更改订单表状态
     * @param orderCode 订单号
     * @return 影响条数
     */
    int cancelByOrderCode(@Param("orderCode")String orderCode);

    /**
     * 根据用户和行业查询设备
     * @param tenantId
     * @param industryCode
     * @return
     */
    List<OrderDevicesVO> getByTenantIdAndIndustryCode(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    /**
     * 查询当前用户订购的设备列表
     * @param tenantId
     * @return
     */
    List<DeviceTreeVO> getTenantDeviceList(String orderCode);

    /**
     *查询所有的行政区划编码与名称
     * @return
     */
    List<SysArea> getAllAreaInfo();

    List<OrderDevicesVO> getDeviceInfoByOrderCodeAndPackageCode(QueryOrderRelationshipVO queryOrderRelationshipVO);

    List<OrderDevicesVO> getDeviceInfoByDeviceCodes(@Param("deviceCodes")List<String> deviceCodes);


    int batchInsert(@Param("list")List<OrderDeviceDO> list);


}

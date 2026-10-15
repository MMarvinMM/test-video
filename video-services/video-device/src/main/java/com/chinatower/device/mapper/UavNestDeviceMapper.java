package com.chinatower.device.mapper;


import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.UavDeviceQueryVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UavNestDeviceMapper {


    /**
     * 查询无人机机巢设备距离信息
     *
     * @param param 参数
     * @return 无人机设备距离信息
     */
    List<DeviceDistanceVO> queryUavNestDeviceDistanceInfo(@Param("param") DeviceDistanceDTO param);

    /***
     * 获取无人机机巢区域树
     * @param param
     * @return
     */
    List<DeviceTreeVO> getUavNestMonitorAreaTree(@Param("param") DeviceReqDTO param);

    List<DeviceTreeVO> getUavNestMonitorAreaTreeWithoutFormat(@Param("param") DeviceReqDTO param);

    /**
     * 获取无人机机巢打点数据
     * @param param
     * @return
     */
    List<DeviceSiteVO> queryUavNestDevicesBySiteCode(@Param("param") SiteRequestDTO param);

    /**
     * 获取无人机组织树
     *
     * @param param
     * @return
     */
    List<DeviceTreeVO> getUavNestMonitorOrgTree(@Param("param") DeviceReqDTO param);

    List<DeviceTreeVO> getUavNestMonitorOrgTreeWithoutFormat(@Param("param") DeviceReqDTO param);

    List<IndustryDeviceTree> queryUavNestSites(@Param("param") SiteRequestDTO param);

    /**
     * 根据无人机设备编码查询无人机设备信息
     *
     * @param deviceCode
     * @return
     */
    DeviceCommonInfoVO qryByDeviceCode(@Param("deviceCode") String deviceCode, @Param("tenantId") String tenantId,
                                       @Param("industryCode") String industryCode, @Param("userId") String userId);
}

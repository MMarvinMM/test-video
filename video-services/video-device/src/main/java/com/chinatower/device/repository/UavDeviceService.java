package com.chinatower.device.repository;


import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.UavPointToFlyQueryReqDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 *
 * 功能描述: 铁塔视角--无人机设备管理
 *
 * @auther: luo.zhenghong
 * @date: 2023/6/8
 * @version: release_v1.5.95
 * @requestID: 中国铁塔_铁塔视联平台__铁塔设备管理-无人机管理
 */
public interface UavDeviceService {

    /**
     * 查询无人机设备及其通道信息
     *
     * @param param
     * @return
     */
    List<UavDeviceQueryVO> queryUavDeviceInfoList(UavDeviceDTO param);

    /**
     * 查询无人机设备及其通道信息
     *
     * @param param
     * @return
     */
    List<UavDeviceQueryVO> queryUavNestDeviceInfoList(UavDeviceDTO param);

    /**
     * 根据设备编码查询无人机设备信息
     *
     * @param deviceCode
     * @return
     */
    UavDeviceQueryVO qryByDeviceCode(String deviceCode);

    /**
     * 修改无人机设备信息
     *
     * @param param
     * @return
     */
    void updateUavDeviceInfo(UavDeviceDTO param);

    /**
     * 查询无人机设备树
     */
    List<DeviceTreeVO> getUavMonitorTree(UavDeviceDTO param);

    /**
     * 查询无人机设备距离信息
     *
     * @param param 参数
     * @return 无人机设备距离信息
     */
    List<DeviceDistanceVO> queryUavDeviceDistanceInfo(DeviceDistanceDTO param);

    /**
     * APP查询无人机设备距离信息
     *
     * @param param 参数
     * @return 无人机设备距离信息
     */
    List<DeviceDistanceVO> queryUavDeviceDistanceInfo4App(DeviceDistanceDTO param);

    /**
     * 无人机区域树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavMonitorAreaTree(DeviceReqDTO deviceReqDTO);

    /**
     * 无人机区域树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavMonitorAreaTreeWithoutFormat(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavMonitorAreaTreeWithoutLogin(DeviceReqDTO deviceReqDTO);

    /**
     * 无人机区域树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavMonitorOrgTree(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavMonitorOrgTreeWithoutFormat(DeviceReqDTO deviceReqDTO);

    /**
     * 查询无人机分布情况
     *
     * @param siteRequestDTO
     * @return
     */
    List<SiteResultVO> queryUavSitesForWE(SiteRequestDTO siteRequestDTO);

    List<IndustryDeviceTree> queryUavSites(SiteRequestDTO siteRequestDTO);

    /**
     * 根据站址编码查询摄像机列表信息
     *
     * @param siteRequestDTO
     * @return
     */
    List<DeviceSiteVO> queryUavDevicesBySiteCode(SiteRequestDTO siteRequestDTO);

    /**
     * 通过关键字查询无人机设备列表
     *
     * @param deviceComprehensiveReq
     * @return
     */
    Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq);


    /**
     * 查询无人机相机型号信息
     *
     * @param param 无人机编码
     * @return 无人机相机型号信息
     */
    List<DeviceCameraModelVO> queryUavCameraModelInfo(UavDeviceDTO param);

    /**
     * 查询无人机相机型号信息
     *
     * @param param 无人机编码
     * @return 无人机相机型号信息
     */
    UavDetailVO queryUavDetail(UavDeviceDTO param);

    /**
     * 根据机巢查询无人机信息
     * @param param 参数
     * @return 返回值
     */
    UavDeviceDTO queryUavByNestInfo(UavDeviceDTO param);

    /**
     * 同步更新当前无人机巢的信息
     *
     * @param param 机巢编码
     * @return 无人机巢的地址信息
     */
    UavNestLocationVO getUavNestLocationInfo(UavDeviceDTO param);

    /**
     * 查询无人机卡片列表
     *
     * @param deviceReqDTO
     * @return
     */
    List<UavCardQueryVO> queryUavInfoList(DeviceReqDTO deviceReqDTO);

    /**
     * 查询指点飞行可用无人机列表
     *
     * @param queryReqDTO 查询请求参数
     * @return 符合条件的无人机卡片列表
     */
    List<UavPointToFlyCardVO> queryUavPointToFlyList(UavPointToFlyQueryReqDTO queryReqDTO);

    /**
     * 根据条件查询设备基本信息
     * @param param
     * @return
     */
    List<UavDeviceQueryVO> queryUavListByCondition(UavDeviceDTO param);

    /**
     * 获取当前租户当前行业下的正在飞行中的无人机设备数量
     * @return 正在飞行中的无人机设备数量
     */
    int getFlyingUavDeviceCount(UavDeviceDTO param);
}

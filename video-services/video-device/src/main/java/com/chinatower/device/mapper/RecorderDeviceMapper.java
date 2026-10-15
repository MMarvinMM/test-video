package com.chinatower.device.mapper;

import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 执法记录仪mapper
 */
public interface RecorderDeviceMapper {

    /**
     * 根据条件查询执法记录仪列表
     * @param recorderDeviceVO
     * @return
     */
    List<RecorderDeviceDTO> getRecorderDeviceListPage(RecorderDeviceVO recorderDeviceVO);

    /**
     * 查询执法记录仪设备组织关系
     * @param recorderDeviceVO
     * @return
     */
    List<OrganDeviceRelVO> queryOrgRecorderDevRels(RecorderDeviceVO recorderDeviceVO);

    /**
     * 查询无人机设备区域树
     *
     * @param recorderDeviceVO
     * @return
     */
    List<DeviceTreeVO> getAreaTree(RecorderDeviceVO recorderDeviceVO);

    /**
     * 查询执法记录仪设备是否存在别名
     * @param recorderDeviceVO
     * @return
     */
    RecorderDeviceDTO getAliasByDeviceCode(RecorderDeviceVO recorderDeviceVO);

    /**
     * 新增别名
     * @param recorderDeviceVO
     * @return
     */
    int addAlias(RecorderDeviceVO recorderDeviceVO);

    /**
     * 修改别名
     * @param recorderDeviceVO
     * @return
     */
    int updateAlias(RecorderDeviceVO recorderDeviceVO);

    /**
     * 根据设备编码查询执法记录仪详情信息
     * @param recorderDeviceVO
     * @return
     */
    RecorderDeviceDTO getByDeviceCode(RecorderDeviceVO recorderDeviceVO);

    /**
     * 根据执法记录仪设备编码查询执法记录仪通道信息
     *
     * @param list
     * @return
     */
    List<ChannelInfoVO> getChannelByDeviceCodes(@Param("list") List<String> list);

    /**
     * 根据执法记录仪设备编码查询执法记录仪通道及别名信息
     *
     * @param list
     * @return
     */
    List<ChannelInfoVO> getChannelAliasByDeviceCodeList(@Param("list") List<String> list,@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);

    /**
     * 根据站址编码和经纬度查询执法记录仪设备列表
     *
     * @param siteRequestDTO
     * @return
     */
    List<DeviceSiteVO> getRecorderDevicesBySiteCode(@Param("param") SiteRequestDTO siteRequestDTO);
    /**
     * 获取执法记录仪设备组织树
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getRecorderMonitorOrgTree(@Param("param") DeviceReqDTO deviceReqDTO);

    /**
     * 查询执法记录仪设备距离信息
     *
     * @param param 参数
     * @return 执法记录仪设备距离信息
     */
    List<DeviceDistanceVO> getRecorderDeviceDistanceInfo(@Param("param") DeviceDistanceDTO param);

    /***
     * 获取执法记录仪设备区域树--大屏使用
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getRecorderMonitorAreaTree(@Param("param") DeviceReqDTO deviceReqDTO);

    List<IndustryDeviceTree> getRecorderSites(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 根据通道编码查询通道信息
     *
     * @param list
     * @return
     */
    List<ChannelInfoVO> getChannelByChannelCodeList(@Param("list") List<String> list);

    /**
     * 根据通道编码查询对应无人机下的所有通道信息
     *
     * @param list
     * @return
     */
    List<ChannelInfoVO> getAllDeviceChannelByChannelCodeList(@Param("list") List<String> list);
    /**
     * 根据关键字查询执法记录仪列表
     * @param deviceComprehensiveReq
     * @return
     */
    List<DeviceComprehensiveVO> queryDeviceListByKeyWord(@Param("param") DeviceComprehensiveReq deviceComprehensiveReq);

    /**
     * 根据条件查询执法记录仪列表基础信息
     * @param recorderDeviceVO
     * @return
     */
    List<RecorderDeviceDTO> queryRecorderListByCondition(RecorderDeviceVO recorderDeviceVO);
}

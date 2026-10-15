package com.chinatower.device.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dataobject.HornDeviceDo;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.dto.TalkV2DTO;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.TalkReq;
import com.chinatower.device.entity.dto.TalkV2Req;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.device.entity.vo.LockResultVo;
import com.chinatower.device.entity.vo.TalkResultVO;
import com.chinatower.device.entity.vo.TalkV2VO;
import com.chinatower.device.entity.vo.TalkV3VO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 喇叭管理 服务类
 */
public interface IHornDeviceRepository extends IService<HornDeviceDo> {

    /**
     * 根据条件分页查询喇叭信息
     * @param hornDeviceDTO 喇叭设备查询信息
     * @return List<HornDeviceVo>
     */
    Result<PageInfo<HornDeviceVo>> queryHornDevicePage(HornDeviceDTO hornDeviceDTO);

    /**
     * 根据条件分页查询喇叭信息--APP端用
     *
     * @param hornDeviceDTO 喇叭设备查询信息
     * @return List<HornDeviceVo>
     */
    @Deprecated
    Result<PageInfo<HornDeviceVo>> queryHornDeviceList(HornDeviceDTO hornDeviceDTO);

    /**
     * 查询喇叭详情
     * @param hornDeviceDTO
     * @return
     */
    Result<HornDeviceVo> queryHornDeviceDetail(HornDeviceDTO hornDeviceDTO);

    /**
     * 获取附件信息
     * @param hornDeviceDTO
     * @return
     */
    Result<List<String>> getFileUrlByDeviceCode(HornDeviceDTO hornDeviceDTO);

    /**
     * 查询行政区域喇叭设备树
     *
     * @param hornDeviceDTO 查询条件入参
     * @return List<DeviceTreeVO>
     */
    List<DeviceTreeVO> getHornMonitorTree(HornDeviceDTO hornDeviceDTO);

    /**
     * 查询行政组织喇叭设备树
     *
     * @param hornDeviceDTO 查询条件入参
     * @return List<DeviceTreeVO>
     */
    List<DeviceTreeVO> getHornMonitorOrgTree(HornDeviceDTO hornDeviceDTO);

    List<DeviceTreeVO> formatOrgTreeList(List<DeviceTreeVO> monitorList);

    /**
     * 修改喇叭设备信息
     * @param hornDeviceDTO 入参
     */
    Result<String> updateHornDevice(HornDeviceDTO hornDeviceDTO);

    /**
     * 开始喊话
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<LockResultVo> startTalk(TalkReq talkReq);

    /**
     * 停止喊话
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<String> stopTalk(TalkReq talkReq);

    /**
     * 开始喊话
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<TalkResultVO<TalkV2VO>> startTalkV2(TalkV2DTO talkReq);

    /**
     * 停止喊话
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<String> stopTalkV2(TalkV2Req talkReq);

    /**
     * 喊话心跳
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<String> heartbeatV2(TalkV2Req talkReq);

    /**
     * 开始喊话
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<TalkV3VO> startTalkV3(TalkV2DTO talkReq);

    /**
     * 停止喊话
     * @param request 入参
     * @return Result<LockResultVo>
     */
    Result<List<TalkV2VO>> stopTalkV3(List<TalkV2Req> request);

    /**
     * 喊话心跳
     * @param request 入参
     * @return Result<LockResultVo>
     */
    Result<List<TalkV2VO>> heartbeatV3(List<TalkV2Req> request);

    /**
     * 开始喊话
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<TalkResultVO<TalkV2VO>> startTalkAppV2(TalkV2DTO talkReq);

    /**
     * 停止喊话
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    Result<String> stopTalkAppV2(TalkV2Req talkReq);

    SysUser selectUserOnlyByUserName(String userName);


    /**
     * 根据区域编码查询喇叭列表
     * @param hornDeviceDTO
     * @return
     */
    List<HornDeviceVo> queryHornForArea(HornDeviceDTO hornDeviceDTO);

    List<HornDeviceVo> querySiteDevTree(HornDeviceDTO hornDeviceDTO);

    /**
     * 根据站址编码查询喇叭列表
     * @param hornDeviceDTO
     * @return
     */
    List<HornDeviceVo> queryHornForSite(HornDeviceDTO hornDeviceDTO);


    /**
     * 查询喇叭设备距离信息
     * @param param 参数
     * @return 喇叭设备距离信息
     */
    List<DeviceDistanceVO> queryHornDeviceDistanceInfo(DeviceDistanceDTO param);

    /**
     *
     * 查询喇叭设备距离信息
     * @param param 参数
     * @return 喇叭设备距离信息
     */
    @Deprecated
    List<DeviceDistanceVO> queryHornDeviceDistanceInfo4App(DeviceDistanceDTO param);

    /**
     *
     * @param hornDeviceDTO 入参-设备编码
     * @return HornDeviceVo 喇叭详情
     */
    @Deprecated
    Result<HornDeviceVo> queryHornInfoByCode(HornDeviceDTO hornDeviceDTO);

    Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq);

    /**
     * 查询喇叭基本信息
     * @param hornDeviceDTO
     * @return
     */
    List<HornDeviceVo> queryHornDeviceListByCondition(HornDeviceDTO hornDeviceDTO);
}

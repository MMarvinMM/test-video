package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.HornDeviceDo;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.dto.HornLockInfo;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.TalkReq;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HornDeviceMapper extends BaseMapper<HornDeviceDo> {

    List<HornDeviceVo> queryHornDeviceList(@Param("param") HornDeviceDTO hornDeviceDTO);

    List<HornDeviceVo> queryHornList(@Param("param") HornDeviceDTO hornDeviceDTO);

    List<DeviceTreeVO> getHornMonitorTree(@Param("param") HornDeviceDTO hornDeviceDTO);

    List<DeviceTreeVO> getHornMonitorOrgTree(@Param("param") HornDeviceDTO hornDeviceDTO);

    int queryHornAliasName(@Param("param") HornDeviceDTO hornDeviceDTO);

    void updateHornAliasName(@Param("param") HornDeviceDTO hornDeviceDTO);

    void addHornAliasName(@Param("param") HornDeviceDTO hornDeviceDTO);

    void deleteHornOrgList(@Param("param") OrganDeviceRelVO organDeviceRelVO);

    void addHornOrgRelList(@Param("list") List<OrganDeviceRelVO> orgRelList);

    void addHornDeviceLockInfo(@Param("param") TalkReq talkReq);

    void deleteLockInfo(@Param("list") List<TalkReq> talkReq);

    List<HornLockInfo> queryLockInfoList(String time);

    SysUser selectUserOnlyByUserName(String userName);

    List<HornDeviceVo> queryHornForArea(@Param("param") HornDeviceDTO hornDeviceDTO);

    List<HornDeviceVo> queryHornForSite(@Param("param") HornDeviceDTO hornDeviceDTO);

    /**
     * 查询设备的收藏状态
     *
     * @param deviceCode
     * @param userId
     * @return
     */
    String queryDeviceCollStatus(@Param("deviceCode") String deviceCode, @Param("userId") String userId);

    HornDeviceVo queryHornDeviceDetail(@Param("param") HornDeviceDTO hornDeviceDTO);

    /**
     * 查询喇叭设备距离信息
     *
     * @param param 参数
     * @return 喇叭设备距离信息
     */
    List<DeviceDistanceVO> queryHornDeviceDistanceInfo(@Param("param") DeviceDistanceDTO param);

    List<HornDeviceVo> querySiteDevTree(@Param("param") HornDeviceDTO hornDeviceDTO);

    /**
     * 根据关键字查询喇叭列表
     * @param deviceComprehensiveReq
     * @return
     */
    List<DeviceComprehensiveVO> queryDeviceListByKeyWord(@Param("param") DeviceComprehensiveReq deviceComprehensiveReq);

    List<DeviceTreeVO> queryAccessNodeInfoByDeviceCodeList(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * 根据条件查询喇叭基本信息
     * @param hornDeviceDTO
     * @return
     */
    List<HornDeviceVo> queryHornDeviceListByCondition(@Param("param") HornDeviceDTO hornDeviceDTO);
}

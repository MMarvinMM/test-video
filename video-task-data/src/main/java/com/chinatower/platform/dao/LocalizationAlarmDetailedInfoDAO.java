package com.chinatower.platform.dao;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.bdm.api.entity.GridListVO;
import com.chinatower.event.entity.vo.UavNestDeviceVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.platform.entity.devicemgmt.vo.TowerDeviceLabelVO;
import com.chinatower.platform.modules.PlatformDataConfigDto;
import com.chinatower.system.api.resp.SysDictDataResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface LocalizationAlarmDetailedInfoDAO {

    /**
     * 根据字典类型查询字典数据
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictDataResponse> getDictDataByType(String dictType);

    /**
     * 查询所有的告警类型
     * @return
     */
    List<Map> findAlarmTypeAll();

    /**
     * 查询网格信息
     * @return
     */
    List<Map<String, Object>> getGridList();

    /**
     * 查询网格下的网格长
     * @param list
     * @return
     */
    List<GridListVO> getKeeperLeader(@Param("list")List<String> list);

    /**
     * 查询摄像机信息
     * @return
     */
    List<DeviceInfoEntity> getDevice();

    /**
     * 查询物联设备信息
     * @return
     */
    List<DeviceInfoEntity> getIotDevice();

    /**
     * 查询物联设备关联摄像机信息
     * @return
     */
    List<DeviceInfoEntity> getIotDevConf();

    /**
     * 查询无人机设备信息
     * @return
     */
    List<DeviceInfoEntity> getUavDeviceInfo();

    List<UavNestDeviceVo> getUavNestDevcieInfo();

    /**
     * 查询设备收藏表
     * @return
     */
    List<String> getDeviceCodeCollect();

    /**
     * 查询雷达设备信息
     * @return
     */
    List<DeviceInfoEntity> getRadarDevice();

    /**
     * 根据行业应用编码查询应用名称
     * @return
     */
    List<SysIndustryConf> getIndustryName(String localNode);

    /**
     * 根据用户ID查询用户信息
     * @return
     */
    List<SysUser> getUserInfoById(List<String> userIdList);

    /**
     * 根据用户名查询用户信息
     * @param userNameList
     * @return
     */
    List<SysUser> getUserInfoByName(@Param("userNameList") List<String> userNameList);

    /**
     * 查询预置位名称
     * @return
     */
    List<Map> getPresetName();

    /**
     * 查询图层信息
     */
    List<Map> getLayerInfo();

    /**
     * 查询电子围栏信息
     * @return
     */
    List<Map> getPolygonPatch();

    /**
     * 获取所有节点配置信息
     * @param platformCodeList
     * @return
     */
    List<PlatformDataConfigDto> getAllSwitchData(@Param("platformCodeList") List<String> platformCodeList);

    /**
     * 根据节点编码获取节点名称
     * @param platformCodeList
     * @return
     */
    List<String> getPlatformCodeList(@Param("platformCodeList") List<String> platformCodeList);

    /**
     * 获取节点信息
     * @return
     */
    List<Map> getPlatformDataList();

    /**
     * 根据属地编码批量查询属地名称
     *
     * @param platformCodeList 属地编码列表
     * @return 属地配置信息
     */
    List<PlatformDataConfigDto> getPlatformInfoByCodes(@Param("platformCodeList") List<String> platformCodeList);

    /**
     * 查询算法名称
     * @return
     */
    List<Map> getAlgorithmNameAll();

    /**
     * 查询摄像机标签
     * @return
     */
    List<TowerDeviceLabelVO> getDeviceLabelAll();
}

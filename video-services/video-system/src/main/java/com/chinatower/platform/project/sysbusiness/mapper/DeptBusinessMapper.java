package com.chinatower.platform.project.sysbusiness.mapper;

import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.project.custom.entity.*;
import com.chinatower.platform.project.sysbusiness.entity.ScreenConfigVO;
import com.chinatower.platform.project.system.domain.VmAlgorithmInfoT;
import com.chinatower.system.api.model.AreaDataVO;
import com.chinatower.platform.project.sysbusiness.entity.LayerUrlVo;
import com.chinatower.platform.project.sysbusiness.entity.SysAreaBusinessVO;
import com.chinatower.platform.project.system.domain.SysDeptStreetRel;
import com.chinatower.system.api.model.SysArea;
import org.apache.ibatis.annotations.Param;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeptBusinessMapper {



    int addOrganCamera(OrganDeviceVO organDeviceVO);


    int addOrganAlarmType(OrganDeviceVO organDeviceVO);

    int addOrganDevice(OrganDeviceVO organDeviceVO);

    int addOrganSatellite(OrganDeviceVO organDeviceVO);

    int delOrganDevice(OrganDeviceVO organDeviceVO);

    int delOrganAlarmType(OrganDeviceVO organDeviceVO);

    int delOrganSatellite(OrganDeviceVO organDeviceVO);

    List<SysAreaBusinessVO> getDeviceList(OrganDeviceVO organDeviceVO);

    List<SysAreaBusinessVO> getSensorList(OrganDeviceVO organDeviceVO);

    List<SysAreaBusinessVO> getAreaList(OrganDeviceVO organDeviceVO);

    List<SysAreaBusinessVO> selectSysArea();


    List<String> getDeptDeviceRefList(OrganDeviceVO organDeviceVO);

    List<String> getDeptSatelliteRefList(OrganDeviceVO organDeviceVO);


    int getProcessByUserCount(String[] userIds,String tableName);


    List<LayerUrlVo> getLayerUrl(String industryCode);


    List<SysDeptStreetRel> queryAreaByDevice(OrganDeviceVO organDeviceVO);

    List<SysDeptStreetRel> queryAreaByRadar(OrganDeviceVO organDeviceVO);

    List<SysDeptStreetRel> queryAreaBySensor(OrganDeviceVO organDeviceVO);

    List<SysDeptStreetRel> queryAreaBySatellite(OrganDeviceVO organDeviceVO);

    List<SysDeptStreetRel> queryAreaByUav(OrganDeviceVO organDeviceVO);//add by liuxt 20230612 组织管理优化需求 查询无人机的管理区域

    List<SysDeptStreetRel> queryAreaByUavNest(OrganDeviceVO organDeviceVO);
    List<SysDeptStreetRel> queryAreaByHorn(OrganDeviceVO organDeviceVO);//add by liuxt 20230612 组织管理优化需求 查询大喇叭的管理区域

    List<SysDeptStreetRel> queryAreaByLER(OrganDeviceVO organDeviceVO);

    List<OrganAreaVO> queryStreetArea(OrganDeviceVO organDeviceVO);

    List<String> getDevicesByDeptList(Set<String> deptList);


    List<AreaDataVO> getAreaByDeptList(Set<String> deptList);


    Set<String> getAllDeptList(String tenantId);

    List<String> getAllDeviceList(String tenantId);

    List<AreaDataVO> getAllAreaList(String tenantId);

    List<SysArea> getSysAreaAll();

    /**
     * 按照部门id查询行政区划
     * @param deptList
     * @return
     */
    List<SysArea> getSysAreaByDeptList(Set<String> deptList);

    /**
     * 按照租户id查询行政区划
     * @param areaList
     * @return
     */
    List<SysArea> getSysAreaListAreaCode(Set<String> areaList);


    List<SysAreaBusinessVO> getSatelliteList(OrganDeviceVO organDeviceVO);

    SatelliteInfoVO getOrgInfo(String xStr, String tenantId);


    List<ScreenDataVO> getScreenData(SysDept dept);

    void updateScreenData(List<ScreenDataVO> needUpdateList);

    void deleteScreenDataConfig(List<ScreenDataVO> needDeleteList);
    void delOrganLayer(OrganDeviceVO organDeviceVO);

    void addOrganLayer(OrganDeviceVO organDeviceVO);

    /**
     * 根据算法编码列表查询算法信息
     */
    List<VmAlgorithmInfoT> selectByAlgorithmCodes(@Param("algorithmCodes") List<String> algorithmCodes);

    /**
     * 查询配置了该告警类型编码的组织有哪些
     */
    List<OrganAlarmTypeVO> getOrganAlarmTypeList(@Param("typeValueList") Set<String> typeValueList);

    int deleteExistingAlarmTypes(@Param("list") List<OrganAlarmTypeVO> alarmTypeList);

    /**
     * 查询需要处理的大屏配置数据（特定父告警类型）
     * @param alarmTypeCodes 父告警类型编码集合
     * @return 大屏配置列表
     */
    List<ScreenConfigVO> selectScreenConfigForProcess(@Param("alarmTypeCodes") Set<String> alarmTypeCodes);

    /**
     * 批量插入大屏配置数据
     * @param configList 配置列表
     * @return 插入数量
     */
    int insertScreenConfigBatch(List<ScreenConfigVO> configList);

    /**
     * 查询需要更新的大屏配置数量记录
     * @param screenDataConfigIds 配置ID列表
     * @return 配置数量记录列表
     */
    List<ScreenConfigVO> selectScreenConfigCountForUpdate(@Param("screenDataConfigIds") List<Long> screenDataConfigIds);

    /**
     * 根据配置ID统计告警类型数量
     * @param screenDataConfigIds 配置ID列表
     * @return 统计结果列表
     */
    List<Map<String, Object>> countAlarmTypeByConfigIds(@Param("screenDataConfigIds") List<Long> screenDataConfigIds);

    /**
     * 批量更新大屏配置数量
     * @param updateList 更新列表
     * @return 更新数量
     */
    int updateScreenConfigCountBatch(@Param("updateList") List<ScreenConfigVO> updateList);

    /**
     * 删除已存在的大屏配置数据
     * @param configList 配置列表
     * @return 删除数量
     */
    int deleteExistingScreenConfig(@Param("configList") List<ScreenConfigVO> configList);

    List<String> getLargeModelAlgorithms();
}

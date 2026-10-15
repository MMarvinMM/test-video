package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.entity.admin.SysTiledArea;
import com.chinatower.platform.project.custom.entity.SatelliteInfoVO;
import com.chinatower.system.api.model.SysArea;
import com.chinatower.system.api.model.SysCapitalVO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.system.api.resp.SysAreaResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 行政区域Mapper接口
 *
 * @author wangzic
 * @date 2019-12-17
 */
@Mapper
public interface SysAreaMapper
{
    /**
     * 查询行政区域
     *
     * @param areaCode 行政区域ID
     * @return 行政区域
     */
    public SysArea selectSysAreaById(String areaCode);

    public List<SysArea> selectSysAreaCodes(@Param("codes") String[] codes);

    /**
     * 查询行政区域列表
     *
     * @param sysArea 行政区域
     * @return 行政区域集合
     */
    public List<SysArea> selectSysAreaList(SysArea sysArea);

    public List<SysArea> selectSysAreaByIds(List<String> areaCodes);


    /**
     * 新增行政区域
     *
     * @param sysArea 行政区域
     * @return 结果
     */
    public int insertSysArea(SysArea sysArea);

    /**
     * 修改行政区域
     *
     * @param sysArea 行政区域
     * @return 结果
     */
    public int updateSysArea(SysArea sysArea);

    /**
     * 删除行政区域
     *
     * @param areaCode 行政区域ID
     * @return 结果
     */
    public int deleteSysAreaById(String areaCode);

    /**
     * 批量删除行政区域
     *
     * @param areaCodes 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysAreaByIds(String[] areaCodes);

    List<String> selectAreaNamesByIds(List<String> list);

    String queryAreaNameById(String areaCode);

    List<SysTiledArea> querySysTiledArea(Map<String,Object> params);

    /**
     * 行政区划
     * @param sysArea 行政区划信息
     * @return  List<SysArea> 查询出的行政区划信息
     */
    List<SysArea> getArea(SysArea sysArea);

    SysCapitalVO getSysCapital(SysCapitalVO vo);

    List<SysArea> getAllArea(SysArea sysArea);

    SysArea getCenterLonLat(String code);

    /**
     * 根据租户查询管理区域列表
     * @param tenantId
     * @return
     */
    List<SysDeptDeviceVO> getManagementAreaByTenantId(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);
    /**
     * @Description: 根据区划名称查询信息
     * @param areaNames 入参
     * @Date: 2022/12/14 11:34
     */
    List<SysArea> selectByAreaName(@Param("list") List<String> list);
    /**
     * @Description: 查询省市区3级区域信息
     * @return java.util.List<com.chinatower.platform.project.system.domain.SysArea>
     * @Date: 2022/12/19 17:07
     */
    List<SysArea> selectAreaByLevel();

    List<SysArea> getAllAreaInfo();

    Long selectMaxId();

    List<SysArea> getAreaTreeList();

    List<SysArea> selectSysAreas(List<SatelliteInfoVO> list);
}

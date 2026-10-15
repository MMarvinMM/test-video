package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.entity.admin.SysTiledArea;
import com.chinatower.platform.project.custom.entity.SatelliteInfoVO;
import com.chinatower.platform.project.system.domain.vo.SysDeptDeviceTreeVO;
import com.chinatower.system.api.model.StreetTreeVO;
import com.chinatower.system.api.model.SysArea;
import com.chinatower.system.api.model.SysCapitalVO;
import com.chinatower.system.api.resp.SysAreaResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 行政区域Service接口
 *
 * @author wangzic
 * @date 2019-12-17
 */
public interface ISysAreaService {
    /**
     * 查询行政区域
     *
     * @param areaCode 行政区域ID
     * @return 行政区域
     */
    SysArea selectSysAreaById(String areaCode);

    Map<String,SysArea> getInfos(@RequestParam String [] codes);

    /**
     * 查询行政区域列表
     *
     * @param sysArea 行政区域
     * @return 行政区域集合
     */
    List<SysArea> selectSysAreaList(SysArea sysArea);

    /**
     * 新增行政区域
     *
     * @param sysArea 行政区域
     * @return 结果
     */
    int insertSysArea(SysArea sysArea);

    /**
     * 修改行政区域
     *
     * @param sysArea 行政区域
     * @return 结果
     */
    int updateSysArea(SysArea sysArea);

    /**
     * 批量删除行政区域
     *
     * @param areaCodes 需要删除的行政区域ID
     * @return 结果
     */
    int deleteSysAreaByIds(String[] areaCodes);

    /**
     * 删除行政区域信息
     *
     * @param areaCode 行政区域ID
     * @return 结果
     */
    int deleteSysAreaById(String areaCode);

    /**
     * 查询全部行政区划信息
     * @return 行政区划信息
     */
    List<SysArea> selectAll();

    List<SysArea> selectList(List<String> areaCodes);

    /**
     * 查询行政区划树结构信息
     * @return 行政区划树结构信息
     */
    List<TreeSelect> selectTreeSelect();

    /**
     * 查询行政区划树结构信息
     * @return 行政区划树结构信息
     */
    List<TreeSelect> selectTreeSelectNew();

    /**
     * 根据区域编码获取组织部门编码
     * @param id
     * @return
     */
    String selectDeptById(String id);

    /**
     * 根据区域编码获取组织部门编码
     * @return
     */
    String selectDeptById(String provinceId,String cityId);


    List<String> selectAreaNamesByIds(List<String> list);

    Map<String,String> selectAllAreaWithDeptRel();


    Map<String,String> selectAreaWithNameRel(List<String> areaCodes);

    Map<String,SysArea> selectAreaMap(List<String> areaCodes);

    List<SysTiledArea> querySysTiledArea(Map<String,Object> params);

    List<SysArea> getArea(SysArea sysArea);

    List<SysArea> getAllArea(SysArea sysArea);

    List<StreetTreeVO> getAllStreetList(StreetTreeVO vo);

    SysCapitalVO getSysCapital(SysCapitalVO vo);

    SysArea getCenterLonLat(String code);

    List<SysDeptDeviceTreeVO> getManagementAreaTreeByTenantId(String tenantId);
    /**
     * @Description: 根据区划名称查询信息
     * @param areaNames 入参
     * @return com.chinatower.common.entity.Result<java.util.List<SysArea>>
     * @Date: 2022/12/14 11:34
     */
    List<SysArea> selectByAreaName(List<String> areaNames);
    /**
     * @Description: 获取部门下拉树列表（只取省市区3级）
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.common.entity.TreeSelect>>
     * @Author: TangJinBo
     * @Date: 2022/12/19 16:43
     */
    List<TreeSelect> treeArea();

    void removeCaffeineCache();

    List<SysArea> getAreaTreeList();

    /**
     * 查询省市区街道的树数据
     * @return
     */
    List<TreeSelect> getAreaTreeWithTownList(boolean otherFlag);

    List<SysArea> selectSysAreas(List<SatelliteInfoVO> list);
}

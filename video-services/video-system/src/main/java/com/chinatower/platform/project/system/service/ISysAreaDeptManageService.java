package com.chinatower.platform.project.system.service;

import com.chinatower.platform.project.system.domain.SysAreaDeptManage;
import com.chinatower.system.api.resp.SysAreaDeptManageResp;

import java.util.List;

/**
 * (SysAreaDeptManage)表服务接口
 *
 * @author zs
 * @since 2021-03-14 12:03:36
 */
public interface ISysAreaDeptManageService {


    /**
     * 根据组织市id 取县级id
     * @param deptCityId
     * @return
     */
    List<SysAreaDeptManage> selectManageByDeptCityId(String deptCityId);

    List<SysAreaDeptManage> selectCountyByManageId(String manageId);

    /**
     * 根据省份行政区域编码查询管理区域编码
     * @param provinceAreaCode
     * @author zheng.li
     * @return List<SysAreaDeptManage>
     */
    List<SysAreaDeptManageResp> selectProvinceDeptByProvinceAreaCode(String provinceAreaCode);

    /**
     * 根据地市行政区域编码查询管理区域编码
     * @param cityAreaCode
     * @author zheng.li
     * @return List<SysAreaDeptManage>
     */
    List<SysAreaDeptManageResp> selectCityDeptByCityAreaCode(String cityAreaCode);

    /**
     * 根据区县行政区域编码查询管理区域编码
     * @param countyAreaCode
     * @author zheng.li
     * @return List<SysAreaDeptManage>
     */
    List<SysAreaDeptManageResp> selectCountyDeptByCountyAreaCode(String countyAreaCode);

    /**
     * 根据行政区域编码获取对应部门编码
     * @param areaCode 区域代码
     * @param level 级别(省,市,区)
     * @author zheng.li
     * @return String
     */
    String getDeptCodeByAreaCode(String areaCode, String level);
}

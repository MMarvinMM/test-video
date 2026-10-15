package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysAreaDeptManage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (SysAreaDeptManage)表数据库访问层
 *
 * @author zs
 * @since 2021-03-14 12:03:34
 */
public interface SysAreaDeptManageMapper {


    @Select("SELECT DISTINCT manage_code, manage_name FROM sys_area_dept_manage WHERE CITY_DEPT_CODE =#{ deptCityId }")
    List<SysAreaDeptManage> selectManageByDeptCityId(@Param("deptCityId") String deptCityId);

    @Select("select distinct COUNTY_AREA_CODE,COUNTY_AREA_NAME from sys_area_dept_manage where manage_code=#{manageId}")
    List<SysAreaDeptManage> selectCountyByManageId(@Param("manageId") String manageId);

    @Select("select distinct CITY_DEPT_CODE,PROVINCE_DEPT_CODE,COUNTY_AREA_CODE from sys_area_dept_manage where manage_code=#{manageCode}")
    List<SysAreaDeptManage> selectOtherByCountyId(@Param("manageCode") String manageCode);

    /**
     * 根据省份行政区域编码查询省部门编码
     * @param provinceAreaCode
     * @author zheng.li
     * @return List<SysAreaDeptManage>
     */
    @Select("select distinct province_dept_code from sys_area_dept_manage where province_area_code=#{provinceAreaCode}")
    List<SysAreaDeptManage> selectProvinceDeptByProvinceAreaCode(@Param("provinceAreaCode") String provinceAreaCode);

    /**
     * 根据地市行政区域编码查询市部门编码
     * @param cityAreaCode
     * @author zheng.li
     * @return List<SysAreaDeptManage>
     */
    @Select("select distinct city_dept_code from sys_area_dept_manage where city_area_code=#{cityAreaCode}")
    List<SysAreaDeptManage> selectCityDeptByCityAreaCode(@Param("cityAreaCode") String cityAreaCode);

    /**
     * 根据区县行政区域编码查询管理区域编码
     * @param countyAreaCode
     * @author zheng.li
     * @return List<SysAreaDeptManage>
     */
    @Select("select distinct manage_code from sys_area_dept_manage where county_area_code=#{countyAreaCode}")
    List<SysAreaDeptManage> selectCountryDeptByCountyAreaCode(@Param("countyAreaCode") String countyAreaCode);

    /**
     * 根据区县行政区域编码查询市部门编码
     * @param countyAreaCode
     * @author zheng.li
     * @return List<SysAreaDeptManage>
     */
    @Select("select distinct city_dept_code from sys_area_dept_manage where county_area_code=#{countyAreaCode}")
    List<SysAreaDeptManage> selectCityDeptByCountyAreaCode(@Param("countyAreaCode") String countyAreaCode);
}


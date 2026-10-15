package com.chinatower.gis.modules.gis.mapper;

import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.system.api.model.SysCapitalVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: wjd
 * @Description:
 * @Date: Created in 15:55 2022/3/14
 * @Modified by:
 */
public interface GisMapper {
    Map<String,Object> getLatitude(@Param("params") GisKeyWordQueryReq gisKeyWordQueryReq);

    /**
     *
     * @return 查询行政区划
     */
    SysArea selectByAreaCode(@Param("areaCode")String areaCode);

    /**
     * 批量查询查询行政区划
     */
    List<SysArea> selectByAreaCodes(@Param("list")List<String> list);

    /**
     * 根据名称查询
     */
    List<SysArea> selectByAreaNames(@Param("list")List<String> list);
    /**
     * 查询行政区/县以上数据
     */
    List<SysArea> selectByDistrictAndCounty();

    /**
     * 查询下级行政区域
     */
    List<SysArea> selectBySysParentCode(@Param("sysAreaParentCode") String sysAreaParentCode);
    /**
     *
     * @return 查询省会
     */
    SysCapitalVO selectByProvCode(@Param("provCode")String provCode);

    /**
     * 全部省会城市
     */
    List<SysCapitalVO> selectByProvList();

}

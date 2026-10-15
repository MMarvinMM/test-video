package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.project.system.domain.SysAreaDeptManage;
import com.chinatower.platform.project.system.mapper.SysAreaDeptManageMapper;
import com.chinatower.platform.project.system.mapper.SysDeptMapper;
import com.chinatower.platform.project.system.service.ISysAreaDeptManageService;
import com.chinatower.system.api.resp.SysAreaDeptManageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * (SysAreaDeptManage)表服务实现类
 *
 * @author zs
 * @since 2021-03-14 12:03:37
 */
@Service("sysAreaDeptManageService")
@RequiredArgsConstructor
public class SysAreaDeptManageServiceImpl implements ISysAreaDeptManageService {

    private final SysAreaDeptManageMapper sysAreaDeptManageMapper;

    private static final String BEIJING_PREFIX = "110";
    private static final String TIANJIN_PREFIX = "120";
    private static final String SHANGHAI_PREFIX = "310";
    private static final String CHONGQING_PREFIX = "500";

    @Override
    public List<SysAreaDeptManage> selectManageByDeptCityId(String deptCityId) {
        /**
         * 一体数据
         */
//        SysDept sysDept=sysDeptMapper.selectDeptById(deptCityId);
//        deptCityId= sysDept.getBussnessUnit()+deptCityId;
        return sysAreaDeptManageMapper.selectManageByDeptCityId(deptCityId);
    }

    @Override
    public List<SysAreaDeptManage> selectCountyByManageId(String manageId) {
        return sysAreaDeptManageMapper.selectCountyByManageId(manageId);
    }

    @Override
    public List<SysAreaDeptManageResp> selectProvinceDeptByProvinceAreaCode(String provinceAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectProvinceDeptByProvinceAreaCode(provinceAreaCode);
        List<SysAreaDeptManageResp> sysAreaDeptManageRespList = BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
        return sysAreaDeptManageRespList;
    }

    @Override
    public List<SysAreaDeptManageResp> selectCityDeptByCityAreaCode(String cityAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectCityDeptByCityAreaCode(cityAreaCode);
        List<SysAreaDeptManageResp> sysAreaDeptManageRespList = BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
        return sysAreaDeptManageRespList;
    }

    @Override
    public List<SysAreaDeptManageResp> selectCountyDeptByCountyAreaCode(String countyAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectCountryDeptByCountyAreaCode(countyAreaCode);
        List<SysAreaDeptManageResp> sysAreaDeptManageRespList = BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
        return sysAreaDeptManageRespList;
    }

    @Override
    public String getDeptCodeByAreaCode(String areaCode, String level) {
        boolean isDirectCity = isDirectCity(areaCode);
        String result = null;
        //省分级别
        if (VideoSystemEnums.AreaLevel.PROVINCE.getIndex().equals(level)){
            return getProvinceDeptCode(areaCode);
        }
        //地市级别
        if (VideoSystemEnums.AreaLevel.CITY.getIndex().equals(level)){
            List<SysAreaDeptManageResp> sysCityDeptManageRespList = null;
            if (isDirectCity){
                //直辖市处理逻辑,直辖市行政区域编码对应关联表中的省行政区域编码
                String code = VideoSystemEnums.DirectCityArea2Dept.getName(areaCode);
                return getProvinceDeptCode(code);
            }else {
                //非直辖市处理逻辑
                sysCityDeptManageRespList = selectCityDeptByCityAreaCode(areaCode);
                if (CollUtil.isNotEmpty(sysCityDeptManageRespList) && sysCityDeptManageRespList.size() > 0){
                    SysAreaDeptManageResp sysAreaDeptManageResp = sysCityDeptManageRespList.get(0);
                    result = sysAreaDeptManageResp.getCityDeptCode();
                }
            }
            return result;
        }
        //区县级别
        if (VideoSystemEnums.AreaLevel.COUNTY.getIndex().equals(level)){
            if (isDirectCity){
                //直辖市处理逻辑,直辖市区县编码对应地市部门编码
                List<SysAreaDeptManageResp> sysCityDeptManageRespList = this.selectCityDeptByCountyAreaCode(areaCode);
                if (CollUtil.isNotEmpty(sysCityDeptManageRespList)){
                    SysAreaDeptManageResp sysAreaDeptManageResp = sysCityDeptManageRespList.get(0);
                    return sysAreaDeptManageResp.getCityDeptCode();
                }
            }else {
                //非直辖市处理逻辑,根据区县编码获取对应区县部门管理区域编码
                List<SysAreaDeptManageResp> sysCountyDeptManageRespList = selectCountyDeptByCountyAreaCode(areaCode);
                if (CollUtil.isNotEmpty(sysCountyDeptManageRespList)){
                    SysAreaDeptManageResp sysAreaDeptManageResp = sysCountyDeptManageRespList.get(0);
                    return sysAreaDeptManageResp.getManageCode();
                }
            }
        }
        return null;
    }

    /**
     * 根据省编码查询省部门编码
     * @param areaCode
     * @return
     */
    private String getProvinceDeptCode(String areaCode) {
        List<SysAreaDeptManageResp> sysAreaDeptManageRespList = selectProvinceDeptByProvinceAreaCode(areaCode);
        if (CollUtil.isNotEmpty(sysAreaDeptManageRespList) && sysAreaDeptManageRespList.size() > 0){
            SysAreaDeptManageResp sysAreaDeptManageResp = sysAreaDeptManageRespList.get(0);
            return sysAreaDeptManageResp.getProvinceDeptCode();
        }
        return null;
    }

    /**
     * 根据区县编码查询地市部门编码
     * @param countyAreaCode
     * @return
     */
    public List<SysAreaDeptManageResp> selectCityDeptByCountyAreaCode(String countyAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectCityDeptByCountyAreaCode(countyAreaCode);
        List<SysAreaDeptManageResp> sysAreaDeptManageRespList = BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
        return sysAreaDeptManageRespList;
    }

    private boolean isDirectCity(String code){
        if (StringUtils.isNotEmpty(code) && (code.startsWith(BEIJING_PREFIX) || code.startsWith(TIANJIN_PREFIX)
                || code.startsWith(SHANGHAI_PREFIX) || code.startsWith(CHONGQING_PREFIX))){
            return true;
        }
        return false;
    }

}

package com.chinatower.device.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.enums.RadarAlarmTypeEnum;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.device.constants.IndErrorMsgConstants;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.constants.RadarAlarmConstants;
import com.chinatower.device.dataobject.RadarAlarmRegionDO;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.RadarAlarmRegionDTO;
import com.chinatower.device.entity.vo.RadarAlarmRegionJson;
import com.chinatower.device.entity.vo.RadarAlarmRegionVo;
import com.chinatower.device.entity.vo.RadarAlarmTemplateVO;
import com.chinatower.device.entity.vo.UnionTemplateVo;
import com.chinatower.device.mapper.RadarAlarmRegionMapper;
import com.chinatower.device.repository.IRadarAlarmRegionService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 雷达告警区域接口实现类
 */
@Slf4j
@Service
public class RadarAlarmRegionServiceImpl extends ServiceImpl<RadarAlarmRegionMapper, RadarAlarmRegionDO> implements IRadarAlarmRegionService {

    @Autowired
    private RadarAlarmRegionMapper radarAlarmRegionMapper;

    @Override
    @DataScope(tenantColumn = "r.tenant_id", industryCode = "r.industry_code")
    public Result<PageInfo<RadarAlarmRegionVo>> queryRadarAlarmRegion(RadarAlarmRegionDTO dto) {
        try {
            List<RadarAlarmRegionVo> resultList;
            Result<PageInfo<RadarAlarmRegionVo>> result;
            if (Boolean.TRUE.equals(dto.getIsEnablePage())){
                if (null == dto.getPageNum() || dto.getPageNum() <= 0) {
                    dto.setPageNum(1);
                }
                if (null == dto.getPageSize() || dto.getPageSize() <= 0) {
                    dto.setPageSize(10);
                }

                PageMethod.startPage(dto.getPageNum(), dto.getPageSize());
            }
            //全部区域信息
            List<RadarAlarmRegionVo> regionList = radarAlarmRegionMapper.queryAlarmRegionList(dto);
            //当区域不为空时--查找其关联的模板
            if (CollectionUtils.isNotEmpty(regionList)){
                //全部区域ID
                List<Long> regionIdList = regionList.stream().map(RadarAlarmRegionVo::getRegionId).collect(Collectors.toList());
                //根据区域ID查出所有关联的模板
                List<UnionTemplateVo> templateList = radarAlarmRegionMapper.getUnionTemByRegionId(regionIdList);
                for (RadarAlarmRegionVo regionVo:regionList){
                    List<UnionTemplateVo> unionTemplateList = templateList.stream().filter(unionTemplateVo -> Objects.equals(unionTemplateVo.getRegionId(), regionVo.getRegionId())).collect(Collectors.toList());
                    regionVo.setUnionTemplateList(unionTemplateList);
                }
            }

            PageInfo<RadarAlarmRegionVo> pageInfo = new PageInfo<>(regionList);
            result = Result.success("查询成功");
            result.setData(pageInfo);
            return result;
        }catch (Exception e){
            log.info("告警区域列表查询异常：",e);
            return Result.fail("区域查询异常");
        }
    }

    @Override
    public Result<List<RadarAlarmTemplateVO>> queryAlarmTemplateByRegionId(RadarAlarmRegionDTO dto) {
        List<RadarAlarmTemplateVO> resultList = radarAlarmRegionMapper.queryAlarmTemplateByRegionId(dto);
        return Result.data(resultList);
    }

    @Override
    public Result<String> addAlarmRegion(RadarAlarmRegionSaveDTO param) {
        checkAndFillRadarAlarmRegionSaveDTOInfo(param);
        param.setRegionJsonStr(JSON.toJSONString(param.getRegionJson()));
        radarAlarmRegionMapper.addAlarmRegion(param);
        return Result.success("操作成功");
    }

    private void checkAndFillRadarAlarmRegionSaveDTOInfo(RadarAlarmRegionSaveDTO param) {
        if (param == null) {
            throw new CustomException("参数不能为空");
        }
        String regionName = param.getRegionName();
        if (StringUtils.isEmpty(regionName)) {
            throw new CustomException("告警区域名称不能为空");
        }

        if (param.getRegionName().length() > 15) {
            throw new CustomException("告警区域名称最多15个字");
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        SysUser sysUser = loginUser.getUser();
        log.info("fillRadarAlarmRegionSaveDTOInfo loginUser:{}", JSON.toJSONString(sysUser));
        Date now = new Date();
        String tenantId = sysUser.getTenantId();
        param.setTenantId(tenantId);
        String industryCode = sysUser.getIndustryCode();
        param.setIndustryCode(industryCode);
        param.setAppVerCode(sysUser.getAppVerCode());
        param.setDeleteFlag(IndustryConstants.ZERO_NUM);
        param.setCreator(sysUser.getUserName());
        param.setCreateTime(now);
        param.setModifier(sysUser.getUserName());
        param.setUpdateTime(now);

        if (radarAlarmRegionMapper.countAlarmRegionSameName(tenantId, industryCode, regionName, param.getId()) > 0) {
            throw new CustomException("同一客户同一行业应用下的区域名称不可重复");
        }
    }

    @Override
    public Result<String> updateAlarmRegion(RadarAlarmRegionSaveDTO param) {
        if (param == null || param.getId() == null) {
            throw new CustomException("告警区域id不能为空");
        }
        checkAndFillRadarAlarmRegionSaveDTOInfo(param);
        // 如果该告警区域关联了已开启的模板，则不允许修改
        if (radarAlarmRegionMapper.countEnabledTemplateInfoByRegionId(param.getId()) > 0) {
            throw new CustomException("该区域已关联开启的告警模板，不允许修改。");
        }
        param.setRegionJsonStr(JSON.toJSONString(param.getRegionJson()));
        radarAlarmRegionMapper.updateAlarmRegion(param);
        return Result.success("操作成功");
    }

    @Override
    public Result<String> deleteAlarmRegion(RadarAlarmRegionDeleteDTO param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        // 判断是否有绑定的模板，如果有则不能删除
        if (radarAlarmRegionMapper.countTemplateInfoByRegionId(param.getRegionId()) > 0) {
            throw new CustomException("该区域已关联告警模板，不允许删除。");
        }
        // 删除告警区域信息
        radarAlarmRegionMapper.deleteAlarmRegionById(param.getRegionId(), loginUser.getUser().getUserName());
        return Result.success("操作成功");
    }

    @Override
    public Result<RadarAlarmRegionSaveDTO> queryAlarmRegionByRegionId(Long regionId) {
        RadarAlarmRegionSaveDTO result = radarAlarmRegionMapper.queryAlarmRegionByRegionId(regionId);
        if (result != null && StringUtils.isNotEmpty(result.getRegionJsonStr())) {
            result.setRegionJson(JSON.parseObject(result.getRegionJsonStr(), RadarAlarmRegionJson.class));
        }
        return Result.data(result);
    }

    @Override
    public Result<List<RadarAlarmRegionDO>> queryRegionByAlarmType(RegionTemplateRelDTO req) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<RadarAlarmRegionDO> radarAlarmRegionList;
        QueryWrapper<RadarAlarmRegionDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RadarAlarmRegionDO::getTenantId,loginUser.getUser().getTenantId());
        queryWrapper.lambda().eq(RadarAlarmRegionDO::getIndustryCode,loginUser.getUser().getIndustryCode());
        queryWrapper.lambda().eq(RadarAlarmRegionDO::getDeleteFlag,RadarAlarmConstants.COMMON_NUM_0);
        //若当前告警模板的告警类型为“过线告警”，则该列表只显示“线性区域”的数据
        if (req.getAlarmTypeValue().equals(RadarAlarmTypeEnum.GX_ALARM.getAlarmTypeValue())){
            queryWrapper.lambda().eq(RadarAlarmRegionDO::getRegionType, RadarAlarmConstants.COMMON_STR_3);
        }else {
            queryWrapper.lambda().in(RadarAlarmRegionDO::getRegionType,RadarAlarmConstants.COMMON_STR_1,RadarAlarmConstants.COMMON_STR_2);
        }
        if (!StringUtils.isEmpty(req.getRegionName())){
            queryWrapper.lambda().like(RadarAlarmRegionDO::getRegionName,req.getRegionName());
        }
        radarAlarmRegionList = radarAlarmRegionMapper.selectList(queryWrapper);
        return Result.data(radarAlarmRegionList);
    }

    @Override
    public Result<Long> getAutoConfigNameIndex() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        return Result.data(radarAlarmRegionMapper.getAutoConfigNameIndex(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode()));
    }
}
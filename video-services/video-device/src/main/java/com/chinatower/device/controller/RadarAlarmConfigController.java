package com.chinatower.device.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.constants.IndErrorMsgConstants;
import com.chinatower.device.dataobject.RadarAlarmRegionDO;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.RadarAlarmRegionDTO;
import com.chinatower.device.dto.RadarAlarmRegionDeleteDTO;
import com.chinatower.device.dto.RadarAlarmRegionSaveDTO;
import com.chinatower.device.entity.dto.RadarAlarmTemplateDTO;
import com.chinatower.device.dto.RadarAlarmTemplateReq;
import com.chinatower.device.entity.vo.RadarAlarmRegionVo;
import com.chinatower.device.entity.vo.RadarAlarmTemplateVO;
import com.chinatower.device.entity.vo.RadarRegionSelectedVO;
import com.chinatower.device.repository.IRadarAlarmRegionService;
import com.chinatower.device.repository.IRadarAlarmTemplateService;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * video-device
 * 客户视角-雷达告警配置controller
 *
 * @author lishihao
 */
@RestController
@RequestMapping("/radarAlarmConfig")
@Slf4j
public class RadarAlarmConfigController {


    @Autowired
    private IRadarAlarmTemplateService radarAlarmTemplateService;

    @Autowired
    private IRadarAlarmRegionService radarAlarmRegionService;

    /**
     * 分页查询雷达告警模板信息
     *
     * @param dto 模板列表查询实体类
     * @return Result<PageInfo<RadarAlarmTemplateVO>>
     * @author lishihao
     */
    @PostMapping("/queryRadarAlarmTemplate")
    public Result<PageInfo<RadarAlarmTemplateVO>> queryRadarAlarmTemplate(@RequestBody RadarAlarmTemplateDTO dto) {
        return radarAlarmTemplateService.queryRadarAlarmTemplate(dto);
    }

    /**
     * 分页查询雷达告警区域信息
     *
     * @param dto 查询实体类
     * @return Result<PageInfo<RadarAlarmRegionVo>>
     * @author lishihao
     */
    @PostMapping("/queryRadarAlarmRegion")
    public Result<PageInfo<RadarAlarmRegionVo>> queryRadarAlarmRegion(@RequestBody RadarAlarmRegionDTO dto) {
        return radarAlarmRegionService.queryRadarAlarmRegion(dto);
    }

    /**
     * 根据区域ID查询关联模板
     *
     * @param dto 查询实体类
     * @return Result<List<RadarAlarmTemplateVO>>
     */
    @PostMapping("/queryAlarmTemplateByRegionId")
    public Result<List<RadarAlarmTemplateVO>> queryAlarmTemplateByRegionId(@RequestBody RadarAlarmRegionDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        dto.setTenantId(loginUser.getUser().getTenantId());
        dto.setIndustryCode(loginUser.getUser().getIndustryCode());

        return radarAlarmRegionService.queryAlarmTemplateByRegionId(dto);
    }

    /**
     * 根据模板ID修改告警模板状态
     *
     * @param dto 查询实体类
     * @return Result<String>
     */
    @PostMapping("/updateTemplateStatusById")
    public Result<String> updateTemplateStatusById(@RequestBody RadarAlarmTemplateVO dto) {

        log.info("修改模板状态接口入参{}", JSON.toJSONString(dto));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        dto.setModifier(loginUser.getUsername());
        dto.setUpdateTime(new Date());
        return radarAlarmTemplateService.updateTemplateStatusById(dto);
    }

    /**
     * 根据ID查询模板所有信息
     * @param dto 查询实体类
     * @return Result<RadarAlarmTemplateVO>
     */
    @PostMapping("/getTemplateInfoById")
    public Result<RadarAlarmTemplateVO> getTemplateInfoById(@RequestBody RadarAlarmTemplateDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        if (dto.getTemplateId() == null) {
            return Result.fail("参数不能为空");
        }
        return radarAlarmTemplateService.getTemplateInfoById(dto);
    }

    /**
     * 根据ID删除雷达告警模板信息
     * @param dto 查询实体类
     * @return Result<String>
     */
    @PostMapping("/deleteTemplateById")
    public Result<String> deleteTemplateById(@RequestBody RegionTemplateRelDTO dto){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail(IndErrorMsgConstants.GET_LOGIN_INFO_ERROR);
        }
        if (dto.getTemplateId() == null){
            return Result.fail("模板id不能为空");
        }
        dto.setTenantId(loginUser.getUser().getTenantId());
        dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        return radarAlarmTemplateService.deleteTemplateById(dto);
    }

    /**
     * 新增告警模板信息
     * @param radarAlarmTemplateReq 告警模板信息实体类
     * @return Result<String>
     */
    @PostMapping("/addTemplate")
    public Result<String> addTemplateInfo(@RequestBody RadarAlarmTemplateReq radarAlarmTemplateReq) {

        return radarAlarmTemplateService.addTemplateInfo(radarAlarmTemplateReq);
    }

    /**
     * 修改告警模板信息
     */
    @PostMapping("/updateTemplate")
    public Result<String> updateTemplateInfo(@RequestBody RadarAlarmTemplateReq radarAlarmTemplateReq) {

        return radarAlarmTemplateService.updateTemplateInfo(radarAlarmTemplateReq);
    }

    /**
     * 关联区域弹框-根据模板告警类型查询全部区域信息
     */
    @PostMapping("/queryRegionByAlarmType")
    public Result<List<RadarAlarmRegionDO>> queryRegionByAlarmType(@RequestBody RegionTemplateRelDTO req){
        if (StringUtils.isEmpty(req.getAlarmTypeValue())){
            return Result.fail("告警类型不能为空！");
        }
        return radarAlarmRegionService.queryRegionByAlarmType(req);
    }

    /**
     * 根据模板ID查询雷达告警区域模板信息
     * @param templateId 模板id
     * @param alarmTypeValue 告警类型
     */
    @GetMapping("/queryAlarmRegionInfoByTemplateId")
    public Result<RadarRegionSelectedVO> queryAlarmRegionInfoByTemplateId(Long templateId, String alarmTypeValue) {
        if (templateId == null) {
            throw new CustomException("模板编码不能为空");
        }
        return radarAlarmTemplateService.queryAlarmRegionInfoByTemplateId(templateId, alarmTypeValue);
    }

    /**
     * 新增雷达告警区域
     */
    @PostMapping("/addAlarmRegion")
    public Result<String> addAlarmRegion(@RequestBody RadarAlarmRegionSaveDTO param) {
        return radarAlarmRegionService.addAlarmRegion(param);
    }

    /**
     * 修改雷达告警区域
     */
    @PostMapping("/updateAlarmRegion")
    public Result<String> updateAlarmRegion(@RequestBody RadarAlarmRegionSaveDTO param) {
        return radarAlarmRegionService.updateAlarmRegion(param);
    }

    /**
     * 删除雷达告警区域
     */
    @PostMapping("/deleteAlarmRegion")
    public Result<String> deleteAlarmRegion(@RequestBody RadarAlarmRegionDeleteDTO param) {
        if (param == null || param.getRegionId() == null) {
            throw new CustomException("告警区域编码不能为空");
        }
        return radarAlarmRegionService.deleteAlarmRegion(param);
    }

    /**
     * 根据区域id查询雷达告警区域
     * @param regionId 区域id
     */
    @GetMapping("/queryAlarmRegionByRegionId")
    public Result<RadarAlarmRegionSaveDTO> queryAlarmRegionByRegionId(Long regionId) {
        if (regionId == null) {
            throw new CustomException("告警区域编码不能为空");
        }
        return radarAlarmRegionService.queryAlarmRegionByRegionId(regionId);
    }

    /**
     * 获取雷达告警区域模板自增名称索引
     */
    @GetMapping("/queryAlarmRegionAutoConfigNameIndex")
    public Result<Long> queryAlarmRegionAutoConfigNameIndex() {
        return radarAlarmRegionService.getAutoConfigNameIndex();
    }

    /**
     * 获取雷达告警区域模板自增名称
     */
    @GetMapping("/queryAlarmTemplateAutoConfigName")
    public Result<String> queryAlarmTemplateAutoConfigName() {
        return radarAlarmTemplateService.getAutoConfigName();
    }
}
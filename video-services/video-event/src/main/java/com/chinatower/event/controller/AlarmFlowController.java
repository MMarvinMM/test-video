package com.chinatower.event.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.dataobject.AlarmFlowConfig;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.dto.FlowConfigModel;
import com.chinatower.event.dto.FlowLinkConfigModel;
import com.chinatower.event.dto.FlowTemplateModel;
import com.chinatower.event.dto.LinkUserDealModel;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.usertree.OrderInfoUserTreeReq;
import com.chinatower.event.entity.vo.SysInfoVo;
import com.chinatower.event.repository.IAlarmFlowConfigService;
import com.chinatower.event.repository.IAlarmTypeClassService;
import com.chinatower.event.repository.IFlowLinkAttrService;
import com.chinatower.event.repository.IFlowLinkConfigService;
import com.chinatower.event.repository.IFlowTemplateService;
import com.chinatower.event.repository.ILinkUserDealService;
import com.chinatower.event.repository.ISysService;
import com.chinatower.common.core.vo.AlarmClassParamVO;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * video-event
 * 告警流程控制器
 *
 * @author chmod
 * @version 1.0
 * @date 2022/2/11 09:52
 */
@RestController
@Slf4j
public class AlarmFlowController {
    @Autowired
    private IFlowLinkConfigService flowLinkConfigService;
    @Autowired
    private IFlowTemplateService flowTemplateService;
    @Autowired
    private IFlowLinkAttrService flowLinkAttrService;
    @Autowired
    private IAlarmFlowConfigService alarmFlowConfigService;
    @Autowired
    private ILinkUserDealService linkUserDealService;
    @Autowired
    private IAlarmTypeClassService alarmTypeClassService;
    @Autowired
    private ISysService sysService;

    /**
     *
     * 功能描述：新增流程环节
     *
     * @param: model
     * @return: Result<FlowLinkConfigModel>
     * @author: zheng.yan
     * @date: 2022-06-09
     * @version:
     */
    @TowerLog(title = "新增流程环节", businessType = BusinessType.INSERT)
    @PostMapping("/flow/alarm/addLinkInfo")
    public Result<FlowLinkConfigModel> addLinkInfo(@Valid @RequestBody FlowLinkConfigModel model) {
        model.setCreator(SecurityUtils.getLoginUser().getUsername());
        return flowLinkConfigService.addLinkInfo(model);
    }

    /**
     *
     * 功能描述：修改环节
     *
     * @param: model
     * @return: Result<String>
     * @author: zheng.yan
     * @date: 2022-06-09
     * @version:
     */
    @TowerLog(title = "修改流程环节", businessType = BusinessType.UPDATE)
    @PostMapping("/flow/alarm/modifyLinkInfo")
    public Result<String> modifyLinkInfo(@RequestBody FlowLinkConfigModel model) {
        model.setModifier(SecurityUtils.getLoginUser().getUsername());
        return flowLinkConfigService.modifyLinkInfo(model);
    }

    /**
     *
     * 功能描述：查询流程环节
     *
     * @param: model
     * @return: Result<Object>
     * @author: zheng.yan
     * @date: 2022-06-09
     * @version:
     */
    @PostMapping("/flow/alarm/queryLinkInfo")
    public Result<Object> queryLinkInfo(@RequestBody FlowLinkConfigModel model) {
        return flowLinkConfigService.queryLinkInfo(model);
    }

    /**
     * 功能描述：删除环节
     *
     * @param model
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 10:53
     * @version:
     */
    @TowerLog(title = "删除流程环节", businessType = BusinessType.DELETE)
    @PostMapping("/flow/alarm/delLinkInfo")
    public Result<String> delLinkInfo(@RequestBody FlowLinkConfigModel model) {
        return flowLinkConfigService.deleteLinkInfo(model.getLinkId());
    }

    /**
     *
     *功能描述：添加流程模板
     *
     * @param: model
     * @return: Result<String>
     * @author: zheng.yan
     * @date: 2022-06-12
     * @version:
     */
    @TowerLog(title = "添加流程模板", businessType = BusinessType.INSERT)
    @PostMapping("/flow/alarm/addFlowTemplate")
    public Result<String> addFlowTemplate(@Valid @RequestBody FlowTemplateModel model) {
        Result<String> result;
        try {
            result = flowTemplateService.addFlowTemplate(model);
        } catch (Exception e) {
            log.error("新增流程模板异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "新增流程模板异常");
        }
        return result;
    }

    /**
     *
     *功能描述：修改流程模板
     *
     * @param: model
     * @return: Result<String>
     * @author: lyy
     * @version: v1.5
     */
    @TowerLog(title = "修改流程模板", businessType = BusinessType.UPDATE)
    @PostMapping("/flow/alarm/modifyFlowTemplate")
    public Result<String> modifyFlowTemplate(@RequestBody FlowTemplateModel model) {
        Result<String> result;
        try {
            result = flowTemplateService.modifyFlowTemplateNew(model);
        } catch (Exception e) {
            log.error("修改流程异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     *
     *功能描述：查询流程模板
     *
     * @param: model
     * @return: Result<Object>
     * @author: zheng.yan
     * @date: 2022-06-13
     * @version:
     */
    @PostMapping("/flow/alarm/queryFlowTemplate")
    public Result<Object> queryFlowTemplate(@RequestBody FlowTemplateModel model) {
        return flowTemplateService.queryFlowTemplate(model);
    }

    /**
     * 功能描述：查询流程模板及其环节属性
     *
     * @param model
     * @return Result<FlowTemplateModel>
     * @author zhen.yan
     * @date 2022/6/15 10:54
     * @version:
     */
    @PostMapping("/flow/alarm/queryFlowAndAttr")
    public Result<FlowTemplateModel> queryFlowAndAttr(@RequestBody FlowTemplateModel model) {
        return flowTemplateService.queryFlowAndAttrNew(model);
    }

    /**
     * 功能描述：删除流程模板
     *
     * @param model
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 10:54
     * @version:
     */
    @TowerLog(title = "删除流程模板", businessType = BusinessType.DELETE)
    @PostMapping("/flow/alarm/delFlowTemplate")
    public Result<String> deleteFlowTemplate(@RequestBody FlowTemplateModel model) {

        Result<String> result;
        try {
            result = flowTemplateService.deleteFlowTemplate(model.getFlowId());
        } catch (Exception e) {
            log.error("删除流程异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：查询下一步所有环节
     *
     * @param model
     * @return Result<List<FlowLinkAttrModel>>
     * @author zhen.yan
     * @date 2022/6/15 10:55
     * @version:
     */
    @PostMapping("/flow/alarm/queryNextLink")
    public Result<List<FlowLinkAttrModel>> queryNextLink(@RequestBody FlowLinkAttrModel model) {
        log.info("queryNextLink:获取下个环节接口入参：{}", JSON.toJSONString(model));
        //接口传入的flowId实际是alarmFlowId
        model.setAlarmFlowId(model.getFlowId());
        model.setFlowId(null);
        return flowLinkAttrService.queryNextLinkNew(model);
    }

    /**
     * 功能描述：查询转派环节信息
     *
     * @param model
     * @return Result<List<FlowLinkAttrModel>>
     * @author zhen.yan
     * @date 2022/6/15 10:55
     * @version:
     */
    @PostMapping("/flow/alarm/queryReassignLink")
    public Result<List<FlowLinkAttrModel>> queryReassignLink(@RequestBody FlowLinkAttrModel model) {
        log.info("queryReassignLink:获取可转派环节接口入参：{}", JSON.toJSONString(model));
        model.setFlowId(null);
        return flowLinkAttrService.queryReassignLinkNew(model);
    }

    /**
     * 功能描述：新增告警流程配置
     *
     * @return Result<String>
     * @author chenmf
     * @date 2022/6/6
     * @version
     */
    @TowerLog(title = "新增告警流程配置", businessType = BusinessType.INSERT)
    @PostMapping("/flow/alarm/addAlarmFlowConfig")
    public Result<String> addAlarmFlowConfig(@RequestBody List<AlarmFlowConfigModel> list) {
        Result<String> result = null;
        try {
            result = alarmFlowConfigService.addAlarmFlowConfig(list);
        } catch (Exception e) {
            log.error("告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：批量更新告警流程配置类型
     *
     * @return Result<String>
     * @author chenmf
     * @date 2022/6/6
     * @version
     */
    @TowerLog(title = "批量更新告警流程配置类型", businessType = BusinessType.UPDATE)
    @PostMapping("/flow/alarm/batchConfigFlow")
    public Result<String> batchConfigFlow(@RequestBody FlowConfigModel flowConfigModel) {
        Result<String> result = null;
        try {
            result = alarmFlowConfigService.batchConfigFlow(flowConfigModel);
        } catch (Exception e) {
            log.error("批量告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：删除告警流程配置
     *
     * @return
     * @author chenmf
     * @date 2022/6/6
     * @version
     */
    @TowerLog(title = "删除告警流程配置", businessType = BusinessType.DELETE)
    @PostMapping("/flow/alarm/deleteAlarmFlowConfig")
    public Result<String> deleteAlarmFlowConfig(@RequestBody AlarmFlowConfigModel model) {
        return alarmFlowConfigService.deleteAlarmFlowConfig(model.getId());
    }

    /**
     * 功能描述：根据条件查询告警流程配置
     *
     * @return
     * @author chenmf
     * @date 2022/6/6
     * @version
     */
    @PostMapping("/flow/alarm/queryAlarmFlowConfig")
    public Result<List<AlarmFlowConfig>> queryAlarmFlowConfig(@RequestBody AlarmFlowConfigModel model) {
        return alarmFlowConfigService.queryAlarmFlowConfig(model);
    }

    /**
     * 功能描述：获取告警流程配置
     *
     * @return Result<List<AlarmFlowConfig>>
     * @author chenmf
     * @date 2022/6/6
     * @version
     */
    @PostMapping("/flow/alarm/getAlarmFlowConfig")
    public Result<List<AlarmFlowConfig>> getAlarmFlowConfig(@RequestBody AlarmFlowConfigModel model) {
        return alarmFlowConfigService.getAlarmFlowConfigNew(model.getTenantId(), model.getAlarmCode(), model.getWarningSourceList());
    }

    /**
     * 功能描述：查询流程开始环节
     *
     * @param model
     * @return Result<FlowModel>
     * @author zhen.yan
     * @date 2022/6/15 10:55
     * @version:
     */
    @PostMapping("/flow/alarm/queryFlowStartLink")
    public Result<FlowModel> queryFlowStartLink(@RequestBody AlarmFlowConfigModel model) {
        return alarmFlowConfigService.queryFlowStartLinkNew(model);
    }

    /**
     * 功能描述：查询流程环节
     *
     * @param model
     * @return Result<List<FlowModel>>
     * @author zhen.yan
     * @date 2022/6/15 10:56
     * @version:
     */
    @PostMapping("/flow/alarm/queryFlowLink")
    public Result<List<FlowModel>> queryFlowLink(@RequestBody AlarmFlowConfigModel model) {
        //接口调用时传入的flowId  实际是alarmFlowId
        /*model.setId(model.getFlowId());
        model.setFlowId(null);*/
        return alarmFlowConfigService.queryFlowLink(model);
    }

    /**
     * 功能描述：添加流程环节处理用户信息
     *
     * @param model
     * @return Result<LinkUserDealModel>
     * @author zhen.yan
     * @date 2022/6/15 10:57
     * @version:
     */
    @TowerLog(title = "添加流程环节处理用户信息", businessType = BusinessType.INSERT)
    @PostMapping("/flow/alarm/addLinkUserDeal")
    public Result<LinkUserDealModel> addLinkUserDeal(@RequestBody @Validated LinkUserDealModel model) {
        return linkUserDealService.addLinkUserDeal(model);
    }

    /**
     * 功能描述：修改流程环节处理状态
     *
     * @param model
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 10:57
     * @version:
     */
    @TowerLog(title = "修改流程环节处理状态", businessType = BusinessType.UPDATE)
    @PostMapping("/flow/alarm/modifyLinkUserDeal")
    public Result<String> modifyLinkUserDeal(@RequestBody @Validated LinkUserDealModel model) {
        return linkUserDealService.modifyLinkUserDeal(model);
    }

    /**
     * 功能描述：获取用户树
     *
     * @param model
     * @return Result<SysInfo>
     * @author zhen.yan
     * @date 2022/6/15 10:57
     * @version:
     */
    @PostMapping("/flow/alarm/getUserTree")
    public Result<SysInfo> getUserTree(@RequestBody FlowLinkAttrModel model) {
        return flowLinkAttrService.getUserTree(model.getRootOrgId(), model.getOrgId(), model.getRoleId());
    }

    /**
     * 功能描述：根据组织ID获取用户所属角色列表
     *
     * @param model
     * @return Result<List < SysInfo>>
     * @author Chensw
     * @date 2022/6/10 14:21
     */
    @PostMapping("/flow/alarm/queryRoles")
    public Result<List<SysInfo>> queryRoles(@RequestBody FlowLinkAttrModel model) {
        return flowLinkAttrService.queryRoles(model.getRootOrgId(), model.getOrgId());
    }

    /**
     * 功能描述：查询租户所以角色
     *
     * @return Result<List<SysInfo>>
     * @author zhen.yan
     * @date 2022/6/15 10:58
     * @version:
     */
    @PostMapping("/flow/alarm/queryTenantRoles")
    public Result<List<SysInfo>> queryTenantRoles() {
        List<SysInfo> roleList = sysService.queryTenantRoles();
        return Result.data(roleList);
    }

    /**
     * 功能描述：生成默认流程、属性及配置
     *
     * @param model
     * @return Result<Map<Long>>
     * @author zhen.yan
     * @date 2022/6/15 10:58
     * @version:
     */
    @PostMapping("/flow/alarm/genDefaultFlowInfo")
    public Result<Map<String, Long>> genDefaultFlowInfo(@RequestBody FlowModel model) {
        Result<Map<String, Long>> result;
        try {
            result = flowTemplateService.genDefaultFlowInfo(model);
        } catch (Exception e) {
            log.error("生成默认流程异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "生成默认流程异常");
        }
        return result;
    }

    /**
     * 功能描述：获取流程(如果没有获取到流程则自动生成默认流程)
     *
     * @param model
     * @return Result<Map<Long>>
     * @author zhen.yan
     * @date 2022/6/15 10:58
     * @version:
     */
    @PostMapping("/flow/alarm/getFlow")
    public Result<Map<String, Object>> getFlow(@RequestBody FlowModel model) {
        Result<Map<String, Object>> result;
        try {
            AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
            alarmFlowConfigModel.setTenantId(model.getTenantId());
            alarmFlowConfigModel.setAlarmCode(model.getAlarmCode());
            alarmFlowConfigModel.setWarningSource(model.getWarningSource());
            Result<FlowModel> configResult = alarmFlowConfigService.queryFlowStartLinkNew(alarmFlowConfigModel);
//            if (ResultCode.FAILURE.getCode() == configResult.getCode()) {
//                // 没有查询到流程，生成默认流程
//                result = flowTemplateService.genDefaultFlowInfo(model);
//            } else
            if (ResultCode.SUCCESS.getCode() == configResult.getCode()) {
                // 查询到流程
                result = Result.success("查询成功");
                Map<String, Object> resultMap = new HashMap<>(1);
                resultMap.put(model.getTenantId() + "_" + model.getWarningSource(), configResult.getData().getFlowId());
                resultMap.put("flow",configResult.getData());
                result.setData(resultMap);
            } else {
                // 参数校验失败或者查询异常
                result = Result.fail(configResult.getCode(), configResult.getMsg());
            }
        } catch (Exception e) {
            log.error("获取流程异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "获取流程异常");
        }
        return result;
    }

    /**
     * 功能描述：获取阈值信息
     *
     * @param alarmClassParamVO
     * @return Result<AlarmClassParamVO>
     * @author zhen.yan
     * @date 2022/6/15 10:59
     * @version:
     */
    @PostMapping("/flow/alarm/getAlarmParamInfo")
    public Result<AlarmClassParamVO> getAlarmParamInfo(@RequestBody AlarmClassParamVO alarmClassParamVO) {
        return alarmTypeClassService.getAlarmParamInfo(alarmClassParamVO);
    }

    /**
     * 功能描述：保存阈值信息
     *
     * @param alarmClassParamVO
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 10:59
     * @version:
     */
    @PostMapping("/flow/alarm/saveAlarmParamInfo")
    public Result<String> saveAlarmParamInfo(@RequestBody AlarmClassParamVO alarmClassParamVO) {
        Result<String> result;
        try {
            result = alarmTypeClassService.saveAlarmParamInfo(alarmClassParamVO);
        } catch (Exception e) {
            log.error("保存阈值信息异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "保存阈值信息异常");
        }
        return result;
    }

    /**
     * 功能描述：根据流程ID，租户ID获取用户树
     *
     * @param model
     * @return Result<SysInfo>
     * @author zhen.yan
     * @date 2022/6/15 11:00
     * @version:
     */
    @PostMapping("/flow/alarm/getFlowByIdUserTree")
    @EventObsEncrypt
    public Result<SysInfoVo> getFlowByIdUserTree(@RequestBody FlowLinkAttrModel model) {
        //接口传入的flowId实际是alarmFlowId
        model.setAlarmFlowId(model.getFlowId());
        model.setFlowId(null);
        return flowLinkAttrService.getFlowByIdUserTree(model);
    }

    /**
     * 功能描述：根据流程ID，租户ID获取用户树(app专用)
     *
     * @version:
     */
    @PostMapping("/flow/alarm/getAppFlowByIdUserTree")
    @EventObsEncrypt
    public Result<SysInfoVo> getAppFlowByIdUserTree(@RequestBody FlowLinkAttrModel model) {
        //接口传入的flowId实际是alarmFlowId
        model.setAlarmFlowId(model.getFlowId());
        model.setFlowId(null);
        return flowLinkAttrService.getAppFlowByIdUserTree(model);
    }

    /**
     * 功能描述：根据流程ID，租户ID获取用户树(app专用)
     *
     * @version:
     */
    @PostMapping("/flow/alarm/getOrderInfoUserTree")
    @EventObsEncrypt
    public Result<SysInfoVo> getOrderInfoUserTree(@RequestBody OrderInfoUserTreeReq model) {
        Result result;
        try {
            result = flowLinkAttrService.getOrderInfoUserTree(model);
        } catch (Exception e) {
            return Result.fail(ResultCode.ERROR.getCode(), "获取用户树异常");
        }
        return result;
    }

}

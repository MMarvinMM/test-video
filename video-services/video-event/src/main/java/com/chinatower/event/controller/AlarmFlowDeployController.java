package com.chinatower.event.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.FlowTemplateIndustryDTO;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigModel;
import com.chinatower.event.repository.*;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * video-event
 * 行业应用初始化流程配置控制器
 *
 * @author lyy
 * @version 1.0
 * @date 2022/7/18 14:14
 */
@RestController
@Slf4j
@Api(tags = {"行业应用初始化流程配置"
})
@RequestMapping(value = "/alarm/flowdeploy")
public class AlarmFlowDeployController extends BaseController {
    @Autowired
    private IAlarmFlowDeployService alarmFlowDeployService;

    /**
     *
     * 功能描述：默认流程查询
     * @author lyy
     * @param: flowTemplateDTO
     * @return: Result<Object>
     *
     */
    @PostMapping("/getFlowTemplateList")
    @ApiOperation(value = "默认流程配置信息", notes = "默认流程配置信息")
    public Result<Object> getFlowTemplateList(@RequestBody FlowTemplateDTO flowTemplateDTO) {
        return alarmFlowDeployService.getFlowTemplateList(flowTemplateDTO);
    }

    /**
     * 功能描述：查询行业、流程、环节配置信息
     * @author lyy
     * @param params
     * @return Result<Object>
     */
    @PostMapping("/queryFlowLinkConfigIndustry")
    public Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustry(@RequestBody Map<String, String> params) {
        return alarmFlowDeployService.queryFlowLinkConfigIndustry(params);
    }

    /**
     * 功能描述：新增行业、流程、环节配置信息
     * @author lyy
     * @param templateIndustryDTO
     * @return Result<Object>
     */
    @TowerLog(title = "新增行业、流程、环节配置信息", businessType = BusinessType.INSERT)
    @PostMapping("/addFlowLinkConfigIndustry")
    public Result<Object> addFlowLinkConfigIndustry(@RequestBody FlowTemplateIndustryDTO templateIndustryDTO) {
        Result<Object> result = null;
        try {
            result = alarmFlowDeployService.addFlowLinkConfigIndustry(templateIndustryDTO);
        } catch (Exception e) {
            log.error("新增告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：查询行业、流程、环节配置信息
     * @author lyy
     * @param params
     * @return Result<Object>
     */
    @PostMapping("/queryFlowLinkConfigIndustryLocalVersion")
    public Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustryLocalVersion(@RequestBody Map<String, String> params) {
        return alarmFlowDeployService.queryFlowLinkConfigIndustryLocalVersion(params);
    }

    /**
     * 功能描述：新增行业、流程、环节配置信息
     * @author lyy
     * @param templateIndustryDTO
     * @return Result<Object>
     */
    @TowerLog(title = "新增行业、流程、环节配置信息", businessType = BusinessType.INSERT)
    @PostMapping("/addFlowLinkConfigIndustryLocalVersion")
    public Result<Object> addFlowLinkConfigIndustryLocalVersion(@RequestBody FlowTemplateIndustryDTO templateIndustryDTO) {
        Result<Object> result = null;
        try {
            result = alarmFlowDeployService.addFlowLinkConfigIndustryLocalVersion(templateIndustryDTO);
        } catch (Exception e) {
            log.error("新增告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：订购初始化流程配置
     * @author lyy
     * @param flowConfigModel
     * @return Result<Object>
     */
    @TowerLog(title = "订购初始化流程配置", businessType = BusinessType.INSERT)
    @PostMapping("/addAlarmFlowTypeAndConfig")
    public Result<Object> addAlarmFlowTypeAndConfig(@RequestBody VmAlarmFlowConfigModel flowConfigModel){
        /*Result<Object> result = null;
        try {
            result = alarmFlowDeployService.addAlarmFlowTypeAndConfig(flowConfigModel);
        } catch (Exception e) {
            log.error("新增告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;*/
        return Result.success("success");
    }

    /**
     * 功能描述：判断角色是否已经被流程使用
     * @author liusq
     * @param param roleId,tenantId, industryCode, appVerCode
     * @return
     */
    @PostMapping("/getFlowsByRole")
    public Result<Object> getFlowsByRole(@RequestBody Map param){
        return alarmFlowDeployService.getFlowsByRole(param);
    }

    /**
     * 功能描述：判断用户是否已经被流程使用
     * @author liusq
     * @param param userId,tenantId, industryCode, appVerCode
     * @return
     */
    @PostMapping("/getFlowsByUser")
    public Result<Object> getFlowsByUser(@RequestBody Map param){
        return alarmFlowDeployService.getFlowsByUser(param);
    }
}

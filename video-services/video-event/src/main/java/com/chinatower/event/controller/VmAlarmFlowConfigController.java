package com.chinatower.event.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.AlarmSoundDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigDTO;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigModel;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.repository.VmAlarmFlowConfigService;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.system.api.resp.SysDictDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * video-event
 * 告警流程配置控制器
 *
 * @author lyy
 * @version v1.5
 * @date 2022/7/18 14:14
 */
@RestController
@Slf4j
@Api(tags = {"配置管理-告警流程配置"
})
@RequestMapping(value = "/alarm/flowconfig")
public class VmAlarmFlowConfigController extends BaseController {

    @Autowired
    private VmAlarmFlowConfigService vmAlarmFlowConfigService;

    /**
     * 功能描述：分页查询告警流程配置
     * @param vmAlarmFlowConfigDTO
     * @return Result<Object>
     * @author lyy
     * @date 2022/07/20
     * @version v1.5
     */
    @PostMapping("/getAlarmFlowConfigList")
    public Result<Object> getAlarmFlowConfigList(@RequestBody VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO) {
        return vmAlarmFlowConfigService.getAlarmFlowConfigList(vmAlarmFlowConfigDTO);
    }

    /**
     * 功能描述：根据租户ID、告警来源、告警类型查询
     * @param vmAlarmFlowConfigDTO
     * @return Result<Object>
     * @author lyy
     * @date 2022/07/21
     * @version v1.5
     */
    @PostMapping("/queryAlarmFlowConfigAndType")
    public Result<Object> queryAlarmFlowConfigAndType(@RequestBody VmAlarmFlowConfigDTO vmAlarmFlowConfigDTO) {
        Result<Object> result;
        try {
            result = vmAlarmFlowConfigService.queryAlarmFlowConfigAndType(vmAlarmFlowConfigDTO);
        } catch (Exception e) {
            log.error("查询告警流程对应的告警类型异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：查询告警流程配置详情、编辑回显
     * @param configModel
     * @return Result<Object>
     * @author lyy
     * @date 2022/07/21
     * @version v1.5
     */
    @PostMapping("/queryAlarmFlowConfigDetail")
    public Result<Object> queryAlarmFlowConfigDetail(@RequestBody VmAlarmFlowConfigModel configModel){
        return vmAlarmFlowConfigService.queryAlarmFlowConfigDetail(configModel);
    }

    /**
     * 功能描述：获取默认告警流程配置名称：“告警流程名称+数字”
     * @param configModel
     * @return Result<String>
     * @author lyy
     * @date 2022/07/21
     * @version v1.5
     */
    @PostMapping("/getAutoConfigName")
    public Result<String> getAutoConfigName(@RequestBody VmAlarmFlowConfigModel configModel) {
        return vmAlarmFlowConfigService.getAutoConfigName(configModel);
    }

    /**
     * 功能描述：校验告警流程配置名称是否唯一
     * @param configModel
     * @return Result<Object>
     * @author lyy
     * @date 2022/07/24
     * @version v1.5
     */
    @PostMapping("/getConfigNameCount")
    public Result<Object> getConfigNameCount(@RequestBody VmAlarmFlowConfigModel configModel) {
        return vmAlarmFlowConfigService.getConfigNameCount(configModel);
    }

    /**
     * 功能描述：新增告警流程配置
     * @param vmAlarmFlowConfigModel
     * @return Result<String>
     * @author lyy
     * @date 2022/07/24
     * @version v1.5
     */
    @TowerLog(title = "新增告警流程配置", businessType = BusinessType.INSERT)
    @PostMapping("/addVmAlarmFlowConfig")
    public Result<String> addVmAlarmFlowConfig(@RequestBody VmAlarmFlowConfigModel vmAlarmFlowConfigModel) {
        Result<String> result = null;
        try {
            result = vmAlarmFlowConfigService.addVmAlarmFlowConfig(vmAlarmFlowConfigModel);
        } catch (Exception e) {
            log.error("新增告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：修改告警流程配置
     * @param flowConfigModel
     * @return Result<String>
     * @author lyy
     * @date 2022/07/23
     * @version v1.5
     */
    @TowerLog(title = "修改告警流程配置", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAlarmFlowConfig")
    public Result<String> updateAlarmFlowConfig(@RequestBody VmAlarmFlowConfigModel flowConfigModel) {
        Result<String> result = null;
        try {
            result = vmAlarmFlowConfigService.updateAlarmFlowConfig(flowConfigModel);
        } catch (Exception e) {
            log.error("修改告警流程配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：删除选择的告警流程配置
     * @param
     * @return Result<String>
     * @author lyy
     * @date 2022/07/22
     * @version v1.5
     */
    @TowerLog(title = "删除选择的告警流程配置", businessType = BusinessType.DELETE)
    @GetMapping("/deleteAlarmFlowConfig")
    public Result deleteAlarmFlowConfig(String tenantId, Long configId) {
        return vmAlarmFlowConfigService.deleteFlowConfig(configId, tenantId, Constants.OPER_TYPE_DEL);
    }


    /**
     * 功能描述：校检告警声音是否可修改或者删除
     *
     * @param model
     * @return Result<Map>
     * @author yhb
     * @date 2023/1/31
     * @version:
     */
    @PostMapping("/selectAlarmSoundOperation")
    Result<Map> selectAlarmSoundOperation(@RequestBody AlarmSoundDTO model) {
        Result<Map> result;
        try {
            result = vmAlarmFlowConfigService.selectAlarmSoundOperation(model);
        } catch (Exception e) {
            log.error(Constants.ALARM_FLOW_CONFIG_SOUND_SUCCESS, e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：查询组织下拉树下的用户信息（提供给APP调用）
     * @param sysUser
     * @return
     */
    @ApiOperation(value = "获取租户组织下拉树下的用户信息")
    @EventObsEncrypt
    @PostMapping("/getUserInfoByName")
    public TableDataInfo getUserInfoByName(@RequestBody SysUser sysUser) {
        List<Map> userList = vmAlarmFlowConfigService.getUserInfoByName(sysUser);
        return getDataTable(userList);
    }

    /**
     * 功能描述：告警来源下拉-查询所有告警来源（含第三方告警来源）
     * @return
     */
    @PostMapping("/findAlarmSourceAll")
    public Result<List<SysDictDataResponse>> findAlarmSourceAll(){
        return vmAlarmFlowConfigService.findAlarmSourceAll();
    }

    /**
     * 功能描述：查询该租户行业选择的第三方告警类型
     * @param subTypeDTO
     * @return
     */
    @PostMapping("/getThirdPartyAlarmTypes")
    public List<AlarmSourceSubTypeVO> getThirdPartyAlarmTypes(@RequestBody AlarmSourceSubTypeDTO subTypeDTO){
        return vmAlarmFlowConfigService.getThirdPartyAlarmTypes(subTypeDTO);
    }
}

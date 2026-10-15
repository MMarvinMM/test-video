package com.chinatower.event.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.VmAlarmTransferConfigDTO;
import com.chinatower.event.entity.vo.VmAlarmTransferConfigVO;
import com.chinatower.event.repository.VmAlarmTransferConfigService;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * video-event
 * 告警转移配置控制器
 *
 * @author lyy
 * @version v1.6.00
 * @date 2023/06/30 10:37
 */
@RestController
@Slf4j
@Api(tags = {"配置管理-告警转移配置"
})
@RequestMapping(value = "/alarm/transfer")
public class VmAlarmTransferConfigController extends BaseController {

    @Autowired
    private VmAlarmTransferConfigService alarmTransferConfigService;

    /**
     * 功能描述：分页查询告警转移配置
     * @param alarmTransferConfigDTO
     * @return Result<Object>
     */
    @PostMapping("/getAlarmTransferConfigList")
    public Result<Object> getAlarmTransferConfigList(@RequestBody VmAlarmTransferConfigDTO alarmTransferConfigDTO) {
        return alarmTransferConfigService.getAlarmTransferConfigList(alarmTransferConfigDTO);
    }

    /**
     * 功能描述：新增提交校验：根据租户ID、告警来源、告警类型查询
     * @param transferConfigDTO
     * @return Result<Object>
     */
    @PostMapping("/queryAlarmTransferConfigAndType")
    public Result<Object> queryAlarmTransferConfigAndType(@RequestBody VmAlarmTransferConfigDTO transferConfigDTO) {
        Result<Object> result;
        try {
            result = alarmTransferConfigService.queryAlarmTransferConfigAndType(transferConfigDTO);
        } catch (Exception e) {
            log.error("查询告警转移关联的告警类型异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：查询告警转移配置详情、编辑回显
     * @param transferConfigVO
     * @return Result<Object>
     */
    @PostMapping("/queryAlarmTransferConfigDetail")
    public Result<Object> queryAlarmTransferConfigDetail(@RequestBody VmAlarmTransferConfigVO transferConfigVO){
        return alarmTransferConfigService.queryAlarmTransferConfigDetail(transferConfigVO);
    }

    /**
     * 功能描述：获取默认告警转移配置名称“告警转移策略+数字（数字累加）”
     * @param transferConfigDTO
     * @return Result<String>
     */
    @PostMapping("/getAutoTransferName")
    public Result<String> getAutoTransferName(@RequestBody VmAlarmTransferConfigDTO transferConfigDTO) {
        return alarmTransferConfigService.getAutoTransferName(transferConfigDTO);
    }

    /**
     * 功能描述：校验告警转移配置名称是否唯一
     * @param transferConfigDTO
     * @return Result<Object>
     */
    @PostMapping("/getTransferNameCount")
    public Result<Object> getTransferNameCount(@RequestBody VmAlarmTransferConfigDTO transferConfigDTO) {
        return alarmTransferConfigService.getTransferNameCount(transferConfigDTO);
    }

    /**
     * 功能描述：新增告警转移配置
     * @param transferConfigDTO
     * @return Result<String>
     */
    @TowerLog(title = "新增告警转移配置", businessType = BusinessType.INSERT)
    @PostMapping("/addVmAlarmTransferConfig")
    public Result<String> addVmAlarmTransferConfig(@RequestBody VmAlarmTransferConfigDTO transferConfigDTO) {
        Result<String> result = null;
        try {
            result = alarmTransferConfigService.addVmAlarmTransferConfig(transferConfigDTO);
        } catch (Exception e) {
            log.error("新增告警转移配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：修改告警转移配置
     * @param transferConfigDTO
     * @return Result<String>
     */
    @TowerLog(title = "修改告警转移配置", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAlarmTransferConfig")
    public Result<String> updateAlarmTransferConfig(@RequestBody VmAlarmTransferConfigDTO transferConfigDTO) {
        Result<String> result = null;
        try {
            result = alarmTransferConfigService.updateAlarmTransferConfig(transferConfigDTO);
        } catch (Exception e) {
            log.error("修改告警转移配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：删除选择的告警转移配置
     * @param
     * @return Result<String>
     */
    @TowerLog(title = "删除选择的告警转移配置", businessType = BusinessType.DELETE)
    @PostMapping("/deleteAlarmTransferConfig")
    public Result deleteAlarmTransferConfig(@RequestBody VmAlarmTransferConfigDTO transferConfigDTO) {
        return alarmTransferConfigService.deleteAlarmTransferConfig(transferConfigDTO.getTenantId(), transferConfigDTO.getId(), Constants.OPER_TYPE_DEL);
    }

}

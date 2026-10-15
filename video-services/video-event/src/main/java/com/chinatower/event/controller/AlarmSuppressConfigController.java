package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.repository.IAlarmSuppressConfigRepository;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * video-event
 * @author luo.zhenghong
 * @date 2022/6/9 9:30
 */
@RestController
@Api(tags = {"雨天模式告警压制管理"
})
@Slf4j
public class AlarmSuppressConfigController {

    @Autowired
    private IAlarmSuppressConfigRepository configRepository;

    /**
     * 功能描述：新增/修改告警压制配置
     *
     * @param dto
     * @return Result
     * @author luo.zhenghong
     * @date 2022/6/15 11:15
     * @version:
     */
    @ApiOperation(value = "新增/修改告警压制配置", notes = "新增/修改告警压制配置")
    @TowerLog(title = "新增/修改告警压制配置", businessType = BusinessType.INSERT)
    @PostMapping("/alarm/suppress/saveOrUpdateConfig")
    public Result saveOrUpdateConfig(@Valid @RequestBody AlarmSuppressConfigDTO dto) {
        return configRepository.saveOrUpdateConfig(dto);
    }

    /**
     * 功能描述：删除压制规则信息
     *
     * @param deviceCode
     * @return Result
     * @author luo.zhenghong
     * @date 2022/6/15 11:16
     * @version:
     */
    @ApiOperation(value = "删除压制规则信息", notes = "删除压制规则信息")
    @TowerLog(title = "删除压制规则信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/alarm/suppress/delete/{deviceCode}")
    public Result delete(@PathVariable(value="deviceCode") String deviceCode) {
        return configRepository.delete(deviceCode);
    }

    /**
     * 功能描述：查询压制规则信息
     *
     * @param deviceCode
     * @return Result
     * @author luo.zhenghong
     * @date 2022/6/15 11:16
     * @version:
     */
    @ApiOperation(value = "查询压制规则信息", notes = "查询压制规则信息")
    @GetMapping("/alarm/suppress/querySuppressConfig/{deviceCode}")
    public Result<AlarmSuppressConfigDTO> querySuppressConfig(@PathVariable(value="deviceCode") String deviceCode) {
        return Result.data(configRepository.querySuppressConfig(deviceCode));
    }

}

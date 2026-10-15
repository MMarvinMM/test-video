package com.chinatower.bdm.modules.layer.controller;

import com.chinatower.bdm.modules.layer.service.VmLayerTemplateConfigService;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.core.dto.bdm.VmLayerTempConfigDTO;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-bdm
 * 图层模版配置控制器
 *
 * @author lyy
 * @version v1.6.60
 * @date 2024/04/29 11:20
 */
@RestController
@Slf4j
@Api(tags = {"配置管理-图层模版配置"
})
@RequestMapping(value = "/alarm/layerTemp")
public class VmLayerTemplateConfigController extends BaseController {

    @Autowired
    private VmLayerTemplateConfigService layerTemplateConfigService;

    /**
     * 分页查询图层模板配置
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    @PostMapping("/getLayerTempConfigList")
    public Result<Object> getLayerTempConfigList(@RequestBody VmLayerTempConfigDTO layerTempConfigDTO) {
        return layerTemplateConfigService.getLayerTempConfigList(layerTempConfigDTO);
    }

    /**
     * 查询图层模板字段详情、编辑回显
     * @param tempConfigDTO
     * @return Result<Object>
     */
    @PostMapping("/queryLayerTempFieldsDetail")
    public TableDataInfo queryLayerTempFieldsDetail(@RequestBody VmLayerTempConfigDTO tempConfigDTO){
        startPage();
        return getDataTable(layerTemplateConfigService.queryLayerTempFieldsDetail(tempConfigDTO));
    }

    /**
     * 校验图层模版名称是否唯一
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    @PostMapping("/getLayerTempNameCount")
    public Result<Object> getLayerTempNameCount(@RequestBody VmLayerTempConfigDTO layerTempConfigDTO) {
        return layerTemplateConfigService.getLayerTempNameCount(layerTempConfigDTO);
    }

    /**
     * 新增编辑图层模板配置
     * @param tempConfigDTO
     * @return Result<String>
     */
    @TowerLog(title = "新增编辑图层模板配置", businessType = BusinessType.INSERT)
    @PostMapping("/addOrModifyLayerTempConfig")
    public Result<String> addOrModifyLayerTempConfig(@RequestBody VmLayerTempConfigDTO tempConfigDTO) {
        Result<String> result = null;
        try {
            result = layerTemplateConfigService.addOrModifyLayerTempConfig(tempConfigDTO);
        } catch (Exception e) {
            log.error("新增编辑图层模板配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 根据选择的告警类型获取图层模板字段信息
     * @param layerTempConfigDTO
     * @return
     */
    @PostMapping("/selectLayerFieldsByType")
    public Result<Object> selectLayerFieldsByType(@RequestBody VmLayerTempConfigDTO layerTempConfigDTO) {
        return layerTemplateConfigService.selectLayerFieldsByType(layerTempConfigDTO);
    }

    /**
     * 根据告警来源获取图层模板下拉数据
     * @param layerTempConfigDTO
     * @return
     */
    @PostMapping("/getLayerTempByTypeList")
    public Result<Object> getLayerTempByTypeList(@RequestBody VmLayerTempConfigDTO layerTempConfigDTO) {
        return layerTemplateConfigService.getLayerTempByTypeList(layerTempConfigDTO);
    }

    /**
     * 删除校验：校验当前图层模板下是否已有图层
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    @PostMapping("/queryLayerTemplateIsExists")
    public Result<Object> queryLayerTemplateIsExists(@RequestBody VmLayerTempConfigDTO layerTempConfigDTO) {
        Result<Object> result;
        try {
            result = layerTemplateConfigService.queryLayerTemplateIsExists(layerTempConfigDTO);
        } catch (Exception e) {
            log.error("校验当前图层模板下是否已有图层异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), e.getMessage());
        }
        return result;
    }

    /**
     * 删除图层模板配置
     * @param
     * @return Result<String>
     */
    @TowerLog(title = "删除选择的图层模板配置", businessType = BusinessType.DELETE)
    @PostMapping("/delLayerTempConfig")
    public Result delLayerTempConfig(@RequestBody VmLayerTempConfigDTO layerTempConfigDTO) {
        return layerTemplateConfigService.deleteLayerTempConfig(layerTempConfigDTO.getId());
    }

}

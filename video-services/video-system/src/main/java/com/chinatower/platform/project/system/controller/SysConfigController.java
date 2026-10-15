package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.ServletUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.ip.IpUtils;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.domain.vo.MsgConfig;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * video-system
 * 参数配置 信息操作处理
 *
 * @author iwhalecloud
 */
@Api(tags = "参数配置 信息操作处理")
@RestController
@RequestMapping("/system/config")
@Slf4j
public class SysConfigController extends TowerBaseController {
    @Autowired
    private ISysConfigService configService;

    private static final String OBS_KEY="obsKey";
    /**
     * 获取参数配置列表
     */
    @ApiOperation(value = "获取参数配置列表")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config) {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

//    @ApiOperation(value = "参数管理")
//    @TowerLog(title = "参数管理", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(@RequestBody  SysConfig config, HttpServletResponse response) {
//        List<SysConfig> list = configService.exportConfigList(config);
//        byte[] data =new ExcelUtil<>(SysConfig.class).exportBytes(list);
//        FileUtils.download(response, data);
//    }

    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation(value = "根据参数编号获取详细信息")
    @GetMapping(value = "/detail/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId) {
        return AjaxResult.success(configService.selectConfigById(configId));
    }

    /**
     * 新增参数配置
     */
    @ApiOperation(value = "新增参数配置")
    @TowerLog(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return Result.fail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreator(SecurityUtils.getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @ApiOperation(value = "修改参数配置")
    @TowerLog(title = "参数管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return Result.fail("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setModifier(SecurityUtils.getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @ApiOperation(value = "删除参数配置")
    @TowerLog(title = "参数管理", businessType = BusinessType.DELETE)
    @GetMapping("/del/{configIds}")
    public Result remove(@PathVariable Long[] configIds) {
        return toAjax(configService.deleteConfigByIds(configIds));
    }

    @ApiOperation(value = "查询obs的秘钥", produces = "application/json")
    @PostMapping("/base/selectObsByKeys")
    public Result<Map<String, String>> selectObsByKeys(@RequestBody SysConfig sysConfig){
        if(StringUtils.isEmpty(sysConfig.getConfigKey())||!OBS_KEY.equals(sysConfig.getConfigKey())){
            return Result.fail("错误的key");
        }
        return configService.selectObsByKeys();
    }

    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation(value = "根据参数key获取value")
    @GetMapping(value = "/base/detail/{configKey}")
    public AjaxResult getConfValue(@PathVariable String configKey) {
        log.info("访问system服务-getConfValue,key={},ip={}" , configKey, IpUtils.getIpAddr(ServletUtils.getRequest()));
        return AjaxResult.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation(value = "根据参数key获取value")
    @PostMapping(value = "/base/detailSdk")
    public AjaxResult getConfValueSdk(@RequestBody String configKey) {
        log.info("访问system服务-getConfValue,key={},ip={}" , configKey, IpUtils.getIpAddr(ServletUtils.getRequest()));
        return AjaxResult.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 根据参数编号批量获取详细信息
     */
    @GetMapping(value = "/base/detail/multiple/{configKeys}")
    public Result<Map<String,SysConfig>> configKeys(@PathVariable String[] configKeys){
        log.info("访问system服务-multiple/configKeys,key={},ip={}" , configKeys, IpUtils.getIpAddr(ServletUtils.getRequest()));
        Map<String,SysConfig> configMap = configService.selectConfigByConfigKeys(configKeys);
        return Result.data(configMap);
    }


    /**
     * 根据行业版本号获取开关值
     */
    @ApiOperation(value = "根据行业版本号获取开关值")
    @PostMapping(value = "/base/msgConfig")
    public Result<String> getMsgConfig(@RequestBody MsgConfig msgConfig) {
        Result result = null;
        String flag = configService.getMsgConfig(msgConfig.getAppVerCode());
        result = Result.success("查询成功");
        result.setData(flag);
        return result;

    }

    /**
     * 刷新字典类型
     */
    @ApiOperation(value = "刷新参数配置缓存")
    @TowerLog(title = "参数管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/refreshCache/{configIds}")
    public Result refreshCache(@PathVariable Long[] configIds) {
        configService.batchRefreshConfigCache(configIds);
        return Result.success("操作成功");
    }

    /**
     * 根据参数编号获取详细信息service调用
     */
    @ApiOperation(value = "根据参数key获取value")
    @GetMapping(value = "/base/info/{configKey}")
    public Map<String,Object> getInfo(@PathVariable String configKey) {
        Map<String,Object> result = new HashMap<>();
        SysConfig sysConfig = configService.selectConfigByKey(configKey);
        result.put("value",sysConfig.getConfigValue());
        return result;
    }

    /**
     * 查询参数配置
     */
    @ApiOperation(value = "查询参数配置")
    @PostMapping(value = "/getConfigInfo")
    public AjaxResult getConfigInfo(@RequestBody SysConfig config) {
        log.info("访问system服务-getConfigInfo,key={},ip={}" , config.getConfigKey(), IpUtils.getIpAddr(ServletUtils.getRequest()));
        return AjaxResult.success(configService.selectConfigByKey(config.getConfigKey()));
    }
}

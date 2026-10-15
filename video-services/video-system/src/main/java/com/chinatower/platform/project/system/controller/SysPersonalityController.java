package com.chinatower.platform.project.system.controller;

import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.dto.SaveSetInfoDTO;
import com.chinatower.platform.project.system.domain.vo.SysInfoSetVO;
import com.chinatower.platform.project.system.service.ISysPersonalityService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * video-system
 * @ClassName SysPersonalityController
 * @Description 个性化设置
 * @Author D
 * @Date 2022/2/23 16:40
 */
@Api(tags = "客户个性化设置")
@RestController
@RequestMapping("/base/system/personality")
public class SysPersonalityController extends TowerBaseController {

    @Autowired
    private ISysPersonalityService personalityService;

    /**
     * @Description 获取个性化配置
     * @author wjd
     * @Date 15:38 2021/7/7
     * @Param [sysInfoSetVO]
     **/
    @ApiOperation(value = "获取个性化配置")
    @PostMapping("/getInfoSetList")
    public AjaxResult getInfoSetList(SysInfoSetVO sysInfoSetVO){
        //查询配置信息
        List<SysInfoSetVO> result = personalityService.getInfoSetList(sysInfoSetVO);

        return AjaxResult.success(result);
    }

    /**
     * 保存个性化配置
     * @return
     */
    @ApiOperation(value = "保存个性化配置")
    @PostMapping("/saveSetInfo")
    public Result<Integer> saveSetInfo(@RequestBody @Validated SaveSetInfoDTO dto){
        return toAjax(personalityService.saveSetsByType(dto.getVoList(),dto.getType()));
    }

    /**
     * 保存个性化主题
     * @param voList
     * @return
     */
    @ApiOperation(value = "保存个性化主题")
    @PostMapping("/saveThemeInfo")
    public Result<Integer> saveThemeInfo(@RequestBody List<SysInfoSetVO> voList){
        return toAjax(personalityService.saveSetsByType(voList,"2"));
    }

    /**
     * 恢复默认
     */
    @ApiOperation("恢复默认")
    @GetMapping("/reset")
    public Result reset(String type){
        String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
        personalityService.reset(tenantId,type);
        return Result.success("恢复成功");
    }

}

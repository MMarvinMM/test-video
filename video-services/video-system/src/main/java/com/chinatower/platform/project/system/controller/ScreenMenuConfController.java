package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.project.system.service.IScreenMenuConfService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.ScreenMenuConfVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-system
 * @ClassName ScreenMenuConfController
 * @Description 大屏菜单配置
 * @Author liuxt
 * @Date 2024/6/24
 */
@Api(tags = "大屏菜单配置")
@RestController
@Slf4j
@RequestMapping("/system/screenMenuConf")
public class ScreenMenuConfController extends TowerBaseController {


    @Autowired
    private IScreenMenuConfService screenMenuConfService;

    /**
     * 查询大屏菜单配置
     * @param screenMenuConfVO
     * @return
     */
    @ApiOperation(value = "查询大屏菜单配置")
    @PostMapping("/queryScreenMenuConf")
    public Result<List<ScreenMenuConfVO>> queryScreenMenuConf(@RequestBody ScreenMenuConfVO screenMenuConfVO)
    {
        List<ScreenMenuConfVO> list = screenMenuConfService.getScreenMenuConf(screenMenuConfVO);
        return Result.data(list);
    }



    /**
     * 保存大屏菜单配置
     */
    @ApiOperation(value = "保存大屏菜单配置")
    @TowerLog(title = "大屏菜单配置", businessType = BusinessType.INSERT)
    @PostMapping(value = "/saveScreenMenuConf")
    @Transactional(rollbackFor = Exception.class)
    public Result saveScreenMenuConf(@RequestBody List<ScreenMenuConfVO> screenMenuConfVOList)
    {
        int num = screenMenuConfService.saveRoleScreenRef(screenMenuConfVOList);
        if(num > 0){
            return Result.success("保存成功");
        }else{
            return Result.fail("保存失败");
        }

    }


}

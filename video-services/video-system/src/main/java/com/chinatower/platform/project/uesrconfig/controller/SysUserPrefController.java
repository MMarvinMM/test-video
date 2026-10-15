package com.chinatower.platform.project.uesrconfig.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.uesrconfig.domain.SysUserPref;
import com.chinatower.platform.project.uesrconfig.service.SysUserPrefService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * video-system
 * @ClassName SysUserPrefController
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/15
 **/
@RestController
@Api(tags = "用户配置")
public class SysUserPrefController {

    @Resource
    SysUserPrefService sysUserPrefService;

    @PostMapping("userPreference/getPreference")
    @ApiOperation("查询偏好设置")
    public Result<List<SysUserPref>> getPreference(@RequestBody SysUserPref sysUserPref) {
        if (StringUtils.isBlank(sysUserPref.getPrefType())) {
            return Result.fail("设置类型不能为空");
        }
        if (StringUtils.isBlank(sysUserPref.getPrefPage())) {
            return Result.fail("页面值不能为空");
        }
        if (sysUserPref.getUserId() == null) {
            sysUserPref.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        }
        LambdaQueryWrapper<SysUserPref> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPref::getDeleteFlag, '0')
                .eq(SysUserPref::getPrefType, sysUserPref.getPrefType())
                .eq(SysUserPref::getPrefPage, sysUserPref.getPrefPage())
                .eq(SysUserPref::getUserId, sysUserPref.getUserId());
        List<SysUserPref> list = sysUserPrefService.list(wrapper);
        return Result.data(list);
    }

    @PostMapping("userPreference/modify")
    @ApiOperation("修改偏好设置")
    public Result<Map> modify(@RequestBody SysUserPref sysUserPref) {
        SysUserPref userPref = new SysUserPref();
        if (sysUserPref.getSysUserPrefId() != null) {
            if (StringUtils.isBlank(sysUserPref.getPrefValue())) {
                throw new CustomException("修改的值不能为空");
            }
            userPref.setSysUserPrefId(sysUserPref.getSysUserPrefId());
            userPref.setPrefValue(sysUserPref.getPrefValue());
            sysUserPrefService.updateById(userPref);
        } else {
            userPref.setUserId(sysUserPref.getUserId());
            userPref.setPrefType(sysUserPref.getPrefType());
            userPref.setPrefValue(sysUserPref.getPrefValue());
            userPref.setPrefPage(sysUserPref.getPrefPage());
            userPref.setDeleteFlag("0");
            sysUserPrefService.save(userPref);
        }
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("id", userPref.getSysUserPrefId());
        return Result.data(map, "修改成功");
    }

}

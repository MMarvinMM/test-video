package com.chinatower.video.modules.videopreset.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.VideoConstants;
import com.chinatower.video.api.entity.GuardDTO;
import com.chinatower.video.api.entity.VideoRequestDTO;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import com.chinatower.video.modules.videopreset.service.IGuardInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName GuardController
 * @Description 看守卫服务
 * @Author chenshaowu
 * @Date 2022/2/21
 **/
@RestController
@Slf4j
@Api(value = "看守卫服务模块",tags = "看守卫服务模块")
public class GuardControllerSDK {

    @Autowired
    private IGuardInfoService guardInfoService;

    @Autowired
    VideoCommonService videoCommonService;


    /**
     * 功能描述: 设置看守卫
     *
     * @param: guardDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/21
     * @version: 1.0
     */
    @ApiOperation(value = "设置看守卫", notes = "设置看守卫")
    @PostMapping("/video/patrol/sdk/doSetGuard")
    public Result<String>  doSetGuard(@Validated @RequestBody GuardDTO guardDTO) {
        log.info("doSetGuard" + VideoConstants.SDKREQ + guardDTO);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user == null || StringUtils.isEmpty(user.getTenantId())) {
            throw new CustomException(DeviceConstants.NO_USER_INFO);
        }
        String tenantId = guardDTO.getTenantId();
        String userCode = guardDTO.getUserCode();
        if (StringUtils.isEmpty(tenantId)) {
            guardDTO.setTenantId(user.getTenantId());
        }
        if (StringUtils.isEmpty(userCode)) {
            SysUserDTO sysUserDTO = new SysUserDTO();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            sysUserDTO.setUserName(user.getUserName());
            guardDTO.setUserCode(videoCommonService.getUserName(sysUserDTO).getUserId());
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE, SecurityUtils.getLoginUser().getUser().getTenantId());
        long guardId = guardInfoService.doSetGuard(guardDTO,headerMap);
        if(guardId == 0){
            return Result.fail("设置看守卫失败!");
        }else if(guardId == -1){
            return Result.fail("获取云台锁定权限失败，暂时无法设置看守位!");
        }else if(guardId == -2){
            return Result.fail("当前摄像机已有看守位，无法设置。");
        }else {
            return Result.success("设置看守卫成功!");
        }
    }

    /**
     * 功能描述: 6.48.	查询事件告警详情
     *
     * @param: guardDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/21
     * @version: 1.0
     */
    @ApiOperation(value = "查询事件告警详情", notes = "查询事件告警详情")
    @PostMapping("/video/patrol/sdk/getAlarmInfo")
    public Result<JSONObject> getAlarmInfo(@Validated @RequestBody VideoRequestDTO videoRequestDTO) {
        log.info("getAlarmInfo" + VideoConstants.SDKREQ + videoRequestDTO);
        return guardInfoService.getAlarmInfo(videoRequestDTO);
    }
}

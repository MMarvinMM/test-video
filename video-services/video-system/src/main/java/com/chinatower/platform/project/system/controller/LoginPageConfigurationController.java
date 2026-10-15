package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.project.system.domain.vo.PersonalLoginConfVO;
import com.chinatower.platform.project.system.service.ILoginPageConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 个性化登录页面配置（userType=0 → config_type=1 客户；userType≠0 → config_type=2 铁塔）
 *
 * @author liuxt
 */
@RestController
@Slf4j
@RequestMapping("/loginPageConfiguration")
public class LoginPageConfigurationController extends TowerBaseController {

    @Autowired
    private ILoginPageConfigurationService loginPageConfigurationService;

    /**
     * 查询登录页面配置（按当前用户 userType 决定 config_type）
     */
    @PostMapping("/queryInfo")
    public Result<PersonalLoginConfVO> queryInfo() {
        try {
            PersonalLoginConfVO config = loginPageConfigurationService.queryLoginPageInfo();
            return Result.data(config);
        } catch (Exception e) {
            log.error("查询登录页面配置异常", e);
            return Result.fail("查询失败");
        }
    }

    /**
     * 保存登录页面配置（按当前用户 userType 写入对应 config_type）
     */
    @PostMapping("/saveInfo")
    @Transactional(rollbackFor = Exception.class)
    public Result saveInfo(@RequestBody PersonalLoginConfVO personalLoginConfVO) {
        try {
            int result = loginPageConfigurationService.saveLoginPageInfo(personalLoginConfVO);
            if (result > 0) {
                return Result.success("保存成功");
            } else {
                return Result.fail("保存失败");
            }
        } catch (Exception e) {
            log.error("保存登录页面配置异常", e);
            return Result.fail("保存失败");
        }
    }

    /**
     * 恢复默认配置（按当前用户 userType 重置对应 config_type）
     */
    @PostMapping("/reset")
    @Transactional(rollbackFor = Exception.class)
    public Result reset() {
        try {
            int result = loginPageConfigurationService.resetLoginPageInfo();
            if (result > 0) {
                return Result.success("恢复默认成功");
            } else {
                return Result.fail("恢复默认失败");
            }
        } catch (Exception e) {
            log.error("恢复默认配置异常", e);
            return Result.fail("恢复默认失败");
        }
    }

    /**
     * 获取登录页面信息（白名单；有登录态按 userType 查 config_type，否则按客户配置）
     */
    @PostMapping("/getLoginPageInfo")
    public Result<PersonalLoginConfVO> getLoginPageInfo(@RequestBody PersonalLoginConfVO personalLoginConfVO) {
        try {
            PersonalLoginConfVO loginPageInfo = loginPageConfigurationService.getLoginPageInfo(personalLoginConfVO);
            if (loginPageInfo != null) {
                return Result.data(loginPageInfo);
            } else {
                return Result.fail("未找到配置信息");
            }
        } catch (Exception e) {
            log.error("获取登录页面信息异常，loginId: {}", personalLoginConfVO.getLoginId(), e);
            return Result.fail("获取失败");
        }
    }
}
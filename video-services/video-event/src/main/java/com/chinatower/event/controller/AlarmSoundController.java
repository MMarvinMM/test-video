package com.chinatower.event.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chinatower.common.core.domain.AlarmSoundDO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.AlarmSoundDTO;
import com.chinatower.event.repository.IAlarmSoundService;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-event
 * 告警声音管理
 */
@RestController
@RequestMapping("/AlarmSound")
@Api(tags = {"/AlarmSound - 告警声音管理"
})
@Slf4j
public class AlarmSoundController extends BaseController {
    @Autowired
    IAlarmSoundService iAlarmSoundService;

    /**
     * 功能描述：分页查询告警声音列表
     *
     * @param model
     * @return Result<IPage<AlarmSoundDO>>
     * @author zhen.yan
     * @date 2022/6/15 11:10
     * @version:
     */
    @DataScope(dbType = EbikeDataBaseConstant.BASE,industryCode = "industryCode")
    @ApiOperation(value = "列表", notes = "列表")
    @PostMapping("/queryAlarmSoundList")
    Result<IPage<AlarmSoundDO>> queryAlarmSoundList(@RequestBody AlarmSoundDTO model) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(model.getTenantId())) {
            model.setTenantId(loginUser.getUser().getTenantId());
            model.setIndustryCode(loginUser.getUser().getIndustryCode());
            model.setAppVerCode(loginUser.getUser().getAppVerCode());
        }
        return iAlarmSoundService.queryAlarmSoundList(model);
    }

    /**
     * 功能描述：修改告警声音
     *
     * @param model
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 11:10
     * @version:
     */
    @TowerLog(title = "修改告警声音接口", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/modifyAlarmSound")
    Result<String> modifyAlarmSound(@RequestBody AlarmSoundDO model) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        //获取操作人
        String operator = SecurityUtils.getOperator();
        model.setUpdatePerson(operator);
        model.setIndustryCode(loginUser.getUser().getIndustryCode());
        model.setAppVerCode(loginUser.getUser().getAppVerCode());
        return iAlarmSoundService.modifyAlarmSound(model);
    }

    /**
     * 功能描述：新增告警声音
     *
     * @param model
     * @return Result<AlarmSoundDO>
     * @author zhen.yan
     * @date 2022/6/15 11:10
     * @version:
     */
    @TowerLog(title = "新增告警声音接口", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/addAlarmSound")
    Result<AlarmSoundDO> addAlarmSound(@RequestBody AlarmSoundDO model) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(model.getTenantId())) {
            model.setTenantId(loginUser.getUser().getTenantId());
        }
        model.setCreatePerson(SecurityUtils.getOperator());
        model.setIndustryCode(loginUser.getUser().getIndustryCode());
        model.setAppVerCode(loginUser.getUser().getAppVerCode());
        return iAlarmSoundService.addAlarmSound(model);
    }

    /**
     * 功能描述：删除告警声音
     *
     * @param alarmSoundId 告警声音id
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 11:11
     * @version:
     */
    @TowerLog(title = "删除告警声音", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({@ApiImplicitParam(name = "alarmSoundId", value = "ID", required = true)
    })
    @GetMapping("/deleteAlarmSound")
    Result<String> deleteAlarmSound(@RequestParam Long alarmSoundId) {
        return iAlarmSoundService.deleteAlarmSound(alarmSoundId);
    }

    /**
     * 功能描述：查询单条告警声音
     *
     * @param alarmSoundId 告警声音id
     * @return Result<AlarmSoundDO>
     * @author zhen.yan
     * @date 2022/6/15 11:12
     * @version:
     */
    @ApiOperation(value = "查询单条信息", notes = "查询单条信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "alarmSoundId", value = "ID", required = true)
    })
    @GetMapping("/selectByAlarmSoundId")
    Result<AlarmSoundDO> selectByAlarmSoundId(@RequestParam Long alarmSoundId) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        return iAlarmSoundService.selectByAlarmSoundId(alarmSoundId, loginUser);
    }

    /**
     * 功能描述：启用/停用告警声音
     *
     * @param model
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 11:12
     * @version:
     */
    @TowerLog(title = "删除告警声音", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "启用/停用", notes = "启用/停用")
    @PostMapping("/updateStatusCd")
    Result<String> updateStatusCd(@RequestBody AlarmSoundDTO model) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        return iAlarmSoundService.updateStatusCd(model, loginUser);
    }

    /**
     * 功能描述：恢复默认
     *
     * @return Result<String>
     * @author zhen.yan
     * @date 2022/6/15 11:14
     * @version:
     */
    @TowerLog(title = "删除告警声音", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "恢复默认", notes = "恢复默认")
    @GetMapping("/reset")
    Result<String> reset() {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        return iAlarmSoundService.reset(loginUser);
    }

    /**
     * 功能描述：启用项
     *
     * @return Result<List<AlarmSoundDO>>
     * @author zhen.yan
     * @date 2022/6/15 11:15
     * @version:
     */
    @ApiOperation(value = "启用项", notes = "启用项")
    @GetMapping("/enableData")
    Result<List<AlarmSoundDO>> enableData() {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        return iAlarmSoundService.enableData(loginUser);
    }

    /**
     * 功能描述：客户启用项
     *
     * @return Result<AlarmSoundDO>
     * @author zhen.yan
     * @date 2022/6/15 11:15
     * @version:
     */
    @ApiOperation(value = "客户启用项", notes = "客户启用项")
    @GetMapping("/selectByTenantId")
    Result<AlarmSoundDO> selectByTenantId() {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        return iAlarmSoundService.selectByTenantId(loginUser);
    }

    /**
     * 功能描述：修改默认告警声音
     *
     * @param model
     * @return Result<String>
     * @version:
     */
    @TowerLog(title = "修改默认告警声音", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改默认告警声音", notes = "修改默认告警声音")
    @PostMapping("/updateDefault")
    Result<String> updateDefault(@RequestBody AlarmSoundDO model) {
        return iAlarmSoundService.updateDefault(model);
    }

    /**
     * 功能描述：检验名称是否重复
     *
     * @param model
     * @return Result<Boolean>
     * @author tian.hao
     * @date 2023/1/30
     * @version:
     */
    @TowerLog(title = "检验名称是否重复", businessType = BusinessType.SELECT)
    @ApiOperation(value = "检验名称是否重复", notes = "检验名称是否重复")
    @PostMapping("/checkAlarmSoundName")
    Result<Boolean> checkAlarmSoundName(@RequestBody AlarmSoundDO model) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        model.setTenantId(loginUser.getUser().getTenantId());
        model.setIndustryCode(loginUser.getUser().getIndustryCode());
        return iAlarmSoundService.checkAlarmSoundName(model);
    }

    /**
     * 功能描述：查询告警声音集合
     *
     * @param model
     * @return Result<List<AlarmSoundDO>>
     * @author tian.hao
     * @date 2023/1/30
     * @version:
     */
    @DataScope(dbType = EbikeDataBaseConstant.BASE,industryCode = "industryCode")
    @ApiOperation(value = "查询告警声音集合", notes = "查询告警声音集合")
    @PostMapping("/selectAlarmSoundList")
    Result<List<AlarmSoundDO>> selectAlarmSoundList(@RequestBody AlarmSoundDTO model) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        model.setTenantId(loginUser.getUser().getTenantId());
        model.setIndustryCode(loginUser.getUser().getIndustryCode());
        return iAlarmSoundService.selectAlarmSoundList(model);
    }

    /**
     * 功能描述：根据告警声音id查询告警声音信息
     * @param id 告警声音id
     * @return
     */
    @ApiOperation(value = "根据告警声音id查询告警声音信息", notes = "根据告警声音id查询告警声音信息")
    @GetMapping("/selectById/{id}")
    Result<AlarmSoundDO> selectById(@PathVariable Long id) {
        return iAlarmSoundService.selectById(id);
    }

    /**
     * 功能描述：查询默认告警声音集合
     *
     * @param model
     * @return Result<List<AlarmSoundDO>>
     * @author tian.hao
     * @date 2023/1/30
     * @version:
     */
    @DataScope(dbType = EbikeDataBaseConstant.BASE,industryCode = "industryCode")
    @ApiOperation(value = "查询默认告警声音集合", notes = "查询默认告警声音集合")
    @PostMapping("/selectDefaultAlarmSoundList")
    Result<List<AlarmSoundDO>> selectDefaultAlarmSoundList(@RequestBody AlarmSoundDTO model) {
        return iAlarmSoundService.selectDefaultAlarmSoundList(model);
    }
}

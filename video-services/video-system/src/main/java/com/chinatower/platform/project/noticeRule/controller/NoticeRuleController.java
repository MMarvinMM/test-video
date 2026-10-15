package com.chinatower.platform.project.noticeRule.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceTableConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.noticeRule.entity.*;
import com.chinatower.platform.project.noticeRule.service.INoticeRuleService;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.NoticeExtendVO;
import com.chinatower.system.api.model.NoticeRuleFilterResultVO;
import com.chinatower.system.api.model.NoticeRuleFilterVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * video-system
 *
 */
@Slf4j
@RestController
public class NoticeRuleController extends TowerBaseController {

    @Resource
    INoticeRuleService iNoticeRuleService;

    /**
     * 根据ID查询详细信息
     *
     * @param:
     * @return:
     * @author:
     * @date:
     * @version:
     */
    @ApiOperation(value = "根据ID查询详细信息")
    @PostMapping("/noticeRule/getInfoById")
    public Result getInfoById(@RequestBody NoticeRuleVO req) {
        log.info("根据ID查询详细信息 入参=" + JSONObject.toJSON(req));
        NoticeRuleDO noticeRuleDO = iNoticeRuleService.getById(req.getId());
        NoticeRuleVO noticeRuleVO = new NoticeRuleVO();
        BeanUtils.copyProperties(noticeRuleDO,noticeRuleVO);
        String extendInfoText = noticeRuleDO.getExtendInfoText();
        List<NoticeExtendVO> extendInfoList = JSON.parseArray(extendInfoText, NoticeExtendVO.class);
        noticeRuleVO.setExtendInfoList(extendInfoList);
        noticeRuleVO.setExtendInfoText(Constants.EMPTY_STR);
        String alarmTypeText = noticeRuleDO.getAlarmTypeText();
        List<NoticeAlarmSourceVO> alarmInfoList = JSON.parseArray(alarmTypeText, NoticeAlarmSourceVO.class);
        noticeRuleVO.setAlarmInfoList(alarmInfoList);
        noticeRuleVO.setAlarmTypeText(Constants.EMPTY_STR);
        return Result.data(noticeRuleVO);
    }


    /**
     * 根据UserId更新消息通知开关
     *
     * @param:
     * @return:
     * @author:
     * @date:
     * @version:
     */
    @ApiOperation(value = "根据UserId更新消息通知开关")
    @PostMapping("/noticeRule/updateIsNotice")
    public Result updateIsNotice(@RequestBody NoticeRuleVO req) {
        log.info("根据UserId更新消息通知开关 入参=" + JSONObject.toJSON(req));
        iNoticeRuleService.updateIsNotice(req);
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 根据登陆人查询详细信息
     *
     * @param:
     * @return:
     * @author:
     * @date:
     * @version:
     */
    @ApiOperation(value = "根据登录人查询规则列表")
    @PostMapping("/noticeRule/getListByUserId")
    public Result getListByUserId(@RequestBody NoticeRuleVO req) {
        return iNoticeRuleService.getListByUserId(req);
    }


    /**
     * 查询租户订购得告警类型
     *
     * @param:
     * @return:
     * @author:
     * @date:
     * @version:
     */
    @ApiOperation(value = "查询租户订购得告警类型(目前提供给APP前端初始化展示用)")
    @PostMapping("/noticeRule/getAlarmTypeByTenant")
    public Result getAlarmType(@RequestBody NoticeRuleVO req) {
        log.info("查询租户订购得告警类型 入参(暂时没有入参)=" + JSONObject.toJSON(req));
        return iNoticeRuleService.getAlarmType();
    }


    /**
     * 删除规则接口
     *
     * @param:
     * @return:
     * @author:
     * @date:
     * @version:
     */
    @ApiOperation(value = "批量删除规则")
    @PostMapping("/noticeRule/deleteByIds")
    public Result deleteByIds(@RequestBody List<Long> req) {
        log.info("批量删除规则 入参=" + JSONObject.toJSON(req));
        List<NoticeRuleDO> updateList = new ArrayList();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        for(Long id :req){
            Date now = new Date();
            NoticeRuleDO noticeRuleDO = new NoticeRuleDO();
            noticeRuleDO.setId(id);
            noticeRuleDO.setDeleteFlag(Constants.ONE_STR);
            noticeRuleDO.setUpdateTime(now);
            noticeRuleDO.setModifier(user.getUserName());
            updateList.add(noticeRuleDO);
        }
        iNoticeRuleService.updateBatchById(updateList);
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 创建用户通知规则
     */
    @ApiOperation(value = "创建用户通知规则")
    @PostMapping("/noticeRule/add")
    public Result add(@Validated @RequestBody NoticeRuleAddVO noticeRuleAddVO) {
        return iNoticeRuleService.add(noticeRuleAddVO);
    }

    /**
     * 修改用户通知规则
     */
    @ApiOperation(value = "修改用户通知规则")
    @PostMapping("/noticeRule/edit")
    public Result edit(@Validated @RequestBody NoticeRuleAddVO noticeRuleAddVO) {
        return iNoticeRuleService.edit(noticeRuleAddVO);
    }

    /**
     * 筛选用户通知规则 for微创
     */
    @ApiOperation(value = "筛选用户通知规则 for微创")
    @PostMapping("/noticeRule/filterUserList")
    public Result<List<NoticeRuleFilterResultVO>> filterUserList(@RequestBody List<NoticeRuleFilterVO> noticeRuleFilterVOList) {
        log.info("筛选用户通知规则入参=" + JSONObject.toJSON(noticeRuleFilterVOList));
        return Result.data(iNoticeRuleService.filterUserList(noticeRuleFilterVOList));
    }

    /**
     * 查询最大默认名
     */
    @ApiOperation(value = "查询最大默认名")
    @GetMapping("/noticeRule/getMaxRuleName")
    public Result<String> getMaxRuleName() {
        return iNoticeRuleService.getMaxRuleName();
    }
}

package com.chinatower.platform.project.system.controller;

import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.system.domain.vo.GetNoticeReqVo;
import com.chinatower.platform.project.system.domain.vo.MessageManageVO;
import com.chinatower.platform.project.system.domain.vo.SysJpushRecord;
import com.chinatower.platform.project.system.service.ISysJpushRecord;
import com.chinatower.platform.project.system.service.ISysNoticeService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * video-system
 *
 */
@Api(tags = "消息管理")
@Slf4j
@RestController
@RequestMapping("/system/messageManage")
public class MessageController extends TowerBaseController {
    @Resource
    private ISysJpushRecord iSysJpushRecord;

    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 消息置为已读
     * @Author liuxt
     */
    @ApiOperation(value = "消息置为已读")
    @PostMapping(value = "/readForApp")
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> readForApp(@RequestBody MessageManageVO vo) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();
        String flag = NullProcessUtil.nvlToString(vo.getFlag(),"");
        Integer num = Constants.ZERO;
        if(Constants.ONE_STR.equals(flag) ||  Constants.ZERO_STR.equals(flag)){

            SysJpushRecord sysJpushRecordV0 = new SysJpushRecord();
            sysJpushRecordV0.setUpdateBy(user.getUserId());

            HttpServletRequest request = RequestUtils.getRequest();
            String clientVersion = request.getHeader("client_version");
            log.info("获取请求头client_version:{}", clientVersion);
            if (StringUtils.isNotEmpty(clientVersion)) {
                String industryCode = user.getIndustryCode();
                if (StringUtils.isEmpty(industryCode)) {
                    industryCode = com.chinatower.platform.project.system.constants.Constants.TOWER_INDUSTRY_CODE;
                }
                sysJpushRecordV0.setAlias(user.getUserId() + "_" + industryCode);
            } else {
                sysJpushRecordV0.setAlias(user.getUserId());
            }
            sysJpushRecordV0.setId(vo.getId());
            log.info("查询极光记录入参:{}", sysJpushRecordV0);
            num = iSysJpushRecord.updateJpushRecord(sysJpushRecordV0);
        }
        if(Constants.TWO_STR.equals(flag)){
            num = noticeService.insertNoticeRead(vo.getNoticeId(),currentUser.getUser().getUserId());
        }
        if(Constants.ZERO_STR.equals(flag)){
            GetNoticeReqVo getNoticeReqVoVO = new GetNoticeReqVo();
            getNoticeReqVoVO.setUserId(currentUser.getUser().getUserId());
            getNoticeReqVoVO.setDeptId(currentUser.getUser().getDeptId());
            num = noticeService.insertNoticeReadBatch(getNoticeReqVoVO);
        }
        return Result.data(num);

    }


    /**
     * 获取未读消息数量
     * @Author liuxt
     */
    @ApiOperation(value = "获取未读消息数量",notes = "获取未读消息数量")
    @PostMapping("/getUnReadCount")
    public Result<Integer> getUnReadCount(@RequestBody MessageManageVO vo){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        Integer num = Constants.ZERO;
        SysJpushRecord sysJpushRecordV0 = new SysJpushRecord();

        HttpServletRequest request = RequestUtils.getRequest();
        String clientVersion = request.getHeader("client_version");
        log.info("获取请求头client_version:{}", clientVersion);
        if (StringUtils.isNotEmpty(clientVersion)) {
            //极光别名修改为 alias=userId_industryCode
            String industryCode = currentUser.getUser().getIndustryCode();
            if (StringUtils.isEmpty(industryCode)) {
                industryCode = com.chinatower.platform.project.system.constants.Constants.TOWER_INDUSTRY_CODE;
            }
            sysJpushRecordV0.setAlias(currentUser.getUser().getUserId() + "_" + industryCode);
        } else {
            sysJpushRecordV0.setAlias(currentUser.getUser().getUserId());
        }
        log.info("查询极光记录入参:{}", sysJpushRecordV0);
        num = iSysJpushRecord.getJpushRecordlistCount(sysJpushRecordV0);
        log.info("极光推送未读消息数量："+num);
        GetNoticeReqVo getNoticeReqVoVO = new GetNoticeReqVo();
        getNoticeReqVoVO.setUserId(currentUser.getUser().getUserId());
        getNoticeReqVoVO.setDeptId(currentUser.getUser().getDeptId());
        getNoticeReqVoVO.setIfRead(Constants.ZERO);
        num = num + noticeService.selectAppNoticeByUserCount(getNoticeReqVoVO);
        log.info("全部未读消息数量："+num);
        return Result.data(num);
    }



}

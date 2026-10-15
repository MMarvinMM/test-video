package com.chinatower.message.station.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.MsgStationReq;
import com.chinatower.message.station.service.MsgStationService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * video-message
 * @ClassName MsgStationController
 * @Description 站内消息管理
 * @Author wjd
 * @Date 2022/1/19 16:18
 */
@Api(value = "站内消息接口-Api", tags = "站内消息接口")
@Slf4j
@RestController
@RequestMapping(value = "/msg/station")
public class MsgStationController extends BaseController {

    @Autowired
    private MsgStationService msgStationService;
    private static final String exceptionLog ="业务处理异常";

    /**
     * 查询消息通知信息
     * @param str
     * @return
     */
    @ApiOperation(value = "查询消息通知信息", notes = "查询消息通知信息")
    @PostMapping(value = "/getMessageInfo")
    public TableDataInfo getMessageInfo(@RequestBody String str) {
        try {
            MsgStationReq msgStationReq = JSONObject.parseObject(str, MsgStationReq.class);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();
            //根据token里的登录账号，查询用户id（铁塔账号切换客户视角，用户id不会变）
            SysUser user1 = msgStationService.selectUserOnlyByUserName(user.getUserName());
            log.info("登录账号:{},用户id:{},行业编码:{}", SecurityUtils.getOperator(),user1.getUserId(),user.getIndustryCode());

            msgStationReq.setUserId(user1.getUserId());
            //租户账号
            if(VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(user.getUserType())){
                msgStationReq.setIndustryCode(user.getIndustryCode());
            }
            log.info("查询消息入参:{}", JSONObject.toJSON(msgStationReq));
            PageHelper.startPage(msgStationReq.getPageNum(), msgStationReq.getPageSize());
            List<Map<String, Object>> list = msgStationService.getMessageInfo(msgStationReq);
            //查询消息信息
            return getDataTable(list);
        }catch (Exception e){
            log.info("查询消息通知信息异常！",e);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(exceptionLog);
            return rspData;
        }
    }


    /**
     * 一键清空通知信息
     * @return
     */
    @ApiOperation(value = "一键清空通知信息", notes = "一键清空通知信息")
    @GetMapping(value = "/oneClickClear")
    public Result oneClickClear() {
        //查询消息信息
        return msgStationService.oneClickClear();
    }

    /**
     * 更新消息
     * @param uniId
     * @return
     */
    @ApiOperation(value = "一键清空通知信息", notes = "一键清空通知信息")
    @PostMapping(value = "/updateMessage")
    public Result udpateMessage(@RequestParam("uniId") String uniId){
        return msgStationService.updateMessage(uniId);
    }



}

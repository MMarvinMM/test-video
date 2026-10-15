package com.chinatower.platform.project.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.compenont.MessageResultCode;
import com.chinatower.platform.entity.request.MsgStationReq;
import com.chinatower.platform.project.service.MsgStationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * video-msg
 * @ClassName MsgStationController
 * @Description 站内消息管理
 * @Author wjd
 * @Date 2022/1/19 16:18
 */
@Api(tags = "站内消息管理")
@Slf4j
@RestController
@RequestMapping(value = "/msg/station")
public class MsgStationController extends BaseController {

    @Autowired
    private MsgStationService msgStationService;


    /**
     * 查询消息通知信息
     * @param msgStationReq
     * @return
     */
    @ApiOperation(value = "查询消息通知信息", notes = "查询消息通知信息")
    @PostMapping(value = "/getMessageInfo")
    public TableDataInfo getMessageInfo( MsgStationReq msgStationReq) {
        startPage();
        List<Map<String,Object>> list = msgStationService.getMessageInfo(msgStationReq);
        //查询消息信息
        return getDataTable(list);
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



}

package com.chinatower.bdm.modules.unionTargetJs.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.UnionTargetJsParam;
import com.chinatower.bdm.modules.unionTargetJs.mapper.UnionTargetJsMapper;
import com.chinatower.bdm.modules.unionTargetJs.service.UnionTargetJsService;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * video-bdm
 * 资源类型
 *
 * @author zhaol
 */
@Api(tags = "雷达")
@RestController
@Slf4j
@RequestMapping("/unionTargetJs")
public class UnionTargetJsController extends TowerBaseController {
    @Autowired
    UnionTargetJsService unionTargetJsService;
    
    @Autowired
    UnionTargetJsMapper unionTargetJsMapper;


    /**
     * 光电联动信令下发
     */
    @ApiOperation(value = "光电联动信令下发")
    @PostMapping("/targetFollow")
    public Result targetFollow(@RequestBody UnionTargetJsParam unionTargetJsParam) {

        //查询摄像机的接入节点--取一个
        String deviceCode = unionTargetJsParam.getDeviceList().get(0).getDeviceCode();
        String accessNode = unionTargetJsMapper.queryDeviceAccessNodeByCode(deviceCode);
        unionTargetJsParam.setAcceptNode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
        unionTargetJsParam.setAccessNode(accessNode);
        String str = JSON.toJSONString(unionTargetJsParam);
        log.info("下发报文:{}",str);
        Result res = unionTargetJsService.targetFollow(unionTargetJsParam);
        return res;
    }




}

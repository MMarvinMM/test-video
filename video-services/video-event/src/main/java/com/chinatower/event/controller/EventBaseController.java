package com.chinatower.event.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.dto.leadertree.LeaderByTenantIdTreeReq;
import com.chinatower.event.entity.vo.SysUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.entity.dto.SysUserDTO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.vo.UsersVO;

import lombok.extern.slf4j.Slf4j;

/**
 * video-event
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: DeviceController.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/3 17:03
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@RequestMapping(value = "/video/base")
@RestController
@Slf4j
public class EventBaseController extends TowerBaseController {

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private ISysService sysService;

    /**
     * 功能描述：查询领导批示用户数据
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:36
     * @version:
     */
    @PostMapping("/getLeaderByTenantId")
    public Result<List<SysUserVO>> getLeaderByTenantId(@RequestBody SysUserDTO dto) {
        List<SysUserVO> sysUserVOList = deviceRepository.getLeaderByTenantId(dto);
        return Result.data(sysUserVOList);
    }

    /**
     * 功能描述：查询领导批示用户数据(根据组织节点展示树状数据)
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:36
     * @version:
     */
    @PostMapping("/getLeaderByTenantIdShowTree")
    @EventObsEncrypt
    public Result<List<SysInfo>> getLeaderByTenantIdShowTree(@RequestBody LeaderByTenantIdTreeReq dto) {
        log.info("EventBaseController-getLeaderByTenantIdShowTree-req: {}", JSON.toJSONString(dto));
        List<SysInfo> sysUserVOList = deviceRepository.getLeaderByTenantIdShowTree(dto);
        log.info("EventBaseController-getLeaderByTenantIdShowTree-resp: {}", JSON.toJSONString(sysUserVOList));
        return Result.data(sysUserVOList);
    }

    /**
     * 功能描述：根据条件查询用户信息
     *
     * @param usersVO
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:36
     * @version:
     */
    @PostMapping("/queryTreeChoose")
    public Result<List<UsersVO>> queryTreeChoose(@RequestBody UsersVO usersVO) {
        List<UsersVO> resultList = sysService.queryTreeChoose(usersVO);
        return Result.data(resultList);
    }
}

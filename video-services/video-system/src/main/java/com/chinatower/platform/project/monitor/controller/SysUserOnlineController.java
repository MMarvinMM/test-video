package com.chinatower.platform.project.monitor.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.utils.StringUtils;

import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.monitor.domain.SysUserOnline;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.service.ISysUserOnlineService;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * video-system
 * 在线用户监控
 *
 * @author chinatower
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends TowerBaseController {
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private ISysUserService sysUserService;


//    @GetMapping("/list")
//    public TableDataInfo list(String ipaddr, String userName) {
//        Collection<String> keys = RedisUtil.keys(Constants.LOGIN_TOKEN_KEY + "*", RedisDBType.PRIMARY);
//        List<SysUserOnline> userOnlineList = new ArrayList<>();
//        for (String key : keys) {
//            LoginUser user = JSON.parseObject(JSON.parse(JSON.toJSONString(RedisUtil.get(key, RedisDBType.PRIMARY))).toString(),LoginUser.class);
//            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
//                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
//                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
//                }
//            } else if (StringUtils.isNotEmpty(ipaddr)) {
//                if (StringUtils.equals(ipaddr, user.getIpaddr())) {
//                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
//                }
//            } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
//                if (StringUtils.equals(userName, user.getUsername())) {
//                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
//                }
//            } else {
//                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
//            }
//        }
//        Collections.reverse(userOnlineList);
//        userOnlineList.removeAll(Collections.singleton(null));
//        return getDataTable(userOnlineList);
//    }

    /**
     * 查询在线用户，RedisUtil.keys不让使用，故暂时只支持精确查询
     * @param ipaddr
     * @param userName
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(String ipaddr, String userName) {
        if(StringUtils.isEmpty(userName)) {
            return getDataTable(new ArrayList<>());
        }
        SysUser sysUser = sysUserService.selectUserByUserName(userName);
        if(ObjectUtil.isEmpty(sysUser)) {
            return getDataTable(new ArrayList<>());
        }
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        String key = Constants.LOGIN_TOKEN_KEY + "web_manage:" + sysUser.getUserId();
        LoginUser user = JSON.parseObject(RedisUtil.get(key, RedisDBType.PRIMARY),LoginUser.class);
        if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
            if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
                userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
            }
        } else if (StringUtils.isNotEmpty(ipaddr)) {
            if (StringUtils.equals(ipaddr, user.getIpaddr())) {
                userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
            }
        } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
            if (StringUtils.equals(userName, user.getUsername())) {
                userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
            }
        } else {
            userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }
    /**
     * 强退用户
     */
    @TowerLog(title = "在线用户", businessType = BusinessType.DELETE)
    @GetMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId)
    {
        RedisUtil.delete(Constants.LOGIN_TOKEN_KEY + tokenId, RedisDBType.PRIMARY);
        return AjaxResult.success();
    }
}

package com.chinatower.platform.project.system.service;

import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.domain.WhiteIp;
import com.chinatower.platform.project.system.domain.vo.WhiteIpVO;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @program: video-platform
 * @ClassName: IWhiteIpService
 * @description: 用户登录IP白名单服务类接口
 * @author: fengty
 * @create: 2024-04-29 10:40
 */
public interface IWhiteIpService {
    Integer saveUserIps(List<WhiteIp> whiteIpList,  SysUser user) throws Exception;

    Integer logicDeleteIps(List<WhiteIp> whiteIpList, SysUser user) throws Exception;

    Integer updateIps(List<WhiteIp> whiteIpList, SysUser user) throws Exception;

    void opreatorWhiteIps(WhiteIpVO whiteIpVO) throws Exception;

    Page<WhiteIp> pageWhiteIps(WhiteIp whiteIp);
}

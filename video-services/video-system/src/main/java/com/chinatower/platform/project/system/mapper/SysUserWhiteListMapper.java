package com.chinatower.platform.project.system.mapper;

import com.chinatower.system.api.model.SysUserWhiteListVO;

import java.util.List;

/**
 * 登录账号验证白名单
 */
public interface SysUserWhiteListMapper {

    SysUserWhiteListVO getUser(String userName);

    String getTenantName(String tenantId);

    int checkUserId(String userName);

    int add(SysUserWhiteListVO vo);

    int delete(List<String> list,String userId);

    List<SysUserWhiteListVO> list(SysUserWhiteListVO vo);

    List<String> getUserList(List<String> list);

    List<String> getTenantId(String platformCode);
}

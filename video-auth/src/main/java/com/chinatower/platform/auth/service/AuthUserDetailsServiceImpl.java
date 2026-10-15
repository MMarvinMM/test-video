package com.chinatower.platform.auth.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.cascade.api.CascadeConfigAPI;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.dto.IndustryReqModel;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.manage.api.IIndustryService;
import com.chinatower.platform.auth.common.constant.MessageConstant;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.auth.project.mapper.SysUserMapper;
import com.chinatower.platform.auth.project.service.ISysUserService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.security.auth.login.AccountExpiredException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * UserDetailsServices
 *
 * @author bruce
 * @date 2021/11/16
 */
@Service
@Slf4j
public class AuthUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ISysUserService userService;
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private FileUrlEncryptUtil obsFileUrlEncryptDeal;

    @Autowired
    private IIndustryService iIndustryService;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private CascadeConfigAPI cascadeConfigAPI;

    /**
     * 根据用户名查询用户信息
     *
     * @param usernameInfos 用户名
     * @return
     */
    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String usernameInfos) {
        String[] args = usernameInfos.split(OauthConstants.MAGIC_CHAR);
        //1、校验参数，如果少于两个说明参数拼接错误
        if (args.length < 2) {
            throw new AuthenticationServiceException(MessageConstant.SERVER_PARAM_ERROR);
        }
        UserDetails securityUser = null;
        //2、根据用户登录类型获取用户信息
        if (args[0].endsWith(OauthConstants.USER_LOGIN_TYPE_MANAGE)) {//后台管理登录的一套用户
            securityUser = prepareManageUser(args[1]);
        } else {//其他登录的用户 比如 可能是C端一套用户 或者是腾讯等第三方登录
            securityUser = prepareAppUser(args[1]);
        }
        if (null == securityUser) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        if (!securityUser.isEnabled()) {
            throw new DisabledException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.CREDENTIALS_EXPIRED);
        }
        return securityUser;
    }

    /**
     * 准备后台管理用户用户信息
     */
    private LoginUser prepareManageUser(String username) {
        log.info("---------后台管理系统用户登录-prepareManageUser：{}", username);
        //1.查询用户信息
        SysUser user = null;
        try {
            try {
                user = userService.selectUserWithPwdByUserName(username, "");
            } catch (UsernameNotFoundException e1) {
                log.info("进入跨节点切换客户场景");
                //表里没查到用户，则查redis固定key是否有值（兼容铁塔账号切换下级节点客户场景）
                String towerUserCacheKey = CacheConstants.TOWER_USER + "web_manage:" + username;
                String towerUserCacheStr = RedisUtil.get(towerUserCacheKey, RedisDBType.PRIMARY);
                if (StrUtil.isNotBlank(towerUserCacheStr)) {
                    //铁塔用户信息
                    LoginUser switchUser = JSON.parseObject(towerUserCacheStr, LoginUser.class);
                    user = switchUser.getUser();
                    return new LoginUser(user, switchUser.getPermissions());
                }else{
                    throw e1;
                }
            }

            if (null != user) {
                if (user.getDept() == null) {
                    //阻止客户才开通未订购的用户登录
                    log.error("账号数据异常->{}无组织id", username);
                    throw new AuthenticationServiceException("账号数据异常");
                }

                //查询本级平台编码
                String platformCode = null;
                String platformLevel = null;
                String platformName = null;
                try {
                    AjaxResult ajaxResult = systemFeignClient.getConfValue("platform_code");
                    if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
                        SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                        if(!ObjectUtils.isEmpty(sysConfig)){
                            platformCode = sysConfig.getConfigValue();
                            if(!StringUtils.isEmpty(platformCode)){
                                //查询平台级别
                                List<String> list = new ArrayList<>();
                                list.add(platformCode);
                                Result<List<PlatformDataConfigVO>> result = cascadeConfigAPI.getInfo(list);
                                if(ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                                    PlatformDataConfigVO platformDataConfigVO = result.getData().get(Constants.ZERO_NUM);
                                    platformLevel = platformDataConfigVO.getPlatformLevel();
                                    platformName = platformDataConfigVO.getPlatformName();
                                }
                            }
                        }
                    }
                }catch (Exception e1){
                    log.error("登录查询平台编码异常=",e1);
                }
                user.setPlatformCode(platformCode);
                user.setPlatformLevel(platformLevel);
                user.setPlatformName(platformName);

                //2.查询权限
                List<String> permissionsList = null;
                //3.查询行业版本信息  --只查租户的
                if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(user.getUserType())) {
                    List<SysIndustryViewRel> list = userMapper.selectUserAppVerCodesByDeptId(user.getDeptId(),platformCode);
                    if (list != null) {
                        try {
                            Set<String> collect = list.stream().filter(e -> !StringUtils.isEmpty(e.getImgUrl())).map(SysIndustryViewRel::getImgUrl).collect(Collectors.toSet());
                            Map<String, String> urlMap = obsFileUrlEncryptDeal.dealObsFileUrlBatch(new ArrayList<String>(collect));
                            list.forEach(r -> {
                                if (!StringUtils.isEmpty(r.getImgUrl())) {
                                    r.setImgUrl(urlMap.get(r.getImgUrl()));
                                }
                            });
                        } catch (Exception e) {
                            log.error("头像信息获取失败");
                        }
                    }
                    //4.这里处理 租户用户如果是单行业版本的时候，先给选择行业和选择版本字段赋值。
                    if (list != null && list.size() == 1) {

                        user.setChooseTradeId(list.get(0).getIndustryCode());
                        user.setChooseAppVerCode(list.get(0).getAppVerCode());

                        user.setIndustryCode(list.get(0).getIndustryCode());
                        user.setAppVerCode(list.get(0).getAppVerCode());
                        try {
                            IndustryReqModel industryReqModel = new IndustryReqModel();
                            industryReqModel.setVersion(user.getAppVerCode());
                            industryReqModel.setIndustryCode(user.getIndustryCode());
                            permissionsList = iIndustryService.getAuthUrlList(industryReqModel).getData();
                        } catch (Exception e) {
                            log.error("根据行业编码、版本号获取鉴权url获取失败", e);
                        }

                        //查询租户对应的区域树
                        List<TreeSelect> areaTree = userService.selectAreaTree(user.getDeptId());
                        if (areaTree != null) {
                            user.setAreaTree(areaTree);
                        }
                    }
                } else {
                    try {
                        IndustryReqModel industryReqModel = new IndustryReqModel();
                        String towerIndustryCode = "999999";
                        String towerAppVersion = "1.0";
                        industryReqModel.setVersion(towerAppVersion);
                        industryReqModel.setIndustryCode(towerIndustryCode);
                        user.setChooseTradeId(towerIndustryCode);
                        user.setChooseAppVerCode(towerAppVersion);

                        user.setIndustryCode(towerIndustryCode);
                        user.setAppVerCode(towerAppVersion);

                        permissionsList = iIndustryService.getAuthUrlList(industryReqModel).getData();
                    } catch (Exception e) {
                        log.error("根据行业编码、版本号获取鉴权url获取失败", e);
                    }
                }
                long s = System.currentTimeMillis();
                Set<String> permissions = userService.getMenuPermission(user);
                long e = System.currentTimeMillis();
                log.info("登录->userService.getMenuPermission耗时：{}ms", e - s);
                if (permissionsList != null) {
                    permissions.addAll(permissionsList);
                }
                return new LoginUser(user, permissions);
            }
        } catch (Exception e) {
            log.error("登录异常：" + username, e);
        }

        log.warn("---------未找到该用户-prepareManageUser：{}", username);
        return null;
    }

    /**
     * 准备其他用户信息
     */
    private LoginUser prepareAppUser(String username) {
        log.info("---------其他用户登录：{}", username);
        return null;
    }


}

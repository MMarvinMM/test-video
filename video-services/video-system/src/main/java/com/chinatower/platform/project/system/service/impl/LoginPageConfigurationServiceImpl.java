package com.chinatower.platform.project.system.service.impl;


import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.entity.TenantMangeResultVO;
import com.chinatower.platform.project.custom.mapper.CustomManageMapper;
import com.chinatower.platform.project.system.domain.vo.PersonalLoginConfVO;
import com.chinatower.platform.project.system.mapper.PersonalLoginConfMapper;
import com.chinatower.platform.project.system.service.ILoginPageConfigurationService;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个性化登录页面配置业务层处理
 *
 * @author chinatower
 */
@Slf4j
@Service
public class LoginPageConfigurationServiceImpl implements ILoginPageConfigurationService {

    /** vm_personal_login_conf_t.config_type：1-客户配置 */
    private static final int CONFIG_TYPE_CUSTOMER = 1;

    /** vm_personal_login_conf_t.config_type：2-铁塔配置 */
    private static final int CONFIG_TYPE_TOWER = 2;

    @Autowired
    private PersonalLoginConfMapper personalLoginConfMapper;

    @Autowired
    private ISysConfigService configService;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    CustomManageMapper customManageMapper;


    @Override
    public PersonalLoginConfVO queryLoginPageInfo() {
        PersonalLoginConfVO returnVO = new PersonalLoginConfVO();
        try {
            // 从缓存获取tenantId
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();
            String tenantId = user.getTenantId();

            int configType = resolveConfigTypeFromUser(user);
            // 查询个性化配置（userType=0 → config_type=1，否则 config_type=2）
            PersonalLoginConfVO queryVO = new PersonalLoginConfVO();
            queryVO.setTenantId(tenantId);
            queryVO.setConfigType(configType);
            List<PersonalLoginConfVO> confList = personalLoginConfMapper.selectPersonalLoginConf(queryVO);
            if (!CollectionUtils.isEmpty(confList)) {
                // 有配置，使用个性化配置
                returnVO = confList.get(0);
            } else {
                // 无配置，使用默认配置
                returnVO = getDefaultConfig();
            }
            dealObsFileUrlCommon(returnVO);
            // 批量加密返回前端
            return returnVO;
        } catch (Exception e) {
            log.error("查询登录页面配置失败", e);
        }
        return returnVO;
    }


    /**
     * 使用加密映射更新配置对象的URL
     */
    private void updateUrlsWithEncryption(PersonalLoginConfVO confVO, Map<String, String> encryptedMap) {
        if (encryptedMap.isEmpty()) {
            return;
        }
        // 更新systemLogoUrl
        if (!StringUtils.isEmpty(confVO.getSystemLogoUrl()) &&
                encryptedMap.containsKey(confVO.getSystemLogoUrl())) {
            confVO.setSystemLogoUrl(encryptedMap.get(confVO.getSystemLogoUrl()));
        }
        // 更新backgroundImageUrl
        if (!StringUtils.isEmpty(confVO.getBackgroundImageUrl())) {
            List<String> encryptedBgUrls = new ArrayList<>();
            String[] bgUrls = confVO.getBackgroundImageUrl().split(",");
            for (String bgUrl : bgUrls) {
                String trimmedUrl = bgUrl.trim();
                if (!StringUtils.isEmpty(trimmedUrl) && encryptedMap.containsKey(trimmedUrl)) {
                    encryptedBgUrls.add(encryptedMap.get(trimmedUrl));
                } else {
                    encryptedBgUrls.add(trimmedUrl);
                }
            }
            confVO.setBackgroundImageUrl(String.join(",", encryptedBgUrls));
        }
    }

    @Override
    public int saveLoginPageInfo(PersonalLoginConfVO personalLoginConfVO) {
        try {
            // 从缓存获取tenantId
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();
            String tenantId = user.getTenantId();
            int configType = resolveConfigTypeFromUser(user);
            // 逻辑删除同租户、同 config_type 的旧配置
            personalLoginConfMapper.logicDeleteByTenantId(tenantId, configType);
            // 设置基本信息
            personalLoginConfVO.setTenantId(tenantId);
            personalLoginConfVO.setConfigType(configType);
            personalLoginConfVO.setCreator(SecurityUtils.getOperator());
            // 插入新配置
            return personalLoginConfMapper.insertPersonalLoginConf(personalLoginConfVO);
        } catch (Exception e) {
            log.error("保存登录页面配置失败", e);
            return 0;
        }
    }

    @Override
    public int resetLoginPageInfo() {
        try {
            // 从缓存获取tenantId
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser user = loginUser.getUser();
            String tenantId = user.getTenantId();
            int configType = resolveConfigTypeFromUser(user);
            // 查询默认配置
            PersonalLoginConfVO defaultConfig = getDefaultConfig();
            personalLoginConfMapper.logicDeleteByTenantId(tenantId, configType);
            // 插入默认配置
            PersonalLoginConfVO confVO = new PersonalLoginConfVO();
            confVO.setTenantId(tenantId);
            confVO.setConfigType(configType);
            confVO.setTitle(defaultConfig.getTitle());
            confVO.setSystemLogoUrl(defaultConfig.getSystemLogoUrl());
            confVO.setBackgroundImageUrl(defaultConfig.getBackgroundImageUrl());
            confVO.setCreator(SecurityUtils.getOperator());
            return personalLoginConfMapper.insertPersonalLoginConf(confVO);
        } catch (Exception e) {
            log.error("恢复默认配置失败", e);
            return 0;
        }
    }

    @Override
    public PersonalLoginConfVO getLoginPageInfo(PersonalLoginConfVO personalLoginConfVO) {
        if (personalLoginConfVO == null) {
            return null;
        }
        String loginId = personalLoginConfVO.getLoginId();
        try {
            // 无 loginId：直接查铁塔个性化配置 config_type=2
            if (!StringUtils.hasText(loginId)) {

                PersonalLoginConfVO towerConf = personalLoginConfMapper.selectByTenantId(null, CONFIG_TYPE_TOWER);
                if (towerConf != null) {
                    dealObsFileUrlCommon(towerConf);
                }
                return towerConf;
            }
            // 解密 loginId（浩鲸的客户 id）→ tenantId
            String tenantId = AESUtil.decrypt(loginId);
            if (StringUtils.isEmpty(tenantId)) {
                log.error("解密loginId失败: {}", loginId);
                return null;
            }
            TenantMangeResultVO tenantInfo = customManageMapper.selectSysTenantInfoByTenantId(tenantId);
            PersonalLoginConfVO returnPersonalLoginConfVO = new PersonalLoginConfVO();
            if (tenantInfo != null && Constants.ONE.equals(tenantInfo.getLoginType())) {
                // 有 loginId 且开启个性化：查客户配置 config_type=1
                returnPersonalLoginConfVO = personalLoginConfMapper.selectByTenantId(tenantId, CONFIG_TYPE_CUSTOMER);
                if (returnPersonalLoginConfVO != null) {
                    dealObsFileUrlCommon(returnPersonalLoginConfVO);
                }
            }
            return returnPersonalLoginConfVO;
        } catch (Exception e) {
            log.error("获取登录页面信息失败，loginId: {}", loginId, e);
            return null;
        }
    }


    /**
     * userType 为 0 → config_type=1（客户），否则 config_type=2（铁塔）
     */
    private static int resolveConfigTypeFromUser(SysUser user) {
        if (user == null || !StringUtils.hasText(user.getUserType())) {
            return CONFIG_TYPE_CUSTOMER;
        }
        return Constants.ZERO.equals(user.getUserType().trim()) ? CONFIG_TYPE_TOWER : CONFIG_TYPE_CUSTOMER;
    }

    /**
     * 获取默认配置
     */
    private PersonalLoginConfVO getDefaultConfig() {
        PersonalLoginConfVO defaultConfig = new PersonalLoginConfVO();
        try {
            // 从参数配置获取默认值
            String defaultTitle = configService.selectConfigValueByKey("systemLoginTitle");
            String defaultLogoUrl = configService.selectConfigValueByKey("systemLoginLogoUrl");
            String defaultBgImageUrl = configService.selectConfigValueByKey("systemLoginBackgroundUrl");
            defaultConfig.setTitle(defaultTitle);
            defaultConfig.setSystemLogoUrl(defaultLogoUrl);
            defaultConfig.setBackgroundImageUrl(defaultBgImageUrl);
        } catch (Exception e) {
            log.error("获取默认配置失败", e);
        }
        return defaultConfig;
    }


    public void dealObsFileUrlCommon(PersonalLoginConfVO returnVO) {
        // 批量加密返回前端
        List<String> urlList = new ArrayList<>();
        // 添加systemLogoUrl到列表
        if (!StringUtils.isEmpty(returnVO.getSystemLogoUrl())) {
            urlList.add(returnVO.getSystemLogoUrl());
        }
        // 添加backgroundImageUrl到列表（多个URL用逗号分割）
        if (!StringUtils.isEmpty(returnVO.getBackgroundImageUrl())) {
            String[] bgUrls = returnVO.getBackgroundImageUrl().split(Constants.COMMA);
            for (String bgUrl : bgUrls) {
                if (!StringUtils.isEmpty(bgUrl.trim())) {
                    urlList.add(bgUrl.trim());
                }
            }
        }
        // 只调一次批量加密方法
        if (!urlList.isEmpty()) {
            Map<String, String> encryptedMap = fileUrlEncryptUtil.dealObsFileUrlBatch(urlList);
            updateUrlsWithEncryption(returnVO, encryptedMap);
        }
    }

}
package com.chinatower.platform.project.system.service;

import com.chinatower.platform.project.system.domain.vo.PersonalLoginConfVO;

import java.util.Map;

/**
 * 个性化登录页面配置业务层
 *
 * @author chinatower
 */
public interface ILoginPageConfigurationService {

    /**
     * 查询个性化登录页面配置
     *
     * @return 配置信息
     */
    PersonalLoginConfVO queryLoginPageInfo();

    /**
     * 保存个性化登录页面配置
     *
     * @param personalLoginConfVO 配置信息
     * @return 保存结果
     */
    int saveLoginPageInfo(PersonalLoginConfVO personalLoginConfVO);

    /**
     * 恢复默认配置
     *
     * @return 恢复结果
     */
    int resetLoginPageInfo();

    /**
     * 获取登录页面信息（白名单接口）
     *
     * @param personalLoginConfVO
     * @return 登录页面配置信息
     */
    PersonalLoginConfVO getLoginPageInfo(PersonalLoginConfVO personalLoginConfVO);
}
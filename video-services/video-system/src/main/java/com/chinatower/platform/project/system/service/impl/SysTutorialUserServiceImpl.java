package com.chinatower.platform.project.system.service.impl;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.domain.SysTutorialUser;
import com.chinatower.platform.project.system.domain.vo.TutorialIndustryVO;
import com.chinatower.platform.project.system.mapper.SysTutorialUserMapper;
import com.chinatower.platform.project.system.service.ISysTutorialUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.content.SysConstants;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @description 用户教程
 * @author luo_ji
 * @date 2022-12-15
 */
@Service
public class SysTutorialUserServiceImpl implements ISysTutorialUserService {

    @Resource
    private SysTutorialUserMapper sysTutorialUserMapper;

    @Override
    public Integer insert(SysTutorialUser sysTutorialUser) {
        sysTutorialUser.setCreator(SecurityUtils.getOperator());
        sysTutorialUser.setCreateTime(DateUtils.getNowDate());
        return sysTutorialUserMapper.insert(sysTutorialUser);
    }

    @Override
    public int delete(Long tutorialId) {
        return sysTutorialUserMapper.delete(tutorialId);
    }

    @Override
    public int update(SysTutorialUser sysTutorialUser) {
        sysTutorialUser.setUpdateBy(SecurityUtils.getOperator());
        sysTutorialUser.setUpdateTime(DateUtils.getNowDate());
        return sysTutorialUserMapper.update(sysTutorialUser);
    }

    @Override
    public SysTutorialUser load(Long tutorialId) {
        SysTutorialUser vo = sysTutorialUserMapper.load(tutorialId);
        dealIndustryInfo(Arrays.asList(vo));
        return vo;
    }

    @Override
    public List<SysTutorialUser> pageList(SysTutorialUser sysTutorialUser) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String userType = currentUser.getUser().getUserType();
        //如果登录人是客户
        if (userType.equals(SysConstants.USET_TYPE_2)) {
            sysTutorialUser.setTenantIsLook(DeviceConstants.ONE_NUM);
            sysTutorialUser.setIndustryCode(currentUser.getUser().getIndustryCode());
        }else{//登录人是铁塔 传的行业编码带逗号
            if(!StringUtils.isEmpty(sysTutorialUser.getIndustryCode())){
                List<String> industryCodeList = Arrays.asList(sysTutorialUser.getIndustryCode().split(Constants.COMMA));
                sysTutorialUser.setIndustryCodeList(industryCodeList);
                sysTutorialUser.setIndustryCode(null);
            }
        }
        List<SysTutorialUser> list = sysTutorialUserMapper.pageList(sysTutorialUser);
        if(!CollectionUtils.isEmpty(list) && userType.equals(SysConstants.USET_TYPE_0)){
            dealIndustryInfo(list);
        }
        return list;
    }


    public void dealIndustryInfo(List<SysTutorialUser> list) {
        TutorialIndustryVO tutorialIndustryVO = new TutorialIndustryVO();
        List<TutorialIndustryVO> industryList = sysTutorialUserMapper.getIndustryList(tutorialIndustryVO);
        for (SysTutorialUser item : list) {
            String industryCode = item.getIndustryCode();
            if(!StringUtils.isEmpty(industryCode)){
                String industryName = "";
                String returnIndustryCode = "";
                List<String> industryCodelist = Arrays.asList(industryCode.split(Constants.COMMA));
                for (String code : industryCodelist) {
                    for (TutorialIndustryVO industry : industryList) {
                        if(code.equals(industry.getIndustryCode())){
                            returnIndustryCode += industry.getIndustryCode() + com.chinatower.platform.constants.Constants.COMMA;
                            industryName += industry.getIndustryName() + Constants.COMMA_CH;
                        }
                    }
                }
                if(!StringUtils.isEmpty(returnIndustryCode)){
                    returnIndustryCode = returnIndustryCode.substring(com.chinatower.platform.constants.Constants.ZERO_NUM,returnIndustryCode.length()- com.chinatower.platform.constants.Constants.ONE_NUM);
                }
                if(!StringUtils.isEmpty(industryName)){
                    industryName = industryName.substring(Constants.ZERO_NUM,industryName.length()-Constants.ONE_NUM);
                }
                item.setIndustryCode(returnIndustryCode);
                item.setIndustryName(industryName);
            }
        }
    }

    @Override
    public String checkRepeat(SysTutorialUser sysTutorialUser) {
        return sysTutorialUserMapper.checkRepeat(sysTutorialUser);
    }

    /**
     * 查询所属行业接口
     * @param tutorialIndustryVO
     * @return
     */
    @Override
    public List<TutorialIndustryVO> getIndustryList(TutorialIndustryVO tutorialIndustryVO) {
        List<TutorialIndustryVO> list = sysTutorialUserMapper.getIndustryList(tutorialIndustryVO);
        return list;
    }

}
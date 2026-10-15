package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.common.Constants;
import com.chinatower.platform.project.system.domain.SysTutorialUser;
import com.chinatower.platform.project.system.domain.vo.SysVideoTutorialVO;
import com.chinatower.platform.project.system.domain.vo.TutorialIndustryVO;
import com.chinatower.platform.project.system.mapper.SysTutorialMapper;
import com.chinatower.platform.project.system.service.ISysTutorialService;
import com.chinatower.platform.project.system.service.ISysTutorialUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.content.SysConstants;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ISysTutorialServiceImpl
 * @Description 系统引导服务实现类
 * @Author D
 * @Date 2022/8/19 17:00
 */
@Service
@Transactional
public class ISysTutorialServiceImpl implements ISysTutorialService {

    @Resource
    SysTutorialMapper sysTutorialMapper;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private ISysTutorialUserService sysTutorialUserService;

    /**
     * 新增视频系统教程
     *
     * @param vo
     * @return
     */
    @Override
    public Result<String> save(SysVideoTutorialVO vo) {
        //校验视频教程名称唯一性，如果重复则提示“视频教程名称已存在。”
        SysVideoTutorialVO existSysVideoTutorialVO = sysTutorialMapper.checkSysVideoTutorialInfo(vo);
        if(ObjectUtils.isNotEmpty(existSysVideoTutorialVO)){
            return Result.fail(Constants.VIDEO_TUTORIAL_NAME_EXIST);
        }
        vo.setCreator(SecurityUtils.getOperator());
        vo.setTutorialId(IdWorker.getIdStr());
        sysTutorialMapper.insert(vo);
        return Result.success(Constants.SUCCESS_MESSAGE);
    }



    /**
     * 编辑视频系统教程
     * @param vo
     * @return 新增结果
     */
    @Override
    public Result<String> updateVideoTutorial(SysVideoTutorialVO vo) {
        //校验视频教程名称唯一性，如果重复则提示“视频教程名称已存在。”
        SysVideoTutorialVO existSysVideoTutorialVO = sysTutorialMapper.checkSysVideoTutorialInfo(vo);
        if(ObjectUtils.isNotEmpty(existSysVideoTutorialVO)){
            return Result.fail(Constants.VIDEO_TUTORIAL_NAME_EXIST);
        }
        vo.setUpdateBy(SecurityUtils.getOperator());
        sysTutorialMapper.updateVideoTutorial(vo);
        return Result.success(Constants.SUCCESS_MESSAGE);
    }



    /**
     * 查询视频系统教程
     *
     * @param sysVideoTutorialVO
     * @return 视频系统教程列表
     */
    @Override
    public Result<PageInfo<SysVideoTutorialVO>> getVideoTutorial(SysVideoTutorialVO sysVideoTutorialVO){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String userType = currentUser.getUser().getUserType();
        //如果登录人是客户
        if (userType.equals(SysConstants.USET_TYPE_2)) {
            sysVideoTutorialVO.setTenantIsLook(Constants.ONE_NUM);
            sysVideoTutorialVO.setIndustryCode(currentUser.getUser().getIndustryCode());
        }else{//登录人是铁塔 传的行业编码带逗号
            if(!StringUtils.isEmpty(sysVideoTutorialVO.getIndustryCode())){
                List<String> industryCodeList = Arrays.asList(sysVideoTutorialVO.getIndustryCode().split(com.chinatower.platform.constants.Constants.COMMA));
                sysVideoTutorialVO.setIndustryCodeList(industryCodeList);
                sysVideoTutorialVO.setIndustryCode(null);
            }
        }
        List<SysVideoTutorialVO> list = sysTutorialMapper.getTutorialVideoList(sysVideoTutorialVO);
        if(!CollectionUtils.isEmpty(list) && userType.equals(SysConstants.USET_TYPE_0)){//重组行业名称和行业编码
            dealIndustryInfo(list);
        }
        Result<PageInfo<SysVideoTutorialVO>> result;
        PageInfo<SysVideoTutorialVO> pageInfo = new PageInfo<>(list);
        result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }


    /**
     * 根据教程ID获取视频系统教程详情
     * @param sysVideoTutorialVO
     * @return 新增结果
     */
    @Override
    public SysVideoTutorialVO getVideoTutorialInfo(SysVideoTutorialVO sysVideoTutorialVO) {
        SysVideoTutorialVO returnSysVideoTutorialVO = sysTutorialMapper.getVideoTutorialInfo(sysVideoTutorialVO);
        if (ObjectUtil.isNotEmpty(returnSysVideoTutorialVO) && StringUtils.isNotEmpty(returnSysVideoTutorialVO.getVideoUrl())){
            String url = fileUrlEncryptUtil.dealObsFileUrl(returnSysVideoTutorialVO.getVideoUrl());
            returnSysVideoTutorialVO.setVideoUrl(url);
        }
        dealIndustryInfo(Arrays.asList(returnSysVideoTutorialVO));
        return returnSysVideoTutorialVO;
    }


    public void dealIndustryInfo(List<SysVideoTutorialVO> list) {
        TutorialIndustryVO tutorialIndustryVO = new TutorialIndustryVO();
        List<TutorialIndustryVO> industryList = sysTutorialUserService.getIndustryList(tutorialIndustryVO);
        for (SysVideoTutorialVO item : list) {
            String industryCode = item.getIndustryCode();
            if(!StringUtils.isEmpty(industryCode)){
                String industryName = "";
                String returnIndustryCode = "";
                List<String> industryCodelist = Arrays.asList(industryCode.split(com.chinatower.platform.constants.Constants.COMMA));
                for (String code : industryCodelist) {
                    for (TutorialIndustryVO industry : industryList) {
                        if(code.equals(industry.getIndustryCode())){
                            returnIndustryCode += industry.getIndustryCode() + com.chinatower.platform.constants.Constants.COMMA;
                            industryName += industry.getIndustryName() + com.chinatower.platform.constants.Constants.COMMA_CH;
                        }
                    }
                }
                if(!StringUtils.isEmpty(returnIndustryCode)){
                    returnIndustryCode = returnIndustryCode.substring(com.chinatower.platform.constants.Constants.ZERO_NUM,returnIndustryCode.length()- com.chinatower.platform.constants.Constants.ONE_NUM);
                }
                if(!StringUtils.isEmpty(industryName)){
                    industryName = industryName.substring(com.chinatower.platform.constants.Constants.ZERO_NUM,industryName.length()- com.chinatower.platform.constants.Constants.ONE_NUM);
                }
                item.setIndustryCode(returnIndustryCode);
                item.setIndustryName(industryName);
            }
        }
    }


    /**
     * 删除视频系统教程
     * @param vo
     * @return
     */
    @Override
    public int delete(SysVideoTutorialVO vo) {
        vo.setUpdateBy(SecurityUtils.getOperator());
        return sysTutorialMapper.delete(vo);
    }



}

package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.dataobject.ProcessCommonWordsDO;
import com.chinatower.event.entity.dto.ProcessCommonWordsDTO;
import com.chinatower.event.mapper.ProcessCommonWordsMapper;
import com.chinatower.event.repository.IProcessCommonWordsService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessCommonWordsServiceImpl implements IProcessCommonWordsService {

    @Autowired
    private ProcessCommonWordsMapper processCommonWordsMapper;

    /**
     * 事件处理常用语查询
     * @param
     * @return
     */
    @Override
    public List<ProcessCommonWordsDTO> getProcessCommonWords() {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        List<ProcessCommonWordsDTO> commonWordsDTOList = null;
        List<ProcessCommonWordsDTO> list = processCommonWordsMapper.getProcessCommonWords(currentUser.getTenantId(), currentUser.getIndustryCode());
        if (!CollectionUtils.isEmpty(list)) {
            //默认常用语集合
            List<ProcessCommonWordsDTO> initWordsDTOList = list.stream().filter(item -> StringUtils.isEmpty(item.getTenantId())
                    && StringUtils.isEmpty(item.getIndustryCode())).collect(Collectors.toList());
            //租户+行业下的常用语
            List<ProcessCommonWordsDTO> wordsDTOList = list.stream().filter(item -> !StringUtils.isEmpty(item.getTenantId())
                    && !StringUtils.isEmpty(item.getIndustryCode())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(wordsDTOList)) {
                for (ProcessCommonWordsDTO wordsDTO : wordsDTOList) {
                    for (ProcessCommonWordsDTO initWordsDTO : initWordsDTOList) {
                        if (wordsDTO.getInitWordsFlag().equals(initWordsDTO.getInitWordsFlag())) {
                            initWordsDTO.setInitWordsFlag(wordsDTO.getInitWordsFlag());
                            initWordsDTO.setContent(wordsDTO.getContent());
                        }
                    }
                }
                commonWordsDTOList = initWordsDTOList;
            } else {
                commonWordsDTOList = initWordsDTOList;
            }
        }
        return commonWordsDTOList;
    }

    /**
     * 编辑保存事件处理常用语
     * @param commonWordsDTOList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessCommonWords(List<ProcessCommonWordsDTO> commonWordsDTOList) {
        log.info("编辑保存事件处理常用语入参：{}", JSON.toJSONString(commonWordsDTOList));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(MessageConstants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        if (CollectionUtils.isNotEmpty(commonWordsDTOList)) {
            for(ProcessCommonWordsDTO commonWordsDTO : commonWordsDTOList) {
                commonWordsDTO.setUserId(currentUser.getUserId());
                commonWordsDTO.setTenantId(currentUser.getTenantId());
                commonWordsDTO.setIndustryCode(currentUser.getIndustryCode());
            }
            List<String> initWordsFlagList = commonWordsDTOList.stream().map(ProcessCommonWordsDTO::getInitWordsFlag).collect(Collectors.toList());
            QueryWrapper<ProcessCommonWordsDO> queryWrapper = new QueryWrapper();
            queryWrapper.in("init_words_flag", initWordsFlagList);
            queryWrapper.eq("tenant_id", currentUser.getTenantId());
            queryWrapper.eq("industry_code", currentUser.getIndustryCode());
            processCommonWordsMapper.delete(queryWrapper);
            processCommonWordsMapper.addProcessCommonWordsBatch(commonWordsDTOList);
        }
    }
}

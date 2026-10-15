package com.chinatower.platform.project.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.core.constant.CommonErrorEnum;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.mapper.SysCommonWordsMapper;
import com.chinatower.platform.project.system.service.ISysCommonWordsService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.VmCommonWordsVO;
import com.chinatower.system.api.request.words.wordsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysCommonWordsServiceImpl implements ISysCommonWordsService {
    @Resource
    private SysCommonWordsMapper sysCommonWordsMapper;

    /**
     * 常用语查询
     *
     * @param wordsVO
     * @return
     */
    @Override
    public List<VmCommonWordsVO> getCommonWords(wordsRequest wordsVO) {
        List<VmCommonWordsVO> list = sysCommonWordsMapper.getCommonWords(wordsVO);

        return list;
    }

    /**
     * 新增白名单
     *
     * @param vo
     * @return
     */
    @Override
    public Result add(wordsRequest vo) {
        if(vo == null || StringUtils.isEmpty(vo.getContent())|| StringUtils.isEmpty(vo.getAppVerCode())|| StringUtils.isEmpty(vo.getUserId())|| StringUtils.isEmpty(vo.getTenantId())|| StringUtils.isEmpty(vo.getTypeId())){
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }

        return Result.condition(sysCommonWordsMapper.add(vo) > 0);
    }
    /**
     * 删除常用语
     *
     * @param vo
     * @return 结果
     */
    @Override
    public int deleteCommonWordsByWordsId(wordsRequest vo) {

       int i = sysCommonWordsMapper.deleteCommonWordsByWordsId(vo);

        return i;
    }

    /**
     * 批量删除常用语
     *
     * @param vo
     * @return 结果
     */
    @Override
    public int deleteCommonWordsByWordsIdBatch(wordsRequest vo) {

        int i = sysCommonWordsMapper.deleteCommonWordsByWordsIdBatch(vo);

        return i;
    }

    /**
     * 修改常用语
     *
     * @param vo
     * @return 结果
     */
    @Override
    public int updateCommonWords(wordsRequest vo) {

        return sysCommonWordsMapper.updateCommonWords(vo);
    }

    /**
     * 修改常用语
     *
     * @param vo
     * @return 结果
     */
    @Override
    public void updateCommonWordsBatch(wordsRequest vo) {

        if (!CollectionUtils.isEmpty(vo.getWordsList())) {
            sysCommonWordsMapper.updateCommonWordsBatch(vo.getWordsList());
        }
    }

    @Override
    public String queryFalseWords(String falseCode) {
        String falseWord = null;
        if (!StringUtils.isEmpty(falseCode)) {
            falseWord = sysCommonWordsMapper.queryFalseWords(falseCode);
        }
        return falseWord;
    }


    /**
     * 巡查任务常用语查询
     *
     * @param wordsVO
     * @return
     */
    @Override
    public List<VmCommonWordsVO> getCommonWordsForInspection(wordsRequest wordsVO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(Constants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        List<VmCommonWordsVO> commonWordsDTOList = null;
        List<VmCommonWordsVO> list = sysCommonWordsMapper.getCommonWordsForInspection(currentUser.getTenantId(), currentUser.getIndustryCode(),wordsVO.getTypeId());
        if (!CollectionUtils.isEmpty(list)) {
            //默认常用语集合
            List<VmCommonWordsVO> initWordsDTOList = list.stream().filter(item -> StringUtils.isEmpty(item.getTenantId())
                    && StringUtils.isEmpty(item.getIndustryCode())).collect(Collectors.toList());
            //租户+行业下的常用语
            List<VmCommonWordsVO> wordsDTOList = list.stream().filter(item -> !StringUtils.isEmpty(item.getTenantId())
                    && !StringUtils.isEmpty(item.getIndustryCode())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(wordsDTOList)) {
                for (VmCommonWordsVO wordsDTO : wordsDTOList) {
                    for (VmCommonWordsVO initWordsDTO : initWordsDTOList) {
                        if (wordsDTO.getInitWordsFlag().equals(initWordsDTO.getInitWordsFlag())) {
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
     * 编辑保存巡查任务常用语
     * @param commonWordsDTOList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommonWordsForInspection(List<wordsRequest> commonWordsDTOList) {
        log.info("编辑保存巡查任务常用语入参：{}", JSON.toJSONString(commonWordsDTOList));
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CustomException(Constants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        if (!CollectionUtils.isEmpty(commonWordsDTOList)) {
            for(wordsRequest commonWordsDTO : commonWordsDTOList) {
                commonWordsDTO.setUserId(currentUser.getUserId());
                commonWordsDTO.setTenantId(currentUser.getTenantId());
                commonWordsDTO.setIndustryCode(currentUser.getIndustryCode());
                commonWordsDTO.setAppVerCode(currentUser.getAppVerCode());
            }
            List<String> initWordsFlagList = commonWordsDTOList.stream().map(wordsRequest::getInitWordsFlag).collect(Collectors.toList());
            wordsRequest wordsRequestVO = new wordsRequest();
            wordsRequestVO.setInitWordsFlagList(initWordsFlagList);
            wordsRequestVO.setTenantId(currentUser.getTenantId());
            wordsRequestVO.setIndustryCode(currentUser.getIndustryCode());
            wordsRequestVO.setTypeId(Constants.NINE);
            sysCommonWordsMapper.deleteCommonWordsBatchForInspection(wordsRequestVO);
            sysCommonWordsMapper.addCommonWordsBatchForInspection(commonWordsDTOList);
        }
    }

    /**
     * 批量新增常用语
     * @param commonWordsDTOList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addCommonWordsBatch(List<wordsRequest> commonWordsDTOList) {
        log.info("批量新增常用语入参：{}", JSON.toJSONString(commonWordsDTOList));
        return Result.condition(sysCommonWordsMapper.addCommonWordsBatch(commonWordsDTOList) > 0);
    }

}

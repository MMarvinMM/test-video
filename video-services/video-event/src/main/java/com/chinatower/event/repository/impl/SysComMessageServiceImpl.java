package com.chinatower.event.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.spring.util.ObjectUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.mapper.SysComMessageMapper;
import com.chinatower.event.repository.SysComMessageService;
import com.chinatower.file.feign.client.FileObsFeignClient;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.response.FileObsEncryptResponse;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.obs.services.ObsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chinatower.system.api.content.SysConstants.SUCCESS_CODE;

/**
 * @author WLF
 * @description
 * @date 2024/10/21 9:53
 */
@Service
@Slf4j
public class SysComMessageServiceImpl implements SysComMessageService {
    @Resource
    private SysComMessageMapper sysComMessageMapper;
    @Resource
    private FileObsFeignClient fileObsFeignClient;

    /**
     * 查询融合通信列表
     *
     * @param communicationVo
     * @return
     */
    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "u.tenant_id", acceptColumn = "ic.accept_node", industryCode = "d.INDUSTRY_CODE", dbType = "base")
    public List<CommunicationVo> queryList(CommunicationVo communicationVo) {
        Integer pageNum = communicationVo.getPageNum();
        Integer pageSize = communicationVo.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<CommunicationVo> sysComMessageList = sysComMessageMapper.queryList(communicationVo);
        return sysComMessageList;
    }

    /**
     * 编辑融合通信信息
     *
     * @param communicationVo
     * @return
     */
    @Override
    public Result<String> editCommunicationInfo(CommunicationVo communicationVo) {
        List<String> numbersToCheck = Arrays.asList(communicationVo.getDispatchDeskNum(), communicationVo.getMobileTerminalNum()).stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (numbersToCheck.stream()
                .map(num -> checkTelNumber(num, communicationVo.getCommunicationId()))
                .anyMatch(result -> ObjectUtil.isNotEmpty(result))) {
            throw new CustomException("该号码已存在");
        }
        communicationVo.setUpdateTime(new Date());
        communicationVo.setUpdateUser(SecurityUtils.getLoginUser().getUser().getUserName());
        if (StringUtils.isEmpty(communicationVo.getCommunicationId())) {
            sysComMessageMapper.insertCommunicationInfo(communicationVo);
        } else {
            sysComMessageMapper.editCommunicationInfo(communicationVo);
        }
        return Result.success("编辑成功");
    }


    /**
     * 通过号码查询用户信息
     *
     * @param communicationVo
     * @return
     */
    @Override
    public CommunicationVo queryInfoByTel(CommunicationVo communicationVo) {
        CommunicationVo communicationVoResult = this.checkTelNumber(communicationVo.getCheckPhoneNumber(),null);
        if (ObjectUtil.isNotEmpty(communicationVoResult) && StringUtils.isNotEmpty(communicationVoResult.getAvatar())){
            List<String> list = new ArrayList<>();
            list.add(communicationVoResult.getAvatar());
            FileObsEncryptRequest fileObsEncryptRequest = new FileObsEncryptRequest();
            if (ObjectUtil.isNotEmpty(list)){
                fileObsEncryptRequest.setFileUrls(list);
                ApiResult<List<FileObsEncryptResponse>> apiResult = fileObsFeignClient.getObsEncryptUrls(fileObsEncryptRequest);
                if (StringUtils.equals(apiResult.getReturnCode(), SUCCESS_CODE)) {
                    List<FileObsEncryptResponse> obsEncryptResponseList = apiResult.getData();
                    // 返回加密的文件URL
                    communicationVoResult.setAvatar(obsEncryptResponseList.get(0).getEncryptUrl());
                }
            }
        }
        return communicationVoResult;
    }

    /**
     * 校验号码唯一性
     *
     * @param checkTelNumber
     * @return
     */
    private CommunicationVo checkTelNumber(String checkTelNumber,String communicationId) {
        CommunicationVo communicationVoResult = sysComMessageMapper.queryInfoByTel(checkTelNumber,communicationId);
        if (ObjectUtil.isNotEmpty(communicationVoResult)) {
            String dispatchDeskNum = communicationVoResult.getDispatchDeskNum();
            String mobileTerminalNum = communicationVoResult.getMobileTerminalNum();
            List<String> numbers = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(dispatchDeskNum)) {
                numbers.add(dispatchDeskNum);
            }
            if (ObjectUtil.isNotEmpty(mobileTerminalNum)) {
                numbers.add(mobileTerminalNum);
            }
            numbers.stream().filter(num -> num.equals(checkTelNumber)).findFirst().ifPresent(num -> {
                if (num.equals(dispatchDeskNum)) {
                    communicationVoResult.setTelNumberType("0");
                } else {
                    communicationVoResult.setTelNumberType("1");
                }
            });
        }
        return communicationVoResult;
    }
}

package com.chinatower.manage.repository.impl;

import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.manage.dataobject.open.OpenIntfInfoVO;
import com.chinatower.manage.dataobject.open.OpenIntfReqInfoVO;
import com.chinatower.manage.dataobject.open.OpenManageInfoVO;
import com.chinatower.manage.dto.open.OpenIntfWholeInfoModel;
import com.chinatower.manage.dto.open.OpenManageInfoModel;
import com.chinatower.manage.mapper.OpenIntfMangeMapper;
import com.chinatower.manage.repository.IOpenIntfMangeService;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * api接口管理业务接口实现
 * @author lmz
 * @version 1.0
 * @date 2022-6-27
 */
@Slf4j
@Service
public class OpenIntfMangeServiceImpl implements IOpenIntfMangeService {

    @Autowired
    private OpenIntfMangeMapper openIntfMangeMapper;
    @Override
    public Result<Object> queryOpenPlatformInfo(OpenManageInfoModel model) {
        List<OpenManageInfoVO> openManageInfoVOList;
        //根据类型 查询API  或者 SDK
        if(StringUtils.equals(Constants.API,model.getType())){
            SysUser user = SecurityUtils.getLoginUser().getUser();
            List<SysRole> roles = user.getRoles();
            String roleType = "";
            if (CollectionUtils.isEmpty(roles)) {
                for (SysRole role : roles) {
                    if (role.getRoleType().equals(Constants.ZERO)) {
                        roleType = role.getRoleType();
                        break;
                    }
                }
            }
            if (!Constants.ZERO.equals(roleType)) {
                model.setTenantId(user.getTenantId());
            }
            openManageInfoVOList = openIntfMangeMapper.queryOpenApiPlatformInfo(model);
        }else{
            openManageInfoVOList = openIntfMangeMapper.queryOpenSdkPlatformInfo(model);
        }
        return Result.data(openManageInfoVOList);
    }

    @Override
    public Result<OpenIntfWholeInfoModel> queryOpenIntfWholeInfoByApiIntfId(Long openIntfId,String type) {
        OpenIntfWholeInfoModel openIntfWholeInfoModel = new OpenIntfWholeInfoModel();
        Result<OpenIntfWholeInfoModel> result =  Result.success(ResultCode.SUCCESS);
        //查询基本信息
        //api_intf_info
        OpenManageInfoVO openIntfMange = new OpenManageInfoVO();
        //API
        if(StringUtils.equals(Constants.API,type)){
            //基础信息
            openIntfMange = openIntfMangeMapper.getOpenApiManageInfo(openIntfId);
            openIntfWholeInfoModel.setOpenManageInfo(openIntfMange);
            //查询扩展信息
            OpenIntfInfoVO openIntfInfoVO = openIntfMangeMapper.qryOpenApiIntfExtendInfo(openIntfId);
            openIntfWholeInfoModel.setOpenIntfInfo(openIntfInfoVO);
            //查询请求参数集合
            List<OpenIntfReqInfoVO> openIntfReqInfoVOList = openIntfMangeMapper.selectOpenApiIntfIdByApiIntfId(openIntfId);
            List<OpenIntfReqInfoVO> reqHeadList = new ArrayList<>();
            List<OpenIntfReqInfoVO> urlInputParamList = new ArrayList<>();
            List<OpenIntfReqInfoVO> rqeParam = new ArrayList<>();
            List<OpenIntfReqInfoVO> reqContentList = new ArrayList<>();
            List<OpenIntfReqInfoVO> resContentList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(openIntfReqInfoVOList)) {
                for (OpenIntfReqInfoVO openIntfReqInfoVO : openIntfReqInfoVOList) {
                    if (Constants.API_REQ_HEAD.equals(openIntfReqInfoVO.getType())) {
                        reqHeadList.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_URL_INPUT_PARAM.equals(openIntfReqInfoVO.getType())) {
                        urlInputParamList.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_REQ_PARAM.equals(openIntfReqInfoVO.getType())) {
                        rqeParam.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_REQ_CONTENT.equals(openIntfReqInfoVO.getType())) {
                        reqContentList.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_RES_CONTENT.equals(openIntfReqInfoVO.getType())) {
                        resContentList.add(openIntfReqInfoVO);
                    }
                }
            }
            Map<String, List<OpenIntfReqInfoVO>> resultMap = new HashMap<>();
            resultMap.put(Constants.API_REQ_HEAD, reqHeadList);
            resultMap.put(Constants.API_URL_INPUT_PARAM, urlInputParamList);
            resultMap.put(Constants.API_REQ_PARAM, rqeParam);
            resultMap.put(Constants.API_REQ_CONTENT, reqContentList);
            resultMap.put(Constants.API_RES_CONTENT, resContentList);
            openIntfWholeInfoModel.setOpenIntfReqInfosMap(resultMap);
            result.setData(openIntfWholeInfoModel);
        }else{
            //查询基本信息
            openIntfMange = openIntfMangeMapper.getOpenSdkManageInfo(openIntfId);
            openIntfWholeInfoModel.setOpenManageInfo(openIntfMange);
            //查询扩展信息
            OpenIntfInfoVO openIntfInfoVO = openIntfMangeMapper.qryOpenSdkIntfExtendInfo(openIntfId);
            openIntfWholeInfoModel.setOpenIntfInfo(openIntfInfoVO);
            //查询请求参数集合
            List<OpenIntfReqInfoVO> openIntfReqInfoVOS = openIntfMangeMapper.queryOpenSdkIntfReqInfoListBySdkIntfId(openIntfId);
            List<OpenIntfReqInfoVO> reqHeadList = new ArrayList<>();
            List<OpenIntfReqInfoVO> urlInputParamList = new ArrayList<>();
            List<OpenIntfReqInfoVO> rqeParam = new ArrayList<>();
            List<OpenIntfReqInfoVO> reqContentList = new ArrayList<>();
            List<OpenIntfReqInfoVO> resContentList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(openIntfReqInfoVOS)) {
                for (OpenIntfReqInfoVO openIntfReqInfoVO : openIntfReqInfoVOS) {
                    if (Constants.API_REQ_HEAD.equals(openIntfReqInfoVO.getType())) {
                        reqHeadList.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_URL_INPUT_PARAM.equals(openIntfReqInfoVO.getType())) {
                        urlInputParamList.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_REQ_PARAM.equals(openIntfReqInfoVO.getType())) {
                        rqeParam.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_REQ_CONTENT.equals(openIntfReqInfoVO.getType())) {
                        reqContentList.add(openIntfReqInfoVO);
                    }
                    if (Constants.API_RES_CONTENT.equals(openIntfReqInfoVO.getType())) {
                        resContentList.add(openIntfReqInfoVO);
                    }
                }
            }
            Map<String, List<OpenIntfReqInfoVO>> resultMap = new HashMap<>();
            resultMap.put(Constants.API_REQ_HEAD, reqHeadList);
            resultMap.put(Constants.API_URL_INPUT_PARAM, urlInputParamList);
            resultMap.put(Constants.API_REQ_PARAM, rqeParam);
            resultMap.put(Constants.API_REQ_CONTENT, reqContentList);
            resultMap.put(Constants.API_RES_CONTENT, resContentList);
            openIntfWholeInfoModel.setOpenIntfReqInfosMap(resultMap);
            result.setData(openIntfWholeInfoModel);
        }
        return result;
    }
}

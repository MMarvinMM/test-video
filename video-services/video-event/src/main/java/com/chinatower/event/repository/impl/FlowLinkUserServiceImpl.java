package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.FlowLinkUser;
import com.chinatower.event.dto.FlowLinkUserModel;
import com.chinatower.event.mapper.FlowLinkUserMapper;
import com.chinatower.event.repository.IFlowLinkUserService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/27 09:16
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.PLATFORM)
public class FlowLinkUserServiceImpl implements IFlowLinkUserService {
    @Autowired
    private FlowLinkUserMapper flowLinkUserMapper;

    @Override
    public Result addFlowLinkUser(FlowLinkUserModel model) {
        Long flowId = model.getFlowId();
        Long flowLinkId = model.getFlowLinkId();
        String userId = model.getUserId();
        String userType = model.getUserType();
        if (null == flowId) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "模板ID不能为空");
        }
        if (null == flowLinkId) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "环节属性ID不能为空");
        }
        if (StringUtils.isEmpty(userId)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "用户ID不能为空");
        }
        if (StringUtils.isEmpty(userType)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "用户类型不能为空");
        }

        Result result = null;
        try {
            flowLinkUserMapper.addFlowLinkUser(model);
            result = Result.success("操作成功");
            result.setData(model);
        } catch (Exception e) {
            log.error("新增流程环节用户异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "新增流程环节用户异常");
        }
        return result;
    }

    @Override
    public Result queryFlowLinkUser(FlowLinkUserModel model) {
        Result result = null;
        try {
            List<FlowLinkUser> resultList = flowLinkUserMapper.queryFlowLinkUser(model);
            result = Result.success("查询成功");
            result.setData(resultList);
        } catch (Exception e) {
            log.error("查询流程环节用户异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "查询流程环节用户异常");
        }

        return result;
    }

    @Override
    public Result modifyFlowLinkUser(FlowLinkUserModel model) {
        Long linkUserId = model.getLinkUserId();
        if (null == linkUserId || linkUserId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        Result result = null;
        try {
            flowLinkUserMapper.modifyFlowLinkUser(model);
            result = Result.success("修改成功");
        } catch (Exception e) {
            log.error("修改流程环节用户异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "修改流程环节用户异常");
        }
        return result;
    }

    @Override
    public Result deleteFlowLinkUser(Long linkUserId) {
        if (null == linkUserId || linkUserId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        Result result = null;
        try {
            flowLinkUserMapper.deleteFlowLinkUser(linkUserId);
            result = Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除流程环节用户异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "删除流程环节用户异常");
        }
        return result;
    }
}

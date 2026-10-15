package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.FlowLinkConfig;
import com.chinatower.event.dto.FlowLinkConfigModel;
import com.chinatower.event.mapper.FlowLinkAttrMapper;
import com.chinatower.event.mapper.FlowLinkConfigMapper;
import com.chinatower.event.repository.IFlowLinkConfigService;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 11:10
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.PLATFORM)
public class FlowLinkConfigServiceImpl implements IFlowLinkConfigService {
    @Autowired
    private FlowLinkConfigMapper flowLinkConfigMapper;
    @Autowired
    private FlowLinkAttrMapper flowLinkAttrMapper;

    @Override
    public Result<FlowLinkConfigModel> addLinkInfo(FlowLinkConfigModel model) {
        Result<FlowLinkConfigModel> result = null;
        try {
            String msg = checkUnique(model);
            if (null != msg) {
                return Result.fail(HttpStatus.NO_CONTENT, msg);
            }
            model.setStatus(Constants.ALARM_FLOW_CONFIG_STATUS_VALID);
            flowLinkConfigMapper.addFlowLinkConfig(model);
            result = Result.success("操作成功");
            result.setData(model);
        } catch (Exception e) {
            log.error("新增流程环节配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "新增流程环节配置异常");
        }
        return result;
    }

    /**
     * 查询流程环节列表信息
     * @author lyy
     * @param model
     * @return Result<Object>
     */
    @Override
    public Result<Object> queryLinkInfo(FlowLinkConfigModel model) {
        Result<Object> result;
        try {
            if (model.isEnablePage()) {
                Integer pageNum = model.getPageNum();
                Integer pageSize = model.getPageSize();
                if (null == pageNum || pageNum <= 0) {
                    pageNum = 1;
                }
                if (null == pageSize || pageSize <= 0) {
                    pageSize = 15;
                }
                PageMethod.startPage(pageNum, pageSize);
                List<FlowLinkConfig> resultList = flowLinkConfigMapper.queryFlowLinkConfig(model);
                PageInfo<FlowLinkConfig> pageInfo = new PageInfo(resultList);
                result = Result.success(Constants.QUERY_WAS_SUCCESSFUL);
                result.setData(pageInfo);
            } else {
                List<FlowLinkConfig> resultList = flowLinkConfigMapper.queryFlowLinkConfig(model);
                result = Result.success(Constants.QUERY_WAS_SUCCESSFUL);
                result.setData(resultList);
            }
        } catch (Exception e) {
            log.error("查询流程环节配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "查询流程环节配置异常");
        }
        return result;
    }

    @Override
    public Result<String> modifyLinkInfo(FlowLinkConfigModel model) {
        Long linkId = model.getLinkId();
        if (null == linkId || linkId <= 0) {
            return Result.fail(HttpStatus.NO_CONTENT, Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        Result<String> result = null;
        try {
            String msg = checkUnique(model);
            if (null != msg) {
                return Result.fail(HttpStatus.NO_CONTENT, msg);
            }
            List<String> flowNameList = checkLinkUse(linkId);
            if (CollectionUtils.isNotEmpty(flowNameList)) {
                return Result.fail(HttpStatus.NO_CONTENT,
                        "该环节已被配置到" + flowNameList.stream().collect(Collectors.joining("、")) + "流程中，不允许编辑。");
            }
            flowLinkConfigMapper.modifyFlowLinkConfig(model);
            result = Result.success("修改成功");
        } catch (Exception e) {
            log.error("修改流程环节配置异常", e);
            result = Result.fail(HttpStatus.NO_CONTENT, "修改流程环节配置异常");
        }
        return result;
    }

    private List<String> checkLinkUse (Long linkId) {
        List<String> resultList = new ArrayList<>();
        List<Map<String, String>> flowjsonList = flowLinkAttrMapper.checkLinkUse(linkId);
        for (Map<String, String> flowjsonMap : flowjsonList) {
            String flowjson = flowjsonMap.get("flow_json");
            String flowname = flowjsonMap.get("flow_name");
            if (StringUtils.isNotEmpty(flowjson) && !flowjson.equals("[]")) {
                JSONObject map = JSON.parseObject(flowjson);
                if (map != null && map.containsKey("nodes")) {
                    JSONArray list = map.getJSONArray("nodes");
                    if (list != null && list.size() > 0) {
                        list.forEach(map1 -> {
                            Map<String, Object> map2 = JSON.parseObject(JSON.toJSONString(map1), Map.class);
                            if (map2.containsKey("id")) {
                                if (linkId.toString().equals(map2.get("id").toString())) {
                                    resultList.add(flowname);
                                }
                            }
                        });
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public Result<String> deleteLinkInfo(Long linkId) {
        if (null == linkId || linkId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        Result<String> result = null;
        try {
            List<String> flowNameList = checkLinkUse(linkId);
            if (CollectionUtils.isNotEmpty(flowNameList)) {
                return Result.fail(HttpStatus.NO_CONTENT,
                        "该环节已被配置到" + flowNameList.stream().collect(Collectors.joining("、")) + "流程中，不允许删除。");
            }
            flowLinkConfigMapper.deleteFlowLinkConfig(linkId);
            result = Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除流程环节配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "删除流程环节配置异常");
        }
        return result;
    }

    @Override
    public List<FlowLinkConfig> queryById(List<String> idList) {
        List<FlowLinkConfig> resultList = null;
        if (CollectionUtils.isNotEmpty(idList)) {
            resultList = flowLinkConfigMapper.queryById(idList);
        }
        return resultList;
    }

    private String checkUnique(FlowLinkConfigModel model) {
        String result = null;
        if (StringUtils.isEmpty(model.getLinkName())) {
            result = "流程环节名称不能为空！";
            return result;
        }
        Integer countName = flowLinkConfigMapper.checkLinkName(model);
        if (countName > 0) {
            result = "流程环节名称已存在！";
        }
        return result;
    }
}

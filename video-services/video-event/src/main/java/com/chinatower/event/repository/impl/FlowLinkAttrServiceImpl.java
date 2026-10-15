package com.chinatower.event.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dataobject.FlowLinkUser;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.usertree.OrderInfoUserTreeReq;
import com.chinatower.event.entity.vo.SysInfoVo;
import com.chinatower.event.mapper.FlowLinkAttrMapper;
import com.chinatower.event.mapper.FlowLinkConfigMapper;
import com.chinatower.event.mapper.FlowLinkUserMapper;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.repository.ICommonRepository;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.IFlowLinkAttrService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:49
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.PLATFORM)
public class FlowLinkAttrServiceImpl implements IFlowLinkAttrService {
    @Autowired
    private FlowLinkAttrMapper flowLinkAttrMapper;
    @Autowired
    private FlowLinkUserMapper flowLinkUserMapper;
    @Autowired
    private FlowLinkConfigMapper flowLinkConfigMapper;
    @Autowired
    private SysMapper sysMapper;
    @Autowired
    private FlowLinkService flowLinkService;
    @Autowired
    private ICommonRepository iCommonRepository;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private IDeviceRepository deviceRepository;
    @Override
    public Result<FlowLinkAttrModel> addFlowLinkAttr(FlowLinkAttrModel model) {
        String linkType = model.getLinkType();
        String userType = model.getUserType();
        String msg = checkParam(model.getFlowId(), model.getCurLink());
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        if (StringUtils.isEmpty(linkType)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "环节类型不能为空");
        }
        if (StringUtils.isEmpty(userType)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "用户类型不能为空");
        }

        Result<FlowLinkAttrModel> result = null;
        try {
            flowLinkAttrMapper.addFlowLinkAttr(model);
            result = Result.success("操作成功");
            result.setData(model);
        } catch (Exception e) {
            log.error("新增流程环节属性异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "新增流程环节属性异常");
        }
        return result;
    }

    /**
     * 功能描述：根据条件查询流程环节属性
     *
     * @return com.chinatower.common.entity.Result<com.chinatower.event.dto.FlowLinkAttrModel>
     * @params [model]
     * @author su.tingning
     * @date 2022/6/10 15:57
     */
    @Override
    public Result<FlowLinkAttrModel> queryFlowLinkAttr(FlowLinkAttrModel model) {
        Result<FlowLinkAttrModel> result = null;
        String msg = checkParam(model.getAlarmFlowId(), model.getCurLink());
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        try {
            List<FlowLinkAttrModel> flowLinkAttrList = flowLinkAttrMapper.queryFlowLinkAttr(model);
            if (CollectionUtils.isNotEmpty(flowLinkAttrList)) {
                //上面查询只能查出一条
                FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrList.get(0);

                if (Constants.USER_TYPE_1.equals(flowLinkAttrModel.getUserType())) {
                    FlowLinkUserModel flowLinkUserModel = new FlowLinkUserModel();
                    flowLinkUserModel.setFlowLinkId(flowLinkAttrModel.getFlowLinkId());
                    List<FlowLinkUser> flowLinkUserList = flowLinkUserMapper.queryFlowLinkUser(flowLinkUserModel);
                    if (CollectionUtils.isNotEmpty(flowLinkUserList)) {
                        Set<String> userIdSet = flowLinkUserList.stream().map(FlowLinkUser::getUserId).collect(Collectors.toSet());
                        flowLinkAttrModel.setUserId(String.join(",", userIdSet));
                    }
                }
                result = Result.success("查询成功");
                result.setData(flowLinkAttrModel);
            } else {
                result = Result.success("查询结果为空");
            }
        } catch (Exception e) {
            log.error(MessageConstants.FLOW_LINK_ATTR_EXCEPTION, e);
            result = Result.fail(ResultCode.ERROR.getCode(), MessageConstants.FLOW_LINK_ATTR_EXCEPTION);
        }

        return result;
    }

    @Override
    public Result<FlowLinkAttrModel> queryFlowLinkAttrNew(FlowLinkAttrModel model) {
        Result<FlowLinkAttrModel> result = null;
        String msg = checkParam(model.getAlarmFlowId(), model.getCurLink());
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        try {
            List<FlowLinkAttrModel> flowLinkAttrList = getFlowLinkAttr(model);
            if (CollectionUtils.isNotEmpty(flowLinkAttrList)) {
                //上面查询只能查出一条
                FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrList.get(0);
                result = Result.success("查询成功");
                result.setData(flowLinkAttrModel);
            } else {
                result = Result.success("查询结果为空");
            }
        } catch (Exception e) {
            log.error(MessageConstants.FLOW_LINK_ATTR_EXCEPTION, e);
            result = Result.fail(ResultCode.ERROR.getCode(), MessageConstants.FLOW_LINK_ATTR_EXCEPTION);
        }

        return result;
    }

    @Override
    public Result<String> modifyFlowLinkAttr(FlowLinkAttrModel model) {
        Long flowLinkId = model.getFlowLinkId();
        if (null == flowLinkId || flowLinkId <= 0) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.PRIMARY_KEY_CANNOT_BE_EMPTY);
        }
        Result<String> result = null;
        try {
            flowLinkAttrMapper.modifyFlowLinkAttr(model);
            result = Result.success("修改成功");
        } catch (Exception e) {
            log.error("修改流程环节属性异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "修改流程环节属性异常");
        }
        return result;
    }

    @Override
    public Result<List<FlowLinkAttrModel>> queryNextLink(FlowLinkAttrModel model) {
        Result<List<FlowLinkAttrModel>> result = null;
        String msg = checkParam(model.getAlarmFlowId(), model.getCurLink());
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        List<FlowLinkAttrModel> flowLinkAttrModelList = flowLinkAttrMapper.queryFlowLinkAttr(model);
        if (CollectionUtils.isNotEmpty(flowLinkAttrModelList)) {
            FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrModelList.get(0);
            String nextLink = flowLinkAttrModel.getNextLink();
            String[] nextLinkArr = nextLink.split(Constants.COMMA);
            List<Long> nextLinkList = Arrays.asList(nextLinkArr).stream().map(str -> Long.valueOf(str)).collect(Collectors.toList());
            //List<FlowLinkConfig> linkConfigList = flowLinkConfigMapper.queryById(Arrays.asList(nextLinkArr));
            List<FlowLinkAttrModel> resultList = flowLinkAttrMapper.queryByCurLinks(nextLinkList, model.getAlarmFlowId());
            //去重
            resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(o -> o.getFlowId() + "-" + o.getCurLink()))), ArrayList::new));
            result = Result.success("查询成功");
            result.setData(resultList);
        } else {
            result = Result.fail(Constants.ALARM_FLOW_LINK_ATTR_NOT_EXIST);
        }
        return result;
    }

    @Override
    public Result<List<FlowLinkAttrModel>> queryNextLinkNew(FlowLinkAttrModel model) {
        Result<List<FlowLinkAttrModel>> result = null;
        String msg = checkParam(model.getAlarmFlowId(), model.getCurLink());
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        List<FlowLinkAttrModel> flowLinkAttrModelList = getFlowLinkAttr(model);
        if (CollectionUtils.isNotEmpty(flowLinkAttrModelList)) {
            try{
                FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrModelList.get(0);
                String nextLink = flowLinkAttrModel.getNextLink();
                String[] nextLinkArr = nextLink.split(Constants.COMMA);
                List<Long> nextLinkList = Arrays.asList(nextLinkArr).stream().map(str -> Long.valueOf(str)).collect(Collectors.toList());
                model.setCurLink(null);
                model.setLinkList(nextLinkList);
                List<FlowLinkAttrModel> resultList = getFlowLinkAttr(model);
                //去重
                resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(o -> o.getFlowId() + "-" + o.getCurLink()))), ArrayList::new));
                result = Result.success("查询成功");
                result.setData(resultList);
            }catch(Exception e){
                result = Result.fail("没有查询到下一流程环节");
            }
        } else {
            result = Result.fail(Constants.ALARM_FLOW_LINK_ATTR_NOT_EXIST);
        }
        return result;
    }

    @Override
    public Result<List<FlowLinkAttrModel>> queryReassignLinkNew(FlowLinkAttrModel model) {
        Long curLinkId = model.getCurLink();
        Result<List<FlowLinkAttrModel>> result = null;
        String msg = checkParam(model.getAlarmFlowId(), model.getCurLink());
        if (msg != null) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), msg);
        }
        //查询当前环节信息获取当前环节上个环节信息
        List<FlowLinkAttrModel> flowLinkAttrModelList = getFlowLinkAttr(model);
        if(CollectionUtils.isNotEmpty(flowLinkAttrModelList)){
            FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrModelList.get(0);
            String preLink = flowLinkAttrModel.getPreLink();
            if(StringUtils.isEmpty(preLink)){
                return Result.fail("该环节当前不支持转派");
            }else{
                //查询上个环节信息对应的下个环节列表
                model.setCurLink(Long.valueOf(preLink));
                List<FlowLinkAttrModel> beforFlowLinkAttrModelList = getFlowLinkAttr(model);
                FlowLinkAttrModel beforFlowLinkAttrModel = beforFlowLinkAttrModelList.get(0);
                String nextLink = beforFlowLinkAttrModel.getNextLink();
                if(!nextLink.contains(",") && Constants.ZERO.equals(beforFlowLinkAttrModel.getTurnFlag())){
                    return Result.fail("该环节当前不支持转派");
                }else{
                    String[] nextLinkArr = nextLink.split(Constants.COMMA);
                    List<Long> nextLinkList = Arrays.asList(nextLinkArr).stream().map(str -> Long.valueOf(str)).collect(Collectors.toList());
                    model.setLinkId(null);
                    model.setCurLink(null);
                    model.setLinkList(nextLinkList);
                    List<FlowLinkAttrModel> resultList = getFlowLinkAttr(model);
                    //去重
                    resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                            new TreeSet<>(Comparator.comparing(o -> o.getFlowId() + "-" + o.getCurLink()))), ArrayList::new));
                    //过滤当前环节
                    resultList = resultList.stream().filter(f ->
                            !String.valueOf(curLinkId).equals(String.valueOf(f.getCurLink()))).collect(Collectors.toList());
                    result = Result.success("查询成功");
                    result.setData(resultList);
                }
            }
        }else{
            result = Result.fail(Constants.ALARM_FLOW_LINK_ATTR_NOT_EXIST);
        }
        return result;
    }

    /**
     * 查询所有下一步流程环节信息
     * @param model
     * @return
     */
    private List<FlowLinkAttrModel> getAllNextFlowLinkAttr(FlowLinkAttrModel model){
        AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
        configModel.setId(model.getAlarmFlowId());
        configModel.setLinkId(model.getCurLink());
        List<FlowModel> flowLink = flowLinkService.getAllNextFlowLink(configModel);
        return BeanUtil.copyToList(flowLink,FlowLinkAttrModel.class);
    }

    /**
     * 获取流程环节信息
     * @param model
     * @return
     */
    private List<FlowLinkAttrModel> getFlowLinkAttr(FlowLinkAttrModel model){
        AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
        configModel.setId(model.getAlarmFlowId());
        configModel.setLinkId(model.getCurLink());
        configModel.setLinkList(model.getLinkList());
        configModel.setGridFlag(model.getGridFlag());
        if(StringUtils.isEmpty(model.getAppVerCode()) || StringUtils.isEmpty(model.getIndustryCode()) ){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser.getUser();
            configModel.setAppVerCode(currentUser.getAppVerCode());
            configModel.setIndustryCode(currentUser.getIndustryCode());
        }else{
            configModel.setAppVerCode(model.getAppVerCode());
            configModel.setIndustryCode(model.getIndustryCode());
        }

        configModel.setWarningSource(model.getWarningSource());
        configModel.setTemplateId(model.getTemplateId());
        List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
        for (FlowModel flowModel:flowModelList) {
            flowModel.setAppVerCode(configModel.getAppVerCode());
            flowModel.setIndustryCode(configModel.getIndustryCode());
        }
        return BeanUtil.copyToList(flowModelList,FlowLinkAttrModel.class);
    }

    /**
     * 最新获取环节处理人，供非行业使用
     * @param model
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="tenant_id",industryCode="industry_code")
    public Result getFlowByIdUserTree(FlowLinkAttrModel model) {
        log.info("FlowLinkAttrServiceImpl-getFlowByIdUserTree-获取调度选择人入参:{}",JSON.toJSONString(model));
        Result result = null;
        String gridUserList = null;
        //查询是否有网格员
        if(!StringUtils.isEmpty(model.getGridId())){
            gridUserList = deviceRepository.getGridUserByGridId(model.getGridId());
        }
        String gridFlag = null;
        if(!StringUtils.isEmpty(gridUserList)){
            gridFlag = Constants.ONE;
        }
        model.setGridFlag(gridFlag);
        List<FlowLinkAttrModel> flowLinkAttrModelList = getFlowLinkAttr(model);
        if (CollectionUtils.isEmpty(flowLinkAttrModelList)) {
            return Result.fail(Constants.ALARM_FLOW_LINK_ATTR_NOT_EXIST);
        }
        FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrModelList.get(0);
        List<String> parentList = sysMapper.queryDeptIdNew(model);
        //给有权限组织树使用
        List<SysInfo> deptList = flowLinkAttrMapper.queryDeptsNew(parentList,model);
        //给所有人组织树使用
        List<SysInfo> beforeDeptList = (List<SysInfo>)SerializationUtils.clone((Serializable) deptList);
        model.setUserType(flowLinkAttrModel.getUserType());
        List<String> userTypeList = Arrays.asList(flowLinkAttrModel.getUserType().split(Constants.COMMA));
        List<String> flowUserList = new ArrayList<>();
        List<Long> flowRoleList = new ArrayList<>();
        //用户
        if (StringUtils.isNotEmpty(flowLinkAttrModel.getUserId())) {
            flowUserList.addAll(Arrays.asList(flowLinkAttrModel.getUserId().split(Constants.COMMA)));
        }
        //角色
        if (StringUtils.isNotEmpty(flowLinkAttrModel.getRoleId())) {
            List<String> flowRoles = Arrays.asList(flowLinkAttrModel.getRoleId().split(Constants.COMMA));
            for (String role:flowRoles) {
                if(!StringUtils.isEmpty(role)){
                    flowRoleList.add(Long.parseLong(role));
                }
            }
        }
        //网格员
        if (userTypeList.contains(Constants.THREE) && gridFlag != null) {
            flowUserList.addAll(Arrays.asList(gridUserList.split(Constants.COMMA)));
        }
        //查询流程配置里所有的用户（后续返回给前端全部联系人树形）,没有配置用户得环节用户集合为空
        List<SysInfo> userList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(flowUserList) || !CollectionUtils.isEmpty(flowRoleList)){
            userList = flowLinkAttrMapper.queryUserByRoleIdNew(parentList,model,flowUserList,flowRoleList);
        }
        //有处理人，且不需要过滤数据权限，默认全部有权限
        if(!CollectionUtils.isEmpty(userList) && StringUtils.isNotEmpty(model.getWarningSource()) &&
                !Constants.FILTER_WARNING_SOURCE.contains(model.getWarningSource())){
            for (SysInfo userInfo:userList) {
                userInfo.setPurviewFlag(Constants.ONE);
            }
        }
        Result<List<SysUser>> reponse =null;
        //最终封装用户集
        List<SysInfo> newUserList = new ArrayList<>();
        //封装符合要求的调用用户是否有摄像机权限的用户list
        List<SysUser> sysUserList =new ArrayList<>();
        //查询数据字典是否调用华苏接口获取用户权限，默认1调用
        String flag =Constants.ONE;
        try{
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                flag= suppressDictData.getData().get(0).getDictValue();
            }
            //如果是卫片告警，不过滤数据权限
            /*if(StringUtils.isNotEmpty(model.getWarningSource()) && Constants.NINE.equals(model.getWarningSource())
                    && Arrays.asList(Constants.REGION_ALARM_LIST).contains(model.getWarningTypeId())){
                flag = Constants.ZERO;
            }*/
            if(Constants.ONE.equals(flag) && StringUtils.isNotEmpty(model.getWarningSource()) && !Constants.ONE.equals(model.getStatusFlag())){
                //调用接口获取有摄像机权限的客户
                List<String> list = userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                reponse =iCommonRepository.getUserByDeptAndDeviceScope(list,model.getTenantId(),model.getDeviceCode(),
                        flowLinkAttrModel.getIndustryCode(),flowLinkAttrModel.getAppVerCode(),model.getWarningSource(),
                        model.getCityId(),model.getCountyId(),model.getProvinceId(),model.getWarningTypeId());
                if(reponse !=null && ResultCode.SUCCESS.getCode() == reponse.getCode()&&reponse.getData()!=null){
                    JSONArray arrayList = JSON.parseArray(JSON.toJSONString(reponse.getData()));
                    for (Object object:arrayList) {
                        sysUserList.add(JSON.parseObject(JSON.toJSONString(object),SysUser.class));
                    }
                }
            }
        }catch (Exception e){
            log.error("FlowLinkAttrServiceImpl-getFlowByIdUserTree-根据设备编号获取有权限的用户列表error,返回原有用户,错误信息:{}",e);
            newUserList = (List<SysInfo>) SerializationUtils.clone((Serializable) userList);
        }
        //判断是否有摄像机code,有的话取摄像机有权限的用户返回，没有按照所查用户返回
        if (StringUtils.isNotEmpty(model.getWarningSource()) && Constants.FILTER_WARNING_SOURCE.contains(model.getWarningSource())) {
            for (SysInfo sysInfo : userList) {
                for (SysUser sysUser : sysUserList) {
                    if (sysInfo.getUserId() != null && sysInfo.getUserId().equals(sysUser.getUserId())
                            && sysInfo.getDeptId() != null && sysInfo.getDeptId().equals(sysUser.getDeptId())) {
                        //有数据权限用户
                        sysInfo.setPurviewFlag(Constants.ONE);
                        newUserList.add(sysInfo);
                    }
                }
            }
        }else{
            newUserList = (List<SysInfo>) SerializationUtils.clone((Serializable) userList);
        }
        if(Constants.ONE.equals(model.getStatusFlag()) || !Constants.ONE.equals(flag)){
            //行业来源不涉及权限过滤
            newUserList = (List<SysInfo>) SerializationUtils.clone((Serializable) userList);
        }
        for (SysInfo dept : deptList) {
            dept.setChildren(new ArrayList<>());
        }
        for (SysInfo dept : beforeDeptList) {
            dept.setChildren(new ArrayList<>());
        }
        //流程配置展示客户管理员时增加返回租户管理员
        this.addAdminUser(model,newUserList,userList,flowLinkAttrModel.getCustomAdmin());
        SysInfoVo sysPowerInfo =this.getUserTree(deptList,newUserList,parentList);
        SysInfoVo sysAllInfo = this.getUserTree(beforeDeptList,userList,parentList);
        // 兼容APP旧版本特此处理
        SysInfoVo root = new SysInfoVo();
        BeanUtils.copyProperties(sysAllInfo,root);
        root.setSysPowerInfo(sysPowerInfo);
        root.setSysAllInfo(sysAllInfo);
        result = Result.success("查询成功");
        result.setData(root);
        return result;
    }

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="tenant_id",industryCode="industry_code")
    public Result getAppFlowByIdUserTree(FlowLinkAttrModel model) {
        log.info("FlowLinkAttrServiceImpl-getAppFlowByIdUserTree-获取app调度选择人入参:{}",JSON.toJSONString(model));
        Result result = null;
        String gridUserList = null;
        //查询是否有网格员
        if(!StringUtils.isEmpty(model.getGridId())){
            gridUserList = deviceRepository.getGridUserByGridId(model.getGridId());
        }
        String gridFlag = null;
        if(!StringUtils.isEmpty(gridUserList)){
            gridFlag = Constants.ONE;
        }
        model.setGridFlag(gridFlag);
        List<FlowLinkAttrModel> flowLinkAttrModelList = getFlowLinkAttr(model);
        if (CollectionUtils.isEmpty(flowLinkAttrModelList)) {
            return Result.fail(Constants.ALARM_FLOW_LINK_ATTR_NOT_EXIST);
        }
        FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrModelList.get(0);
        List<String> parentList = sysMapper.queryDeptIdNew(model);
        //给有权限组织树使用
        List<SysInfo> deptList = flowLinkAttrMapper.queryDeptsNew(parentList,model);
        //给所有人组织树使用
        List<SysInfo> beforeDeptList = (List<SysInfo>)SerializationUtils.clone((Serializable) deptList);
        model.setUserType(flowLinkAttrModel.getUserType());
        List<String> userTypeList = Arrays.asList(flowLinkAttrModel.getUserType().split(Constants.COMMA));
        List<String> flowUserList = new ArrayList<>();
        List<Long> flowRoleList = new ArrayList<>();
        //用户
        if (StringUtils.isNotEmpty(flowLinkAttrModel.getUserId())) {
            flowUserList.addAll(Arrays.asList(flowLinkAttrModel.getUserId().split(Constants.COMMA)));
        }
        //角色
        if (StringUtils.isNotEmpty(flowLinkAttrModel.getRoleId())) {
            List<String> flowRoles = Arrays.asList(flowLinkAttrModel.getRoleId().split(Constants.COMMA));
            for (String role:flowRoles) {
                if(!StringUtils.isEmpty(role)){
                    flowRoleList.add(Long.parseLong(role));
                }
            }
        }
        //网格员
        if (userTypeList.contains(Constants.THREE) && gridFlag != null) {
            flowUserList.addAll(Arrays.asList(gridUserList.split(Constants.COMMA)));
        }
        //查询流程配置里所有的用户（后续返回给前端全部联系人树形）,没有配置用户得环节用户集合为空
        List<SysInfo> userList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(flowUserList) || !CollectionUtils.isEmpty(flowRoleList)){
            userList = flowLinkAttrMapper.queryUserByRoleIdNew(parentList,model,flowUserList,flowRoleList);
        }
        //有处理人，且不需要过滤数据权限，默认全部有权限
        if(!CollectionUtils.isEmpty(userList) && StringUtils.isNotEmpty(model.getWarningSource()) &&
                !Constants.FILTER_WARNING_SOURCE.contains(model.getWarningSource())){
            for (SysInfo userInfo:userList) {
                userInfo.setPurviewFlag(Constants.ONE);
            }
        }
        Result<List<SysUser>> reponse =null;
        //最终封装用户集
        List<SysInfo> newUserList = new ArrayList<>();
        //封装符合要求的调用用户是否有摄像机权限的用户list
        List<SysUser> sysUserList =new ArrayList<>();
        //查询数据字典是否调用华苏接口获取用户权限，默认1调用
        String flag =Constants.ONE;
        try{
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                flag= suppressDictData.getData().get(0).getDictValue();
            }
            if(Constants.ONE.equals(flag) && StringUtils.isNotEmpty(model.getWarningSource()) && !Constants.ONE.equals(model.getStatusFlag())){
                //调用接口获取有摄像机权限的客户
                List<String> list = userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                reponse =iCommonRepository.getUserByDeptAndDeviceScope(list,model.getTenantId(),model.getDeviceCode(),
                        flowLinkAttrModel.getIndustryCode(),flowLinkAttrModel.getAppVerCode(),model.getWarningSource(),
                        model.getCityId(),model.getCountyId(),model.getProvinceId(),model.getWarningTypeId());
                if(reponse !=null && ResultCode.SUCCESS.getCode() == reponse.getCode()&&reponse.getData()!=null){
                    JSONArray arrayList = JSON.parseArray(JSON.toJSONString(reponse.getData()));
                    for (Object object:arrayList) {
                        sysUserList.add(JSON.parseObject(JSON.toJSONString(object),SysUser.class));
                    }
                }
            }
        }catch (Exception e){
            log.error("FlowLinkAttrServiceImpl-getFlowByIdUserTree-根据设备编号获取有权限的用户列表error,返回原有用户,错误信息:{}",e);
            newUserList = (List<SysInfo>) SerializationUtils.clone((Serializable) userList);
        }
        //判断是否有摄像机code,有的话取摄像机有权限的用户返回，没有按照所查用户返回
        if (StringUtils.isNotEmpty(model.getWarningSource()) && Constants.FILTER_WARNING_SOURCE.contains(model.getWarningSource())) {
            for (SysInfo sysInfo : userList) {
                for (SysUser sysUser : sysUserList) {
                    if (sysInfo.getUserId() != null && sysInfo.getUserId().equals(sysUser.getUserId())
                            && sysInfo.getDeptId() != null && sysInfo.getDeptId().equals(sysUser.getDeptId())) {
                        //有数据权限用户
                        sysInfo.setPurviewFlag(Constants.ONE);
                        newUserList.add(sysInfo);
                    }
                }
            }
        }else{
            newUserList = (List<SysInfo>) SerializationUtils.clone((Serializable) userList);
        }
        if(Constants.ONE.equals(model.getStatusFlag()) || !Constants.ONE.equals(flag)){
            //行业来源不涉及权限过滤
            newUserList = (List<SysInfo>) SerializationUtils.clone((Serializable) userList);
        }
        for (SysInfo dept : deptList) {
            dept.setChildren(new ArrayList<>());
        }
        for (SysInfo dept : beforeDeptList) {
            dept.setChildren(new ArrayList<>());
        }
        //流程配置展示客户管理员时增加返回租户管理员
        this.addAdminUser(model,newUserList,userList,flowLinkAttrModel.getCustomAdmin());
        SysInfoVo sysPowerInfo =this.getAppUserTree(deptList,newUserList,parentList);
        SysInfoVo sysAllInfo = this.getAppUserTree(beforeDeptList,userList,parentList);
        // 兼容APP旧版本特此处理
        SysInfoVo root = new SysInfoVo();
        BeanUtils.copyProperties(sysAllInfo,root);
        root.setSysPowerInfo(sysPowerInfo);
        root.setSysAllInfo(sysAllInfo);
        root.setSysPowerList(newUserList);
        root.setSysAllList(userList);
        result = Result.success("查询成功");
        result.setData(root);
        return result;
    }

    /**
     * 用户选择树是否返回租户管理员逻辑
     * @param model
     * @param newUserList 有权限用户
     * @param userList 所有用户
     */
    public void addAdminUser(FlowLinkAttrModel model,List<SysInfo> newUserList,List<SysInfo> userList,String customAdmin){
        //增加租户管理员用户返回
        SysInfo adminUser = flowLinkAttrMapper.queryAdminUser(model.getTenantId());
        //展示管理员开关为是的时候，默认列表需要展示管理员，给到的前端标记位改为0
        if(adminUser != null){
            if(Constants.ONE.equals(customAdmin)){
                adminUser.setTenantAdmin(Constants.ZERO);
            }
            if(CollectionUtils.isEmpty(newUserList) && Constants.ONE.equals(customAdmin)){
                //模糊查询时输入名字和租户管理员不匹配不返沪租户管理员
                if(StringUtils.isEmpty(model.getUserName())){
                    newUserList.add(adminUser);
                    if(!CollectionUtils.isEmpty(userList)){
                        List<String> listString =userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                        if(!listString.contains(adminUser.getUserId())){
                            userList.add(adminUser);
                        }
                    }
                }else{
                    if(adminUser.getUserName().contains(model.getUserName())){
                        newUserList.add(adminUser);
                    }
                }
            }else if(!CollectionUtils.isEmpty(newUserList)){
                List<String> listString =userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                List<String> listNewString = newUserList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                if(adminUser !=null && !listNewString.contains(adminUser.getUserId())){
                    if(Constants.ONE.equals(customAdmin)){
                        //模糊查询时输入名字和租户管理员不匹配不返沪租户管理员
                        if(StringUtils.isEmpty(model.getUserName())){
                            newUserList.add(adminUser);
                        }else{
                            if(adminUser.getUserName().contains(model.getUserName())){
                                newUserList.add(adminUser);
                            }
                        }
                    }
                }else if(adminUser!=null){
                    for (SysInfo user:newUserList) {
                        if(adminUser.getUserId().equals(user.getUserId())){
                            user.setPurviewFlag(Constants.ONE);
                        }
                    }
                }
                if (adminUser != null && !listString.contains(adminUser.getUserId())) {
                    //模糊查询时输入名字和租户管理员不匹配不返沪租户管理员
                    if (StringUtils.isEmpty(model.getUserName())) {
                        userList.add(adminUser);
                    } else {
                        if (adminUser.getUserName().contains(model.getUserName())) {
                            userList.add(adminUser);
                        }
                    }
                } else if (adminUser != null) {
                    for (SysInfo user : userList) {
                        if (adminUser.getUserId().equals(user.getUserId())) {
                            user.setPurviewFlag(Constants.ONE);
                        }
                    }
                }
            }else if(!CollectionUtils.isEmpty(userList)){
                List<String> listString =userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                if (!listString.contains(adminUser.getUserId())) {
                    //模糊查询时输入名字和租户管理员不匹配不返沪租户管理员
                    if (StringUtils.isEmpty(model.getUserName())) {
                        userList.add(adminUser);
                    } else {
                        if (adminUser.getUserName().contains(model.getUserName())) {
                            userList.add(adminUser);
                        }
                    }
                } else if (adminUser != null) {
                    for (SysInfo user : userList) {
                        if (adminUser.getUserId().equals(user.getUserId())) {
                            user.setPurviewFlag(Constants.ONE);
                        }
                    }
                }
            }
        }
    }


    public SysInfoVo getAppUserTree(List<SysInfo> deptList,List<SysInfo> userList,List<String> parentList){
        deptList.forEach(dept -> {
            userList.forEach(user -> {
                String userDeptId = user.getDeptId();
                if(userDeptId!=null && !(userDeptId.indexOf(",")>0) ){
                    if (userDeptId.equals(dept.getDeptId())) {
                        dept.getChildren().add(user);
                    }
                }else{
                    List<String> deptIdTemps = Arrays.asList(userDeptId.split(Constants.COMMA));
                    for(String deptIdTemp:deptIdTemps){
                        if (deptIdTemp.equals(dept.getDeptId())) {
                            dept.getChildren().add(user);
                            continue;
                        }
                    }
                }
            });
        });
        String firstNode="";
        String firstName="";
        for (SysInfo first:deptList) {
            if(parentList.get(0).equals(first.getDeptId())){
                firstNode=first.getDeptId();
                firstName=first.getDeptName();
                break;
            }
        }
       /* List<SysInfo> deptListNew = new ArrayList<>();
        //判断是否有摄像机code,有的话取摄像机有权限的用户返回，没有按照所查用户返回
        for (SysInfo sysInfo:deptList) {
            if(sysInfo.getChildren() != null && sysInfo.getChildren().size() >0){
                deptListNew.add(sysInfo);
            }
        }*/
        deptList = deptList.stream().distinct().collect(Collectors.toList());
        JSONArray treeAry = TreeUtils.listToTree(deptList, Constants.DEPT_ID, Constants.PARENT_ID);
        List<SysInfoVo> list = JSON.parseArray(JSON.toJSONString(treeAry), SysInfoVo.class);
        SysInfoVo sysInfo = new SysInfoVo();
        if(CollectionUtils.isNotEmpty(list) && list.size() == 1){
            sysInfo = list.get(0);
        }else{
            sysInfo = new SysInfoVo(firstNode, firstName);
            sysInfo.setDeptId(firstNode);
            sysInfo.setDeptName(firstName);
            sysInfo.setChildren(list);
        }
        return sysInfo;
    }


    public SysInfoVo getUserTree(List<SysInfo> deptList,List<SysInfo> userList,List<String> parentList){
        deptList.forEach(dept -> {
            userList.forEach(user -> {
                String userDeptId = user.getDeptId();
                if(userDeptId!=null && !(userDeptId.indexOf(",")>0) ){
                    if (userDeptId.equals(dept.getDeptId())) {
                        dept.getChildren().add(user);
                    }
                }else{
                    List<String> deptIdTemps = Arrays.asList(userDeptId.split(Constants.COMMA));
                    for(String deptIdTemp:deptIdTemps){
                        if (deptIdTemp.equals(dept.getDeptId())) {
                            dept.getChildren().add(user);
                            continue;
                        }
                    }
                }
            });
        });
        String firstNode="";
        String firstName="";
        for (SysInfo first:deptList) {
            if(parentList.get(0).equals(first.getDeptId())){
                firstNode=first.getDeptId();
                firstName=first.getDeptName();
                break;
            }
        }
        List<SysInfo> deptListNew = new ArrayList<>();
        //判断是否有摄像机code,有的话取摄像机有权限的用户返回，没有按照所查用户返回
        for (SysInfo sysInfo:deptList) {
            if(sysInfo.getChildren() != null && sysInfo.getChildren().size() >0){
                deptListNew.add(sysInfo);
            }
        }
        deptListNew = deptListNew.stream().distinct().collect(Collectors.toList());
        JSONArray treeAry = TreeUtils.listToTree(deptListNew, Constants.DEPT_ID, Constants.PARENT_ID);
        List<SysInfoVo> list = JSON.parseArray(JSON.toJSONString(treeAry), SysInfoVo.class);
        SysInfoVo sysInfo = new SysInfoVo();
        if(CollectionUtils.isNotEmpty(list) && list.size() == 1){
            sysInfo = list.get(0);
        }else{
            sysInfo = new SysInfoVo(firstNode, firstName);
            sysInfo.setDeptId(firstNode);
            sysInfo.setDeptName(firstName);
            sysInfo.setChildren(list);
        }
        return sysInfo;
    }

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="tenant_id",industryCode="industry_code")
    public Result<SysInfoVo> getFlowByIdUserTreeNew(FlowLinkAttrModel model) {
        log.info("FlowLinkAttrServiceImpl-getFlowByIdUserTreeNew-获取调度选择人入参:{}",JSON.toJSONString(model));
        Result result = null;
        String gridUserList = null;
        //查询是否有网格员
        if(!StringUtils.isEmpty(model.getGridId())){
            gridUserList = deviceRepository.getGridUserByGridId(model.getGridId());
        }
        String gridFlag = null;
        if(!StringUtils.isEmpty(gridUserList)){
            gridFlag = Constants.ONE;
        }
        model.setGridFlag(gridFlag);
        List<FlowLinkAttrModel> flowLinkAttrModelList = getFlowLinkAttr(model);
        if (CollectionUtils.isEmpty(flowLinkAttrModelList)) {
            return Result.fail(Constants.ALARM_FLOW_LINK_ATTR_NOT_EXIST);
        }
        FlowLinkAttrModel flowLinkAttrModel = flowLinkAttrModelList.get(0);
        List<String> parentList = sysMapper.queryDeptIdNew(model);
        List<SysInfo> deptList = flowLinkAttrMapper.queryDeptsNew(parentList,model);
        String firstNode="";
        String firstName="";
        for (SysInfo first:deptList) {
            if(parentList.get(0).equals(first.getDeptId())){
                firstNode=first.getDeptId();
                firstName=first.getDeptName();
                break;
            }
        }
        model.setUserType(flowLinkAttrModel.getUserType());
        List<String> userTypeList = Arrays.asList(flowLinkAttrModel.getUserType().split(Constants.COMMA));
        List<String> flowUserList = new ArrayList<>();
        List<Long> flowRoleList = new ArrayList<>();
            //用户
            if(StringUtils.isNotEmpty(flowLinkAttrModel.getUserId())){
                flowUserList.addAll(Arrays.asList(flowLinkAttrModel.getUserId().split(Constants.COMMA)));
            }
            //角色
            if(StringUtils.isNotEmpty(flowLinkAttrModel.getRoleId())){
                List<String> flowRoles = Arrays.asList(flowLinkAttrModel.getRoleId().split(Constants.COMMA));
                for (String role:flowRoles) {
                    if(!StringUtils.isEmpty(role)){
                        flowRoleList.add(Long.parseLong(role));
                    }
                }
            }
            //网格员
            if(flowLinkAttrModel.getUserType().contains(Constants.THREE) && gridFlag != null){
                flowUserList.addAll(Arrays.asList(gridUserList.split(Constants.COMMA)));
            }

        //查询流程配置里所有的用户（后续返回给前端全部联系人树形）,没有配置用户得环节用户集合为空
        List<SysInfo> allUserList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(flowUserList) || !CollectionUtils.isEmpty(flowRoleList)){
            allUserList = flowLinkAttrMapper.queryUserByRoleIdNew(parentList,model,flowUserList,flowRoleList);
        }
        //用户去重
        Set<SysInfo> userListSet = new HashSet<>(allUserList);
        List<SysInfo> userList = new ArrayList<>(userListSet);
        Result<List<SysUser>> reponse =null;
        //最终封装用户集
        List<SysInfo> newUserList = new ArrayList<>();
        //封装符合要求的调用用户是否有摄像机权限的用户list
        List<SysUser> sysUserList =new ArrayList<>();
        //查询数据字典是否调用华苏接口获取用户权限，默认1调用
        String flag =Constants.ONE;
        try{
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                flag= suppressDictData.getData().get(0).getDictValue();
            }
            if(Constants.ONE.equals(flag) && StringUtils.isNotEmpty(model.getWarningSource()) && !Constants.ONE.equals(model.getStatusFlag())){
                //调用接口获取有摄像机权限的客户
                List<String> list = userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                reponse =iCommonRepository.getUserByDeptAndDeviceScope(list,model.getTenantId(),model.getDeviceCode(),
                        flowLinkAttrModel.getIndustryCode(),flowLinkAttrModel.getAppVerCode(),model.getWarningSource(),
                        model.getCityId(),model.getCountyId(),model.getProvinceId(),model.getWarningTypeId());
               if(reponse !=null && ResultCode.SUCCESS.getCode() == reponse.getCode()&&reponse.getData()!=null){
                    JSONArray arrayList = JSON.parseArray(JSON.toJSONString(reponse.getData()));
                    for (Object object:arrayList) {
                        sysUserList.add(JSON.parseObject(JSON.toJSONString(object),SysUser.class));
                    }
                }
            }
        }catch (Exception e){
            log.error("FlowLinkAttrServiceImpl-getFlowByIdUserTreeNew-根据设备编号获取有权限的用户列表error,返回原有用户,错误信息:{}",e);
            newUserList = userList;
        }
        //判断是否有摄像机code,有的话取摄像机有权限的用户返回，没有按照所查用户返回
        if (StringUtils.isNotEmpty(model.getWarningSource()) && Constants.FILTER_WARNING_SOURCE.contains(model.getWarningSource())) {
            for (SysInfo sysInfo : userList) {
                for (SysUser sysUser : sysUserList) {
                    if (sysInfo.getUserId() != null && sysInfo.getUserId().equals(sysUser.getUserId())
                            && sysInfo.getDeptId() != null && sysInfo.getDeptId().equals(sysUser.getDeptId())) {
                        newUserList.add(sysInfo);
                    }
                }
            }
        }else{
            newUserList = userList;
        }
        if(Constants.ONE.equals(model.getStatusFlag()) || !Constants.ONE.equals(flag)){
            //行业来源不涉及权限过滤
            newUserList = userList;
        }
        for (SysInfo dept : deptList) {
            dept.setChildren(new ArrayList<>());
        }
        //增加租户管理员用户返回
        SysInfo adminUser = flowLinkAttrMapper.queryAdminUser(model.getTenantId());
        if(CollectionUtils.isEmpty(newUserList)){
            newUserList.add(adminUser);
        }else{
            List<String> listString =userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
            if(adminUser!=null && !listString.contains(adminUser.getUserId())){
                newUserList.add(adminUser);
            }
        }
        List<SysInfo> finalNewUserList = newUserList;
        deptList.forEach(dept -> {
            finalNewUserList.forEach(user -> {
                String userDeptId = user.getDeptId();
                if(userDeptId!=null && !(userDeptId.indexOf(",")>0) ){
                    if (userDeptId.equals(dept.getDeptId())) {
                        dept.getChildren().add(user);
                    }
                }else{
                    List<String> deptIdTemps = Arrays.asList(userDeptId.split(Constants.COMMA));
                    for(String deptIdTemp:deptIdTemps){
                        if (deptIdTemp.equals(dept.getDeptId())) {
                            dept.getChildren().add(user);
                            continue;
                        }
                    }
                }
            });
        });
        List<SysInfo> deptListNew = new ArrayList<>();
        //判断是否有摄像机code,有的话取摄像机有权限的用户返回，没有按照所查用户返回
        for (SysInfo sysInfo:deptList) {
            if(sysInfo.getChildren() != null && sysInfo.getChildren().size() >0){
                deptListNew.add(sysInfo);
            }
        }
        deptListNew = deptListNew.stream().distinct().collect(Collectors.toList());
        JSONArray treeAry = TreeUtils.listToTree(deptListNew, Constants.DEPT_ID, Constants.PARENT_ID);
        List<SysInfoVo> list = JSON.parseArray(JSON.toJSONString(treeAry), SysInfoVo.class);
        SysInfoVo sysInfo;
        if(CollectionUtils.isNotEmpty(list) && list.size() == 1){
            sysInfo = list.get(0);
        }else{
            sysInfo = new SysInfoVo(firstNode, firstName);
            sysInfo.setChildren(list);
        }
        List<SysInfoVo> rootChild = new LinkedList<>();
        //去重
        HashSet h = new HashSet(list);
        rootChild.clear();
        rootChild.addAll(h);
        result = Result.success("查询成功");
        result.setData(sysInfo);
        return result;
    }

    @Override
    public Result getOrderInfoUserTree(OrderInfoUserTreeReq model) {
        if(StringUtils.isEmpty(model.getProcessIds())){
            return null;
        }
        List<String> parentList = sysMapper.queryOrderInfoDeptId(model);
        List<SysInfo> deptList = flowLinkAttrMapper.queryOrderInfoDepts(parentList,model);
        String[] userIdList = model.getProcessIds().split(",");
        List<UserInfoDto> userList = sysMapper.queryUserListInfo(Arrays.asList(userIdList));
        List<SysInfo> realUserList = new ArrayList<>();
        for (UserInfoDto userInfo:userList) {
            SysInfo user = new SysInfo();
            BeanUtil.copyProperties(userInfo,user);
            realUserList.add(user);
        }
        for (SysInfo dept : deptList) {
            dept.setChildren(new ArrayList<>());
        }
        SysInfoVo sysAllInfo = this.getAppUserTree(deptList,realUserList,parentList);
        // 兼容APP旧版本特此处理
        SysInfoVo root = new SysInfoVo();
        BeanUtils.copyProperties(sysAllInfo,root);
        root.setSysAllInfo(sysAllInfo);
        root.setSysAllList(realUserList);
        Result result = Result.success("查询成功");
        result.setData(root);
        return result;
    }

    public SysUser getUser(String userId,String appVerCode,String industryCode,String deviceCode){
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setAppVerCode(appVerCode);
        user.setIndustryCode(industryCode);
        user.setDeviceCodeList(Arrays.asList(deviceCode));
        return user;
    }


    public void delChildren(List<SysInfoVo> list){
        Iterator<SysInfoVo> it = list.iterator();
        while (it.hasNext()){
            SysInfoVo info =it.next();
            if((null == info.getChildren() || info.getChildren().size()<1) && StringUtils.isEmpty(info.getUserId())){
                it.remove();
            }else{
                if(null != info.getChildren() && info.getChildren().size() > 0){
                    delChildren(info.getChildren());
                }
            }
        }
    }


    @Override
    public Result<SysInfo> getUserTree(String rootDeptIds, String deptIds, String roleIds) {
        Result<SysInfo> result = null;
        if (StringUtils.isEmpty(rootDeptIds)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "根组织ID不能为空");
        }
        if (StringUtils.isEmpty(deptIds)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "组织ID不能为空");
        }

        //汇总查询
        String deptStr = rootDeptIds + Constants.COMMA + deptIds;
        List<String> deptIdList = Arrays.asList(deptStr.split(Constants.COMMA));

        SysInfo root = new SysInfo(Constants.TOWER_ROOT_AREA, Constants.TOWER_ROOT_AREA_NAME);
        root.setChildren(flowLinkAttrMapper.queryDeptUsers(deptIdList));
        result = Result.success("查询成功");
        result.setData(root);

        return result;
    }

    /**
     * 根据组织ID获取用户所属角色树
     *
     * @param
     * @return
     */
    @Override
    public Result<List<SysInfo>> queryRoles(String rootDeptIds, String deptIds) {
        Result result = null;
        if (StringUtils.isEmpty(rootDeptIds)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "根组织ID不能为空");
        }
        if (StringUtils.isEmpty(deptIds)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "组织ID不能为空");
        }

        //汇总查询
        String deptStr = rootDeptIds + Constants.COMMA + deptIds;
        List<String> deptIdList = Arrays.asList(deptStr.split(Constants.COMMA));

        List<SysInfo> roleList = flowLinkAttrMapper.queryRoles(deptIdList);
        result = Result.success("查询成功");
        result.setData(roleList);
        return result;
    }

    private String checkParam(Long flowId, Long linkId) {
        String result = null;
        if (null == flowId) {
            result = "模板ID不能为空";
        }
        if (null == linkId) {
            result = "当前环节不能为空";
        }
        return result;
    }
}

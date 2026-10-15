package com.chinatower.platform.project.custom.service.impl;


import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.vo.FunctionTypeVO;
import com.chinatower.device.entity.vo.PlatformInfoVo;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.system.api.feign.client.CustomFeignClient;
import com.chinatower.platform.project.custom.common.Constants;
import com.chinatower.platform.project.custom.entity.*;
import com.chinatower.platform.project.custom.mapper.CustomManageMapper;
import com.chinatower.platform.project.custom.service.ICustomManageService;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.mapper.SysDictDataMapper;
import com.chinatower.platform.project.system.mapper.SysUserMapper;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.utils.CascadeConnectUtils;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.feign.client.WangGuanFeignClient;
import com.chinatower.system.api.model.AlarmTypeReqVo;
import com.chinatower.system.api.model.TenantInfoVO;
import com.chinatower.system.api.model.UpdateTenantReqVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigInfoSubVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigQueryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author：liuxt
 * @Description：客户管理
 * @Date：2022/8/5 11:34
 * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
 */
@Service("customManageService")
@Slf4j
public class CustomManageServiceImpl implements ICustomManageService {

    @Resource
    CustomManageMapper customManageMapper;

    @Resource
    SysDictDataMapper sysDictDataMapper;

    @Autowired
    private CascadeConnectUtils cascadeConnectUtils;

    private static final ExecutorService THREAD_POOL_PUSH = Executors.newFixedThreadPool(20);

    @Autowired
    private ISysConfigService sysConfigService;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private MsgFeignClient msgFeignClient;

    @Value("${sms.centerUrl:https://sl.chinatowercom.cn}")
    private String centerUrl;

    @Value("${sms.loginUrl}")
    private String url;

    @Value("${sms.template.updateTenantLoginType:smsvideo20230824515}")
    private String templateCode;

//    @Resource
//    SendToOthersUtil sendToOthersUtil;


//    @Value("${gateway_url}")
//    private String url;

    @Resource
    WangGuanFeignClient wangGuanFeignClient;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * @Author：liuxt
     * @Description：查询行政区域列表
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    public Result getTenantTree(TenantTreeReqVO tenantTreeReqVO){
//        this.getOperationList(tenantTreeReqVO);
        List<TenantTreeResultVO> list=customManageMapper.getTenantTree(tenantTreeReqVO);
        return  Result.data(list);
    }

    /**
     * @Author：liuxt
     * @Description：查询API配置信息
     * @Date：2022/8/5 11:34
     * @param apiIntfInfoVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    public Result getApiIntfInfo(ApiIntfInfoVO apiIntfInfoVO){
        List<ApiIntfInfoVO> list=customManageMapper.getApiIntfInfo(apiIntfInfoVO);
        return  Result.data(list);
    }

    /**
     * @Author：liuxt
     * @Description：批量更新客户信息
     * @Date：2022/8/5 11:34
     * @param updateTenantReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> updateTenantInfoBatch(UpdateTenantReqVO updateTenantReqVO){

        try{
            if (CollectionUtils.isEmpty(updateTenantReqVO.getCustomerInfoList())){
                return Result.fail("客户信息不能为空！");
            }
            List<TenantInfoVO> customerInfoList = updateTenantReqVO.getCustomerInfoList();
            String currentLocationCode = "";
            if (Boolean.FALSE.equals(updateTenantReqVO.getSyncFlag())){
                //异步同步过来的数据带着modifier修改人，不需要再设置修改人  正常修改需要设置
                SysUser loginUser = SecurityUtils.getLoginUser().getUser();
                //获取当前节点
                currentLocationCode = loginUser.getPlatformCode();
                String userName = loginUser.getUserName();
                updateTenantReqVO.setOperationModifier(userName);
                updateTenantReqVO.setCurrentLocationCode(currentLocationCode);
                customerInfoList.forEach(item ->{
                    item.setOperationModifier(userName);
                });
                //若为并行研判--判断研判节点是否为当前节点
                if (Constants.TWO_FLAG.equals(updateTenantReqVO.getOperationType())){
                    String operationNode = updateTenantReqVO.getOperationNode();
                    if (!currentLocationCode.equals(operationNode)){
                        return Result.fail("并行研判时研判节点必须为当前节点！");
                    }
                }
            }
            //先删除
            customManageMapper.updateTenantOperationList(customerInfoList);
            if (updateTenantReqVO.getIsOperation().equals("1")){
                //后插入
                AlarmTypeReqVo alarmTypeReqVo;
                List<AlarmTypeReqVo> alarmTypeReqVoList = new ArrayList<>();
                List<TenantInfoVO> tenantInfoList = updateTenantReqVO.getCustomerInfoList();
                for (TenantInfoVO tenantInfo : tenantInfoList){
                    alarmTypeReqVo = new AlarmTypeReqVo();
                    alarmTypeReqVo.setTenantId(tenantInfo.getTenantId());
                    alarmTypeReqVo.setOperationType(updateTenantReqVO.getOperationType());
                    alarmTypeReqVo.setAlarmSource("-1");
                    alarmTypeReqVo.setOperationAlarmTypeStr("");
                    alarmTypeReqVo.setDeleteFlag(0);
                    alarmTypeReqVo.setModifier(tenantInfo.getOperationModifier());
                    alarmTypeReqVo.setUpdateTime(new Date());
                    alarmTypeReqVo.setIsOperation(updateTenantReqVO.getIsOperation());
                    alarmTypeReqVo.setAcceptNode(tenantInfo.getLocationCode());
                    alarmTypeReqVo.setIndustryCode(tenantInfo.getIndustryCode());
                    alarmTypeReqVo.setAppVerCode(tenantInfo.getAppVerCode());
                    alarmTypeReqVo.setOperationNode(updateTenantReqVO.getOperationNode());
                    alarmTypeReqVoList.add(alarmTypeReqVo);
                }
                customManageMapper.insertTenantOperation(alarmTypeReqVoList);
            }

            //判断是否属地化
            SysConfig dependenceManageConfig = sysConfigService.selectConfigByKey("dependence_manage");

            //推送到所有上级节点--中心节点、属地化不推
            if (Boolean.FALSE.equals(updateTenantReqVO.getSyncFlag()) && dependenceManageConfig.getConfigValue().equals(com.chinatower.platform.constants.Constants.TWO)){
                log.info("当前执行同步操作的节点:{}",JSON.toJSONString(currentLocationCode));
                operationBatchPushDeal(updateTenantReqVO);
            }
            return Result.success("批量配置成功！");
        }catch (Exception e){
            log.error("批量研判失败：",e);
            return Result.fail("批量研判失败!");
        }
    }

    /**
     * @Author：liuxt
     * @Description：获取订单客户列表数据
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return List<TenantMangeResultVO>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @DataScope(deptColumn = "",tenantColumn = "tenant.tenant_id")
    @Override
    public  List<TenantMangeResultVO> getOrderTenantInfo(TenantTreeReqVO tenantTreeReqVO) {
        return getOrderTenantInfoCommon(tenantTreeReqVO);
    }

    @Override
    public List<TenantMangeResultVO> getOrderTenantInfoCommon(TenantTreeReqVO tenantTreeReqVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 当前节点编码
        String currentLocationCode = loginUser.getUser().getPlatformCode();
        log.info("当前登录节点编码：{}",JSON.toJSONString(currentLocationCode));
        //判断筛选条件是否为运营研判未开启
        if (!CollectionUtils.isEmpty(tenantTreeReqVO.getOperationList())
                && tenantTreeReqVO.getOperationList().get(0).equals(Constants.ZERO_STR)){
            tenantTreeReqVO.setOperationFlag(true);
        }
        //查询客户基本信息
        List<TenantMangeResultVO> list = customManageMapper.getOrderTenantInfo(tenantTreeReqVO);
        List<String> customerIdList = new ArrayList<>();
        Set<String> nameSet =new HashSet<>();
        for (TenantMangeResultVO tenantMangeResultVO : list) {
            customerIdList.add(tenantMangeResultVO.getCustomerCode());
            //定制化登陆需求
            generateEncryptedAddress(tenantMangeResultVO);
        }
        if (!CollectionUtils.isEmpty(customerIdList)) {
            tenantTreeReqVO.setTenantIdList(customerIdList);
            //查询客户订单数量
            List<Map> orderNumList = customManageMapper.getOrderNum(customerIdList);
            //查询客户有效设备数量
            List<Map> devNumList = customManageMapper.getDevNum(customerIdList);
            //查询有效客户订单数量
            List<Map> validOrderNumList = customManageMapper.getValidOrderNum(customerIdList);
            //查询订购客户列表
            List<String> orderCustomerList = customManageMapper.getOrderCustomerList(customerIdList);

            //查询客户节点信息
            List<TenantLocationRelVO> tenantLocationRelList = customManageMapper.getTenantLocationRelList(tenantTreeReqVO);
            log.info("客户下所有节点信息：{}",JSON.toJSONString(tenantLocationRelList));

            if (!CollectionUtils.isEmpty(tenantLocationRelList)){
                //当中心节点环境时-下沉节点行业信息需从下沉节点行业信息表sys_localization_industry_conf中获取
                //下沉节点环境行业信息正常从sys_industry_conf表中取-在查询客户节点信息的sql中处理
                if (Boolean.TRUE.equals(tenantTreeReqVO.getIsCenterNodeFlag())){
                    List<TenantLocationRelVO> allIndustryInfoList = new ArrayList<>();
                    //中心节点行业编码
                    List<String> centerIndustryCodeList = tenantLocationRelList.stream().filter(item -> item.getLocationCode().equals(Constants.CENTER_LOCATION))
                            .map(TenantLocationRelVO::getIndustryCode).collect(Collectors.toList());
                    //下沉节点行业编码
                    List<String> locationIndustryCodeList = tenantLocationRelList.stream().filter(item -> !item.getLocationCode().equals(Constants.CENTER_LOCATION))
                            .map(TenantLocationRelVO::getIndustryCode).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(centerIndustryCodeList)){
                        //中心节点行业编码对应行业信息
                        List<TenantLocationRelVO> centerIndustryInfoList = customManageMapper.getCenterIndustryInfo(centerIndustryCodeList);
                        allIndustryInfoList.addAll(centerIndustryInfoList);
                    }
                    if (!CollectionUtils.isEmpty(locationIndustryCodeList)){
                        //下沉节点行业编码对应行业信息
                        List<TenantLocationRelVO> locationIndustryInfoList = customManageMapper.getLocationIndustryInfo(locationIndustryCodeList);
                        allIndustryInfoList.addAll(locationIndustryInfoList);
                    }
                    if (!CollectionUtils.isEmpty(allIndustryInfoList)){
                        tenantLocationRelList.forEach(item ->{
                            //匹配各节点行业名称
                            for (TenantLocationRelVO industryInfo : allIndustryInfoList){
                                if (item.getLocationCode().equals(industryInfo.getLocationCode())
                                        && item.getIndustryCode().equals(industryInfo.getIndustryCode())){
                                    item.setIndustryName(industryInfo.getIndustryName());
                                    break;
                                }
                            }
                        });

                    }
                }
                //查询客户节点有效订单数
                List<Map> locationValidOrderNumList = customManageMapper.getLocationValidOrderNum(customerIdList);
                //查询客户节点有效设备数
                List<Map> locationValidDevNumList = customManageMapper.getLocationValidDevNum(customerIdList);
                //查询运营研判信息
                List<TenantLocationRelVO> operationInfoList = customManageMapper.getOperationInfo(customerIdList);
                //存储运营研判修改人名字
                operationInfoList.forEach(area ->{
                    if (!StringUtils.isEmpty(area.getOperationModifier())){
                        nameSet.add(area.getOperationModifier());
                    }
                });
                for (TenantLocationRelVO item : tenantLocationRelList){
                    locationValidOrderNumList.stream().filter(area ->
                            (Objects.equals(item.getTenantId(),area.get(Constants.CUSTOMERCODE))
                                &&Objects.equals(item.getLocationCode(),area.get(Constants.LOCATIONCODE))
                                &&Objects.equals(item.getIndustryCode(),area.get(Constants.INDUSTRYCODE))
                                &&Objects.equals(item.getAppVerCode(),area.get(Constants.APPVERCODE)))
                    ).forEach(area -> {
                        item.setLocationValidOrderNum(area.get(Constants.LOCATIONVALIDORDERNUM).toString());
                    });
                    locationValidDevNumList.stream().filter(area ->
                            (Objects.equals(item.getTenantId(),area.get(Constants.CUSTOMERCODE))
                                    &&Objects.equals(item.getLocationCode(),area.get(Constants.LOCATIONCODE))
                                    &&Objects.equals(item.getIndustryCode(),area.get(Constants.INDUSTRYCODE))
                                    &&Objects.equals(item.getAppVerCode(),area.get(Constants.APPVERCODE)))
                    ).forEach(area -> {
                        item.setLocationValidDevNum(area.get(Constants.LOCATIONVALIDDEVNUM).toString());
                    });
                    //存储第三方告警修改人名字
                    if (!StringUtils.isEmpty(item.getThirdAlarmModifier())){
                        nameSet.add(item.getThirdAlarmModifier());
                    }
                    //判断节点是否为当前节点
                    item.setIsCurrentNode(org.apache.commons.lang3.StringUtils.equals(item.getLocationCode(), currentLocationCode));
                    if (item.getLocationValidOrderNum().equals(Constants.ZERO_STR)){
                        //判断节点下是否有有效订单（是否有退订、失效）--无的话运营研判、第三方告警为关闭状态
                        item.setIsOperation(Constants.ZERO_STR);
                        item.setIsOperationName("未开启");
                        item.setThirdAlarmStatus(Constants.ZERO);
                        item.setThirdAlarmStatusName("未开启");
                    }else {
                        //设置运营研判信息
                        List<TenantLocationRelVO> operList = operationInfoList.stream().filter(area ->
                                area.getTenantId().equals(item.getTenantId()) && area.getIndustryCode().equals(item.getIndustryCode())
                                        && area.getAppVerCode().equals(item.getAppVerCode()) && area.getLocationCode().equals(item.getLocationCode())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(operList)){
                            if (operList.size()>1){
                                //说明有开启也有未开启---这种情况以开启为准
                                operList.forEach(area -> {
                                    if (Constants.YES_FLAG.equals(area.getIsOperation())){
                                        item.setIsOperation(area.getIsOperation());
                                        item.setIsOperationName(area.getIsOperationName());
                                        item.setOperationModifier(area.getOperationModifier());
                                        item.setOperationModifierDate(area.getOperationModifierDate());
                                    }
                                });
                            }else {
                                //只有一条--直接用
                                TenantLocationRelVO operationInfo = operList.get(0);
                                item.setIsOperation(operationInfo.getIsOperation());
                                item.setIsOperationName(operationInfo.getIsOperationName());
                                item.setOperationModifier(operationInfo.getOperationModifier());
                                item.setOperationModifierDate(operationInfo.getOperationModifierDate());
                            }
                        }
                    }
                    //把非当前节点的第三方告警状态名称设置为空
                    if (!currentLocationCode.equals(item.getLocationCode())){
                        item.setThirdAlarmStatusName("");
                        item.setThirdAlarmStatus(2); //不展示
                    }
                }

            }

            //有运营研判筛选条件
            if (!CollectionUtils.isEmpty(tenantTreeReqVO.getOperationList())){
                tenantLocationRelList = tenantLocationRelList.stream().filter(item -> tenantTreeReqVO.getOperationList().contains(item.getIsOperation())).collect(Collectors.toList());
            }
            if (!StringUtils.isEmpty(tenantTreeReqVO.getOperationModifier())){
                tenantLocationRelList = tenantLocationRelList.stream().filter(item -> !StringUtils.isEmpty(item.getOperationModifier())
                        && item.getOperationModifier().contains(tenantTreeReqVO.getOperationModifier())).collect(Collectors.toList());
            }
            if (!StringUtils.isEmpty(tenantTreeReqVO.getOperationModifierDateStart()) && !StringUtils.isEmpty(tenantTreeReqVO.getOperationModifierDateEnd())){
                Date operationDateStart = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,tenantTreeReqVO.getOperationModifierDateStart());
                Date operationDateEnd = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,tenantTreeReqVO.getOperationModifierDateEnd());
                tenantLocationRelList = tenantLocationRelList.stream()
                        .filter(rel -> !StringUtils.isEmpty(rel.getOperationModifierDate()))
                        .filter(item -> {
                    Date operationDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,item.getOperationModifierDate());
                    return operationDate.after(operationDateStart) && operationDate.before(operationDateEnd);
                }).collect(Collectors.toList());
            }


            //按客户编码匹配，将订单数量存入list
            for(TenantMangeResultVO item : list){
                orderNumList.stream().filter(area -> Objects.equals(item.getCustomerCode(), area.get(Constants.CUSTOMERCODE))).forEach(area -> {
                    item.setOrderNum(area.get(Constants.ORDERNUM).toString());
                });
                devNumList.stream().filter(area -> Objects.equals(item.getCustomerCode(), area.get(Constants.CUSTOMERCODE))).forEach(area -> {
                    item.setDevNum(area.get(Constants.DEVNUM).toString());
                });
                validOrderNumList.stream().filter(area -> Objects.equals(item.getCustomerCode(), area.get(Constants.CUSTOMERCODE))).forEach(area -> {
                    item.setValidOrderNum(area.get(Constants.ORDERNUM).toString());
                });

                if(orderCustomerList.contains(item.getCustomerCode() )){
                    item.setCustomerStatus(Constants.YES_FLAG);
                    item.setCustomerStatusName(Constants.USEFUL);
                }else{
                    item.setCustomerStatus(Constants.ONLINE_STATUS_ZERO);
                    item.setCustomerStatusName(Constants.UNUSEFUL);
                }
                if(!StringUtils.isEmpty(item.getOpenModifier())){
                    nameSet.add(item.getOpenModifier());
                }
                if(!StringUtils.isEmpty(item.getApiCreateBy())){
                    nameSet.add(item.getApiCreateBy());
                }
            }
            if(!CollectionUtils.isEmpty(nameSet)){
                Map<String, SysUser> nameMap = sysUserMapper.getMapByUserNames(nameSet);
                if (MapUtils.isNotEmpty(nameMap)) {
                    list.forEach(x -> {
                        SysUser apiCreateUser = nameMap.get(x.getOpenModifier());
                        x.setApiCreateName(apiCreateUser == null ? "" : apiCreateUser.getNickName());
                    });
                    tenantLocationRelList.forEach(x -> {
                        SysUser operationUser = nameMap.get(x.getOperationModifier());
                        SysUser thirdAlarmUser = nameMap.get(x.getThirdAlarmModifier());
                        x.setOperationModifierName(operationUser == null ? "" : operationUser.getNickName());
                        x.setThirdAlarmModifierName(thirdAlarmUser == null? "" : thirdAlarmUser.getNickName());
                    });
                }
            }
            List<TenantLocationRelVO> finalTenantLocationRelList = tenantLocationRelList;
            log.info("经过处理后客户下所有节点信息：{}",JSON.toJSONString(finalTenantLocationRelList));
            list.forEach(item ->{
                List<TenantLocationRelVO> locationRelVOList = new ArrayList<>();
                for (TenantLocationRelVO locationRelVO : finalTenantLocationRelList){
                    if (item.getCustomerCode().equals(locationRelVO.getTenantId())){
                        locationRelVOList.add(locationRelVO);
                    }
                }
                item.setTenantLocationRelVOList(locationRelVOList);
            });
            log.info("客户列表信息：{}",JSON.toJSONString(list));
        }
        return list;
    }

    @DataScope(deptColumn = "",tenantColumn = "tenant.tenant_id")
    @Override
    public  List<TenantMangeResultVO> queryTenantInfoByPlatCode(TenantTreeReqVO tenantTreeReqVO) {
        return queryTenantInfoByPlatCodeCommon(tenantTreeReqVO);
    }

    @Override
    public List<TenantMangeResultVO> queryTenantInfoByPlatCodeCommon(TenantTreeReqVO tenantTreeReqVO) {
        List<String> tenantIdList = customManageMapper.queryTenantIdListByPlatCode(tenantTreeReqVO.getPlatCode());
        tenantTreeReqVO.setTenantIdList(tenantIdList);
        //查询客户基本信息
        List<TenantMangeResultVO> resultList = customManageMapper.selectTenantInfoList(tenantTreeReqVO);
        if (CollectionUtils.isNotEmpty(tenantIdList)) {
            resultList.forEach(x -> x.setIsRel(tenantIdList.contains(x.getCustomerCode()) ? 1 : 0));
        }
        return resultList;
    }

    /**
     * @Author：liuxt
     * @Description：关键字查询租户信息列表(铁塔视角)
     * @Date：2022/8/5 11:34
     * @param tenantVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    @DataScope(tenantColumn = "t.tenant_id")
    public Result queryTenantInfo(TenantVO tenantVO){
//            LoginUser currentUser = SecurityUtils.getLoginUser();
//            List<String> list = RedisUtil.getList(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + currentUser.getUser().getDeptId(), String.class, RedisDBType.PRIMARY);
//            if(CollUtil.isNotEmpty(list)){
//                tenantVO.setTenantList(list);
//            }
        return  queryTenantInfoCommon(tenantVO);
    }
    @Override
    public Result queryTenantInfoCommon(TenantVO tenantVO){
        List<TenantVO> tenantList= customManageMapper.queryTenantInfo(tenantVO);
        return  Result.data(tenantList);
    }

    /**
     * @Author：liuxt
     * @Description：保存api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createAuthConfig(AuthRegisterApiVO vo){
        try{
            if(!StringUtils.isEmpty(vo.getAppKey())){
                vo.setAppKey(AESUtil.decryptBase64(vo.getAppKey()));
            }
            if(!StringUtils.isEmpty(vo.getSecretKey())){
                vo.setSecretKey(AESUtil.decryptBase64(vo.getSecretKey()));
            }
            //查询客户状态是否有效
            List<String> list = new ArrayList();
            list.add(vo.getCustomerCode());
            List<String> orderCustomerList = customManageMapper.getOrderCustomerList(list);
            if(CollectionUtils.isEmpty(orderCustomerList)){
                return  Result.fail(Constants.INVALID_CUSTOMER_NOT_ALLOW_OPER);
            }
            AuthRegisterApiVO result = new AuthRegisterApiVO();
            String  id = "";
            List<AuthRegisterFuncApiVO> responseList = vo.getAuthRegisterFuncApiList();
            if(Constants.ONLINE_STATUS_ZERO.equals(vo.getIsOpen())){//如果否 没数据  则不进行任何操作
                //查询api注册表
                AuthRegisterApiVO tempAuthRegisterApiVO = new AuthRegisterApiVO();
                tempAuthRegisterApiVO.setIsOpen(Constants.YES_FLAG);
                tempAuthRegisterApiVO.setCustomerCode(vo.getCustomerCode());
                result = customManageMapper.getAuthConfig(tempAuthRegisterApiVO);
                //如果没查到，直接返回提示
                if(ObjectUtils.isEmpty(result)){
                    return  Result.success(Constants.SUCCESS_MSG);
                }else{
                    id = result.getId();
                    vo.setId(id);
                    vo.setOpenModifier(SecurityUtils.getUsername());
                    //更新api注册表
                    customManageMapper.updateAuthRegisterNew(vo);
                }
            }else if(Constants.YES_FLAG.equals(vo.getIsOpen())){//如果是 处理逻辑
                AuthRegisterApiVO newTempAuthRegisterApiVO = new AuthRegisterApiVO();
                newTempAuthRegisterApiVO.setCustomerCode(vo.getCustomerCode());
                //查询api注册表
                result = customManageMapper.getAuthConfig(newTempAuthRegisterApiVO);
                //如果没查到，代表第一次配置
                if(ObjectUtils.isEmpty(result)){
                    id = IdUtil.simpleUUID();
                    vo.setId(id);
                    vo.setCreateBy(SecurityUtils.getUsername());
                    vo.setOpenModifier(SecurityUtils.getUsername());
                    //插入api注册表
                    customManageMapper.insertAuthRegister(vo);
                }else{
                    id = result.getId();
                    vo.setId(id);
                    vo.setOpenModifier(SecurityUtils.getUsername());
                    //更新api注册表
                    customManageMapper.updateAuthRegisterNew(vo);
                    //将api注册功能表该条数据置为失效
                    customManageMapper.updateAuthRegisterFunc(vo);
                }

                if(!CollectionUtils.isEmpty(responseList)){
                    for(AuthRegisterFuncApiVO authRegisterFuncApiVO:responseList) {
                        authRegisterFuncApiVO.setRegisterId(id);
                    }
                    //插入api注册功能表
                    customManageMapper.insertAuthRegisterFunc(responseList);
                }
            }

            //将客户配置的API接口，ak/sk等信息，同步至网关平台
            log.info("===API同步网关平台 start===");
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject content = new JSONObject();
                        List urlVoList = new ArrayList();
                        content.put(Constants.APPKEY,vo.getAppKey());
                        content.put(Constants.APPSECRET,vo.getSecretKey());

                        //客户维度不传ak、sk
                        content.put(Constants.CLIENTID,vo.getCustomerCode());
                        if(!CollectionUtils.isEmpty(responseList)){
                            for (int i = 0; i < responseList.size(); i++) {
                                JSONObject urlObject = new JSONObject();
                                urlObject.put(Constants.URL,responseList.get(i).getCallbackUrl());
                                if(Constants.one100.equals(responseList.get(i).getApiIntfType())){//客户端传api接口编码
                                    urlObject.put(Constants.EVENTID,responseList.get(i).getApiIntfCode());
                                }
                                urlVoList.add(urlObject);
                            }
                        }

                        content.put(Constants.URLVOLIST,urlVoList);
//                        Map<String, String> headerMap = new HashMap<>();
                        //将api信息发送给网关 入参：url，content，headerMap
                        log.info("appSignAuth接口请求参数为："+content);
                        Result responseResult = wangGuanFeignClient.appSignAuth(content);
//                        HttpResponse result = SendToOthersUtil.sendAPI(url,JSONObject.toJSONString(content),headerMap);
                        log.info("appSignAuth接口返回参数为："+responseResult);
                        //将api信息发送给网关 返回：result
                    } catch (Exception e) {
                        log.error("===API同步网关平台 error",e);
                    }
                }
            });
            thread.start();
            log.info("===API同步网关平台 finish===");
            return  Result.success(Constants.SUCCESS_MSG);
        }catch (Exception e){
            log.error("createAuthConfig错误：",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return  Result.fail(Constants.FAIL_MSG);
        }
    }



    /**
     * @Author：liuxt
     * @Description：更新租户信息
     * @Date：2022/8/5 11:34
     * @param map
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    public Result  updateTenantInfo(Map map){

        int result=customManageMapper.updateTenantInfo(map);
        return  Result.data(result);
    }

    /**
     * @Author：liuxt
     * @Description：更新租户信息(新)
     * @Date：2022/8/5 11:34
     * @param alarmTypeReqVo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateTenantInfoNew(AlarmTypeReqVo alarmTypeReqVo){

        try{
            //查询该行业节点下是否有有效订单数
            List<TenantLocationRelVO> customerValidOrder = customManageMapper.getCustomerValidOrderNum(alarmTypeReqVo);
            if(CollectionUtils.isEmpty(customerValidOrder)){
                return  Result.fail(Constants.INVALID_CUSTOMER_NOT_ALLOW_OPER);
            }
            String currentLocationCode = "";
            if (Boolean.FALSE.equals(alarmTypeReqVo.getSyncFlag())){
                //异步同步过来的数据不用设置修改人
                alarmTypeReqVo.setModifier(SecurityUtils.getUsername());
                //获取当前登录节点
                currentLocationCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
                //用户只能修改当前节点的数据
                if (!currentLocationCode.equals(alarmTypeReqVo.getAcceptNode())){
                    return Result.fail("只能修改当前节点的数据！");
                }
                //判断并行研判的研判节点是否为当前节点
                List<AlarmTypeReqVo> alarmTypeList = alarmTypeReqVo.getAlarmTypeList();
                List<AlarmTypeReqVo> parallelAlarmTypeList = alarmTypeList.stream().filter(item -> Constants.TWO_FLAG.equals(item.getOperationType())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(parallelAlarmTypeList)){
                    String operationNode = parallelAlarmTypeList.get(0).getOperationNode();
                    if (!currentLocationCode.equals(operationNode)){
                        return Result.fail("并行研判时研判节点必须为当前节点！");
                    }
                }
            }
            //不再修改客户表中的运营研判信息
//        result=customManageMapper.updateTenantInfoNew(alarmTypeReqVo);
            //先删后插
            customManageMapper.updateTenantOperation(alarmTypeReqVo);
            if (alarmTypeReqVo.getIsOperation().equals("1")){
                List<AlarmTypeReqVo> alarmTypeList = alarmTypeReqVo.getAlarmTypeList();
                List<String> serialAlarmTypeCodeList = new ArrayList<>();
                List<String> parallelAlarmTypeCodeList = new ArrayList<>();
                for (AlarmTypeReqVo alarmType : alarmTypeList){
                    String[] split = alarmType.getOperationAlarmTypeStr().split(",");
                    List<String> alarmTypeCodeList = Arrays.asList(split);
                    if (alarmType.getOperationType().equals("1")){
                        serialAlarmTypeCodeList.addAll(alarmTypeCodeList);
                    }else {
                        parallelAlarmTypeCodeList.addAll(alarmTypeCodeList);
                    }
                }
                List<String> collect = serialAlarmTypeCodeList.stream().filter(parallelAlarmTypeCodeList::contains).collect(Collectors.toList());
                if (!collect.isEmpty()){
                    return Result.fail("同一告警类型只能选择一种研判模式!");
                }
                alarmTypeList.forEach(item ->{
                    item.setIsOperation(alarmTypeReqVo.getIsOperation());
                    item.setAcceptNode(alarmTypeReqVo.getAcceptNode());
                    item.setIndustryCode(alarmTypeReqVo.getIndustryCode());
                    item.setAppVerCode(alarmTypeReqVo.getAppVerCode());
                    item.setTenantId(alarmTypeReqVo.getTenantId());
                    item.setModifier(alarmTypeReqVo.getModifier());
                    item.setUpdateTime(DateUtils.getNowDate());
                    item.setDeleteFlag(Constants.ZERO);
                });
                customManageMapper.insertTenantOperation(alarmTypeList);
            }

            //判断是否属地化
            SysConfig dependenceManageConfig = sysConfigService.selectConfigByKey("dependence_manage");

            //同步到所有上级节点--中心节点和属地化不推
            if (Boolean.FALSE.equals(alarmTypeReqVo.getSyncFlag()) && dependenceManageConfig.getConfigValue().equals(com.chinatower.platform.constants.Constants.TWO)){
                log.info("当前执行同步操作的节点:{}",JSON.toJSONString(currentLocationCode));
                operationPushDeal(alarmTypeReqVo);
            }
            return Result.success("运营研判配置成功！");
        }catch (Exception e){
            log.error("运营研判配置异常：",e);
            return Result.fail("运营研判配置异常!");
        }
    }

    private void operationPushDeal(AlarmTypeReqVo alarmTypeReqVo){
        log.info("updateTenantInfoNew syncOperationInfo 运营研判操作同步逻辑");
        //异步处理运营研判推送
        CompletableFutureHelper.orTimeout(
                CompletableFuture.runAsync(() -> this.syncOperationInfo(alarmTypeReqVo), THREAD_POOL_PUSH)
                        .exceptionally(e -> {
                            log.error("运营研判操作推送失败, e :", e);
                            return null;
                        }),
                10, TimeUnit.SECONDS).exceptionally(e -> {
                    log.error("运营研判操作推送超时, e:", e);
                    return null;
                }
        );
    }
    private void operationBatchPushDeal(UpdateTenantReqVO updateTenantReqVO){
        log.info("updateTenantInfoNew syncOperationInfo 运营研判操批量作同步逻辑");
        //异步处理运营研判推送
        CompletableFutureHelper.orTimeout(
                CompletableFuture.runAsync(() -> this.syncOperationInfoBatch(updateTenantReqVO), THREAD_POOL_PUSH)
                        .exceptionally(e -> {
                            log.error("运营研判批量操作推送失败, e :", e);
                            return null;
                        }),
                10, TimeUnit.SECONDS).exceptionally(e -> {
                    log.error("运营研判批量操作推送超时, e:", e);
                    return null;
                }
        );
    }


    public void syncOperationInfoBatch(UpdateTenantReqVO updateTenantReqVO){
        updateTenantReqVO.setSyncFlag(true);
        //批量研判的受理节点为当前登录账号的节点
        String locationCode = updateTenantReqVO.getCurrentLocationCode();
        //查询本节点的所有上级节点
        List<String> parentLocationCodeList = new ArrayList<>();
        String parentCodeStr = customManageMapper.queryChildPlatformCode(locationCode);
        if (!StringUtils.isEmpty(parentCodeStr)){
            parentLocationCodeList = Splitter.on(",").splitToList(parentCodeStr);
        }
        log.info("syncOperationInfo parentLocationCodeList:{}", JSON.toJSONString(parentLocationCodeList));
        if (!CollectionUtils.isEmpty(parentLocationCodeList)){
            List<OperationPushRecordVO> resultRecordList = new ArrayList<>();
            for (String pushLocationCode : parentLocationCodeList){
                String pushStatus = Constants.YES_FLAG;
                String pushFailReason = "";
                try {
                    //处理推送逻辑
                    Result result = RemoteNodeUtils.create(pushLocationCode)
                            .getFeign(CustomFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                            .updateTenantInfoBatchSync(updateTenantReqVO);
                    log.info("syncOperationInfoBatch 推送结果：{}",JSON.toJSONString(result));
                    if (result.getCode() != ResultCode.SUCCESS.getCode()){
                        pushStatus = Constants.ZERO_STR;
                        pushFailReason = result.getMsg();
                    }
                } catch (Exception e) {
                    log.error("CustomManageServiceImpl updateTenantInfoBatchSync error, e:", e);
                    pushStatus = Constants.ZERO_STR;
                    pushFailReason = e.getMessage();
                }
                resultRecordList.addAll(assembleInsertOperationBatchPushDO(pushFailReason,pushStatus, pushLocationCode, updateTenantReqVO));
            }
            // 处理新增推送记录逻辑
            if (!CollectionUtils.isEmpty(resultRecordList)) {
                //先查询每个客户是否已经有过推送
                OperationPushRelVO operationPushRelVO = new OperationPushRelVO();
                operationPushRelVO.setParentLocationCodeList(parentLocationCodeList);
                List<OperationPushRelVO> recordInfoList = customManageMapper.queryOperationPushRecordList(operationPushRelVO);
                List<OperationPushRecordVO> updateList = resultRecordList.stream().filter(item -> {
                    long count = recordInfoList.stream().filter(e -> e.getAcceptNode().equals(item.getAcceptNode()) && e.getTenantId().equals(item.getTenantId())
                            && e.getIndustryCode().equals(item.getIndustryCode()) && e.getAppVerCode().equals(item.getAppVerCode())).count();
                    return count > 0;
                }).collect(Collectors.toList());
                List<OperationPushRecordVO> insertList = resultRecordList.stream().filter(item -> {
                    long count = recordInfoList.stream().filter(e -> e.getAcceptNode().equals(item.getAcceptNode()) && e.getTenantId().equals(item.getTenantId())
                            && e.getIndustryCode().equals(item.getIndustryCode()) && e.getAppVerCode().equals(item.getAppVerCode())).count();
                    return count == 0;
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(updateList)){
                    customManageMapper.updateOperationPushRecordBatch(updateList);
                }
                if (!CollectionUtils.isEmpty(insertList)){
                    customManageMapper.insertOperationPushRecordBatch(insertList);
                }

            }
        }

    }

    private void syncOperationInfo(AlarmTypeReqVo alarmTypeReqVo){

        alarmTypeReqVo.setSyncFlag(true);
        String locationCode = alarmTypeReqVo.getAcceptNode();
        if (StringUtils.isEmpty(locationCode)){
            log.error("syncOperationInfo 受理节点为空，不处理同步逻辑");
            return;
        }
        //查询本节点的所有上级节点
        List<String> parentLocationCodeList = new ArrayList<>();
        String parentCodeStr = customManageMapper.queryChildPlatformCode(locationCode);
        if (!StringUtils.isEmpty(parentCodeStr)){
            parentLocationCodeList = Splitter.on(",").splitToList(parentCodeStr);
        }
        log.info("syncOperationInfo parentLocationCodeList:{}", JSON.toJSONString(parentLocationCodeList));
        if (!CollectionUtils.isEmpty(parentLocationCodeList)){
            //推送记录集合
            List<OperationPushRecordVO> recordList = new ArrayList<>();
            for (String pushLocationCode : parentLocationCodeList){
                log.info("推送节点：{}",JSON.toJSONString(pushLocationCode));
                String pushStatus = Constants.YES_FLAG;
                String pushFailReason = "";
                try {
                    //处理推送逻辑
                    Result result = RemoteNodeUtils.create(pushLocationCode)
                            .getFeign(CustomFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                            .updateTenantInfoNewSync(alarmTypeReqVo);
                    log.info("syncOperationInfo 推送结果：{}",JSON.toJSONString(result));
                    if (result.getCode() != ResultCode.SUCCESS.getCode()){
                        pushStatus = Constants.ZERO_STR;
                        pushFailReason = result.getMsg();
                    }
                } catch (Exception e) {
                    log.error("CustomManageServiceImpl updateTenantInfoNewSync error, e:", e);
                    pushStatus = Constants.ZERO_STR;
                    pushFailReason = e.getMessage();
                }
                recordList.add(assembleInsertOperationPushDO(pushFailReason,pushStatus,pushLocationCode,alarmTypeReqVo));
            }
            // 处理新增推送记录逻辑
            if (!CollectionUtils.isEmpty(recordList)) {
                OperationPushRelVO operationPushRelVO = new OperationPushRelVO();
                operationPushRelVO.setTenantId(alarmTypeReqVo.getTenantId());
                operationPushRelVO.setIndustryCode(alarmTypeReqVo.getIndustryCode());
                operationPushRelVO.setAppVerCode(alarmTypeReqVo.getAppVerCode());
                operationPushRelVO.setParentLocationCodeList(parentLocationCodeList);
                //先查询记录表中是否有本次要插入的数据
                List<OperationPushRelVO> locationRelVOList = customManageMapper.queryOperationPushRecordList(operationPushRelVO);
                if (CollectionUtils.isEmpty(locationRelVOList)){
                    //没有则插入
                    customManageMapper.insertOperationPushRecordBatch(recordList);
                }else {
                    //有则修改
                    customManageMapper.updateOperationPushRecordBatch(recordList);
                }

            }
        }

    }

    private List<OperationPushRecordVO> assembleInsertOperationBatchPushDO(String pushFailReason, String pushStatus, String pushLocationCode,UpdateTenantReqVO updateTenantReqVO){
        List<TenantInfoVO> customerInfoList = updateTenantReqVO.getCustomerInfoList();
        List<OperationPushRecordVO> recordVOList = new ArrayList<>();
        for (TenantInfoVO tenantInfoVO : customerInfoList){
            OperationPushRecordVO pushRecordVO = new OperationPushRecordVO();
            Date date = new Date();
            pushRecordVO.setAcceptNode(pushLocationCode);
            pushRecordVO.setTenantId(tenantInfoVO.getTenantId());
            pushRecordVO.setIndustryCode(tenantInfoVO.getIndustryCode());
            pushRecordVO.setAppVerCode(tenantInfoVO.getAppVerCode());
            pushRecordVO.setPushTime(date);
            pushRecordVO.setPushStatus(pushStatus);
            pushRecordVO.setPushFailReason(pushFailReason);
            pushRecordVO.setCreator(updateTenantReqVO.getOperationModifier());
            pushRecordVO.setCreateTime(date);
            pushRecordVO.setModifier(updateTenantReqVO.getOperationModifier());
            pushRecordVO.setUpdateTime(date);
            pushRecordVO.setDeleteFlag(Constants.ZERO);
            recordVOList.add(pushRecordVO);
        }

        return recordVOList;
    }

    private OperationPushRecordVO assembleInsertOperationPushDO(String pushFailReason, String pushStatus, String pushLocationCode, AlarmTypeReqVo alarmTypeReqVo){
        OperationPushRecordVO pushRecordVO = new OperationPushRecordVO();
        Date date = new Date();
        pushRecordVO.setAcceptNode(pushLocationCode);
        pushRecordVO.setTenantId(alarmTypeReqVo.getTenantId());
        pushRecordVO.setIndustryCode(alarmTypeReqVo.getIndustryCode());
        pushRecordVO.setAppVerCode(alarmTypeReqVo.getAppVerCode());
        pushRecordVO.setPushTime(date);
        pushRecordVO.setPushStatus(pushStatus);
        pushRecordVO.setPushFailReason(pushFailReason);
        pushRecordVO.setCreator(alarmTypeReqVo.getModifier());
        pushRecordVO.setCreateTime(date);
        pushRecordVO.setModifier(alarmTypeReqVo.getModifier());
        pushRecordVO.setUpdateTime(date);
        pushRecordVO.setDeleteFlag(Constants.ZERO);
        return pushRecordVO;
    }



    @Override
    public Result<List<PlatformInfoVo>> queryAllLocationCode(PlatformInfoVo platformInfoReqVo) {

        try{
            if (StringUtils.isEmpty(platformInfoReqVo.getPlatformCode())){
                //批量研判-研判节点为当前登录账号节点
                platformInfoReqVo.setPlatformCode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
            }
            String locationCode = platformInfoReqVo.getPlatformCode();
            //查询本节点以及上级所有节点信息
            List<String> parentLocationCodeList = new ArrayList<>();
            parentLocationCodeList.add(locationCode);
            String parentCodeStr = customManageMapper.queryChildPlatformCode(locationCode);
            parentLocationCodeList.addAll(Splitter.on(",").splitToList(parentCodeStr));
            List<PlatformInfoVo> platformInfoReqVos = customManageMapper.getParentLocationCodeInfo(parentLocationCodeList);
            List<PlatformInfoVo> resultList = buildTree(null, platformInfoReqVos);
            return Result.data(resultList);
        }catch (Exception e){
            log.error("查询节点信息异常：",e);
            return Result.fail("查询节点信息失败!");
        }

    }

    @Override
    public Result<PageInfo<TenantLocationRelVO>> queryTenantList(TenantLocationReqVO reqVO) {
        try {
            //查询客户基本信息
            TenantTreeReqVO treeReqVO = new TenantTreeReqVO();
            List<TenantMangeResultVO> list = customManageMapper.getOrderTenantInfo(treeReqVO);
            List<String> customerIdList = new ArrayList<>();
            for (TenantMangeResultVO tenantMangeResultVO : list) {
                customerIdList.add(tenantMangeResultVO.getCustomerCode());
            }
            List<TenantLocationRelVO> tenantLocationRelList = new ArrayList<>();
            Result<PageInfo<TenantLocationRelVO>> result;
            if (!CollectionUtils.isEmpty(customerIdList)){
                treeReqVO.setTenantIdList(customerIdList);
                if (!StringUtils.isEmpty(reqVO.getTenantId())){
                    treeReqVO.setTenantId(reqVO.getTenantId());
                }
                if (!StringUtils.isEmpty(reqVO.getLocationCode())){
                    treeReqVO.setLocationCode(reqVO.getLocationCode());
                }
                if (!StringUtils.isEmpty(reqVO.getIndustryCode())){
                    treeReqVO.setIndustryCode(reqVO.getIndustryCode());
                }
                //设置当前节点--列表展示当前登录用户本节点受理的全部客户信息
                treeReqVO.setCurrentLocationCode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
                treeReqVO.setOrderFlag(Constants.YES_FLAG);
                //查询客户节点信息
                PageMethod.startPage(reqVO.getPageNum(),reqVO.getPageSize());
                tenantLocationRelList= customManageMapper.getTenantLocationRelList(treeReqVO);
            }
            PageInfo<TenantLocationRelVO> pageInfo = new PageInfo<>(tenantLocationRelList);
            result = Result.success("查询成功！");
            result.setData(pageInfo);
            return result;
        }catch (Exception e){
            log.error("查询批量配置客户列表异常：",e);
            return Result.fail("查询节点信息异常!");
        }
    }

    @Override
    public List<OperationPushRelVO> queryOperationPushList(OperationPushRelVO operationPushRelVO) {
        try {
            if (operationPushRelVO.getPageSize() == null || operationPushRelVO.getPageSize() <= 0){
                //默认每页13条
                operationPushRelVO.setPageSize(13);
            }
            if (operationPushRelVO.getPageNum() == null || operationPushRelVO.getPageNum() <=0){
                operationPushRelVO.setPageNum(1);
            }
            //查询本节点的所有上级节点
            String locationCode = operationPushRelVO.getLocationCode();
            String parentCodeStr = customManageMapper.queryChildPlatformCode(locationCode);
            log.info("parentCodeStr:{}",JSON.toJSONString(parentCodeStr));
            if (StringUtils.isEmpty(parentCodeStr)) {
                return Lists.newArrayList();
            }
            List<String> parentLocationCodeList = Splitter.on(",").splitToList(parentCodeStr);
            operationPushRelVO.setParentLocationCodeList(parentLocationCodeList);
            log.info("推送节点编码集合:{}",JSON.toJSONString(parentLocationCodeList));
            List<OperationPushRelVO> operationPushRelVOS = customManageMapper.queryOperationPushRecordList(operationPushRelVO);
            log.info("推送列表信息：{}",JSON.toJSONString(operationPushRelVOS));
            return operationPushRelVOS;
        }catch (Exception e){
            log.error("运营研判推送列表查询失败：",e);
            return Collections.emptyList();
        }
    }

    @Override
    public Result<String> pushOperationInfo(OperationPushRelVO operationPushRelVO) {

        log.info("手动推送入参：{}",JSON.toJSONString(operationPushRelVO));
        //先查出该节点下最新的运营研判信息
        List<AlarmTypeReqVo> alarmTypeReqVoList = customManageMapper.queryOperationInfoList(operationPushRelVO);
        log.info("运营研判信息集合：{}",JSON.toJSONString(alarmTypeReqVoList));
        //封装推送数据
        AlarmTypeReqVo alarmTypeReqVo = new AlarmTypeReqVo();
        alarmTypeReqVo.setSyncFlag(true);
        alarmTypeReqVo.setTenantId(operationPushRelVO.getTenantId());
        alarmTypeReqVo.setAcceptNode(operationPushRelVO.getLocationCode());
        alarmTypeReqVo.setIndustryCode(operationPushRelVO.getIndustryCode());
        alarmTypeReqVo.setAppVerCode(operationPushRelVO.getAppVerCode());
        alarmTypeReqVo.setModifier(SecurityUtils.getLoginUser().getUser().getUserName());
        if (CollectionUtils.isEmpty(alarmTypeReqVoList)){
            //运营研判集合为空--关闭
            alarmTypeReqVo.setIsOperation(Constants.ZERO_STR);
        }else {
            alarmTypeReqVo.setIsOperation(Constants.YES_FLAG);
            alarmTypeReqVo.setAlarmTypeList(alarmTypeReqVoList);
        }
        log.info("手动推送信息：{}",JSON.toJSONString(alarmTypeReqVo));
        //封装推送记录信息
        List<OperationPushRecordVO> updateRecordList = new ArrayList<>();
        String pushStatus = Constants.YES_FLAG;
        String pushFailReason = "";
        String pushLocationCode = operationPushRelVO.getPushAcceptNode();
        log.info("推送节点：{}",JSON.toJSONString(pushLocationCode));
        //调用推送接口
        try {
            Result result = RemoteNodeUtils.create(pushLocationCode)
                    .getFeign(CustomFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                    .updateTenantInfoNewSync(alarmTypeReqVo);
            log.info("pushOperationInfo 推送结果：{}",JSON.toJSONString(result));
            if (result.getCode() != ResultCode.SUCCESS.getCode()){
                pushStatus = Constants.ZERO_STR;
                pushFailReason = result.getMsg();
            }
        } catch (Exception e) {
            log.error("CustomManageServiceImpl updateTenantInfoNewSync error, e:", e);
            pushStatus = Constants.ZERO_STR;
            pushFailReason = e.getMessage();
        }
        updateRecordList.add(assembleInsertOperationPushDO(pushFailReason,pushStatus,pushLocationCode,alarmTypeReqVo));
        customManageMapper.updateOperationPushRecordBatch(updateRecordList);
        if (pushStatus.equals(Constants.ZERO_STR)){
            return Result.fail("推送" + operationPushRelVO.getAcceptNodeName() + "节点失败！");
        }else {
            return Result.success("推送" + operationPushRelVO.getAcceptNodeName() + "节点成功！");
        }
    }

    @Override
    public Result updateThirdAlarm(ThirdAlarmInfoVO thirdAlarmReq) {
        try {
            //查询该行业节点下是否有有效订单数
            AlarmTypeReqVo alarmTypeReqVo = new AlarmTypeReqVo();
            BeanUtils.copyProperties(thirdAlarmReq,alarmTypeReqVo);
            //对传递过来的加密信息进行解密后赋值给原来的参数
            thirdAlarmReq.setAppKey(AESUtil.decryptBase64(thirdAlarmReq.getAppKey()));
            thirdAlarmReq.setSecretKey(AESUtil.decryptBase64(thirdAlarmReq.getSecretKey()));
            List<TenantLocationRelVO> customerValidOrder = customManageMapper.getCustomerValidOrderNum(alarmTypeReqVo);
            if(CollectionUtils.isEmpty(customerValidOrder)){
                return Result.fail(Constants.INVALID_CUSTOMER_NOT_ORDER_OPER);
            }
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            String userName = loginUser.getUserName();
            Date nowDate = DateUtils.getNowDate();
            if (Objects.equals(thirdAlarmReq.getIsOpen(),Constants.ONE)){
                //开启第三方告警
                String currentPlatformCode = loginUser.getPlatformCode();
                if (!currentPlatformCode.equals(thirdAlarmReq.getAcceptNode())){
                    return Result.fail("只能修改当前节点的数据！");
                }
                if (StringUtils.isEmpty(thirdAlarmReq.getAppKey())){
                    return Result.fail("appKey不能为空！");
                }
                if (StringUtils.isEmpty(thirdAlarmReq.getThirdAlarmName())){
                    return Result.fail("第三方告警名称不能为空！");
                }
                if (thirdAlarmReq.getThirdAlarmName().length() > 20){
                    return Result.fail("第三方告警名称最多20个字符！");
                }
                if (StringUtils.isEmpty(thirdAlarmReq.getSecretKey())){
                    return Result.fail("secretKey不能为空！");
                }
                if (!StringUtils.isEmpty(thirdAlarmReq.getRemark()) && thirdAlarmReq.getRemark().length() > 200){
                    return Result.fail("简介最多支持输入200个字！");
                }
                //先查询数据库中是否已存在
                ThirdAlarmInfoVO thirdAlarmInfoVO = customManageMapper.queryThirdAlarmInfo(thirdAlarmReq);
                if (thirdAlarmInfoVO != null){
                    //已存在--修改
                    thirdAlarmReq.setModifier(userName);
                    thirdAlarmReq.setUpdateTime(nowDate);
                    customManageMapper.updateThirdAlarmInfo(thirdAlarmReq);
                }else {
                    //不存在--新增
                    thirdAlarmReq.setCreator(userName);
                    thirdAlarmReq.setCreateTime(nowDate);
                    thirdAlarmReq.setModifier(userName);
                    thirdAlarmReq.setUpdateTime(nowDate);
                    customManageMapper.insertThirdAlarmInfo(thirdAlarmReq);
                }
            }else {
                //关闭第三方告警
                //先查询数据库中是否已存在
                ThirdAlarmInfoVO thirdAlarmInfoVO = customManageMapper.queryThirdAlarmInfo(thirdAlarmReq);
                if (thirdAlarmInfoVO != null){
                    //关闭时若数据库已存在数据---修改状态   不存在则不处理
                    thirdAlarmReq.setModifier(userName);
                    thirdAlarmReq.setUpdateTime(nowDate);
                    customManageMapper.closeThirdAlarm(thirdAlarmReq);
                }
            }
            return Result.success("第三方告警配置成功！");
        }catch (Exception e){
            log.error("第三方告警配置失败：",e);
            return Result.fail("第三方告警配置异常！");
        }
    }

    @Override
    public Result<ThirdAlarmInfoVO> queryThirdAlarm(ThirdAlarmInfoVO thirdAlarmInfoVO) {

        try {
            ThirdAlarmInfoVO resultInfo = new ThirdAlarmInfoVO();
            //先查询数据库是否已有配置信息
            ThirdAlarmInfoVO thirdAlarmInfo = customManageMapper.queryThirdAlarmInfo(thirdAlarmInfoVO);
            if (!ObjectUtils.isEmpty(thirdAlarmInfo)){
                //若存在--则直接返回
                BeanUtils.copyProperties(thirdAlarmInfo,resultInfo);
            }else {
                //不存在--说明是新开启，需要设置appKey和secretKey
                resultInfo.setAppKey(UUID.randomUUID().toString().replace("-", ""));
                resultInfo.setSecretKey(UUID.randomUUID().toString().replace("-", ""));
            }
            String secretSecretKey= AESUtil.encryptBase64(resultInfo.getSecretKey());
            String secretAppKey= AESUtil.encryptBase64(resultInfo.getAppKey());
            resultInfo.setAppKey(secretAppKey);
            resultInfo.setSecretKey(secretSecretKey);
            return Result.data(resultInfo);
        }catch (Exception e){
            log.error("第三方告警查询失败：",e);
            return Result.fail("第三方告警查询异常！");
        }
    }

    public List<PlatformInfoVo> buildTree(PlatformInfoVo rootPlatform, List<PlatformInfoVo> platformInfoList){
        if(CollectionUtils.isEmpty(platformInfoList)) {
            return Collections.emptyList();
        }
        if(Objects.isNull(rootPlatform)) {
            return platformInfoList.stream().filter(e -> Constants.ZERO_STR.equals(e.getPlatformParentCode())).filter(a -> {
                a.setParentPlatform(buildTree(a,platformInfoList));
                return true;
            }).collect(Collectors.toList());
        }else {
            return platformInfoList.stream().filter(e -> rootPlatform.getPlatformCode().equals(e.getPlatformParentCode())).filter(a -> {
                a.setParentPlatform(buildTree(a,platformInfoList));
                return true;
            }).collect(Collectors.toList());
        }
    }

    /**
     * @Author：liuxt
     * @Description：获取权限配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    public Result getAuthConfig(AuthRegisterApiVO vo){
        try{
            //查询主表（api注册表）
            AuthRegisterApiVO result = customManageMapper.getAuthConfig(vo);
            if(!ObjectUtils.isEmpty(result)){
                AuthRegisterFuncApiVO authRegisterFuncApiVO = new AuthRegisterFuncApiVO();
                authRegisterFuncApiVO.setRegisterId(result.getId());
                //api注册功能表
                List<AuthRegisterFuncApiVO> list = customManageMapper.getAuthConfigFunc(authRegisterFuncApiVO);
                if(!CollectionUtils.isEmpty(list)){
                    result.setAuthRegisterFuncApiList(list);
                }
                result.setAppKey(AESUtil.encryptBase64(result.getAppKey()));
                result.setSecretKey(AESUtil.encryptBase64(result.getSecretKey()));
            }else{//如果没查到，代表第一次配置，返回ak，sk
                result = new AuthRegisterApiVO();
                result.setCustomerName(vo.getCustomerName());
                result.setCustomerCode(vo.getCustomerCode());
                result.setAppKey(AESUtil.encryptBase64(UUID.randomUUID().toString().replace("-", "")));
                result.setSecretKey(AESUtil.encryptBase64(UUID.randomUUID().toString().replace("-", "")));
            }
            return Result.data(result);
        }catch (Exception e){
            log.error("getAuthConfig错误：",e);
            return  Result.fail(Constants.FAIL_MSG);
        }

    }



    /**
     * @Author：liuxt
     * @Description：获取订单信息
     * @Date：2022/8/5 11:34
     * @param tenantOrderVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    public Result queryTenantOrder(@RequestBody TenantOrderVO tenantOrderVO) {
        Map outMap = new HashMap();
        TenantVO tenantVO = new TenantVO();
        tenantVO.setTenantId(tenantOrderVO.getCustCode());
        TenantVO tenantInfo = customManageMapper.queryTenantById(tenantVO);
        List<SysDictData> giftAlgorithmEnum = sysDictDataMapper.selectDictDataByType(Constants.SYS_GIVE_ALGORITHM);
        PageHelper.startPage(tenantOrderVO.getPageNum(), tenantOrderVO.getPageSize());
        List<CustomOrderInfoVO> showData = customManageMapper.queryCustOrderInfo(tenantOrderVO);
        PageInfo<CustomOrderInfoVO> pageInfo = new PageInfo<CustomOrderInfoVO>();
        pageInfo.setList(showData);
        for (CustomOrderInfoVO oi : showData) {
            List<CustomOrderPackageVO> packageData = oi.getPackageData();
            for (CustomOrderPackageVO pd : packageData){
                if (pd.getAlgorithmData() != null && pd.getAlgorithmData().size() != 0) {
                    Iterator<CustomOrderAlgorithmsVO> it = pd.getAlgorithmData().iterator();
                    while (it.hasNext()) {
                        CustomOrderAlgorithmsVO orderAlgorithmsVO = it.next();
                        String algorithmName = orderAlgorithmsVO.getAlgorithmName();
                        String str = orderAlgorithmsVO.getAlgorithmCode().substring(Constants.ZERO,Constants.TWO);
                        for(SysDictData sysDictData : giftAlgorithmEnum){
                            if(str.equals(sysDictData.getDictValue())){
                                orderAlgorithmsVO.setAlgorithmName(algorithmName+Constants.GIVE_MSG);
                                break;
                            }

                        }
                    }
                }
            }
        }
        outMap.put(Constants.TENANT_INFO,tenantInfo);
        outMap.put(Constants.SHOW_DATA,pageInfo);
        return Result.data(outMap);

    }


    /**
     * @Author：zhaol
     * @Description：set开启状态list
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    public void getOperationList(TenantTreeReqVO tenantTreeReqVO){
        List<String> operationList = new ArrayList<>();
        if(!org.springframework.util.StringUtils.isEmpty(tenantTreeReqVO.getIsOperation())){
            if((Constants.ONLINE_STATUS_ZERO).equals(tenantTreeReqVO.getIsOperation())){
                operationList.add(Constants.ONLINE_STATUS_ZERO);
            }else{
                operationList.add(Constants.YES_FLAG);
                operationList.add(Constants.TWO_FLAG);
            }
        }
        tenantTreeReqVO.setOperationList(operationList);
    }


    /**
     * @Author：liuxt
     * @Description：Map to Map
     * @Date：2022/8/5 11:34
     * @param sourceMap
     * @param targetMap
     * @return Map
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    public static Map mapTpMap(Map sourceMap, Map targetMap){
        if(sourceMap != null && !sourceMap.isEmpty()){
            Iterator it=sourceMap.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry=(Map.Entry)it.next();
                Object key=entry.getKey();
                if(key!=null && sourceMap.get(key)!=null) {
                    targetMap.put(key.toString(), sourceMap.get(key));
                }
            }
        }
        return targetMap;
    }



    /**
     * @Author：liuxt
     * @Description：保存api配置(暴露给退订续订调用)
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result syncApiInfo(ApiVO vo){
        try{
            if(ObjectUtils.isEmpty(vo.getOperateFlag())) {
                return Result.success(Constants.NOT_NULL_OPERATEFLAG);
            }else{
                if(Constants.YES_FLAG.equals(vo.getOperateFlag())){//订购
                    AuthRegisterFuncApiVO authRegisterFuncApiVO = new AuthRegisterFuncApiVO();
                    authRegisterFuncApiVO.setDeleteFlag(Constants.ONLINE_STATUS_ZERO);
                    authRegisterFuncApiVO.setStatus(Constants.ONLINE_STATUS_ZERO);
                    authRegisterFuncApiVO.setCustomerCode(vo.getCustomerCode());
                    //api注册表和api注册功能表关联查询 查delete_flag和status为0的(不存在is_open为0 而delete_flag和status为0的情况)
                    List<AuthRegisterFuncApiVO> authRegisterFuncApiVOList = customManageMapper.getApiAndFuncInfo(authRegisterFuncApiVO);
                    if(CollectionUtils.isEmpty(authRegisterFuncApiVOList)){
                        return  Result.success(Constants.SUCCESS_MSG);
                    }else{
                        AuthRegisterApiVO authRegisterApiVO = new AuthRegisterApiVO();
                        authRegisterApiVO.setCustomerCode(vo.getCustomerCode());
                        authRegisterApiVO.setIsOpen(Constants.YES_FLAG);
                        authRegisterApiVO.setOpenModifier(SecurityUtils.getUsername());
                        //将主表is_open更新为1
                        customManageMapper.updateAuthRegisterNew(authRegisterApiVO);

                        AuthRegisterFuncApiVO updateAuthRegisterFuncApiVO = new AuthRegisterFuncApiVO();
                        authRegisterFuncApiVO.setDeleteFlag(Constants.YES_FLAG);
                        authRegisterFuncApiVO.setStatus(Constants.YES_FLAG);
                        authRegisterFuncApiVO.setOldStatus(Constants.ONLINE_STATUS_ZERO);
                        authRegisterFuncApiVO.setOldDeleteFlag(Constants.ONLINE_STATUS_ZERO);
                        authRegisterFuncApiVO.setCustomerCode(vo.getCustomerCode());
                        //将api注册功能表delete_flag和status为0的更新为1
                        customManageMapper.updateAuthRegisterFuncStatus(authRegisterFuncApiVO);
                    }
                }else if(Constants.TWO_FLAG.equals(vo.getOperateFlag())){//退订
                    AuthRegisterApiVO authRegisterApiVO = new AuthRegisterApiVO();
                    authRegisterApiVO.setCustomerCode(vo.getCustomerCode());
                    //查询主表（api注册表）
                    AuthRegisterApiVO result = customManageMapper.getAuthConfig(authRegisterApiVO);
                    //如果没查到或者没有开放，则无需同步网关接口
                    if(ObjectUtils.isEmpty(result) || Constants.ONLINE_STATUS_ZERO.equals(result.getIsOpen())){
                        return  Result.success(Constants.SUCCESS_MSG);
                    }else{//开放的api
                        authRegisterApiVO.setIsOpen(Constants.ONLINE_STATUS_ZERO);
                        authRegisterApiVO.setOpenModifier(SecurityUtils.getUsername());
                        //将主表is_open更新为0
                        customManageMapper.updateAuthRegisterNew(authRegisterApiVO);

                        AuthRegisterFuncApiVO authRegisterFuncApiVO = new AuthRegisterFuncApiVO();
                        authRegisterFuncApiVO.setDeleteFlag(Constants.ONLINE_STATUS_ZERO);
                        authRegisterFuncApiVO.setStatus(Constants.ONLINE_STATUS_ZERO);
                        authRegisterFuncApiVO.setOldStatus(Constants.YES_FLAG);
                        authRegisterFuncApiVO.setOldDeleteFlag(Constants.YES_FLAG);
                        authRegisterFuncApiVO.setCustomerCode(vo.getCustomerCode());
                        //将api注册功能表delete_flag和status为1的更新为0
                        customManageMapper.updateAuthRegisterFuncStatus(authRegisterFuncApiVO);
                    }
                }
            }
            //将客户配置的API接口，ak/sk等信息，同步至网关平台
            log.info("===API同步网关平台 start===");
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        AuthRegisterApiVO authRegisterApiVO = new AuthRegisterApiVO();
                        authRegisterApiVO.setCustomerCode(vo.getCustomerCode());
                        //查询主表（api注册表）
                        AuthRegisterApiVO result = customManageMapper.getAuthConfig(authRegisterApiVO);

                        AuthRegisterFuncApiVO authRegisterFuncApiVO = new AuthRegisterFuncApiVO();
                        authRegisterFuncApiVO.setDeleteFlag(Constants.YES_FLAG);
                        authRegisterFuncApiVO.setStatus(Constants.YES_FLAG);
                        authRegisterFuncApiVO.setCustomerCode(vo.getCustomerCode());
                        List<AuthRegisterFuncApiVO> responseList = customManageMapper.getApiAndFuncInfo(authRegisterFuncApiVO);

                        JSONObject content = new JSONObject();
                        List urlVoList = new ArrayList();
                        content.put(Constants.APPKEY,result.getAppKey());
                        content.put(Constants.APPSECRET,result.getSecretKey());
                        content.put(Constants.CLIENTID,vo.getCustomerCode());
                        if(!CollectionUtils.isEmpty(responseList)){
                            for (int i = 0; i < responseList.size(); i++) {
                                JSONObject urlObject = new JSONObject();
                                urlObject.put(Constants.URL,responseList.get(i).getCallbackUrl());
                                if(Constants.one100.equals(responseList.get(i).getApiIntfType())){//客户端传api接口编码
                                    urlObject.put(Constants.EVENTID,responseList.get(i).getApiIntfCode());
                                }
                                urlVoList.add(urlObject);
                            }
                        }
                        content.put(Constants.URLVOLIST,urlVoList);
//                        Map<String, String> headerMap = new HashMap<>();
                        //将api信息发送给网关 入参：url，content，headerMap
                        log.info("appSignAuth接口请求参数为："+content);
//                        HttpResponse apiResult = SendToOthersUtil.sendAPI(url,JSONObject.toJSONString(content),headerMap);
                        Result responseResult = wangGuanFeignClient.appSignAuth(content);
                        log.info("appSignAuth接口返回参数为："+responseResult);
                        //将api信息发送给网关 返回：apiResult
                    } catch (Exception e) {
                        log.error("===API同步网关平台 error",e);
                    }
                }
            });
            thread.start();
            log.info("===API同步网关平台 finish===");
            return  Result.success(Constants.SUCCESS_MSG);
        }catch (Exception e){
            log.error("syncApiInfo错误：",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return  Result.fail(Constants.FAIL_MSG);
        }
    }


    /**
     * @Author：zhaozj
     * @Description：根据客户编码判断是否开启API(外部系统调用)
     * @Date：2022/9/1 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @Override
    public Result ifOpenApi(ApiVO vo){
        try{
            if(ObjectUtils.isEmpty(vo.getCustomerCode())) {
                return Result.fail(Constants.NOT_NULL_CUSTOMERCODE);
            }else{
                AuthRegisterApiVO apiVO = new AuthRegisterApiVO();
                apiVO.setCustomerCode(vo.getCustomerCode());
                //查询主表（api注册表）
                AuthRegisterApiVO result = customManageMapper.getAuthConfig(apiVO);

                if (!ObjectUtils.isEmpty(result) && !StringUtils.isEmpty(result.getIsOpen())) {
                    vo.setIsOpen(result.getIsOpen());
                    return Result.data(vo);
                } else {
                    vo.setIsOpen("0");
                    return Result.data(vo);
                }
            }
        }catch (Exception e){
            log.error("ifOpenApi错误：",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(Constants.FAIL_MSG);
        }
    }



    /**
     * @Author：liuxt
     * @Description：获取全部算法信息树接口
     * @Date：2022/9/1 11:34
     * @param
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台_全部算法信息树接口
     */
    @Override
    public Map getAllAlgorithmInfoTree() {
        Map map = new HashMap<>();
        List<FunctionTypeVO> oList = customManageMapper.getAllAlgorithmInfoTree();
        map.put("functionTypeList", oList);
        return map;
    }

    @Override
    @Transactional
    public Result<String> saveTenantMapConfig(TenantMapConfigInfoVO param) {
        if (param == null
                || StringUtils.isEmpty(param.getTenantId())
                || StringUtils.isEmpty(param.getAcceptNode())
                || StringUtils.isEmpty(param.getIndustryCode())
                || StringUtils.isEmpty(param.getAppVerCode())
                || StringUtils.isEmpty(param.getMapType())) {
            throw new CustomException("必填参数不能为空");
        }

        // 先删后插
        customManageMapper.deleteTenantMapConfigByCondition(param);

        List<TenantMapConfigVO> insertList = convert2TenantMapConfigVOList(param);
        if (CollectionUtils.isNotEmpty(insertList)) {
            customManageMapper.saveTenantMapConfig(insertList);
        }
        return Result.success(Constants.SUCCESS_MSG);
    }

    @Override
    public Result<List<TenantMapConfigInfoVO>> queryTenantMapConfigInfo(TenantMapConfigQueryVO param) {
        // 参数校验
        checkParam4MapConfig(param);
        List<TenantMapConfigInfoVO> resultList = Lists.newArrayList();
        List<TenantMapConfigVO> dataList = customManageMapper.queryTenantMapConfigInfo(param);
        if (CollectionUtils.isNotEmpty(dataList)) {

            // key -> mapType value -> data
            Map<String, List<TenantMapConfigVO>> dataMap = dataList.stream().collect(Collectors.groupingBy(TenantMapConfigVO::getMapType));

            for (Map.Entry<String, List<TenantMapConfigVO>> entry : dataMap.entrySet()) {
                TenantMapConfigInfoVO data = new TenantMapConfigInfoVO();
                data.setTenantId(param.getTenantId());
                data.setAcceptNode(param.getAcceptNode());
                data.setIndustryCode(param.getIndustryCode());
                data.setAppVerCode(param.getAppVerCode());
                data.setMapType(entry.getKey());
                data.setMapUrlInfoList(entry.getValue().stream().map(x -> {
                    TenantMapConfigInfoSubVO mapUrlInfo = new TenantMapConfigInfoSubVO();
                    mapUrlInfo.setUrl(x.getMapUrl());
                    mapUrlInfo.setMinZoom(x.getMinZoom());
                    mapUrlInfo.setMaxZoom(x.getMaxZoom());
                    return mapUrlInfo;
                }).collect(Collectors.toList()));
                resultList.add(data);
            }
        }
        return Result.data(resultList);
    }

    private void checkParam4MapConfig(TenantMapConfigQueryVO param) {
        if (param == null
                || StringUtils.isEmpty(param.getTenantId())
                || StringUtils.isEmpty(param.getAcceptNode())
                || StringUtils.isEmpty(param.getIndustryCode())
                || StringUtils.isEmpty(param.getAppVerCode())) {
            throw new CustomException("必填参数不能为空");
        }
    }

    @Override
    public Result<Map<String, List<TenantMapConfigInfoSubVO>>> queryTenantMapConfigInfo4Tenant(TenantMapConfigQueryVO param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        SysUser sysUser = loginUser.getUser();
        log.info("queryTenantMapConfigInfo4Tenant userInfo:{}", JSON.toJSONString(sysUser));
        param.setTenantId(sysUser.getTenantId());
        param.setIndustryCode(sysUser.getIndustryCode());
        param.setAppVerCode(sysUser.getAppVerCode());
        param.setAcceptNode(sysUser.getPlatformCode());
        // 参数校验
        checkParam4MapConfig(param);
        Map<String, List<TenantMapConfigInfoSubVO>> resultMap = Maps.newHashMap();
        List<TenantMapConfigVO> dataList = customManageMapper.queryTenantMapConfigInfo(param);
        if (CollectionUtils.isNotEmpty(dataList)) {

            // key -> mapType value -> data
            Map<String, List<TenantMapConfigVO>> dataMap = dataList.stream().collect(Collectors.groupingBy(TenantMapConfigVO::getMapType));

            for (Map.Entry<String, List<TenantMapConfigVO>> entry : dataMap.entrySet()) {
                String mapType = entry.getKey();
                List<TenantMapConfigInfoSubVO> mapInfoList = entry.getValue().stream().map(x -> {
                    TenantMapConfigInfoSubVO mapUrlInfo = new TenantMapConfigInfoSubVO();
                    mapUrlInfo.setUrl(x.getMapUrl());
                    mapUrlInfo.setMinZoom(x.getMinZoom());
                    mapUrlInfo.setMaxZoom(x.getMaxZoom());
                    return mapUrlInfo;
                }).collect(Collectors.toList());
                resultMap.put(mapType, mapInfoList);
            }
        }
        return Result.data(resultMap);
    }

    @Override
    public Result<String> updateCustomerLoginType(TenantVO tenantVO) {
        if (Objects.isNull(tenantVO) || StringUtil.isEmpty(tenantVO.getTenantId())) {
            return Result.fail("客户信息为空");
        }
        TenantMangeResultVO exist = customManageMapper.selectSysTenantInfoByTenantId(tenantVO.getTenantId());
        if (exist.getLoginType().equals(tenantVO.getLoginType())) {
            return Result.success("更新成功");
        }
        // 如果登录类型为0，则清空登录地址
        if (Constants.ZERO_STR.equals(tenantVO.getLoginType())) {
            tenantVO.setLoginUrl(null);
        }
        tenantVO.setModifier(SecurityUtils.getLoginUser().getUser().getUserName());
        tenantVO.setModifierDate(new Date());
        customManageMapper.updateLoginTypeAndUrlByTenantId(tenantVO);
        List<SysUser> users = sysUserMapper.getTenantUsersByTenantId(tenantVO.getTenantId());
        for (SysUser user : users) {
            sendMessage4TenantUser(tenantVO.getLoginUrl(), user.getPhonenumber());
        }
        return Result.success("更新成功");
    }

    private List<TenantMapConfigVO> convert2TenantMapConfigVOList(TenantMapConfigInfoVO param) {
        List<TenantMapConfigVO> resultList = Lists.newArrayList();
        if (param == null || CollectionUtils.isEmpty(param.getMapUrlInfoList())) {
            return resultList;
        }
        String mapType = param.getMapType();
        String tenantId = param.getTenantId();
        String industryCode = param.getIndustryCode();
        String appVerCode = param.getAppVerCode();
        String acceptNode = param.getAcceptNode();
        String creator = SecurityUtils.getLoginUser().getUser().getUserName();
        Date now = new Date();
        AtomicInteger index = new AtomicInteger(1);
        return param.getMapUrlInfoList().stream().map(x -> {
            TenantMapConfigVO data = new TenantMapConfigVO();
            data.setMapType(mapType);
            data.setMapUrl(x.getUrl());
            data.setMaxZoom(x.getMaxZoom());
            data.setMinZoom(x.getMinZoom());
            data.setSort(index.getAndIncrement());
            data.setTenantId(tenantId);
            data.setIndustryCode(industryCode);
            data.setAppVerCode(appVerCode);
            data.setAcceptNode(acceptNode);
            data.setCreator(creator);
            data.setCreateTime(now);
            data.setDeleteFlag(0);
            return data;
        }).collect(Collectors.toList());
    }

    private void generateEncryptedAddress(TenantMangeResultVO tenantMangeResultVO) {
        //如果是默认 先生成一个返回给前端
        if (!Constants.ONE_STR.equals(tenantMangeResultVO.getLoginType())) {
            String encTenantId = AESUtil.encrypt(tenantMangeResultVO.getCustomerCode());
            String loginUrl;
            if (url.endsWith("/login")) {
                loginUrl = url + "?t=" + encTenantId;
            }
            else {
                loginUrl = url + "/login?t=" + encTenantId;
            }
            tenantMangeResultVO.setLoginUrl(loginUrl);
        }
    }

    private void sendMessage4TenantUser(String loginUrl, String phoneNumber) {
        try{
            Map<String,Object> map = new HashMap<>();
            if (StringUtils.isEmpty(loginUrl)) {
                loginUrl = url;
            }
            map.put("url", loginUrl);
            // 内网推送
            CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
            commonSmsRequest.setMsgType(com.chinatower.event.constants.Constants.MSG_TYPE_SMS);
            commonSmsRequest.setPhone(phoneNumber);
            commonSmsRequest.setRequestMap(JSON.toJSONString(map));
            commonSmsRequest.setTemplateCode(templateCode);
            Result result = msgFeignClient.sendMessage(commonSmsRequest);
            log.info("sendMessage4TenantUser param:{}, result:{}",
                    JSON.toJSONString(commonSmsRequest), JSON.toJSONString(result));
        } catch (Exception e){
            log.info("个性化登陆发送通知短信异常:"+e);
        }
    }
}

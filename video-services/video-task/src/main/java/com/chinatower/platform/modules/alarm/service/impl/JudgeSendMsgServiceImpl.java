package com.chinatower.platform.modules.alarm.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.analysis.api.entity.JudgeStatisticsVO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.modules.alarm.dao.JudgeStatisticsDAO;
import com.chinatower.platform.modules.alarm.entity.*;
import com.chinatower.platform.modules.alarm.service.JudgeSendMsgService;
import com.chinatower.platform.utils.DataUtil;
import com.chinatower.common.core.utils.TableUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @className: JudgeSendMsgServiceImpl
 * @description: 研判统计定时任务推送
 * @author: yongqi.han
 * @date: 2022/9/5
 * @requestID 需求单号及需求名称
 */

@Service
@Slf4j
public class JudgeSendMsgServiceImpl implements JudgeSendMsgService {

    @Resource
    private MsgFeignClient msgFeignClient;

    @Resource
    private JudgeStatisticsDAO judgeStatisticsDAO;

    /**
     * 短信模板编码
     */

    //@Value("${sms.judgeMsg.templateCode:xlhm_yfxg}")
    @Value("${sms.templateCode.tatistics:}")
    private String templateCode;

    @Override
    public String judgeSendMsg(String param) {
        try{
            log.info("------统计待研判数量发送短信定时------开始");
            //根据当前时间，获取告警事件分月表名
            DateVO dateVO = new DateVO();
            Date date = new Date();
            String dateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, date);

            List<JudgeStatisticsVO> listAlarmType = judgeStatisticsDAO.getAlarmTypeList();
            Map<String,String> alarmMap = listAlarmType.stream().collect(Collectors.toMap(JudgeStatisticsVO::getAlarmTypeId,JudgeStatisticsVO::getAlarmTypeName,(key1,key2)->key1));

            dateVO.setTableName(TableUtil.getTableName(date,Constants.WARN_ORDER_TEBLE));
            //查询个租户维度产生告警的告警类型
            List<JudgeStatisticsVO> judgeAlarmTypeList = judgeStatisticsDAO.getJudgeAlarmTypeList(dateVO);

            List<String> userLists = new ArrayList<>(); //用户列表
            List<String> gridUserLists = new ArrayList<>(); //网格用户列表
            List<Map> roleLists = new ArrayList<>(); //角色列表
            List<Map> gridRoleLists = new ArrayList<>(); //网格角色列表
            //以租户、告警类型、行业和行业版本维度进行告警类型是否发送短信类型整理
            List<JudgeStatisticsVO> listAlarmTypeAll = new ArrayList<>();
            if (!CollectionUtils.isEmpty(judgeAlarmTypeList)) {
                this.checkIfSendSms(judgeAlarmTypeList,listAlarmTypeAll,userLists,gridUserLists,roleLists,gridRoleLists,param);
                log.info("checkIfSendSms-匹配的告警类型信息：{}",JSON.toJSONString(listAlarmTypeAll));
                if (!CollectionUtils.isEmpty(listAlarmTypeAll)) {
                    //查询需要发短信的租户告警类型对应的告警统计
                    dateVO.setTableName(TableUtil.getTableName(date,Constants.WARN_ORDER_TEBLE));
                    dateVO.setAlarmTypeClassList(listAlarmTypeAll);
                    List<JudgeStatisticsVO> judgeList = judgeStatisticsDAO.getJudgeList(dateVO);

                    if (!CollectionUtils.isEmpty(judgeList)) {
                        //根据告警类型关联流程配置中研判环节用户和角色查询 所有所有需要发送短信的用户信息
                        List<JudgeUserInfoVO> userList = judgeStatisticsDAO.getUserByTypeUser(userLists,roleLists);
                        userList = userList.stream().distinct().collect(Collectors.toList());
                        //待研判用户管理集合信息
                        List<JudgeUserInfoVO> userInfoList = userList.stream().filter(item -> Objects.equals(item.getRoleId(),Constants.FOUR)).collect(Collectors.toList());
                        //客户管理员集合 租户管理员角色和租户管理员
                        List<JudgeUserInfoVO> managerList = userList.stream().filter(item -> Objects.equals(item.getRoleId(),Constants.TWO)||Objects.equals(item.getRoleId(),Constants.ONE)).collect(Collectors.toList());
                        //整合所有要发用户的userIDList
                        List<String> smsUserList = userInfoList.stream().map(JudgeUserInfoVO::getUserId).collect(Collectors.toList());
                        List<String> smsManagerList = managerList.stream().map(JudgeUserInfoVO::getUserId).collect(Collectors.toList());
                        smsUserList.addAll(smsManagerList);
                        String todayString = DateUtils.parseDateToStr("yyyy-MM-dd",new Date());
                        SmsSendStatisticsDto smsSendStatisticsDto = new SmsSendStatisticsDto();
                        smsSendStatisticsDto.setSendDate(todayString);
                        smsSendStatisticsDto.setUserList(smsUserList);
                        //查询上述可发送用户在当日未超短信数量的用户
                        List<SmsSendStatisticsDto> smsSendStatisticsDtoList = judgeStatisticsDAO.getSmsSendStatistics(smsSendStatisticsDto);
                        List<SmsSendStatisticsDto> insertList = new ArrayList<>();
                        //给研判角色发短信
                        if (!CollectionUtils.isEmpty(userInfoList)) {
                            //待研判用户 查询用户的权限信息
                            this.addUserRoleDataScope(userInfoList);
                            //用户信息按照租户进行分组聚合
                            Map<String,List<JudgeUserInfoVO>> userInfoAll = userInfoList.stream().filter(item->CharSequenceUtil.isNotEmpty(item.getTenantId())).collect(Collectors.groupingBy(JudgeUserInfoVO::getTenantId));

                            //发送短信list
                            List<JudgeStatisticsVO> result = new ArrayList<>();
                            //以租户、告警类型、行业和行业版本维度进行消息发送
                            for(JudgeStatisticsVO alarmTypeVO:listAlarmTypeAll){
                                List<JudgeUserInfoVO> userInfoTemps = MapUtils.getObject(userInfoAll,alarmTypeVO.getTenantId());
                                log.info("checkIfSendSms-匹配的告警类型：{}，和用户信息：{}",JSON.toJSONString(alarmTypeVO),JSON.toJSONString(userInfoTemps));
                                if(userInfoTemps!=null && !userInfoTemps.isEmpty()){
                                    for(JudgeUserInfoVO judgeUserInfoVO :userInfoTemps){
                                        //判断相同租户下的用户是否该告警类型需要短信通知的用户
                                        /*if(checkUserMatchFlow(alarmTypeVO,judgeUserInfoVO)){
                                            continue;
                                        }*/
                                        if(checkUserMatchFlowNew(alarmTypeVO,judgeUserInfoVO)){
                                            continue;
                                        }
                                        //判断用户下的告警类型下的当日告警事件数量之和
                                        Integer count = Constants.INT_ZERO;
                                        //判断用户下的告警类型下的当月告警事件数量之和
                                        Integer countAll = Constants.INT_ZERO;
                                        for(JudgeStatisticsVO judgeStatisticsVO : judgeList){
                                            //用户数据权限，包含告警统计的设备字段
                                            if (checkUserMatchPermission(alarmTypeVO,judgeUserInfoVO,judgeStatisticsVO)) {
                                                countAll+= judgeStatisticsVO.getJudgeNum();
                                                if(Constants.ONE.equals(judgeStatisticsVO.getFlag())){
                                                    count += judgeStatisticsVO.getJudgeNum();
                                                }
                                            }
                                        }
                                        if (countAll > Constants.INT_ZERO) {
                                            //有需要发送短信且用户今日有发送短信
                                            String flag =Constants.PARAM_ZERO;
                                            for (SmsSendStatisticsDto smsSendStatistics:smsSendStatisticsDtoList) {
                                                if(smsSendStatistics.getUserId().equals(judgeUserInfoVO.getUserId()) ){
                                                    flag =Constants.ONE;
                                                    if(StringUtils.isEmpty(alarmTypeVO.getTimeLimePush()) || smsSendStatistics.getSendCount() <Integer.valueOf(alarmTypeVO.getTimeLimePush())){
                                                        smsSendStatistics.setSendCount((Integer.valueOf(smsSendStatistics.getSendCount())+1));
                                                        this.getParamSms(alarmMap,count,countAll,judgeUserInfoVO,alarmTypeVO,result);
                                                    }
                                                }
                                            }
                                            //有发送短信且用户今日未发送短信
                                            if(Constants.PARAM_ZERO.equals(flag)){
                                                String insertFlag =Constants.PARAM_ZERO;
                                                for (SmsSendStatisticsDto insert:insertList) {
                                                    //单次发送多条短信情况
                                                    if(insert.getUserId().equals(judgeUserInfoVO.getUserId())){
                                                        insertFlag=Constants.ONE;
                                                        if(StringUtils.isEmpty(alarmTypeVO.getTimeLimePush()) || insert.getSendCount()<Integer.valueOf(alarmTypeVO.getTimeLimePush())){
                                                            insert.setSendCount(insert.getSendCount()+1);
                                                            this.getParamSms(alarmMap,count,countAll,judgeUserInfoVO,alarmTypeVO,result);

                                                        }
                                                    }
                                                }
                                                //单次发送一条情况
                                                if (Constants.PARAM_ZERO.equals(insertFlag)) {
                                                    this.getParamSms(alarmMap,count,countAll,judgeUserInfoVO,alarmTypeVO,result);
                                                    SmsSendStatisticsDto insertSmsSendStatisticsDto = new SmsSendStatisticsDto();
                                                    insertSmsSendStatisticsDto.setUserId(judgeUserInfoVO.getUserId());
                                                    insertSmsSendStatisticsDto.setSendDate(todayString);
                                                    insertSmsSendStatisticsDto.setSendCount(1);
                                                    insertList.add(insertSmsSendStatisticsDto);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //发送短信
                            this.sendMessage(result,dateStr);
                        }

                        //给客户管理员发送短信
                        if (!CollectionUtils.isEmpty(managerList)) {
                            //发送短信list
                            List<JudgeStatisticsVO> result = new ArrayList<>();
                            //用户管理员信息按照租户进行分组聚合
                            Map<String,List<JudgeUserInfoVO>> managerAll = managerList.stream().filter(item->CharSequenceUtil.isNotEmpty(item.getTenantId())).collect(Collectors.groupingBy(JudgeUserInfoVO::getTenantId));
                            for(JudgeStatisticsVO alarmTypeVO : listAlarmTypeAll){
                                //是否通知管理员为：通知
//                                if ((Constants.ONE).equals(alarmTypeVO.getNotifyManager())) {
//                                }
                                List<JudgeUserInfoVO> managerTemps = MapUtils.getObject(managerAll,alarmTypeVO.getTenantId());
                                log.info("checkIfSendSms-匹配的告警类型：{}，用户管理员信息：{}",JSON.toJSONString(alarmTypeVO),JSON.toJSONString(managerTemps));
                                if(managerTemps != null && managerTemps.size()>0){
                                    for(JudgeUserInfoVO judgeUserInfoVO :managerTemps){
                                        //判断用户管理员是否该告警类型需要短信通知的用户
                                        if(checkUserMatchFlowNew(alarmTypeVO,judgeUserInfoVO)){
                                            continue;
                                        }
                                        //判断用户管理员下的告警类型下的告警事件数量
                                        Integer count = Constants.INT_ZERO;
                                        Integer countAll = Constants.INT_ZERO;
                                        for(JudgeStatisticsVO judgeStatisticsVO : judgeList){
                                            //用户数据权限，包含告警统计的设备字段
                                            if (checkTowGridId(alarmTypeVO.getGridId(),judgeStatisticsVO.getGridId()) && alarmTypeVO.getLinkId().equals(judgeStatisticsVO.getLinkId()) &&
                                                    alarmTypeVO.getTenantId().equals(judgeStatisticsVO.getTenantId()) &&alarmTypeVO.getAlarmTypeId().equals(judgeStatisticsVO.getAlarmTypeId())
                                                    &&alarmTypeVO.getIndustryCode().equals(judgeStatisticsVO.getIndustryCode()) &&alarmTypeVO.getAppVerCode().equals(judgeStatisticsVO.getAppVerCode()) ) {
                                                countAll+= judgeStatisticsVO.getJudgeNum();
                                                if(Constants.ONE.equals(judgeStatisticsVO.getFlag())){
                                                    count += judgeStatisticsVO.getJudgeNum();
                                                }
                                            }
                                        }
                                        if (countAll > Constants.INT_ZERO) {
                                            //有需要发送短信且用户今日有发送短信
                                            String flag =Constants.PARAM_ZERO;
                                            for (SmsSendStatisticsDto smsSendStatistics:smsSendStatisticsDtoList) {
                                                if(smsSendStatistics.getUserId().equals(judgeUserInfoVO.getUserId()) ){
                                                    flag =Constants.ONE;
                                                    if(StringUtils.isEmpty(alarmTypeVO.getTimeLimePush()) || smsSendStatistics.getSendCount() <Integer.valueOf(alarmTypeVO.getTimeLimePush())){
                                                        smsSendStatistics.setSendCount((Integer.valueOf(smsSendStatistics.getSendCount())+1));
                                                        this.getParamSms(alarmMap,count,countAll,judgeUserInfoVO,alarmTypeVO,result);
                                                    }

                                                }
                                            }
                                            //有发送短信且用户今日未发送短信
                                            if(Constants.PARAM_ZERO.equals(flag)){
                                                String insertFlag =Constants.PARAM_ZERO;
                                                for (SmsSendStatisticsDto insert:insertList) {
                                                    //单次发送多条短信情况
                                                    if(insert.getUserId().equals(judgeUserInfoVO.getUserId())){
                                                        insertFlag=Constants.ONE;
                                                        if(StringUtils.isEmpty(alarmTypeVO.getTimeLimePush()) || insert.getSendCount()<Integer.valueOf(alarmTypeVO.getTimeLimePush())){
                                                            insert.setSendCount(insert.getSendCount()+1);
                                                            this.getParamSms(alarmMap,count,countAll,judgeUserInfoVO,alarmTypeVO,result);

                                                        }
                                                    }
                                                }
                                                //单次发送一条情况
                                                if (Constants.PARAM_ZERO.equals(insertFlag)) {
                                                    this.getParamSms(alarmMap,count,countAll,judgeUserInfoVO,alarmTypeVO,result);
                                                    SmsSendStatisticsDto insertSmsSendStatisticsDto = new SmsSendStatisticsDto();
                                                    insertSmsSendStatisticsDto.setUserId(judgeUserInfoVO.getUserId());
                                                    insertSmsSendStatisticsDto.setSendDate(todayString);
                                                    insertSmsSendStatisticsDto.setSendCount(1);
                                                    insertList.add(insertSmsSendStatisticsDto);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //发送短信
                            this.sendMessage(result,dateStr);
                        }

                        //更新上述用户当日短信数量
                        if(!CollectionUtils.isEmpty(smsSendStatisticsDtoList)){
                            judgeStatisticsDAO.updateSmsSendStatistics(smsSendStatisticsDtoList);
                        }

                        //插入上述用户当日第一次接收短信消息数据
                        if(!CollectionUtils.isEmpty(insertList)){
                            judgeStatisticsDAO.insertSmsSendStatistics(insertList);
                        }
                    }
                }
            }
            log.info("------统计待研判数量发送短信------结束");
        } catch (Exception e) {
            log.info("******统计待研判数量发送短信***********系统异常信息*****************：{}" ,e);
        }
        return "发送成功";
    }

    public void getParamSms(Map<String,String> alarmMap,Integer count,Integer countAll,JudgeUserInfoVO judgeUserInfoVO,JudgeStatisticsVO alarmTypeVO,List<JudgeStatisticsVO> result){
        JudgeStatisticsVO vo = new JudgeStatisticsVO();
        vo.setAlarmTypeName(MapUtils.getString(alarmMap,alarmTypeVO.getAlarmTypeId()));
        vo.setJudgeNum(count);
        vo.setJudgeNumAll(countAll);
        vo.setMobilePhone(judgeUserInfoVO.getMobilePhone());
        vo.setTenantId(alarmTypeVO.getTenantId());
        vo.setNotifyManager(Constants.ONE);
        result.add(vo);
    }


    @Override
    public boolean getSendMsgSwitch() {
        boolean flag = true;
        String switchFlag = judgeStatisticsDAO.getSendMsgSwitch();
        if(Constants.PARAM_ZERO.equals(switchFlag)){
            flag = false;
        }
        return flag;
    }

    /**
     * @description: 查询用户角色权限进行添加操作
     * @author: yongqi.han
     * @date: 2022/10/18
     * @param userInfoList
     * @return: void
     */
    private void addUserRoleDataScope(List<JudgeUserInfoVO> userInfoList) {
        List<UserDataScopeVO> userDataList = judgeStatisticsDAO.getRoleDataScopeByUserId(userInfoList);
        if(userDataList!=null){
            List<DeptDataScopeVO> deptDataScopeAll = new ArrayList<>();
            Map<String,List<UserDataScopeVO>> userDataMap = userDataList.stream().filter(item->CharSequenceUtil.isNotEmpty(item.getDataScope())).collect(Collectors.groupingBy(UserDataScopeVO::getDataScope));
            if(MapUtils.getObject(userDataMap,Constants.DATA_SCOPE_ALL)!=null ){ //所有数据权限
                this.userScopeDataHandler(Constants.DATA_SCOPE_ALL,userDataMap,deptDataScopeAll);
            }
            if(MapUtils.getObject(userDataMap,Constants.DATA_SCOPE_CUSTOM)!=null){//自定义数据权限
                this.userScopeDataHandler(Constants.DATA_SCOPE_CUSTOM,userDataMap,deptDataScopeAll);
            }
            if(MapUtils.getObject(userDataMap,Constants.DATA_SCOPE_DEPT)!=null){//本组织数据权限
                this.userScopeDataHandler(Constants.DATA_SCOPE_DEPT,userDataMap,deptDataScopeAll);
            }
            if(MapUtils.getObject(userDataMap,Constants.DATA_SCOPE_DEPT_AND_CHILD)!=null){//本组织及组织以下组织数据权限
                this.userScopeDataHandler(Constants.DATA_SCOPE_DEPT_AND_CHILD,userDataMap,deptDataScopeAll);
            }

            Map<String,List<DeptDataScopeVO>> deptDataMap = deptDataScopeAll.stream().filter(item->CharSequenceUtil.isNotEmpty(item.getUserId())).collect(Collectors.groupingBy(DeptDataScopeVO::getUserId));
            for (JudgeUserInfoVO judgeUserInfoVO:userInfoList){
                List<DeptDataScopeVO> userDeptList = MapUtils.getObject(deptDataMap,judgeUserInfoVO.getUserId());
                if(userDeptList!=null && !userDeptList.isEmpty()){
                    String deviceCodeStr = null;
                    String regionCodeStr =  null;
                    for(DeptDataScopeVO deptDataScopeVO:userDeptList){
                        if(Constants.ONE.equals(deptDataScopeVO.getDataScope())){
                            judgeUserInfoVO.setDataScope(Constants.ONE);
                            break;
                        }else{
                            if(!StrUtil.isEmpty(deptDataScopeVO.getDeviceCodeStr())){
                                deviceCodeStr = deviceCodeStr+","+deptDataScopeVO.getDeviceCodeStr() ;
                            }
                            if(!StrUtil.isEmpty(deptDataScopeVO.getRegionCodeStr())){
                                regionCodeStr = regionCodeStr+","+deptDataScopeVO.getRegionCodeStr() ;
                            }
                        }
                    }
                    judgeUserInfoVO.setDeviceCodeStr(deviceCodeStr);
                    judgeUserInfoVO.setRegionCodeStr(regionCodeStr);
                }
            }
        }

    }

    /**
     * @description: 用户权限数据封装
     * @author: yongqi.han
     * @date: 2022/10/19
     * @param dataScope
     * @param userDataMap
     * @param deptDataScopeAll
     * @return: void
     */
    private void userScopeDataHandler(String dataScope, Map<String, List<UserDataScopeVO>> userDataMap, List<DeptDataScopeVO> deptDataScopeAll) {
        if(Constants.DATA_SCOPE_ALL.equals(dataScope)){
            List<UserDataScopeVO> userDataScopeList = MapUtils.getObject(userDataMap,dataScope);
            if(userDataScopeList!=null){
                for(UserDataScopeVO userDataScopeVO:userDataScopeList){
                    DeptDataScopeVO deptDataScope = new DeptDataScopeVO();
                    deptDataScope.setUserId(userDataScopeVO.getUserId());
                    deptDataScope.setDataScope(Constants.ONE);
                    deptDataScopeAll.add(deptDataScope);
                }
            }
        }else{
            List<UserDataScopeVO> userDataScopeList = MapUtils.getObject(userDataMap,dataScope);
            if(userDataScopeList!=null){
                List<DeptDataScopeVO> dataList = judgeStatisticsDAO.getDataScope(userDataScopeList);
                for (DeptDataScopeVO deptDataScope:dataList){
                    deptDataScopeAll.add(deptDataScope);
                }
            }
        }
    }

    public boolean checkTowGridId(String one,String two){
        if(StringUtils.isEmpty(one) && StringUtils.isEmpty(two)){
            return true;
        }
        if(StringUtils.isEmpty(one) && !StringUtils.isEmpty(two)){
            return false;
        }
        if(!StringUtils.isEmpty(one) && StringUtils.isEmpty(two)){
            return false;
        }
        if(!StringUtils.isEmpty(one) && !StringUtils.isEmpty(two)){
            if(one.equals(two)){
                return true;
            }
        }
        return false;
    }


    /**
     * @description: 判断租户下用户的数据权限信息
     * @author: yongqi.han
     * @date: 2022/10/17
     * @param alarmTypeVO
     * @param judgeUserInfoVO
     * @return: boolean
     */
    private boolean checkUserMatchPermission(JudgeStatisticsVO alarmTypeVO, JudgeUserInfoVO judgeUserInfoVO,JudgeStatisticsVO judgeStatisticsVO) {
        boolean flag = false;
        if(checkTowGridId(alarmTypeVO.getGridId(),judgeStatisticsVO.getGridId()) && alarmTypeVO.getLinkId().equals(judgeStatisticsVO.getLinkId()) &&
                alarmTypeVO.getTenantId().equals(judgeStatisticsVO.getTenantId()) &&alarmTypeVO.getAlarmTypeId().equals(judgeStatisticsVO.getAlarmTypeId())
            &&alarmTypeVO.getIndustryCode().equals(judgeStatisticsVO.getIndustryCode())&&alarmTypeVO.getAppVerCode().equals(judgeStatisticsVO.getAppVerCode())){
            if(Constants.PARAM_ZERO.equals(alarmTypeVO.getDataFilterPermiss())){
                return true;
            }

            //统计不同告警来源下的权限数据信息 ai、一键、摄像机、物联根据设备匹配 权限 卫星根据区域匹配权限
            String warningSource = judgeStatisticsVO.getWarningSource();
            try{
                if(Constants.ONE.equals(judgeUserInfoVO.getDataScope())){  //所有数据权限
                    flag = true;
                }else{
                    if(Constants.DEVICE_PERMISSION.contains(warningSource) &&judgeUserInfoVO.getDeviceCodeStr()!=null && judgeUserInfoVO.getDeviceCodeStr().contains(judgeStatisticsVO.getDeviceCode())){
                        flag = true;
                    }else if(Constants.REGION_PERMISSION.equals(warningSource)&&judgeUserInfoVO.getRegionCodeStr()!=null && judgeStatisticsVO.getRegionCode() !=null && judgeUserInfoVO.getRegionCodeStr().contains(judgeStatisticsVO.getRegionCode()) ){
                        flag = true;
                    }else if(Constants.OTHER_PERMISSION.equals(warningSource)){
                        flag = true;
                    }
                }
            }catch (Exception e){
                log.error("checkUserMatchPermission异常:{}",e);
            }


        }
        return flag;
    }

    /**
     * @description: 判断告警类型下配置的告警流程中关联的用户和角色  是否匹配查询的用户
     * @author: yongqi.han
     * @date: 2022/10/17
     * @param alarmTypeVO
     * @param judgeUserInfoVO
     * @return: boolean
     */
    private boolean checkUserMatchFlow(JudgeStatisticsVO alarmTypeVO, JudgeUserInfoVO judgeUserInfoVO) {
        boolean flag= true;
        String userType = alarmTypeVO.getUserType();
        String roleUsers = alarmTypeVO.getRoleUsers();
        if(CharSequenceUtil.isEmpty(roleUsers) ||(!Constants.ONE.equals(judgeUserInfoVO.getRoleId()) &&(!alarmTypeVO.getIndustryCode().equals(judgeUserInfoVO.getIndustryCode())
                || !alarmTypeVO.getAppVerCode().equals(judgeUserInfoVO.getAppVerCode())) ) ){
            return flag;
        }
        if(Constants.ONE.equals(userType) ){
            List<String> userLists = Arrays.asList(roleUsers.split(","));
            if(userLists.contains(judgeUserInfoVO.getUserId())){
                flag= false;
            }
        }else{
            if(StrUtil.isNotEmpty(roleUsers) && StrUtil.isNotEmpty(judgeUserInfoVO.getRoles())){
                List<String> roleLists = Arrays.asList(roleUsers.split(","));
                List<String> roles = Arrays.asList(judgeUserInfoVO.getRoles().split(","));
                boolean roleFlag = false;
                for(String roleId:roleLists){
                    for (String roleTemp:roles){
                        if(roleTemp!=null && roleTemp.equals(roleId)){
                            roleFlag = true;
                            flag= false;
                            break;
                        }
                    }
                    if(roleFlag){
                        break;
                    }
                }
            }
        }

        return flag;
    }

    /**
     * @description: 判断告警类型下配置的告警流程中关联的用户和角色  是否匹配查询的用户
     * @author: yongqi.han
     * @date: 2022/10/17
     * @param alarmTypeVO
     * @param judgeUserInfoVO
     * @return: boolean
     */
    private boolean checkUserMatchFlowNew(JudgeStatisticsVO alarmTypeVO, JudgeUserInfoVO judgeUserInfoVO) {
        boolean flag= true;
        String userType = alarmTypeVO.getUserType();
        //String roleUsers = alarmTypeVO.getRoleUsers();
        String userIds =alarmTypeVO.getUserIds();
        String roleIds =alarmTypeVO.getRoleIds();
        //流程配置没有查询到用户及角色  或者  该用户不是租户管理员
        if((StringUtils.isEmpty(userIds) && StringUtils.isEmpty(roleIds)) ||
                (!Constants.ONE.equals(judgeUserInfoVO.getRoleId()) &&(!alarmTypeVO.getIndustryCode().equals(judgeUserInfoVO.getIndustryCode())
                        || !alarmTypeVO.getAppVerCode().equals(judgeUserInfoVO.getAppVerCode())) )){
            return flag;
        }
        if(!StringUtils.isEmpty(userIds)){
            List<String> userLists = Arrays.asList(userIds.split(","));
            if(userLists.contains(judgeUserInfoVO.getUserId())){
                flag= false;
            }
        }
        if(!StringUtils.isEmpty(roleIds) && !StringUtils.isEmpty(judgeUserInfoVO.getRoles())){
            List<String> roleLists = Arrays.asList(roleIds.split(","));
            List<String> roles = Arrays.asList(judgeUserInfoVO.getRoles().split(","));
            boolean roleFlag = false;
            for(String roleId:roleLists){
                for (String roleTemp:roles){
                    if(roleTemp!=null && roleTemp.equals(roleId)){
                        roleFlag = true;
                        flag= false;
                        break;
                    }
                }
                if(roleFlag){
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * @description: 检查是否要发送短信通知
     * @author: yongqi.han
     * @date: 2022/10/14
     * @param judgeAlarmTypeList 入参
     * @param listAlarmTypeAll 出参
     * @param userLists 出参
     * @param roleLists 出参
     * @return: void
     */
    private void checkIfSendSms(List<JudgeStatisticsVO> judgeAlarmTypeList,List<JudgeStatisticsVO> listAlarmTypeAll, List<String> userLists, List<String> gridUserLists,List<Map> roleLists,List<Map> gridRoleLists,String param) throws Exception {
        //获取当天0点到当前时间的分钟数
        long min = getCurrentMin(new Date());
        //查询所有租户的告警流程配置的研判环节短信间隔时间
        List<JudgeStatisticsVO> alarmTypeClassListTemp = judgeStatisticsDAO.getJudgeSentTimeMinute();
        //查询租户行业默认流程配置的研判环节短信间隔时间
        List<JudgeStatisticsVO> alarmIndustryList = judgeStatisticsDAO.getDefaultJudgeSentTimeMinute();
        List<String> gridList =judgeAlarmTypeList.stream().map(JudgeStatisticsVO::getGridId).collect(Collectors.toList());
        List<JudgeGridInfoVO> gridInfoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(gridList)){
            //获取所有网格的网格员
            gridInfoList = judgeStatisticsDAO.getGridUserByGridIdList(gridList);
        }

        //todo appVerCode后续无需匹配
        for(JudgeStatisticsVO judgeVO:judgeAlarmTypeList){
            boolean flag = false;
            Map roleTypeMap = new HashMap();
            for(JudgeStatisticsVO judgeTimeTemp:alarmTypeClassListTemp){
                if(judgeTimeTemp.getLinkId().equals(judgeVO.getLinkId()) &&
                        judgeVO.getTenantId()!=null&&judgeVO.getAlarmTypeId()!=null&&judgeVO.getIndustryCode()!=null&&judgeVO.getAppVerCode()!=null
                        && judgeVO.getTenantId().equals(judgeTimeTemp.getTenantId()) && judgeVO.getAlarmTypeId().equals(judgeTimeTemp.getAlarmTypeId())
                        && judgeVO.getIndustryCode().equals(judgeTimeTemp.getIndustryCode()) &&judgeVO.getAppVerCode().equals(judgeTimeTemp.getAppVerCode())){
                    //过滤不执行的告警类型
                    if (this.checkConfigTimeInterval(judgeTimeTemp,min,param)) {  //检查配置是否满足短信发送条件
                        judgeVO.setNotifyManager(judgeTimeTemp.getNotifyManager());
                        judgeVO.setUserType(judgeTimeTemp.getUserType());
                        //judgeVO.setRoleUsers(judgeTimeTemp.getRoleUsers());
                        String userIds="";
                        String roleIds="";
                        //有勾选网格员模式
                        if(judgeTimeTemp.getUserType().contains(Constants.THREE)){
                            if(!StringUtils.isEmpty(judgeVO.getGridId())){
                                //是否有该网格id的网格员标记位（默认没有）
                                boolean indexFlag =false;
                                for (JudgeGridInfoVO gridInfo : gridInfoList) {
                                    if(gridInfo.getGridId().equals(judgeVO.getGridId())){
                                        //流程有勾选网格员处理模式且该告警类型匹配的工单有网格员
                                        userIds=this.getTowStringMerge(gridInfo.getGridUsers(),judgeTimeTemp.getUserIds());
                                        roleIds=judgeTimeTemp.getRoleIds();
                                        indexFlag =true;
                                        break;
                                    }
                                }
                                if(!indexFlag){
                                    //流程有勾选网格员处理模式且该告警类型匹配的工单没有查到网格员
                                    userIds=this.getTowStringMerge(judgeTimeTemp.getUserIds(),judgeTimeTemp.getGridUserIds());
                                    roleIds=this.getTowStringMerge(judgeTimeTemp.getRoleIds(),judgeTimeTemp.getGridRoleIds());
                                }
                            }else{
                                //流程有勾选网格员处理模式且该告警类型匹配的工单没有网格
                                userIds=this.getTowStringMerge(judgeTimeTemp.getUserIds(),judgeTimeTemp.getGridUserIds());
                                roleIds=this.getTowStringMerge(judgeTimeTemp.getRoleIds(),judgeTimeTemp.getGridRoleIds());
                            }
                        }else{
                            //流程未勾选网格员处理模式
                            userIds=judgeTimeTemp.getUserIds();
                            roleIds=judgeTimeTemp.getRoleIds();
                        }
                        judgeVO.setUserIds(userIds);
                        judgeVO.setRoleIds(roleIds);
                        judgeVO.setDataFilterPermiss(judgeTimeTemp.getDataFilterPermiss());
                        judgeVO.setTimeLimePush(judgeTimeTemp.getTimeLimePush());
                        listAlarmTypeAll.add(judgeVO);
                        if(!StringUtils.isEmpty(userIds)){
                            List<String> userTemp = Arrays.asList(userIds.split(","));
                            userLists.addAll(userTemp);
                        }
                        if(!StringUtils.isEmpty(roleIds)){
                            roleTypeMap.put(Constants.TENANT_ID,judgeVO.getTenantId());
                            roleTypeMap.put(Constants.TYPE_VALUE,judgeVO.getAlarmTypeId());
                            roleTypeMap.put(Constants.INDUSTRY_CODE,judgeVO.getIndustryCode());
                            roleTypeMap.put(Constants.APP_VER_CODE,judgeVO.getAppVerCode());
                            roleTypeMap.put(Constants.ROLE_USERS,roleIds);
                        }
                    }
                    flag = true;
                    break;
                }
            }
            if(flag){
                if(roleTypeMap!=null&&!roleTypeMap.isEmpty()){
                    roleLists.add(roleTypeMap);
                }
                continue;
            }
            for(JudgeStatisticsVO judgeTemp:alarmIndustryList){
                if(judgeTemp.getLinkId().equals(judgeVO.getLinkId()) &&
                        judgeVO.getTenantId()!=null&&judgeVO.getAlarmTypeId()!=null&&judgeVO.getIndustryCode()!=null&&judgeVO.getAppVerCode()!=null
                        && judgeVO.getIndustryCode().equals(judgeTemp.getIndustryCode()) &&judgeVO.getAppVerCode().equals(judgeTemp.getAppVerCode())
//                        && judgeVO.getWarningSource().contains(judgeTemp.getWarningSource())
                ){
                    //过滤不执行的告警类型
                    if (this.checkConfigTimeInterval(judgeTemp,min,param)) { //检查配置是否满足短信发送条件
                        judgeVO.setNotifyManager(judgeTemp.getNotifyManager());
                        judgeVO.setUserType(judgeTemp.getUserType());
                        //judgeVO.setRoleUsers(judgeTemp.getRoleUsers());
                        String userIds="";
                        String roleIds="";
                        if(!StringUtils.isEmpty(judgeVO.getGridId())){
                            //是否有该网格id的网格员标记位（默认没有）
                            boolean indexFlag =false;
                            for (JudgeGridInfoVO gridInfo : gridInfoList) {
                                if(gridInfo.getGridId().equals(judgeVO.getGridId())){
                                    //流程有勾选网格员处理模式且该告警类型匹配的工单有网格员
                                    userIds=gridInfo.getGridUsers();
                                    roleIds=judgeTemp.getRoleIds();
                                    indexFlag =true;
                                    break;
                                }
                            }
                            if(!indexFlag){
                                //流程匹配的工单有网格id,但是没有查询到网格员
                                roleIds=this.getTowStringMerge(judgeTemp.getRoleIds(),judgeTemp.getGridRoleIds());
                            }
                        }else{
                            //流程匹配的工单没有网格id
                            roleIds=judgeTemp.getRoleIds();
                        }
                        judgeVO.setUserIds(userIds);
                        judgeVO.setRoleIds(roleIds);
                        judgeVO.setDataFilterPermiss(judgeTemp.getDataFilterPermiss());
                        judgeVO.setTimeLimePush(judgeTemp.getTimeLimePush());
                        listAlarmTypeAll.add(judgeVO);
                        if(!StringUtils.isEmpty(userIds)){
                            List<String> userTemp = Arrays.asList(userIds.split(","));
                            userLists.addAll(userTemp);
                        }
                        if(!StringUtils.isEmpty(roleIds)){
                            roleTypeMap.put(Constants.TENANT_ID,judgeVO.getTenantId());
                            roleTypeMap.put(Constants.TYPE_VALUE,judgeVO.getAlarmTypeId());
                            roleTypeMap.put(Constants.INDUSTRY_CODE,judgeVO.getIndustryCode());
                            roleTypeMap.put(Constants.APP_VER_CODE,judgeVO.getAppVerCode());
                            roleTypeMap.put(Constants.ROLE_USERS,roleIds);
                        }

                    }
                    break;
                }
            }
            if(roleTypeMap!=null&&!roleTypeMap.isEmpty()){
                roleLists.add(roleTypeMap);
            }
        }
    }

    public String getTowStringMerge(String one,String two){
        if(StringUtils.isEmpty(one)){
            return two;
        }
        if(StringUtils.isEmpty(two)){
            return one;
        }
        if(!StringUtils.isEmpty(one) && !StringUtils.isEmpty(two)){
            return one+","+two;
        }
        return "";
    }


    /**
     * @description: 检查配置的时间间隔，时间段是否满足发短信限制
     * @author: yongqi.han
     * @date: 2022/10/24
     * @param judgeTemp
     * @param min  0点到当前时间点的分钟数
     * @param param  定时任务执行周期
     * @return: boolean
     */
    private boolean checkConfigTimeInterval(JudgeStatisticsVO judgeTemp, long min,String param) {
        log.info("checkConfigTimeInterval-告警流程模板配置信息-"+JSON.toJSONString(judgeTemp));
        boolean flag = false;
        int judgeSentTimeMinute = judgeTemp.getMsgNotifyLimit();
        //1、判断是否在短信发送时间段
        if(StrUtil.isNotEmpty(judgeTemp.getMsgSendTimeInterval())){
            //配置的短信模板时间区间
            String msgSendTimeInterval = judgeTemp.getMsgSendTimeInterval();
            if(msgSendTimeInterval != null && msgSendTimeInterval.contains("-")){
                try{
                    //根据-切分为配置开始时间和结束后时间
                    String[] msgSendTimeArr = msgSendTimeInterval.split("-");
                    //判断当前时间是否在该时间区间内（HH:mm:ss）
                    String[] nowTime = DateUtils.dateTimeNow("HH:mm:ss").split(":");
                    String[] beginTime = msgSendTimeArr[0].split(":");
                    String[] endTime = msgSendTimeArr[1].split(":");
                    //如果当前时间不在配置范围内，不予发送短信
                    if(!this.compareTime(nowTime,beginTime,endTime)){
                        return flag;
                    }
                }catch (Exception e){
                    log.error("配置的短信模板时间区间数据异常:",e);
                }
            }
        }
        //2、当前时间点的分钟数 % 配置的时间间隔 ，取余为0，则发送短信
        if(judgeSentTimeMinute>0){
            if(min % judgeSentTimeMinute == Constants.INT_ZERO ||( min > judgeSentTimeMinute && min % judgeSentTimeMinute <Long.parseLong(param) ) ){
                flag = true;
            }
        }
        return flag;
    }
    /**
     * 判断当前时间是否在配置时间区间内
     * @param nowTime 当前时间
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public boolean compareTime(String[] nowTime,String[] beginTime,String[] endTime){
        int nowTotal=Integer.valueOf(nowTime[0])*3600+Integer.valueOf(nowTime[1])*60+Integer.valueOf(nowTime[2]);
        int beginTotal=Integer.valueOf(beginTime[0])*3600+Integer.valueOf(beginTime[1])*60+Integer.valueOf(beginTime[2]);
        int endTotal=Integer.valueOf(endTime[0])*3600+Integer.valueOf(endTime[1])*60+Integer.valueOf(endTime[2]);
        if(nowTotal >= beginTotal && nowTotal <= endTotal){
            return true;
        }else{
            return false;
        }
    }

    private void sendMessage(List<JudgeStatisticsVO> result, String dateStr) {
        try{
            log.info("批量研判统计短信发送参数信息：{}",JSON.toJSONString(result));
            if(result!=null && !result.isEmpty()){
                for(JudgeStatisticsVO judgeStatisticsVO : result){
                    if (!StringUtils.isEmpty(judgeStatisticsVO.getMobilePhone())) {
                        HashMap map = new HashMap<>();
                        map.put("dateTime", dateStr);
                        map.put("type", judgeStatisticsVO.getAlarmTypeName());
                        map.put("number", String.valueOf(judgeStatisticsVO.getJudgeNum()) );
                        map.put("number2",String.valueOf(judgeStatisticsVO.getJudgeNumAll()));
                        // 内网推送
                        CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
                        commonSmsRequest.setMsgType(Constants.MSG_TYPE_SMS);
                        commonSmsRequest.setPhone(judgeStatisticsVO.getMobilePhone());
                        commonSmsRequest.setRequestMap(JSON.toJSONString(map));
                        //templateCode:dafo_otql
                        commonSmsRequest.setTemplateCode(templateCode);
                        log.info("短信发送请求体：{}",JSON.toJSONString(commonSmsRequest));
                        msgFeignClient.sendMessage(commonSmsRequest);
                       /* MsgRequestVO requestVO = new MsgRequestVO();
                        requestVO.setSmsTemplateCode(templateCode);
                        requestVO.setPhoneNumber(judgeStatisticsVO.getMobilePhone());
                        requestVO.setParaMap(JSON.toJSONString(map));
                        log.info("短信发送请求体：{}",JSON.toJSONString(requestVO));
                        msgFeignClient.innovationSendMessage(requestVO);*/
                    }
                }
            }
        }catch(Exception e){
            log.error("批量研判短信息发送异常：{}",e);
        }


    }



    /**
     * 获取当天0点到当前时间的分钟数
     * @param date
     * @return
     * @throws Exception
     */
    public long getCurrentMin(Date date) throws Exception{
        String today = DataUtil.dateToStringYMD(date);
        long start = DataUtil.dateToStamp(today + Constants.STERT_DATE_HMS);
        long end = date.getTime();
        long x = end - start;
        //0点 处理成 24点
        if(x == 0){
            x = 1000L * 60 * 60 * 24;
        }
        long min =  x / (1000 * 60);
        return min;
    }


}


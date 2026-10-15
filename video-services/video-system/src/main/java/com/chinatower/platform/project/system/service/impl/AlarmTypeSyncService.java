package com.chinatower.platform.project.system.service.impl;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.IVideoManageRemoteService;
import com.chinatower.manage.api.dto.ScreenDataConfigVO;
import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import com.chinatower.platform.project.custom.entity.*;
import com.chinatower.platform.project.sysbusiness.common.Constants;
import com.chinatower.platform.project.system.mapper.SysDeptMapper;
import com.chinatower.platform.project.system.service.IAlarmTypeSyncService;
import com.chinatower.system.api.content.SysConstants;
import com.chinatower.system.api.request.alarmType.ThirdPartyAlarmTypeVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @ClassName AlarmTypeSyncService
 * @Description
 * @Author liuxt
 * @Date 2025年1月10日15:12:22
 **/
@Service
@Slf4j
public class AlarmTypeSyncService implements IAlarmTypeSyncService {

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private IVideoManageRemoteService videoManageRemoteService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void alarmTypeMathFactoryConsumerList(List<String> messages) {
        long start = System.currentTimeMillis();
        log.info(start + "alarmTypeMathFactoryConsumerList=" + JSON.toJSONString(messages));
        try {
            List<AlarmTypeSyncDataVO> alarmTypeSyncDataVOList = new ArrayList<>();
            messages.forEach(kafkaMessage -> {
                AlarmTypeSyncVO alarmTypeSyncVO = JSON.parseObject(kafkaMessage, AlarmTypeSyncVO.class);
                if(Constants.INSERT.equals(alarmTypeSyncVO.getType())){
                    List<AlarmTypeSyncDataVO> dataList = alarmTypeSyncVO.getData();
                    if (!CollectionUtils.isEmpty(dataList)) {
                        alarmTypeSyncDataVOList.addAll(dataList);
                    }
                }
            });
            if(!CollectionUtils.isEmpty(alarmTypeSyncDataVOList)) {
                List<OrganAlarmTypeVO> organAlarmTypeVOlist = new ArrayList<>();
                List<ScreenDataConfigVO> screenDataConfigVOList = new ArrayList<>();
                for (int i = 0; i < alarmTypeSyncDataVOList.size(); i++) {
                    OrganAlarmTypeVO tempVO = new OrganAlarmTypeVO();
                    tempVO.setTenantId(alarmTypeSyncDataVOList.get(i).getTenantId());
                    tempVO.setIndustryCode(alarmTypeSyncDataVOList.get(i).getIndustryCode());
                    tempVO.setAppVerCode(alarmTypeSyncDataVOList.get(i).getAppVerCode());
                    tempVO.setWarningSource(alarmTypeSyncDataVOList.get(i).getAlarmSource());
                    tempVO.setTypeValue(alarmTypeSyncDataVOList.get(i).getTypeValue());
                    organAlarmTypeVOlist.add(tempVO);
                }
                List<OrganAlarmTypeVO> existAlarmTypeSyncDataVOList = deptMapper.getOrganAlarmTypeList(organAlarmTypeVOlist);
                if (!CollectionUtils.isEmpty(existAlarmTypeSyncDataVOList)) {
                    Iterator<AlarmTypeSyncDataVO> iterator = alarmTypeSyncDataVOList.iterator();
                    while (iterator.hasNext()) {
                        AlarmTypeSyncDataVO alarmTypeSyncDataVO = iterator.next();
                        for (OrganAlarmTypeVO existAlarmTypeSyncDataVO : existAlarmTypeSyncDataVOList) {
                            if (alarmTypeSyncDataVO.getTypeValue().equals(existAlarmTypeSyncDataVO.getTypeValue()) &&
                                    alarmTypeSyncDataVO.getAlarmSource().equals(existAlarmTypeSyncDataVO.getWarningSource()) ) {
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
                List<OrganAlarmTypeVO> finalList = new ArrayList<>();
                //根据上述list找出新增的告警类型tenant_id不为null的list,代表是客户的告警类型
                List<AlarmTypeSyncDataVO> addTenantList = alarmTypeSyncDataVOList.stream().filter(item -> item.getTenantId() != null).collect(Collectors.toList());
                List<OrganAlarmTypeVO> addTenantDeptList = new ArrayList<>();
                        //查询全部组织
                List<OrganAlarmTypeVO> allDeptList = deptMapper.selectAllDeptList();
                for (int i = 0; i < allDeptList.size(); i++) {
                    for (int j = 0; j < addTenantList.size(); j++) {
                        if(allDeptList.get(i).getTenantId().equals(addTenantList.get(j).getTenantId()) &&
                                allDeptList.get(i).getIndustryCode().equals(addTenantList.get(j).getIndustryCode())){
                            addTenantDeptList.add(allDeptList.get(i));
                            break;
                        }
                    }
                }
                List<AlarmTypeSyncDataVO> tenantList = new ArrayList<>();
                List<AlarmTypeSyncDataVO> oneKeyAlarmTypeListTenant = new ArrayList<>();
                if(!CollectionUtils.isEmpty(addTenantList)){//客户list补充一键告警类型
                    for (int i = 0; i < addTenantList.size(); i++) {
                        AlarmTypeSyncDataVO oneKeyAlarmTypeVO = new AlarmTypeSyncDataVO();
                        oneKeyAlarmTypeVO.setTenantId(addTenantList.get(i).getTenantId());
                        oneKeyAlarmTypeVO.setIndustryCode(addTenantList.get(i).getIndustryCode());
                        oneKeyAlarmTypeVO.setTypeValue(addTenantList.get(i).getTypeValue());
                        oneKeyAlarmTypeVO.setAlarmSource(Constants.THREE_STR);
                        oneKeyAlarmTypeListTenant.add(oneKeyAlarmTypeVO);
                        // 增加大屏配置的告警类型
                        ScreenDataConfigVO configVO = new ScreenDataConfigVO();
                        List<ScreenDataConfigParticipationVO> alarmTypeList = new ArrayList<>();
                        configVO.setTenantId(alarmTypeSyncDataVOList.get(i).getTenantId());
                        configVO.setIndustryCode(alarmTypeSyncDataVOList.get(i).getIndustryCode());
                        // 租户新增都是人工上报 还需要手动加上一键告警
                        ScreenDataConfigParticipationVO  participationTwo = new ScreenDataConfigParticipationVO();
                        participationTwo.setSource(alarmTypeSyncDataVOList.get(i).getAlarmSource());
                        participationTwo.setCode(alarmTypeSyncDataVOList.get(i).getTypeValue());
                        alarmTypeList.add(participationTwo);
                        ScreenDataConfigParticipationVO  participationThree = new ScreenDataConfigParticipationVO();
                        participationThree.setSource(Constants.THREE_STR);
                        participationThree.setCode(alarmTypeSyncDataVOList.get(i).getTypeValue());
                        alarmTypeList.add(participationThree);
                        configVO.setAlarmTypeList(alarmTypeList);
                        screenDataConfigVOList.add(configVO);
                    }
                }
                tenantList.addAll(addTenantList);
                tenantList.addAll(oneKeyAlarmTypeListTenant);
                List distinctTenantList =  tenantList.stream().distinct().collect(Collectors.toList());
                dealFinalList(distinctTenantList,addTenantDeptList,finalList,false);

                //根据上述list找出新增的告警类型tenant_id是null的list,,代表是铁塔的告警类型或者是算法仓同步的告警类型
                //每条告警类型变为3条（原告警来源的告警类型，人工上报来源的告警类型，一键告警来源的告警类型）
                List<AlarmTypeSyncDataVO> addTowerListNew = new ArrayList<>();
                List<AlarmTypeSyncDataVO> addTowerList2 = alarmTypeSyncDataVOList.stream().filter(
                        item -> StringUtils.isEmpty(item.getTenantId())
                ).collect(Collectors.toList());
                List<AlarmTypeSyncDataVO> renGongAlarmTypeList = new ArrayList<>();
                List<AlarmTypeSyncDataVO> oneKeyAlarmTypeList = new ArrayList<>();
                for (int i = 0; i < addTowerList2.size(); i++) {
                    AlarmTypeSyncDataVO renGongOrganAlarmTypeVO = new AlarmTypeSyncDataVO();
                    renGongOrganAlarmTypeVO.setTypeValue(addTowerList2.get(i).getTypeValue());
                    renGongOrganAlarmTypeVO.setAlarmSource(Constants.TWO_STR);
                    renGongAlarmTypeList.add(renGongOrganAlarmTypeVO);
                    AlarmTypeSyncDataVO oneKeyAlarmTypeVO = new AlarmTypeSyncDataVO();
                    oneKeyAlarmTypeVO.setTypeValue(addTowerList2.get(i).getTypeValue());
                    oneKeyAlarmTypeVO.setAlarmSource(Constants.THREE_STR);
                    oneKeyAlarmTypeList.add(oneKeyAlarmTypeVO);
                    // 增加大屏配置的告警类型
                    ScreenDataConfigVO configVO = new ScreenDataConfigVO();
                    List<ScreenDataConfigParticipationVO> alarmTypeList = new ArrayList<>();
                    configVO.setTenantId(addTowerList2.get(i).getTenantId());
                    configVO.setIndustryCode(addTowerList2.get(i).getIndustryCode());
                    ScreenDataConfigParticipationVO  participationVOTwo = new ScreenDataConfigParticipationVO();
                    participationVOTwo.setSource(Constants.TWO_STR);
                    participationVOTwo.setCode(addTowerList2.get(i).getTypeValue());
                    ScreenDataConfigParticipationVO  participationVOThree = new ScreenDataConfigParticipationVO();
                    participationVOThree.setSource(Constants.THREE_STR);
                    participationVOThree.setCode(addTowerList2.get(i).getTypeValue());
                    alarmTypeList.add(participationVOTwo);
                    alarmTypeList.add(participationVOThree);
                    configVO.setAlarmTypeList(alarmTypeList);
                    screenDataConfigVOList.add(configVO);
                }
                addTowerListNew.addAll(addTowerList2);
                addTowerListNew.addAll(renGongAlarmTypeList);
                addTowerListNew.addAll(oneKeyAlarmTypeList);
                List distinctAddTowerListNew =  addTowerListNew.stream().distinct().collect(Collectors.toList());
                dealFinalList(distinctAddTowerListNew,allDeptList,finalList,true);
                try {
                    //批量插入【vm_organ_alarm_type_rel_t】
                    List<List<OrganAlarmTypeVO>> partitionList = Lists.partition(finalList, Constants.FIVE_THOUSAND);
                    partitionList.forEach(item ->
                            deptMapper.insertOrganAlarmTypeBatch(item)
                    );
                    // 大屏配置入库
                    if(!CollectionUtils.isEmpty(screenDataConfigVOList)){
                        // 去重
                        screenDataConfigVOList = screenDataConfigVOList.stream().distinct().collect(Collectors.toList());
                        videoManageRemoteService.addConfig(screenDataConfigVOList);
                    }
                }catch (Exception e){
                    log.error(Constants.BATCH_INSERT_FAIL,e);
                }
            }
        } catch (Exception e) {
            log.info("alarmTypeMathFactoryConsumerList异常", e);
        }
    }



//    @Override
//    public void thirdPartyAlarmMathFactoryConsumerList(List<String> messages) {
//        long start = System.currentTimeMillis();
//        log.info(start + "thirdPartyAlarmMathFactoryConsumerList=" + messages.size());
//        try {
//            List<ThirdPartyAlarmTypeSyncDataVO> alarmTypeSyncDataVOList = new ArrayList<>();
//            messages.forEach(kafkaMessage -> {
//                ThirdPartyAlarmTypeSyncVO thirdPartyalarmTypeSyncVO = JSON.parseObject(kafkaMessage, ThirdPartyAlarmTypeSyncVO.class);
//                if(Constants.INSERT.equals(thirdPartyalarmTypeSyncVO.getType())){
//                    List<ThirdPartyAlarmTypeSyncDataVO> dataList = thirdPartyalarmTypeSyncVO.getData();
//                    if (!CollectionUtils.isEmpty(dataList)) {
//                        alarmTypeSyncDataVOList.addAll(dataList);
//                    }
//                }
//            });
//            if(!CollectionUtils.isEmpty(alarmTypeSyncDataVOList)) {
//                List<OrganAlarmTypeVO> organAlarmTypeVOlist = new ArrayList<>();
//                List<ScreenDataConfigVO> screenDataConfigVOList = new ArrayList<>();
//                for (int i = 0; i < alarmTypeSyncDataVOList.size(); i++) {
//                    OrganAlarmTypeVO tempVO = new OrganAlarmTypeVO();
//                    tempVO.setTenantId(alarmTypeSyncDataVOList.get(i).getTenantId());
//                    tempVO.setIndustryCode(alarmTypeSyncDataVOList.get(i).getIndustryCode());
//                    tempVO.setAppVerCode(alarmTypeSyncDataVOList.get(i).getAppVerCode());
//                    tempVO.setWarningSource(Constants.ELEVEN_STR);
//                    tempVO.setTypeValue(alarmTypeSyncDataVOList.get(i).getAlarmType());
//                    organAlarmTypeVOlist.add(tempVO);
//                }
//                List<OrganAlarmTypeVO> existThirdPartyAlarmTypeSyncDataVOList = deptMapper.getOrganAlarmTypeList(organAlarmTypeVOlist);
//                if (!CollectionUtils.isEmpty(existThirdPartyAlarmTypeSyncDataVOList)) {
//                    Iterator<ThirdPartyAlarmTypeSyncDataVO> iterator = alarmTypeSyncDataVOList.iterator();
//                    while (iterator.hasNext()) {
//                        ThirdPartyAlarmTypeSyncDataVO alarmTypeSyncDataVO = iterator.next();
//                        for (OrganAlarmTypeVO existAlarmTypeSyncDataVO : existThirdPartyAlarmTypeSyncDataVOList) {
//                            if (alarmTypeSyncDataVO.getAlarmType().equals(existAlarmTypeSyncDataVO.getTypeValue()) ) {
//                                iterator.remove();
//                                break;
//                            }
//                        }
//                    }
//                }
//                List<OrganAlarmTypeVO> finalList = new ArrayList<>();
//                //根据上述list找出新增的告警类型tenant_id不为null的list,代表是客户的告警类型
//                dealThirdFinalList(alarmTypeSyncDataVOList,finalList);
//                if(!CollectionUtils.isEmpty(finalList)){
//                    for (int i = 0; i < finalList.size(); i++) {
//                        // 增加大屏配置的告警类型
//                        ScreenDataConfigVO configVO = new ScreenDataConfigVO();
//                        List<ScreenDataConfigParticipationVO> alarmTypeList = new ArrayList<>();
//                        configVO.setTenantId(finalList.get(i).getTenantId());
//                        configVO.setIndustryCode(finalList.get(i).getIndustryCode());
//                        ScreenDataConfigParticipationVO  participationVO = new ScreenDataConfigParticipationVO();
//                        participationVO.setSource(Constants.ELEVEN_STR);
//                        participationVO.setCode(finalList.get(i).getTypeValue());
//                        alarmTypeList.add(participationVO);
//                        configVO.setAlarmTypeList(alarmTypeList);
//                        screenDataConfigVOList.add(configVO);
//                    }
//                }
//                try {
//                    //批量插入【vm_organ_alarm_type_rel_t】
//                    List<List<OrganAlarmTypeVO>> partitionList = Lists.partition(finalList, Constants.FIVE_THOUSAND);
//                    partitionList.forEach(item ->
//                            deptMapper.insertOrganAlarmTypeBatch(item)
//                    );
//                    // 大屏配置入库
//                    if(!CollectionUtils.isEmpty(screenDataConfigVOList)){
//                        videoManageRemoteService.addConfig(screenDataConfigVOList);
//                    }
//                }catch (Exception e){
//                    log.error(Constants.BATCH_INSERT_FAIL,e);
//                }
//            }
//        } catch (Exception e) {
//            log.info("thirdPartyAlarmMathFactoryConsumerList异常", e);
//        }
//    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> thirdPartyAlarmSync(ThirdPartyAlarmTypeVO vo) {
        long start = System.currentTimeMillis();
        log.info(start + "ThirdPartyAlarmTypeSyncDataVO=" + JSON.toJSONString(vo));
        try {
            List<OrganAlarmTypeVO> addFinalList = new ArrayList<>();
            List<OrganAlarmTypeVO> delFinalList = new ArrayList<>();
            String tenantId = vo.getTenantId();
            String industryCode = vo.getIndustryCode();
            String appVerCode = vo.getAppVerCode();
            List<AlarmTypeSyncDataVO> list = new ArrayList<>();
            AlarmTypeSyncDataVO tempVO = new AlarmTypeSyncDataVO();
            tempVO.setTenantId(tenantId);
            tempVO.setIndustryCode(industryCode);
            tempVO.setAppVerCode(appVerCode);
            list.add(tempVO);
            //查询全部客户或部分客户的组织
            List<OrganAlarmTypeVO> deptList = deptMapper.selectOrganList(list);
            if(!CollectionUtils.isEmpty(vo.getDeleteAlarmTypeList())){
                delFinalList = dealThirdAlarmFinalList(tenantId,industryCode,appVerCode,deptList,vo.getDeleteAlarmTypeList());
            }
            if(!CollectionUtils.isEmpty(vo.getAddAlarmTypeList())){
                addFinalList = dealThirdAlarmFinalList(tenantId,industryCode,appVerCode,deptList,vo.getAddAlarmTypeList());
            }
            List<ScreenDataConfigVO> screenDataConfigList = new ArrayList<>();
            List<String> ternantAddAlarmTypeList = vo.getAddAlarmTypeList();
            List<String> ternantDeleteAlarmTypeList = vo.getDeleteAlarmTypeList();
            if(!CollectionUtils.isEmpty(ternantAddAlarmTypeList)){
                organizationalParameters(Constants.ZERO_STR, vo, ternantAddAlarmTypeList, screenDataConfigList);
            }

            if(!CollectionUtils.isEmpty(ternantDeleteAlarmTypeList)){
                organizationalParameters(Constants.ONE_STR, vo, ternantDeleteAlarmTypeList, screenDataConfigList);
            }
            try {
                if(!CollectionUtils.isEmpty(delFinalList)){
                    //批量删除【vm_organ_alarm_type_rel_t】
                    List<List<OrganAlarmTypeVO>> partitionList = Lists.partition(delFinalList, Constants.FIVE_THOUSAND);
                    partitionList.forEach(item ->
                            deptMapper.delOrganAlarmTypeBatch(item)
                    );
                }
                if(!CollectionUtils.isEmpty(addFinalList)){
                    //批量插入【vm_organ_alarm_type_rel_t】
                    List<List<OrganAlarmTypeVO>> partitionList = Lists.partition(addFinalList, Constants.FIVE_THOUSAND);
                    partitionList.forEach(item ->
                            deptMapper.insertOrganAlarmTypeBatch(item)
                    );
                }
                if(!CollectionUtils.isEmpty(screenDataConfigList)){
                    log.info("editThirdPartyAlarmConfig=" + JSON.toJSONString(screenDataConfigList));
                    videoManageRemoteService.editThirdPartyAlarmConfig(screenDataConfigList);
                }
                return Result.success("同步成功");
            }catch (Exception e){
                log.error(Constants.BATCH_INSERT_FAIL,e);
                return Result.fail("同步失败");
            }
        } catch (Exception e) {
            log.info("thirdPartyAlarmSync异常", e);
            return Result.fail("同步失败");
        }
    }

    public void organizationalParameters(String deleteFlag, ThirdPartyAlarmTypeVO vo, List<String> ternantEditAlarmTypeList, List<ScreenDataConfigVO> screenDataConfigList) {
        ScreenDataConfigVO editVO = new ScreenDataConfigVO();
        editVO.setDeleteFlag(deleteFlag);
        editVO.setTenantId(vo.getTenantId());
        editVO.setIndustryCode(vo.getIndustryCode());
        List<ScreenDataConfigParticipationVO> editThirdPartyAlarmList = new ArrayList<>();
        for (String alarmTypeCode: ternantEditAlarmTypeList) {
            ScreenDataConfigParticipationVO participationVO = new ScreenDataConfigParticipationVO();
            participationVO.setCode(alarmTypeCode);
            editThirdPartyAlarmList.add(participationVO);
        }
        editVO.setAlarmTypeList(editThirdPartyAlarmList);
        screenDataConfigList.add(editVO);
    }



    public void dealFinalList(List<AlarmTypeSyncDataVO> addList,List<OrganAlarmTypeVO> deptList,List<OrganAlarmTypeVO> finalList,boolean towerFlag) {
        if(!CollectionUtils.isEmpty(addList) && !CollectionUtils.isEmpty(deptList)){
            for (int i = 0; i < deptList.size(); i++) {
                OrganAlarmTypeVO deptVO = deptList.get(i);
                for (int j = 0; j < addList.size(); j++) {
                        if(towerFlag || !towerFlag && deptList.get(i).getTenantId().equals(addList.get(j).getTenantId())
                            && deptList.get(i).getIndustryCode().equals(addList.get(j).getIndustryCode())){
                            //不用app_ver_code判断 因为新增客户告警和新增铁塔告警该字段都为空
                        AlarmTypeSyncDataVO tempVO = addList.get(j);
                        OrganAlarmTypeVO tmpOrganAlarmTypeVO = new OrganAlarmTypeVO();
                        tmpOrganAlarmTypeVO.setTenantId(deptVO.getTenantId());
                        tmpOrganAlarmTypeVO.setDeptId(deptVO.getDeptId());
                        tmpOrganAlarmTypeVO.setWarningSource(tempVO.getAlarmSource());
                        tmpOrganAlarmTypeVO.setTypeValue(tempVO.getTypeValue());
                        tmpOrganAlarmTypeVO.setCreator(Constants.ADD);
                        tmpOrganAlarmTypeVO.setIndustryCode(deptVO.getIndustryCode());
//                            tmpOrganAlarmTypeVO.setAppVerCode(deptVO.getAppVerCode());
                        finalList.add(tmpOrganAlarmTypeVO);
                    }
                }
            }

        }
    }


//    public void dealThirdFinalList(List<ThirdPartyAlarmTypeSyncDataVO> addList,List<OrganAlarmTypeVO> finalList) {
//        if(!CollectionUtils.isEmpty(addList)){
//            List<AlarmTypeSyncDataVO> list = new ArrayList<>();
//            for (int i = 0; i < addList.size(); i++) {
//                AlarmTypeSyncDataVO tempVO = new AlarmTypeSyncDataVO();
//                tempVO.setTenantId(addList.get(i).getTenantId());
//                tempVO.setIndustryCode(addList.get(i).getIndustryCode());
//                tempVO.setAppVerCode(addList.get(i).getAppVerCode());
//                list.add(tempVO);
//            }
//            //查询全部客户或部分客户的组织
//            List<OrganAlarmTypeVO> deptList = deptMapper.selectOrganList(list);
//            if(!CollectionUtils.isEmpty(deptList)){
//                for (int i = 0; i < deptList.size(); i++) {
//                    OrganAlarmTypeVO deptVO = deptList.get(i);
//                    for (int j = 0; j < addList.size(); j++) {
//                        if(deptList.get(i).getTenantId().equals(addList.get(j).getTenantId())
//                                && deptList.get(i).getIndustryCode().equals(addList.get(j).getIndustryCode())
//                                && deptList.get(i).getAppVerCode().equals(addList.get(j).getAppVerCode())){
//                            //第三方告警增加app_ver_code判断 因为新增第三方告警时会落该字段
//                            ThirdPartyAlarmTypeSyncDataVO tempVO = addList.get(j);
//                            OrganAlarmTypeVO tmpOrganAlarmTypeVO = new OrganAlarmTypeVO();
//                            tmpOrganAlarmTypeVO.setTenantId(deptVO.getTenantId());
//                            tmpOrganAlarmTypeVO.setDeptId(deptVO.getDeptId());
//                            tmpOrganAlarmTypeVO.setWarningSource(Constants.ELEVEN_STR);
//                            tmpOrganAlarmTypeVO.setTypeValue(tempVO.getAlarmType());
//                            tmpOrganAlarmTypeVO.setCreator(Constants.ADD);
//                            tmpOrganAlarmTypeVO.setIndustryCode(deptVO.getIndustryCode());
//                            tmpOrganAlarmTypeVO.setAppVerCode(tempVO.getAppVerCode());
//                            finalList.add(tmpOrganAlarmTypeVO);
//                        }
//                    }
//                }
//            }
//        }
//    }



    public List<OrganAlarmTypeVO> dealThirdAlarmFinalList(String tenantId,String industryCode,String appVerCode,List<OrganAlarmTypeVO> deptList,List<String> alarmTypeList) {
        List<OrganAlarmTypeVO> finalList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(deptList) && !CollectionUtils.isEmpty(alarmTypeList) ){
            for (int i = 0; i < deptList.size(); i++) {
                OrganAlarmTypeVO deptVO = deptList.get(i);
                for (int j = 0; j < alarmTypeList.size(); j++) {
                    //第三方告警增加app_ver_code判断 因为新增第三方告警时会落该字段
                    String alarmType = alarmTypeList.get(j);
                    OrganAlarmTypeVO tmpOrganAlarmTypeVO = new OrganAlarmTypeVO();
                    tmpOrganAlarmTypeVO.setTenantId(tenantId);
                    tmpOrganAlarmTypeVO.setDeptId(deptVO.getDeptId());
                    tmpOrganAlarmTypeVO.setWarningSource(Constants.ELEVEN_STR);
                    tmpOrganAlarmTypeVO.setTypeValue(alarmType);
                    tmpOrganAlarmTypeVO.setCreator(Constants.ADD);
                    tmpOrganAlarmTypeVO.setIndustryCode(industryCode);
                    tmpOrganAlarmTypeVO.setAppVerCode(appVerCode);
                    finalList.add(tmpOrganAlarmTypeVO);
                }
            }
        }
        return finalList;
    }
}





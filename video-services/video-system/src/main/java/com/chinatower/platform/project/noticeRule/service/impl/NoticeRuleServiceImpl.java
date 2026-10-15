package com.chinatower.platform.project.noticeRule.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.IMessageOperateService;
import com.chinatower.event.api.feign.client.IAlarmTypeService;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.DelayMsgTypeDto;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceTableConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.noticeRule.entity.*;
import com.chinatower.platform.project.noticeRule.mapper.SysNoticeRuleMapper;
import com.chinatower.platform.project.noticeRule.service.INoticeRuleService;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.*;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 *  消息提醒规则表 服务实现类
 * </p>
 *
 * @author
 * @since 2023-02-01
 */
@Slf4j
@Service
public class NoticeRuleServiceImpl extends ServiceImpl<SysNoticeRuleMapper, NoticeRuleDO> implements INoticeRuleService {

    @Resource
    private IAlarmTypeService iAlarmTypeService;

    @Resource
    private IMessageOperateService iMessageOperateService;

    @Override
    public int updateIsNotice(NoticeRuleVO noticeRuleVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        log.info("更新告警消息通知规则开关接口 入参={} , userId = {},industryCode = {},time = {}" , JSONObject.toJSON(noticeRuleVO),user.getUserId(),user.getIndustryCode(),new Date().getTime());
        noticeRuleVO.setUserId(user.getUserId());
        noticeRuleVO.setIndustryCode(user.getIndustryCode());
        this.baseMapper.updateIsNotice(noticeRuleVO);

        //是否开启通知 0:关闭 1:开启
        if(Constants.ZERO_STR.equals(noticeRuleVO.getIsNotice())){
            try {
                DelayMsgTypeDto delayMsgTypeDto = new DelayMsgTypeDto();
                BeanUtils.copyProperties(noticeRuleVO,delayMsgTypeDto);
                // 推送类型 0:消息类 1:通知类
                delayMsgTypeDto.setMsgType(Constants.ZERO_STR);
                log.info("批量更改延迟消息状态为已处理接口入参{}",JSONObject.toJSON(delayMsgTypeDto));
                Result result = iMessageOperateService.updateDealyMessageMsgType(delayMsgTypeDto);
                log.info("批量更改延迟消息状态为已处理接口出参{}",JSONObject.toJSON(result));
            } catch (Exception e) {
                log.error("批量更改延迟消息状态为已处理接口异常",e);
            }
        }

        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result add(NoticeRuleAddVO noticeRuleAddVO){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        log.info("创建用户通知规则入参{}={}", user.getUserId(), JSONObject.toJSON(noticeRuleAddVO));
        //去空格
        noticeRuleAddVO.setRuleName(noticeRuleAddVO.getRuleName().replaceAll("\\s*", ""));
        //校验规则名称是否重复-用户级别
        Result result = this.checkRuleName(noticeRuleAddVO.getRuleName(), user.getUserId(),null);
        if(!ObjectUtils.isEmpty(result)){
            return result;
        }

        NoticeRuleDO noticeRuleDO = new NoticeRuleDO();
        BeanUtils.copyProperties(noticeRuleAddVO,noticeRuleDO);

        QueryWrapper<NoticeRuleDO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(Constants.USER_ID, user.getUserId())
                .eq(Constants.INDUSTRY_CODE, user.getIndustryCode());

        //查询存量规则
        List<NoticeRuleDO> noticeRuleDOS = this.list(queryWrapper);
        if(!CollectionUtils.isEmpty(noticeRuleDOS)){
            noticeRuleDO.setIsNotice(noticeRuleDOS.get(Constants.ZERO).getIsNotice());
        }
        noticeRuleDO.setUserId(user.getUserId());
        noticeRuleDO.setIndustryCode(user.getIndustryCode());
        noticeRuleDO.setAppVerCode(user.getAppVerCode());
        noticeRuleDO.setCreator(user.getUserName());
        noticeRuleDO.setCreateTime(new Date());

        List<NoticeExtendVO> extendInfo = noticeRuleAddVO.getExtendInfo();
        List<NoticeAlarmSourceVO> alarmInfo = noticeRuleAddVO.getAlarmInfo();
        if(!CollectionUtils.isEmpty(alarmInfo)){
            noticeRuleDO.setAlarmTypeText(new Gson().toJson(alarmInfo));
        }
        if(!CollectionUtils.isEmpty(extendInfo)){
            noticeRuleDO.setExtendInfoText(new Gson().toJson(extendInfo));
        }
        //替换存量规则的相同告警类型
        this.replaceHisAlarmType(user,noticeRuleAddVO);
        this.baseMapper.insert(noticeRuleDO);
        return Result.success("创建成功！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result edit(NoticeRuleAddVO noticeRuleAddVO){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        log.info("修改用户通知规则入参{}={}", user.getUserId(), JSONObject.toJSON(noticeRuleAddVO));
        if(ObjectUtils.isEmpty(noticeRuleAddVO.getId())){
            return Result.fail("规则唯一标识不能为空！");
        }
        //去空格
        noticeRuleAddVO.setRuleName(noticeRuleAddVO.getRuleName().replaceAll("\\s*", ""));
        //校验规则名称是否重复-用户级别
        Result result = this.checkRuleName(noticeRuleAddVO.getRuleName(), user.getUserId(),noticeRuleAddVO.getId());
        if(!ObjectUtils.isEmpty(result)){
            return result;
        }

        NoticeRuleDO noticeRuleDO = new NoticeRuleDO();
        BeanUtils.copyProperties(noticeRuleAddVO,noticeRuleDO);
        noticeRuleDO.setModifier(user.getUserName());
        noticeRuleDO.setUpdateTime(new Date());

        List<NoticeAlarmSourceVO> alarmInfo = noticeRuleAddVO.getAlarmInfo();
        List<NoticeExtendVO> extendInfo = noticeRuleAddVO.getExtendInfo();
        if(!CollectionUtils.isEmpty(alarmInfo)){
            noticeRuleDO.setAlarmTypeText(new Gson().toJson(alarmInfo));
        }
        if(!CollectionUtils.isEmpty(extendInfo)){
            noticeRuleDO.setExtendInfoText(new Gson().toJson(extendInfo));
        }
        //替换存量规则的相同告警类型
        this.replaceHisAlarmType(user,noticeRuleAddVO);
        this.baseMapper.updateById(noticeRuleDO);
        return Result.success("修改成功！");
    }

    /**
     * 功能描述: 替换存量规则的相同告警类型
     * @param
     * @author: hued
     * @date: 2023年2月1日17:45:31
     * @return
     * @version: 1.5.70
     * @requestID
     */
    public void replaceHisAlarmType(SysUser user,NoticeRuleAddVO noticeRuleAddVO){
        Long editId = noticeRuleAddVO.getId();
        List<NoticeAlarmSourceVO> alarmInfoList = noticeRuleAddVO.getAlarmInfo();
        //需要更新的规则集合
        List<NoticeRuleDO> updateRuleList = new ArrayList<>();
        //需要删除的规则集合
        List<NoticeRuleDO> deleteRuleList = new ArrayList<>();

        //将入参所有告警来源下的告警类型组成新的告警类型集合
        List<NoticeAlarmTypeVO> alarmTypeInList = new ArrayList<>();
        for (NoticeAlarmSourceVO noticeAlarmSourceVO : alarmInfoList) {
            for (NoticeAlarmTypeVO noticeAlarmTypeVO : noticeAlarmSourceVO.getAlarmTypeVOList()) {
                alarmTypeInList.add(noticeAlarmTypeVO);
            }
        }

        QueryWrapper<NoticeRuleDO> queryWrapper = new QueryWrapper<>();
        if(ObjectUtils.isEmpty(editId)){
            queryWrapper
                    .eq(Constants.USER_ID, user.getUserId())
                    .eq(Constants.INDUSTRY_CODE, user.getIndustryCode())
                    .eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
        }else{
            queryWrapper
                    .ne(Constants.ID, editId)
                    .eq(Constants.USER_ID, user.getUserId())
                    .eq(Constants.INDUSTRY_CODE, user.getIndustryCode())
                    .eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
        }

        //查询存量规则
        List<NoticeRuleDO> noticeRuleDOS = this.list(queryWrapper);
        for (NoticeRuleDO noticeRuleDO : noticeRuleDOS) {
            Long id = noticeRuleDO.getId();
            String alarmTypeText = noticeRuleDO.getAlarmTypeText();
            if(!StringUtils.isEmpty(alarmTypeText)){
                List<NoticeAlarmSourceVO> alarmTypeVOList = JSON.parseArray(alarmTypeText, NoticeAlarmSourceVO.class);

                //将数据库所有告警来源下的告警类型组成新的告警类型集合
                List<NoticeAlarmTypeVO> alarmTypeDBList = new ArrayList<>();
                for (NoticeAlarmSourceVO noticeAlarmSourceVO : alarmTypeVOList) {
                    for (NoticeAlarmTypeVO noticeAlarmTypeVO : noticeAlarmSourceVO.getAlarmTypeVOList()) {
                        alarmTypeDBList.add(noticeAlarmTypeVO);
                    }
                }

                //将数据库的告警类型集合，转为map
                HashMap<String, NoticeAlarmTypeVO> noticeAlarmSourceDBMap = new HashMap<>();
                for (NoticeAlarmTypeVO noticeAlarmTypeVO: alarmTypeDBList) {
                    noticeAlarmSourceDBMap.put(noticeAlarmTypeVO.getAlarmSource()
                            + com.chinatower.platform.project.custom.common.Constants.HYPHEN
                            + noticeAlarmTypeVO.getTypeValue(), noticeAlarmTypeVO);
                }

                //true:此条规则需要更新，false:不需要更新
                boolean flag = false;
                //入参规则
                for (NoticeAlarmTypeVO noticeAlarmTypeVO : alarmTypeInList) {
                    StringBuffer key = new StringBuffer(noticeAlarmTypeVO.getAlarmSource())
                            .append(com.chinatower.platform.project.custom.common.Constants.HYPHEN)
                            .append(noticeAlarmTypeVO.getTypeValue());

                    //数据库规则告警类型包含入参规则告警类型，则删除数据库的该告警类型
                    if (noticeAlarmSourceDBMap.containsKey(key.toString())) {
                        noticeAlarmSourceDBMap.remove(key.toString());
                        flag = true;
                    }
                }

                //需要更新存量规则的告警类型
                if(flag){
                    for (int i = alarmTypeVOList.size() - 1; i >= 0; i--) {
                        NoticeAlarmSourceVO noticeAlarmSourceVO = alarmTypeVOList.get(i);
                        List<NoticeAlarmTypeVO> updateAlarmTypeList = new ArrayList<>();
                        for (Map.Entry<String, NoticeAlarmTypeVO> entry : noticeAlarmSourceDBMap.entrySet()) {
                            NoticeAlarmTypeVO value = entry.getValue();
                            if (noticeAlarmSourceVO.getAlarmSource().equals(value.getAlarmSource())) {
                                updateAlarmTypeList.add(value);
                            }
                        }
                        if(CollectionUtils.isEmpty(updateAlarmTypeList)){
                            alarmTypeVOList.remove(i);
                        }else{
                            noticeAlarmSourceVO.setAlarmTypeVOList(updateAlarmTypeList);
                        }
                    }
                    if(CollectionUtils.isEmpty(alarmTypeVOList)){
                        NoticeRuleDO deleteRuleDO = new NoticeRuleDO();
                        deleteRuleDO.setId(id);
                        deleteRuleDO.setModifier(user.getUserName());
                        deleteRuleDO.setUpdateTime(new Date());
                        deleteRuleDO.setDeleteFlag(DeviceConstants.YES_FLAG);
                        deleteRuleList.add(deleteRuleDO);
                    }else{
                        NoticeRuleDO updateRuleDO = new NoticeRuleDO();
                        updateRuleDO.setId(id);
                        updateRuleDO.setAlarmTypeText(new Gson().toJson(alarmTypeVOList));
                        updateRuleDO.setModifier(user.getUserName());
                        updateRuleDO.setUpdateTime(new Date());
                        updateRuleList.add(updateRuleDO);
                    }
                }
            }
        }

        if(!CollectionUtils.isEmpty(updateRuleList)){
            this.baseMapper.updateRuleBatch(updateRuleList);
        }
        if(!CollectionUtils.isEmpty(deleteRuleList)){
            this.baseMapper.updateRuleBatch(deleteRuleList);
        }
    }

    /**
     * //用户下无规则（有效+无效）：按照默认规则通知
     * //用户下有规则，根据告警来源+告警类型+告警时间判断是否命中，
     * //  未命中：消息类
     * //  命中：
     * //      开：通知类
     * //      关：消息类
     * @param noticeRuleFilterVOList
     * @return
     */
    @Override
    public List<NoticeRuleFilterResultVO> filterUserList(List<NoticeRuleFilterVO> noticeRuleFilterVOList){
        List<NoticeRuleFilterResultVO> resultList = new ArrayList<>();
        //按照默认规则通知的用户list
        List<NoticeRuleFilterVO> defaultList;

        if(!CollectionUtils.isEmpty(noticeRuleFilterVOList)){
            //查询所有规则（有效+无效）
            List<NoticeRuleDO> noticeRuleDOS = this.baseMapper.selectRuleBatchList(noticeRuleFilterVOList);
            //过滤按照默认规则通知的用户list
            defaultList = noticeRuleFilterVOList.stream().filter(pe ->
                            containEntity(pe.getUserId(),pe.getIndustryCode(), noticeRuleDOS) <= -1).
                    collect(Collectors.toList());
            //处理默认规则的用户集合
            if(!CollectionUtils.isEmpty(defaultList)){
                log.info("匹配默认规则用户集合={}",JSONObject.toJSON(defaultList));
                List<NoticeExtendVO> extendInfoList = new ArrayList<>();

                //取出rule_type为0的默认规则(初始化的数据)
                QueryWrapper<NoticeRuleDO> QueryDefault = new QueryWrapper<>();
                QueryDefault.eq(Constants.RULE_TYPE,Constants.ZERO_STR);//默认规则
                NoticeRuleDO defaultNoticeRule = this.getOne(QueryDefault);
                if(!ObjectUtils.isEmpty(defaultNoticeRule) && !StringUtils.isEmpty(defaultNoticeRule.getExtendInfoText())){
                    String extendInfoText = defaultNoticeRule.getExtendInfoText();
                    extendInfoList = JSON.parseArray(extendInfoText, NoticeExtendVO.class);
                }
                for (NoticeRuleFilterVO noticeRuleFilterVO : defaultList) {
                    NoticeRuleFilterResultVO noticeRuleFilterResultVO = new NoticeRuleFilterResultVO();
                    BeanUtils.copyProperties(noticeRuleFilterVO,noticeRuleFilterResultVO);
                    noticeRuleFilterResultVO.setMsgType(Constants.ONE_STR);//通知类
                    noticeRuleFilterResultVO.setNoticeExtendVO(extendInfoList);
                    resultList.add(noticeRuleFilterResultVO);
                }
            }

            //将查询出来的有效规则包含的告警类型转换到map中，key：用户编码-行业编码-告警来源-告警类型，value：规则唯一标识
            HashMap<String, Long> noticeAlarmSourceDBMap = new HashMap<>();
            for (NoticeRuleDO noticeRuleDO : noticeRuleDOS) {
                //有效规则
                if (Constants.ZERO_STR.equals(noticeRuleDO.getDeleteFlag())) {
                    String alarmTypeText = noticeRuleDO.getAlarmTypeText();
                    if (!StringUtils.isEmpty(alarmTypeText)) {
                        List<NoticeAlarmSourceVO> alarmInfoList = JSON.parseArray(alarmTypeText, NoticeAlarmSourceVO.class);
                        for (NoticeAlarmSourceVO noticeAlarmSourceVO : alarmInfoList) {
                            for (NoticeAlarmTypeVO noticeAlarmTypeVO : noticeAlarmSourceVO.getAlarmTypeVOList()) {
                                StringBuffer key = new StringBuffer(noticeRuleDO.getUserId())
                                        .append(Constants.LINE_CENTER).append(noticeRuleDO.getIndustryCode())
                                        .append(Constants.LINE_CENTER).append(noticeAlarmTypeVO.getAlarmSource())
                                        .append(Constants.LINE_CENTER).append(noticeAlarmTypeVO.getTypeValue());

                                noticeAlarmSourceDBMap.put(key.toString(),noticeRuleDO.getId());
                            }
                        }
                    }
                }
            }

            //去掉defaultList中的用户
            List<NoticeRuleFilterVO> noticeRuleFilterNewList = noticeRuleFilterVOList.stream().filter(pe ->
                            containEntity(pe.getUserId(),pe.getIndustryCode(), noticeRuleDOS) > -1).
                    collect(Collectors.toList());

            //循环去掉defaultList后的用户集合
            for (NoticeRuleFilterVO noticeRuleFilterVO : noticeRuleFilterNewList) {
                boolean ifHit = false;
                String isNotice = "";
                String alarmTime = noticeRuleFilterVO.getAlarmTime();//告警时间
                List<NoticeExtendVO> extendInfoList = new ArrayList<>();

                StringBuffer key = new StringBuffer(noticeRuleFilterVO.getUserId())
                        .append(Constants.LINE_CENTER).append(noticeRuleFilterVO.getIndustryCode())
                        .append(Constants.LINE_CENTER).append(noticeRuleFilterVO.getWarningSource())
                        .append(Constants.LINE_CENTER).append(noticeRuleFilterVO.getWarningTypeId());

                //key匹配成功
                log.info("重组有效规则Map={}",JSONObject.toJSON(noticeAlarmSourceDBMap));
                if(noticeAlarmSourceDBMap.containsKey(key.toString())){
                    List<NoticeRuleDO> filterRuleList = noticeRuleDOS.stream().filter(item -> Objects.equals(item.getId(), noticeAlarmSourceDBMap.get(key.toString()))).collect(Collectors.toList());
                    NoticeRuleDO noticeRuleDO = filterRuleList.get(Constants.ZERO);
                    String extendInfoText = noticeRuleDO.getExtendInfoText();
                    log.info("命中告警类型的规则id={},isNotice={}",noticeRuleDO.getId(),noticeRuleDO.getIsNotice());
                    log.info("命中告警类型的规则扩展信息={}",extendInfoText);

                    //根据告警时间/定时时间，匹配星期、生效时间,并返回匹配上的元素
                    List<NoticeExtendVO> noticeMatchExtendVOList = this.matchExtend(extendInfoText, alarmTime);
                    if(!CollectionUtils.isEmpty(noticeMatchExtendVOList)){
                        ifHit = true;
                        isNotice = noticeRuleDO.getIsNotice();
                        extendInfoList.addAll(noticeMatchExtendVOList);
                    }

                }

                // 推送类型 0:消息类 1:通知类
                String msgType = Constants.ZERO_STR;//消息类
                //命中规则
                if(ifHit && Constants.ONE_STR.equals(isNotice)){
                    msgType = Constants.ONE_STR;//通知类
                }
                NoticeRuleFilterResultVO noticeRuleFilterResultVO = new NoticeRuleFilterResultVO();
                BeanUtils.copyProperties(noticeRuleFilterVO,noticeRuleFilterResultVO);
                noticeRuleFilterResultVO.setMsgType(msgType);
                noticeRuleFilterResultVO.setNoticeExtendVO(extendInfoList);
                resultList.add(noticeRuleFilterResultVO);
            }
        }
        //返回值 消除对同一对象循环引用的问题
        JSONArray jsonArrayRtn= JSONArray.parseArray(JSON.toJSONString(resultList, SerializerFeature.DisableCircularReferenceDetect));
        List<NoticeRuleFilterResultVO> r = jsonArrayRtn.toJavaList(NoticeRuleFilterResultVO.class);
        log.info("筛选用户通知规则出参={}",JSONObject.toJSON(r));
        return r;
    }

    public int containEntity(String userId, String industryCode, List<NoticeRuleDO> list) {
        int res = -1;
        for (int i = 0; i < list.size(); i++) {
            if (userId.equals(list.get(i).getUserId()) &&
                    industryCode.equals(list.get(i).getIndustryCode())) {
                res = i;
                break;
            }
        }
        return res;
    }

    /**
     * 功能描述: 根据登录人查询列表
     * @param
     * @author: lijiwei
     * @date: 2023年2月2日17:45:31
     * @return
     * @version: 1.5.70
     * @requestID
     */
    @Override
    public Result getListByUserId(NoticeRuleVO req){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        log.info("根据登录人查询规则列表 入参={} , userId = {},industryCode = {},time = {}" , JSONObject.toJSON(req),user.getUserId(),user.getIndustryCode(),new Date().getTime());
        QueryWrapper<NoticeRuleDO> noticeRuleQuery = new QueryWrapper<>();
        if(ObjectUtils.isEmpty(user.getIndustryCode())||ObjectUtils.isEmpty(user.getUserId())){
            return Result.fail("获取登录信息不完整！");
        }
        noticeRuleQuery.eq(Constants.USER_ID, user.getUserId());
        noticeRuleQuery.eq(Constants.INDUSTRY_CODE, user.getIndustryCode());
        noticeRuleQuery.orderByDesc(Constants.UPDATE_TIME,Constants.CREATE_TIME);
        //根据登陆人的userId和industryCode判断是否有规则
        List<NoticeRuleDO> list = this.list(noticeRuleQuery);

        try{
            //与当前订购的告警类型拉齐，防止订单变更或退订后，通知规则的告警类型历史数据依然保留的问题
            this.checkOrderAlarm(list);
        }catch (Exception e){
            log.error("与当前订购告警类型拉齐异常！", e);
        }

        //如果没有规则的话 将默认规则全部插入表中
        if(CollectionUtils.isEmpty(list)){
            //取出rule_type为0的默认规则(初始化的数据)
            QueryWrapper<NoticeRuleDO> QueryDefault = new QueryWrapper<>();
            QueryDefault.eq(Constants.RULE_TYPE,Constants.ZERO_STR);
            NoticeRuleDO defaultNoticeRule = this.getOne(QueryDefault);
            log.info("查询数据库默认规则 - 返回:"+ JSONObject.toJSON(defaultNoticeRule));
            if(ObjectUtils.isEmpty(defaultNoticeRule)){
                return Result.fail("未配置默认规则配置！");
            }
            //为当前登陆人新建一条规则 将默认规则赋予当前登陆人
            Date now = new Date();
            NoticeRuleDO noticeRuleDO = new NoticeRuleDO();
            noticeRuleDO.setRuleName(defaultNoticeRule.getRuleName());
            noticeRuleDO.setUserId(user.getUserId());
            noticeRuleDO.setIndustryCode(user.getIndustryCode());
            noticeRuleDO.setAppVerCode(user.getAppVerCode());
            //判断如果默认规则中告警类型配置为空 则取租户配置得告警类型
            if(StringUtils.isEmpty(defaultNoticeRule.getAlarmTypeText())){
                //调用公共方法获取租户订购得告警类型List
                Result<List<AlarmSourceSubTypeVO>> result = this.getAlarmType();
                if(!CollectionUtils.isEmpty(result.getData())){
                    noticeRuleDO.setAlarmTypeText(new Gson().toJson(result.getData()));
                }
            }else{
                noticeRuleDO.setAlarmTypeText(defaultNoticeRule.getAlarmTypeText());
            }
            noticeRuleDO.setExtendInfoText(defaultNoticeRule.getExtendInfoText());
            noticeRuleDO.setCreateTime(now);
            noticeRuleDO.setCreator(user.getUserName());
            this.save(noticeRuleDO);
            noticeRuleDO.setIsNotice(defaultNoticeRule.getIsNotice());
            noticeRuleDO.setDeleteFlag(Constants.ZERO_STR);
            list.add(noticeRuleDO);
            log.info("查询数据库默认规则 - 拼接后:"+ JSONObject.toJSON(list));
        }
        List<NoticeRuleVO> rList = new ArrayList<>();
        list.stream().forEach(item -> {
            NoticeRuleVO noticeRuleVOS = new NoticeRuleVO();
            BeanUtils.copyProperties(item,noticeRuleVOS);
            //列表接口有字段返回太长 前端看不到日志 以下四个字段先设置为空
            noticeRuleVOS.setAlarmTypeText(Constants.EMPTY_STR);
            noticeRuleVOS.setExtendInfoText(Constants.EMPTY_STR);
            if(Constants.ZERO_STR.equals(noticeRuleVOS.getDeleteFlag())){
                rList.add(noticeRuleVOS);
            }
        });
        NoticeRuleListVO noticeRuleListVO = new NoticeRuleListVO();
        noticeRuleListVO.setIsNotice(list.get(0).getIsNotice());
        noticeRuleListVO.setList(rList);
        log.info("根据登录人查询规则列表方法 - 返回:"+ JSONObject.toJSON(noticeRuleListVO));
        return Result.data(noticeRuleListVO);
    }

    /**
     * 与当前订购的告警类型拉齐，防止订单变更或退订后，通知规则的告警类型历史数据依然保留的问题
     * @param list
     */
    private void checkOrderAlarm(List<NoticeRuleDO> list) throws Exception{
        //删除状态的规则
        List<NoticeRuleDO> deleteList = list.stream().filter(e-> Constants.ONE_STR.equals(e.getDeleteFlag())).collect(Collectors.toList());
        //过滤有效的规则
        list = list.stream().filter(e-> Constants.ZERO_STR.equals(e.getDeleteFlag())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(list)){
            //查询当前订购的告警来源、类型
            Result<List<AlarmSourceSubTypeVO>> result = this.getAlarmType();
            log.info("getAlarmType="+JSONObject.toJSON(result));
            if(ResultCode.SUCCESS.getCode() == result.getCode()){
                //需要更新的规则集合
                List<NoticeRuleDO> updateRuleList = new ArrayList<>();
                //需要删除的规则集合
                List<NoticeRuleDO> deleteRuleList = new ArrayList<>();

                List<AlarmSourceSubTypeVO> alarmSourceOrderList = result.getData();
                //将订购的告警类型集合，转为map
                List<String> orderAlarmList = new ArrayList<>();
                for (AlarmSourceSubTypeVO alarmSourceSubTypeVO : alarmSourceOrderList) {
                    for (AlarmTypeVO noticeAlarmTypeVO: alarmSourceSubTypeVO.getAlarmTypeVOList()) {
                        //key规则：告警来源-告警类型
                        orderAlarmList.add(noticeAlarmTypeVO.getAlarmSource() + Constants.LINE_CENTER + noticeAlarmTypeVO.getTypeValue());
                    }
                }
                for (NoticeRuleDO noticeRuleDO : list) {
                    Long id = noticeRuleDO.getId();
                    String alarmTypeText = noticeRuleDO.getAlarmTypeText();

                    if(!StringUtils.isEmpty(alarmTypeText)){
                        List<NoticeAlarmSourceVO> alarmInfoList = JSON.parseArray(alarmTypeText, NoticeAlarmSourceVO.class);
                        boolean flag = false;
                        for (int i = alarmInfoList.size() - 1; i >= 0; i--) {
                            NoticeAlarmSourceVO noticeAlarmSourceVO = alarmInfoList.get(i);
                            List<NoticeAlarmTypeVO> alarmTypeVOList = noticeAlarmSourceVO.getAlarmTypeVOList();
                            for (int j = alarmTypeVOList.size() - 1; j >= 0; j--) {
                                NoticeAlarmTypeVO noticeAlarmTypeVO = alarmTypeVOList.get(j);
                                //key规则：告警来源-告警类型
                                String key = noticeAlarmTypeVO.getAlarmSource() + Constants.LINE_CENTER + noticeAlarmTypeVO.getTypeValue();
                                //数据库有，订购没有，需要移除
                                if(!orderAlarmList.contains(key)){
                                    alarmTypeVOList.remove(noticeAlarmTypeVO);
                                    flag = true;
                                }
                            }
                            //告警类型节点为空，则删除上级告警来源节点
                            if(CollectionUtils.isEmpty(alarmTypeVOList)){
                                alarmInfoList.remove(noticeAlarmSourceVO);
                            }
                        }
                        if(flag){
                            if(CollectionUtils.isEmpty(alarmInfoList)){
                                NoticeRuleDO deleteRuleDO = new NoticeRuleDO();
                                deleteRuleDO.setId(id);
                                deleteRuleDO.setModifier("order");
                                deleteRuleDO.setUpdateTime(new Date());
                                deleteRuleDO.setDeleteFlag(DeviceConstants.YES_FLAG);
                                deleteRuleList.add(deleteRuleDO);
                                //将list中的对应规则更新为删除状态
                                noticeRuleDO.setDeleteFlag(DeviceConstants.YES_FLAG);
                            }else{
                                NoticeRuleDO updateRuleDO = new NoticeRuleDO();
                                updateRuleDO.setId(id);
                                updateRuleDO.setAlarmTypeText(new Gson().toJson(alarmInfoList));
                                updateRuleDO.setModifier("order");
                                updateRuleDO.setUpdateTime(new Date());
                                updateRuleList.add(updateRuleDO);
                            }
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(updateRuleList)){
                    log.info("updateRuleList="+JSONObject.toJSON(updateRuleList));
                    this.baseMapper.updateRuleBatch(updateRuleList);
                }
                if(!CollectionUtils.isEmpty(deleteRuleList)){
                    log.info("deleteRuleList="+JSONObject.toJSON(deleteRuleList));
                    this.baseMapper.updateRuleBatch(deleteRuleList);
                }
            }
            list.addAll(deleteList);
        }
    }


    @Override
    public Result getAlarmType(){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        AlarmSourceSubTypeDTO alarmSourceSubTypeDTO = new AlarmSourceSubTypeDTO();
        alarmSourceSubTypeDTO.setTenantId(user.getTenantId());
        alarmSourceSubTypeDTO.setAppVerCode(user.getAppVerCode());
        alarmSourceSubTypeDTO.setIndustryCode(user.getIndustryCode());
        alarmSourceSubTypeDTO.setWithThirdAlarmType(true);
        log.info("调用查询租户订购的告警类型 - 入参"+ JSONObject.toJSON(alarmSourceSubTypeDTO));
        Result<List<AlarmSourceSubTypeVO>> result = iAlarmTypeService.findAlarmSourceSubType(alarmSourceSubTypeDTO);
        log.info("调用查询租户订购的告警类型 - 返回"+ JSONObject.toJSON(result));
        //将订购得所有告警类型拼接到  人工上报:2 和 一键告警:3 下面分别包含所有查出的告警类型
        if(!CollectionUtils.isEmpty(result.getData())){
            List<AlarmTypeVO> listAll = new ArrayList<>();
            List<AlarmTypeVO> listAllS = new ArrayList<>();
            List<AlarmTypeVO> thirdAlarmS = new ArrayList<>();
            AlarmSourceSubTypeVO alarmSourceSubTypeVOF = new AlarmSourceSubTypeVO();
            AlarmSourceSubTypeVO alarmSourceSubTypeVOS = new AlarmSourceSubTypeVO();
            AlarmSourceSubTypeVO alarmSourceSubTypethirdAlarmS = new AlarmSourceSubTypeVO();
            result.getData().stream().forEach(alarmSourceSubTypeVO -> {
                if(Constants.ELEVEN_STR.equals(alarmSourceSubTypeVO.getAlarmSource())){
                    alarmSourceSubTypeVO.getAlarmTypeVOList().stream().forEach(
                            alarmTypeVO -> {
                                AlarmTypeVO a = new AlarmTypeVO();
                                a.setAlarmSource(Constants.ELEVEN_STR);
                                a.setAlarmSourceName(alarmTypeVO.getAlarmSourceName());
                                a.setTypeName(alarmTypeVO.getTypeName());
                                a.setAlarmTypeId(alarmTypeVO.getAlarmTypeId());
                                a.setIcon(alarmTypeVO.getIcon());
                                a.setTypeValue(alarmTypeVO.getTypeValue());
                                thirdAlarmS.add(a);
                            }
                    );
                }
                alarmSourceSubTypeVO.getAlarmTypeVOList().stream().forEach(
                        alarmTypeVO -> {
                            AlarmTypeVO a = new AlarmTypeVO();
                            a.setAlarmSource(Constants.THREE_STR);
                            a.setAlarmSourceName(Constants.YiJianGaoJing);
                            a.setTypeName(alarmTypeVO.getTypeName());
                            a.setAlarmTypeId(alarmTypeVO.getAlarmTypeId());
                            a.setIcon(alarmTypeVO.getIcon());
                            a.setTypeValue(alarmTypeVO.getTypeValue());
                            listAll.add(a);
                            AlarmTypeVO as = new AlarmTypeVO();
                            as.setAlarmSource(Constants.TWO_STR);
                            as.setAlarmSourceName(Constants.RenGongShangBao);
                            as.setTypeName(alarmTypeVO.getTypeName());
                            as.setAlarmTypeId(alarmTypeVO.getAlarmTypeId());
                            as.setIcon(alarmTypeVO.getIcon());
                            as.setTypeValue(alarmTypeVO.getTypeValue());
                            listAllS.add(as);
                        }
                );
//                if(!Constants.ELEVEN_STR.equals(alarmSourceSubTypeVO.getAlarmSource())){
//                    if(!Constants.TWO_STR.equals(alarmSourceSubTypeVO.getAlarmSource())){
//                        alarmSourceSubTypeVO.getAlarmTypeVOList().stream().forEach(
//                                alarmTypeVO -> {
//                                    AlarmTypeVO a = new AlarmTypeVO();
//                                    a.setAlarmSource(Constants.THREE_STR);
//                                    a.setAlarmSourceName(Constants.YiJianGaoJing);
//                                    a.setTypeName(alarmTypeVO.getTypeName());
//                                    a.setAlarmTypeId(alarmTypeVO.getAlarmTypeId());
//                                    a.setIcon(alarmTypeVO.getIcon());
//                                    a.setTypeValue(alarmTypeVO.getTypeValue());
//                                    listAll.add(a);
//                                    AlarmTypeVO as = new AlarmTypeVO();
//                                    as.setAlarmSource(Constants.TWO_STR);
//                                    as.setAlarmSourceName(Constants.RenGongShangBao);
//                                    as.setTypeName(alarmTypeVO.getTypeName());
//                                    as.setAlarmTypeId(alarmTypeVO.getAlarmTypeId());
//                                    as.setIcon(alarmTypeVO.getIcon());
//                                    as.setTypeValue(alarmTypeVO.getTypeValue());
//                                    listAllS.add(as);
//                                }
//                        );
//                    }else{
//                        listAllS.addAll(alarmSourceSubTypeVO.getAlarmTypeVOList());
//                    }
//                } else{
//                    alarmSourceSubTypeVO.getAlarmTypeVOList().stream().forEach(
//                            alarmTypeVO -> {
//                                AlarmTypeVO a = new AlarmTypeVO();
//                                a.setAlarmSource(Constants.ELEVEN_STR);
//                                a.setAlarmSourceName(alarmTypeVO.getAlarmSourceName());
//                                a.setTypeName(alarmTypeVO.getTypeName());
//                                a.setAlarmTypeId(alarmTypeVO.getAlarmTypeId());
//                                a.setIcon(alarmTypeVO.getIcon());
//                                a.setTypeValue(alarmTypeVO.getTypeValue());
//                                thirdAlarmS.add(a);
//                            }
//                    );
//                }
            });
            // 删除原来存在的人工上报的数据
            Iterator<AlarmSourceSubTypeVO> iterator = result.getData().iterator();
            while (iterator.hasNext()){
                AlarmSourceSubTypeVO alarmSourceSubTypeVOTemp = iterator.next();
                if(Constants.TWO_STR.equals(alarmSourceSubTypeVOTemp.getAlarmSource())){
                    iterator.remove();
                }
                if(Constants.ELEVEN_STR.equals(alarmSourceSubTypeVOTemp.getAlarmSource())){
                    iterator.remove();
                }
            }
            List<AlarmTypeVO> listAllDistinct = listAll.stream().distinct().collect(Collectors.toList());
            List<AlarmTypeVO> listAllSDistinct = listAllS.stream().distinct().collect(Collectors.toList());
            alarmSourceSubTypeVOS.setAlarmTypeVOList(listAllDistinct);
            alarmSourceSubTypeVOS.setAlarmSource(Constants.THREE_STR);
            alarmSourceSubTypeVOS.setAlarmSourceName(Constants.YiJianGaoJing);
            result.getData().add(alarmSourceSubTypeVOS);
            alarmSourceSubTypeVOF.setAlarmTypeVOList(listAllSDistinct);
            alarmSourceSubTypeVOF.setAlarmSource(Constants.TWO_STR);
            alarmSourceSubTypeVOF.setAlarmSourceName(Constants.RenGongShangBao);
            result.getData().add(alarmSourceSubTypeVOF);
            if(!CollectionUtils.isEmpty(thirdAlarmS)){
                alarmSourceSubTypethirdAlarmS.setAlarmTypeVOList(thirdAlarmS);
                alarmSourceSubTypethirdAlarmS.setAlarmSource(Constants.ELEVEN_STR);
                alarmSourceSubTypethirdAlarmS.setAlarmSourceName(thirdAlarmS.get(Constants.ZERO).getAlarmSourceName());
                result.getData().add(alarmSourceSubTypethirdAlarmS);
            }
        }
        //返回值 消除对同一对象循环引用的问题
        JSONArray jsonArrayRtn= JSONArray.parseArray(JSON.toJSONString(result.getData(), SerializerFeature.DisableCircularReferenceDetect));
        List<AlarmSourceSubTypeVO> r = jsonArrayRtn.toJavaList(AlarmSourceSubTypeVO.class);

        //将icon字段置为null，节省空间
        for (AlarmSourceSubTypeVO alarmSourceSubTypeVO : r) {
            for (AlarmTypeVO alarmTypeVO : alarmSourceSubTypeVO.getAlarmTypeVOList()) {
                alarmTypeVO.setIcon(null);
            }
        }

        result.setData(r);
        log.info("查询租户订购的告警类型公共方法 end - 返回:"+ JSONObject.toJSON(result));
        return  result;
    }

    /**
     * 匹配星期、生效时间,并返回匹配上的元素
     * @param extendInfoText
     * @param matchTime
     * @return 只返回与当前告警日期符合的扩展信息，比如今天周四，返回所有周四的扩展信息
     */
    public List<NoticeExtendVO> matchExtend(String extendInfoText, String matchTime){
        List<NoticeExtendVO> resultList = new ArrayList<>();

        if (!StringUtils.isEmpty(extendInfoText)) {
            boolean hit = false;
            List<NoticeExtendVO> extendInfoList = JSON.parseArray(extendInfoText, NoticeExtendVO.class);
            for (NoticeExtendVO noticeExtendVO : extendInfoList) {
                List<String> dayNo = noticeExtendVO.getDayNo();
                //根据请求时间匹配
                if(dayNo.contains(DateUtils.getWeek(matchTime))){
                    if(!hit){
                        List<NoticeExtendTimeVO> timeList = noticeExtendVO.getTimeList();
                        if (!CollectionUtils.isEmpty(timeList)){
                            NoticeExtendTimeVO noticeExtendTimeVO = timeList.get(Constants.ZERO);
                            String startTime = noticeExtendTimeVO.getStartTime();
                            String endTime = noticeExtendTimeVO.getEndTime();
                            //判断告警时间是否在有效时间段内
                            if (DateUtils.isEffectiveDate(matchTime.substring(10,16), startTime, endTime)) {
                                hit = true;
                            }
                        }
                    }
                    resultList.add(noticeExtendVO);
                }
            }
            //如果没命中，则清空结果集
            if(!hit){
                resultList = new ArrayList<>();
            }
        }

        return resultList;
    }

    /**
     * 校验规则名称是否重复
     * @param ruleName
     * @param userId
     * @return
     */
    public Result checkRuleName(String ruleName,String userId,Long id){
        QueryWrapper<NoticeRuleDO> queryWrapper = new QueryWrapper<>();

        if(ObjectUtils.isEmpty(id)){
            queryWrapper
                    .eq(Constants.USER_ID, userId)
                    .eq(Constants.RULE_NAME, ruleName)
                    .eq(Constants.DELETE_FLAG, Constants.ZERO_STR);
        }else{
            queryWrapper
                    .eq(Constants.USER_ID, userId)
                    .eq(Constants.RULE_NAME, ruleName)
                    .ne(Constants.ID,id)
                    .eq(Constants.DELETE_FLAG, Constants.ZERO_STR);
        }

        if(!CollectionUtils.isEmpty(this.list(queryWrapper))){
            return Result.fail("规则名称不能重复！");
        }
        return null;
    }

    @Override
    public Result<String> getMaxRuleName(){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        StringBuffer maxRuleName = new StringBuffer(Constants.INIT_RULE_NAME);
        QueryWrapper<NoticeRuleDO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(Constants.USER_ID, user.getUserId())
                .eq(Constants.DELETE_FLAG, Constants.ZERO_STR)
                .likeRight(Constants.RULE_NAME, Constants.INIT_RULE_NAME)
                .orderByDesc(Constants.RULE_NAME);
        //查询存量规则
        List<NoticeRuleDO> noticeRuleDOS = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(noticeRuleDOS)){
            maxRuleName.append(Constants.ONE_STR);
        }else{
            //取默认规则名称后部分，重新组合成list
            List<String> ruleNameList = noticeRuleDOS.stream().map(s -> s.getRuleName().substring(Constants.INIT_RULE_NAME.length()))
                    .filter(temp -> !StringUtils.isEmpty(temp) && this.isNumber(temp)).collect(Collectors.toList());

            if(CollectionUtils.isEmpty(ruleNameList)){
                maxRuleName.append(Constants.ONE_STR);
            }else{
                //List<String>转为List<Integer>
                List<Integer> codesInteger = ruleNameList.stream().map(Integer::parseInt).collect(Collectors.toList());
                //倒序排序
                Collections.sort(codesInteger,Collections.reverseOrder());
                maxRuleName.append(codesInteger.get(Constants.ZERO) + Constants.ONE);
            }
        }
        return Result.data(maxRuleName.toString());
    }

    /**
     * 判断字符串是否是纯数字
     * @param str
     * @return
     */
    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str.replaceAll("\\s*", "")).matches();
    }
}

package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.SysAlarmReasonRelDO;
import com.chinatower.event.entity.dto.AlarmReasonQueryDTO;
import com.chinatower.event.entity.dto.AlarmReasonSaveDTO;
import com.chinatower.event.entity.dto.AlarmReasonSaveSubDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.vo.AlarmReasonVO;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.event.mapper.AlarmTypeMapper;
import com.chinatower.event.mapper.SysAlarmReasonMapper;
import com.chinatower.event.repository.IAlarmTypeRepository;
import com.chinatower.event.repository.ISysAlarmReasonService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class SysAlarmReasonServiceImpl implements ISysAlarmReasonService {

    @Autowired
    private SysAlarmReasonMapper sysAlarmReasonMapper;

    @Autowired
    private IAlarmTypeRepository alarmTypeRepository;

    @Autowired
    private AlarmTypeMapper alarmTypeMapper;

    private static final int TYPE_INSERT = 1;

    private static final int TYPE_UPDATE = 2;

    private static final int TYPE_DELETE = 3;


    @Override
    public List<AlarmReasonVO> list(AlarmReasonQueryDTO param) {
        if (param == null || StringUtils.isEmpty(param.getTypeValue())) {
            throw new CustomException("必填参数不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException("用户未登录");
        }
        SysUser user = loginUser.getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        return sysAlarmReasonMapper.listByCondition(param);
    }

    @Override
    public List<AlarmReasonVO> queryAlarmReasonList(AlarmReasonQueryDTO param) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException("无法获取当前登录用户信息，请重新登陆");
        }
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        if(StringUtils.isEmpty(param.getTenantId())){
            param.setTenantId(tenantId);
        }
        if(StringUtils.isEmpty(param.getIndustryCode())){
            param.setIndustryCode(industryCode);
        }
        AlarmSourceSubTypeDTO sourceSubTypeDTO = new AlarmSourceSubTypeDTO();
        sourceSubTypeDTO.setTenantId(tenantId);
        sourceSubTypeDTO.setIndustryCode(industryCode);
        List<String> alarmSources = alarmTypeRepository.findAlarmSource(sourceSubTypeDTO);
        sourceSubTypeDTO.setAlarmSources(alarmSources);
        //查询客户的告警来源对应的告警类型
        List<AlarmTypeVO> alarmTypeVOS = alarmTypeMapper.findAlarmTypeByOrder(sourceSubTypeDTO);
        alarmTypeVOS.addAll(alarmTypeMapper.findAlarmTypeBySource(sourceSubTypeDTO));
        List<String> typeValues = alarmTypeVOS.stream().map(alarmTypeVO -> alarmTypeVO.getTypeValue()).collect(Collectors.toList());
        param.setTypeValueList(typeValues);
        param.setTenantId(tenantId);
        param.setIndustryCode(industryCode);
        return sysAlarmReasonMapper.queryAlarmReason(param);
    }

    @Override
    @Transactional
    public void save(AlarmReasonSaveDTO param) {
        if (param == null || StringUtils.isEmpty(param.getTypeValue())) {
            throw new CustomException("必填参数不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException("用户未登录");
        }
        SysUser user = loginUser.getUser();
        // 客户编码
        String tenantId = user.getTenantId();
        // 行业编码
        String industryCode = user.getIndustryCode();
        // 告警类型编码
        String typeValue = param.getTypeValue();
        // 新增列表
        List<SysAlarmReasonRelDO> insertList = Lists.newArrayList();
        // 更新列表
        List<SysAlarmReasonRelDO> updateList = Lists.newArrayList();
        // 删除列表
        List<Long> deleteIdList = Lists.newArrayList();
        // 当前时间作为创建时间、更新时间
        Date now = new Date();
        // 本次新增、更新的告警原因列表 - 校验告警原因是否重复使用
        List<String> saveReasonList = Lists.newArrayList();
        // 本次更新、删除的告警原因编码 - 校验告警原因是否重复排除使用
        List<Long> excludeReasonIdList = Lists.newArrayList();
        param.getAlarmReasonList().forEach(x -> {
            if (Objects.equals(TYPE_INSERT, x.getType()) || Objects.equals(TYPE_UPDATE, x.getType())) {
                if (StringUtils.isEmpty(x.getReason())) {
                    throw new CustomException("告警原因不能为空");
                }
                if (x.getReason().length() > 10) {
                    throw new CustomException("最多支持输入10个文字");
                }
                if (saveReasonList.contains(x.getReason())) {
                    throw new CustomException("告警原因不能重复");
                }
                saveReasonList.add(x.getReason());
                if (Objects.equals(TYPE_INSERT, x.getType())) {
                    insertList.add(convert2SysAlarmReasonRelDO(x, typeValue, now, user));
                } else {
                    if (x.getId() == null) {
                        throw new CustomException("更新告警原因-告警原因编码不能为空");
                    }
                    excludeReasonIdList.add(x.getId());
                    updateList.add(convert2SysAlarmReasonRelDO(x, typeValue, now, user));
                }
            } else if (Objects.equals(TYPE_DELETE, x.getType())) {
                if (x.getId() == null) {
                    throw new CustomException("删除告警原因-告警原因编码不能为空");
                }
                excludeReasonIdList.add(x.getId());
                deleteIdList.add(x.getId());
            }
        });

        if (CollectionUtils.isNotEmpty(saveReasonList)) {
            // 如果有新增、更新的告警原因，校验下和已有的原因是否存在重复的
            int existNameCount = sysAlarmReasonMapper.countExistReasonName(tenantId, industryCode, typeValue,
                    saveReasonList, excludeReasonIdList);
            if (existNameCount > 0) {
                throw new CustomException("告警原因不能重复");
            }
        }
        // 客户 + 行业 + 告警类型 下已经存在的告警原因数量
        int existsCount = sysAlarmReasonMapper.countByCondition(tenantId, industryCode, typeValue);
        // 告警类型对应已经存在的告警原因数量 + 本次新增的数量 - 本次需要删除的数量 不能超过20个
        if (existsCount + insertList.size() - deleteIdList.size() > 20) {
            throw new CustomException("最多可新增20个告警原因");
        }
        // 处理新增告警原因
        if (CollectionUtils.isNotEmpty(insertList)) {
            sysAlarmReasonMapper.insertBatch(insertList);
        }
        // 处理更新告警原因
        if (CollectionUtils.isNotEmpty(updateList)) {
            for (SysAlarmReasonRelDO alarmReason : updateList) {
                sysAlarmReasonMapper.updateByCondition(alarmReason);
            }
        }
        // 处理删除告警原因
        if (CollectionUtils.isNotEmpty(deleteIdList)) {
            sysAlarmReasonMapper.deleteByCondition(typeValue, deleteIdList, tenantId, industryCode);
        }
    }

    private SysAlarmReasonRelDO convert2SysAlarmReasonRelDO(AlarmReasonSaveSubDTO alarmReason, String typeValue,
                                                            Date now, SysUser user) {
        if (alarmReason == null) {
            return null;
        }
        String userName = user.getUserName();
        SysAlarmReasonRelDO entity = new SysAlarmReasonRelDO();
        if (alarmReason.getId() != null) {
            entity.setId(alarmReason.getId());
        }
        entity.setTypeValue(typeValue);
        entity.setReason(alarmReason.getReason());
        entity.setTenantId(user.getTenantId());
        entity.setIndustryCode(user.getIndustryCode());
        entity.setAppVerCode(user.getAppVerCode());
        entity.setCreator(userName);
        entity.setCreateTime(now);
        entity.setModifier(userName);
        entity.setUpdateTime(now);
        entity.setDeleteFlag(Constants.ZERO_NUM);
        return entity;
    }
}

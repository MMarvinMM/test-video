package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.VmAlarmTransferConfigVO;
import com.chinatower.event.entity.vo.VmAlarmTypeTransferVO;
import com.chinatower.event.mapper.VmAlarmTransferConfigMapper;
import com.chinatower.event.repository.VmAlarmTransferConfigService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警流程配置业务接口实现
 * @author lyy
 * @version v1.5
 * @date 2022/7/20
 */
@Slf4j
@Service
public class VmAlarmTransferConfigServiceImpl implements VmAlarmTransferConfigService {

    @Autowired
    private VmAlarmTransferConfigMapper alarmTransferConfigMapper;

    /**
     * 功能描述：查询告警转移配置列表信息
     * @author lyy
     * @param alarmTransferConfigDTO
     * @return Result<Object>
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="tenant_id", industryCode="industry_code")
    public Result<Object> getAlarmTransferConfigList(VmAlarmTransferConfigDTO alarmTransferConfigDTO) {
        PageInfo<VmAlarmTransferConfigVO> pageInfo;
        try {
            //设置分页参数
            Integer pageNum = alarmTransferConfigDTO.getPageNum();
            Integer pageSize = alarmTransferConfigDTO.getPageSize();
            if (null == pageNum || pageNum <= 0) {
                pageNum = 1;
            }
            if (null == pageSize || pageSize <= 0) {
                pageSize = 10;
            }
            PageMethod.startPage(pageNum, pageSize);
            List<VmAlarmTransferConfigVO> resultList = alarmTransferConfigMapper.getAlarmTransferConfigList(alarmTransferConfigDTO);
            setName(resultList);
            pageInfo = new PageInfo(resultList);
        } catch (Exception e) {
            return Result.fail(ResultCode.ERROR.getCode(), "查询告警转移配置异常");
        }
        return Result.data(pageInfo, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 功能描述：提交校验，根据租户ID、告警来源、告警类型、行业编码查询
     * @param transferConfigDTO
     * @return Result<Object>
     */
    @Override
    public Result<Object> queryAlarmTransferConfigAndType(VmAlarmTransferConfigDTO transferConfigDTO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            throw new CustomException(Constants.NO_MANAGE_PERMISSION);
        }
        if (StringUtils.isEmpty(transferConfigDTO.getTenantId())) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_TENANT_ID);
        }
        if(CollectionUtils.isEmpty(transferConfigDTO.getAlarmSourceAndTypeList())) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_ALARM_TYPE);
        }
        if(StringUtils.isEmpty(transferConfigDTO.getIndustryCode())) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), Constants.CHECK_INDUSTRY_CODE);
        }
        List<VmAlarmTransferConfigVO> transferConfigList = alarmTransferConfigMapper.queryAlarmTransferConfigAndType(transferConfigDTO);
        //判断告警转移配置中告警来源+告警类型是否已有转移配置
        if (CollectionUtils.isNotEmpty(transferConfigList)) {
            //如果已有转移配置，提示用户是否覆盖有转移配置（如果是，调用新增接口；如果否，页面保持原状）
            return Result.fail(ResultCode.FAILURE.getCode(), Constants.ALARM_TRANSFER_CONFIG_EXSITS_SOURCEANDTYPE);
        }
        return Result.success(ResultCode.SUCCESS.getCode() + Constants.COMMA +Constants.ALARM_TRANSFER_CONFIG_NOTEXSITS_SOURCEANDTYPE);
    }

    /**
     * 功能描述：查看详情、编辑回显
     * @param transferConfigVO
     * @return Result<Object>
     */
    @Override
    public Result<Object> queryAlarmTransferConfigDetail(VmAlarmTransferConfigVO transferConfigVO) {
        try {
            //根据ID获取告警转移配置
            transferConfigVO = alarmTransferConfigMapper.queryAlarmTransferConfigById(transferConfigVO.getId());
            //根据转移配置ID获取关联的告警来源、告警类型
            List<VmAlarmTypeTransferVO> alarmTypeTransferVOS = alarmTransferConfigMapper.getAlarmTypeByTransferId(transferConfigVO.getId());
            transferConfigVO.setAlarmTypeTransferList(alarmTypeTransferVOS);
        } catch (Exception e) {
            log.error("告警转移配置查询异常：{}",e);
            return Result.fail(ResultCode.FAILURE.getCode(), Constants.OUERY_ALARM_TRANSFER_CONFIG_ERROR);
        }
        return Result.data(transferConfigVO, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 功能描述：获取默认名称“告警转移策略+数字（数字累加）”
     * @param transferConfigDTO
     * @return Result<String>
     */
    @Override
    public Result<String> getAutoTransferName(VmAlarmTransferConfigDTO transferConfigDTO) {
        return Result.data(alarmTransferConfigMapper.getAutoTransferName(transferConfigDTO));
    }

    /**
     * 功能描述：校验告警转移配置名称是否唯一
     * @param transferConfigDTO
     * @return Result<Object>
     */
    @Override
    public Result<Object> getTransferNameCount(VmAlarmTransferConfigDTO transferConfigDTO) {
        int rows = alarmTransferConfigMapper.getTransferNameCount(transferConfigDTO);
        if (rows > 0) {
            return Result.data(rows, Constants.ALARM_TRANSFER_CONFIG_NAME_Y);
        }
        return Result.data(rows);
    }

    /**
     * 功能描述：新增告警转移配置、告警来源、告警类型信息
     * @param transferConfigDTO
     * @return Result<String>
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> addVmAlarmTransferConfig(VmAlarmTransferConfigDTO transferConfigDTO) throws Exception {
        return insertAlarmFlowConfig(transferConfigDTO);
    }
    private Result<String> insertAlarmFlowConfig(VmAlarmTransferConfigDTO transferConfigDTO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || org.springframework.util.StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            throw new CustomException(Constants.NO_USER_INFO);
        }
        if (StringUtils.isNotEmpty(transferConfigDTO.getAlarmTransferName())) {
            /**
             * 新增前：先删除已存在告警类型的告警转移配置信息
             */
            this.deleteExistedTransferConfig(transferConfigDTO);
            //设置创建人、修改人属性值
            SysUser currentUser = loginUser.getUser();
            if (currentUser != null) {
                if (StringUtils.isEmpty(transferConfigDTO.getCreator())) {
                    transferConfigDTO.setCreator(currentUser.getUserName());
                }
                transferConfigDTO.setModifier(currentUser.getUserName());
            }
            //判断是否是编辑操作，若不是则进行告警配置新增操作
            if (!Constants.OPER_TYPE_MOD.equals(transferConfigDTO.getOperType())){
                /**
                 * 新增告警转移配置表
                 */
                Integer transferConfigRow = alarmTransferConfigMapper.addVmAlarmTransferConfig(transferConfigDTO);
                if (transferConfigRow <= 0) {
                    return Result.fail(Constants.ALARM_TRANSFER_CONFIG_SAVE_FAIL);
                }
            }else {
                /**
                 * 更新告警转移配置表
                 */
                alarmTransferConfigMapper.modAlarmTransferConfigById(transferConfigDTO);
            }

            //添加告警类型表
            List<VmAlarmTypeTransferVO> alarmTypeTransferList = transferConfigDTO.getAlarmSourceAndTypeList();
            if (!CollectionUtils.isEmpty(alarmTypeTransferList)) {
                for (VmAlarmTypeTransferVO alarmTypeTransferVO : alarmTypeTransferList) {
                    alarmTypeTransferVO.setTenantId(transferConfigDTO.getTenantId());
                    alarmTypeTransferVO.setAlarmTransferId(transferConfigDTO.getId()); //告警转移配置id
                    alarmTypeTransferVO.setCreator(transferConfigDTO.getCreator());
                    alarmTypeTransferVO.setModifier(transferConfigDTO.getModifier());
                    alarmTypeTransferVO.setIndustryCode(transferConfigDTO.getIndustryCode());
                }
                /**
                 * 批量新增转移配置关联的告警类型表
                 */
                alarmTransferConfigMapper.batchAddAlarmTypeTransfer(alarmTypeTransferList);
            }
        }
        return Result.success(Constants.ALARM_TRANSFER_CONFIG_SAVE_SUCCESS);
    }

    /**
     * 功能描述：编辑修改告警转移配置
     * @param transferConfigDTO
     * @return Result<String>
     */
    @Override
    @Transactional
    public Result<String> updateAlarmTransferConfig(VmAlarmTransferConfigDTO transferConfigDTO) throws Exception {
        //根据转移配置ID+租户ID 删除告警转移配置以及关联的告警来源告警类型
        delTransferConfig(transferConfigDTO.getTenantId(), transferConfigDTO.getId(), Constants.OPER_TYPE_MOD);
        transferConfigDTO.setOperType(Constants.OPER_TYPE_MOD);
        Result<String> result= insertAlarmFlowConfig(transferConfigDTO);
        if (ResultCode.SUCCESS.equals(result.getCode())){
            result.setMsg(Constants.ALARM_TRANSFER_CONFIG_MOD_SUCCESS);
        }
        return result;
    }

    /**
     * 功能描述：新增配置时有重复告警类型则删除之前的告警类型配置
     * @param transferConfigDTO
     * @return int
     */
    @Override
    public int deleteExistedTransferConfig(VmAlarmTransferConfigDTO transferConfigDTO) {
        //根据租户ID、告警类型id+来源id查询出对应的告警转移配置
        List<String> alarmTransferIds = alarmTransferConfigMapper.getTransferIdByAlarmType(transferConfigDTO);
        //逻辑删除转移配置关联的告警类型信息
        int i = alarmTransferConfigMapper.delAlarmTypeList(transferConfigDTO);
        if (i > 0) {
            // 查询已删除告警类型的告警转移配置
            List<Long> emptyTransferIds = alarmTransferConfigMapper.selectEmptyTransferIds(transferConfigDTO ,alarmTransferIds);
            if (emptyTransferIds != null && emptyTransferIds.size() > 0) {
                // 通过转移策略id删除关联告警类型
                this.deleteTransferConfigs(transferConfigDTO.getTenantId(), emptyTransferIds, Constants.OPER_TYPE_DEL);
            }
        }
        return i;
    }

    /**
     * 功能描述：删除告警转移配置、告警来源+告警类型信息
     * @param alarmTransferIds
     * @param tenantId
     * @return int
     */
    @Override
    public int deleteTransferConfigs(String tenantId, List<Long> alarmTransferIds, String operType) {
        Map param = new HashMap();
        param.put(Constants.PARAM_TENANT_ID, tenantId);
        param.put("alarmTransferIds", alarmTransferIds);
        int i = 0;
        //逻辑删除告警流程关联的告警类型表
        alarmTransferConfigMapper.delAlarmTypeByTransferId(param);
        if(Constants.OPER_TYPE_DEL.equals(operType)){
            //逻辑删除告警转移配置
            i = alarmTransferConfigMapper.delAlarmTransferConfig(param);
        }
        return i;
    }

    /**
     * 功能描述：删除告警流程配置
     * @param transferId
     * @param tenantId
     * @return Result<String>
     */
    @Override
    @Transactional
    public Result<String> deleteAlarmTransferConfig(String tenantId, Long transferId, String operType) {
        return delTransferConfig(tenantId, transferId, operType);
    }
    private Result<String> delTransferConfig(String tenantId, Long transferId, String operType) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtil.isEmpty(loginUser.getUser().getTenantId())) {
            return Result.fail(ResultCode.ERROR.getCode(), Constants.NO_MANAGE_PERMISSION);
        }
        if (transferId == null || tenantId == null || tenantId.length() <= 0) {
            return Result.fail("转移策略id或租户编码不可为空");
        }
        List<Long> transferIds = new ArrayList<>();
        transferIds.add(transferId);
        int i = this.deleteTransferConfigs(tenantId, transferIds, operType);
        if (i > 0) {
            return Result.success("告警转移配置删除成功");
        } else {
            return Result.fail("告警转移配置删除失败");
        }
    }

    /**
     * 功能描述：补充租户名称
     * @param transferConfigList
     * @return List<VmAlarmTransferConfigVO>
     */
    private List<VmAlarmTransferConfigVO> setName(List<VmAlarmTransferConfigVO> transferConfigList) {
        if (CollectionUtils.isNotEmpty(transferConfigList)) {
            List<String> tenantIdList = transferConfigList.stream().filter(obj -> StringUtils.isNotEmpty(obj.getTenantId())).map(VmAlarmTransferConfigVO::getTenantId).collect(Collectors.toList());
            Map<String, String> tenantMap = new HashMap<>(10);
            if (CollectionUtils.isNotEmpty(tenantIdList)) {
                List<SysTenantInfo> tenantInfoList = alarmTransferConfigMapper.queryTenantList(tenantIdList);
                Map<String, String> resultMap = tenantInfoList.stream().collect(Collectors.toMap(SysTenantInfo::getTenantId, SysTenantInfo::getTenantName));
                tenantMap.putAll(resultMap);
            }
            transferConfigList.forEach(transferConfig -> {
                if (null != transferConfig.getTenantId() && tenantMap.containsKey(transferConfig.getTenantId())) {
                    transferConfig.setTenantName(tenantMap.get(transferConfig.getTenantId()));
                }
            });
        }
        return transferConfigList;
    }
}

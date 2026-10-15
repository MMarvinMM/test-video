package com.chinatower.algorithm.repository.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.algorithm.dataobject.*;
import com.chinatower.algorithm.mapper.SysSceneAlarmMapper;
import com.chinatower.algorithm.repository.ISysSceneAlarmRepository;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 场景告警关系表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Slf4j
@Service
public class SysSceneAlarmRepositoryImpl implements ISysSceneAlarmRepository {

    /**
     * 绊线绘制类型配置code常量
     */
    private static final String DICT_CV_MODE_TRIP_THREAD_TYPE = "dict-cvModeTripThreadType";

    /**
     * JSON字段常量 - dictList
     */
    private static final String FIELD_DICT_LIST = "dictList";

    /**
     * JSON字段常量 - itemCode
     */
    private static final String FIELD_ITEM_CODE = "itemCode";

    @Resource
    private SysSceneAlarmMapper sysSceneAlarmMapper;

    @Resource
    SystemFeignClient systemFeignClient;


    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public SysSceneConfigDO selectSysSceneAlarmById(String sceneId) {

        
        // 查询场景配置表
        return sysSceneAlarmMapper.selectSceneConfigBySceneId(sceneId);


    }

    @Override
    public List<SysSceneAlarmDO> selectSysSceneAlarmList(SysSceneAlarmDO sysSceneAlarmDO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();

        // 转换为QueryDTO
        SysSceneAlarmQueryDTO queryDTO = new SysSceneAlarmQueryDTO();
        if (sysSceneAlarmDO != null) {
            queryDTO.setSceneId(sysSceneAlarmDO.getSceneId());
            queryDTO.setSceneName(sysSceneAlarmDO.getSceneName());
            if (sysSceneAlarmDO.getAlarmType() != null) {
                List<String> alarmTypeList = new ArrayList<>();
                alarmTypeList.add(sysSceneAlarmDO.getAlarmType());
                queryDTO.setAlarmTypeList(alarmTypeList);
            }
            queryDTO.setAlarmTypeName(sysSceneAlarmDO.getAlarmTypeName());
            queryDTO.setAlgorithmCode(sysSceneAlarmDO.getAlgorithmCode());
        }
        queryDTO.setTenantId(user.getTenantId());
        queryDTO.setIndustryCode(user.getIndustryCode());
        queryDTO.setAppVerCode(user.getAppVerCode());
        
        return sysSceneAlarmMapper.selectSysSceneAlarmList(queryDTO);
    }

    @Override
    public List<SysSceneAlarmDO> selectSysSceneAlarmListWithPage(SysSceneAlarmQueryDTO queryDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();

        // 设置租户、行业、应用版本过滤条件
        if (queryDTO == null) {
            queryDTO = new SysSceneAlarmQueryDTO();
        }
        queryDTO.setTenantId(user.getTenantId());
        queryDTO.setIndustryCode(user.getIndustryCode());

        // 设置分页
        if (queryDTO.getPageNum() != null && queryDTO.getPageSize() != null) {
            PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        }

        return sysSceneAlarmMapper.selectSysSceneAlarmListWithPage(queryDTO);
    }

    @Override
    public TableDataInfo selectSysSceneAlarmTreeList(SysSceneAlarmQueryDTO queryDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();

        // 设置租户、行业、应用版本过滤条件
        if (queryDTO == null) {
            queryDTO = new SysSceneAlarmQueryDTO();
        }
        queryDTO.setTenantId(user.getTenantId());
        queryDTO.setIndustryCode(user.getIndustryCode());

        // 不设置分页，查询全部数据
        // 直接查询树状结构（MyBatis的collection会自动构建树）
        List<SceneAlarmTreeVO> allTreeList = sysSceneAlarmMapper.selectSysSceneAlarmListForTree(queryDTO);

        TableDataInfo dataTable = new TableDataInfo();

        if (allTreeList == null || allTreeList.isEmpty()) {
            log.debug("查询到树状结构数量: 0");
            return dataTable;
        }

        // 在内存中进行分页（按场景维度分页）
        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();
        
        if (pageNum != null && pageSize != null && pageNum > 0 && pageSize > 0) {
            // 计算分页范围
            int total = allTreeList.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);

            
            // 截取分页数据（创建新列表，避免 subList 的潜在问题）
            List<SceneAlarmTreeVO> pagedList = new ArrayList<>(allTreeList.subList(startIndex, endIndex));
            log.debug("查询到树状结构数量: {}, 分页后: {} (pageNum={}, pageSize={})", 
                    total, pagedList.size(), pageNum, pageSize);
            dataTable.setCode(200);
            dataTable.setRows(pagedList);
            dataTable.setTotal(total);
            return dataTable;
        } else {
            // 没有分页参数，返回全部数据
            log.debug("查询到树状结构数量: {}", allTreeList.size());
            return dataTable;
        }
    }
    
    @Override
    public long selectSysSceneAlarmTreeListTotal(SysSceneAlarmQueryDTO queryDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();

        // 设置租户、行业、应用版本过滤条件
        if (queryDTO == null) {
            queryDTO = new SysSceneAlarmQueryDTO();
        }
        queryDTO.setTenantId(user.getTenantId());
        queryDTO.setIndustryCode(user.getIndustryCode());

        // 查询全部数据以获取总数（不设置分页参数）
        queryDTO.setPageNum(null);
        queryDTO.setPageSize(null);
        List<SceneAlarmTreeVO> allTreeList = sysSceneAlarmMapper.selectSysSceneAlarmListForTree(queryDTO);
        return allTreeList != null ? allTreeList.size() : 0;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysSceneAlarmBatch(SysSceneAlarmAddDTO addDTO) {
        if (addDTO == null || CollectionUtils.isEmpty(addDTO.getAlarmTypeList())) {
            return 0;
        }

        String modelConfigJson = sysSceneAlarmMapper.selectModelConfigFromAlgorithmInfo(addDTO.getAlgorithmCode());
        // 校验场景下所有告警类型是否都支持绊线绘制，混用则抛出异常
        this.checkTripThread(addDTO.getAlarmTypeList(), modelConfigJson);

        // 校验场景名称
        String validateError = validateSceneName(addDTO.getSceneName(), null);
        if (validateError != null) {
            throw new CustomException(validateError, 400);
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();
        String sceneId = getUUID(); // 生成场景ID

        // 1. 插入场景配置表
        SysSceneConfigDO sceneConfig = new SysSceneConfigDO();
        sceneConfig.setSceneId(sceneId);
        sceneConfig.setSceneName(addDTO.getSceneName());
        sceneConfig.setTypeValue(sceneId); // type_value存scene_id的值  页面新增填scene_id，同步填alarm_type
        sceneConfig.setAlgorithmCode(addDTO.getAlgorithmCode());
        setCreateInfoForConfig(sceneConfig);
        sysSceneAlarmMapper.insertSceneConfig(sceneConfig);

        // 2. 批量插入场景告警关系表
        List<SysSceneAlarmRelDO> insertList = new ArrayList<>();
        for (AlarmTypeItem item : addDTO.getAlarmTypeList()) {
            SysSceneAlarmRelDO relDO = new SysSceneAlarmRelDO();
            relDO.setSceneId(sceneId);
            relDO.setAlarmType(item.getAlarmType()); // 使用alarmTypeList字段
            relDO.setAlarmTypeName(item.getAlarmTypeName());
            relDO.setAlgorithmCode(addDTO.getAlgorithmCode());
            relDO.setCreator(user.getUserName());
            relDO.setTenantId(user.getTenantId());
            relDO.setIndustryCode(user.getIndustryCode());
            relDO.setAppVerCode(user.getAppVerCode());
            insertList.add(relDO);
        }

        if (!CollectionUtils.isEmpty(insertList)) {
            sysSceneAlarmMapper.insertSysSceneAlarmRelBatch(insertList);
        }

        // 3. 同步到sys_alarm_type表（alarm_source = '3'）
        syncAlarmTypeToSysAlarmType(sceneId, addDTO.getSceneName(), user);

        return insertList.size();
    }

    /**
     * 同步告警类型到sys_alarm_type表
     *
     * @param sceneId 场景ID
     * @param sceneName
     * @param user 当前用户
     */
    private void syncAlarmTypeToSysAlarmType(String sceneId, String sceneName, SysUser user) {

        //查询参数配置获取大模型告警
       String icon = "";
       AjaxResult ajaxResult = systemFeignClient.getConfValue("big_model_alarm_icon");
       if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
           SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
           icon = sysConfig.getConfigValue();
       }

        List<SysAlarmTypeDO> alarmTypeDOList = new ArrayList<>();
        Date now = new Date();
        // 每个告警类型创建一条记录
        // type_value存scene_id，type_name存告警类型名称
        SysAlarmTypeDO alarmTypeDO = new SysAlarmTypeDO();
        alarmTypeDO.setAlarmSource("1"); // 3-一键告警
        alarmTypeDO.setTypeValue(sceneId); // type_value存scene_id
        alarmTypeDO.setTypeName(sceneName); // type_name存 sceneName
        alarmTypeDO.setSource("3"); // 1-操作
        alarmTypeDO.setDeleteFlag(0);
        alarmTypeDO.setCreator(user.getUserName());
        alarmTypeDO.setCreateTime(now);
        alarmTypeDO.setTenantId(user.getTenantId());
        alarmTypeDO.setIndustryCode(user.getIndustryCode());
        alarmTypeDO.setIcon(icon);
        alarmTypeDOList.add(alarmTypeDO);

        // 批量插入告警类型
        if (CollectionUtil.isNotEmpty(alarmTypeDOList)) {
            sysSceneAlarmMapper.insertAlarmTypeBatch(alarmTypeDOList);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysSceneAlarmBatch(SysSceneAlarmUpdateDTO updateDTO) {
        if (updateDTO == null || updateDTO.getSceneId() == null) {
            return 0;
        }

        String modelConfigJson = sysSceneAlarmMapper.selectModelConfigFromAlgorithmInfo(updateDTO.getAlgorithmCode());
        // 校验场景下所有告警类型是否都支持绊线绘制，混用则抛出异常
        this.checkTripThread(updateDTO.getAlarmTypeList(), modelConfigJson);

        // 根据sceneId查询场景配置（updateDTO.getSceneId()是Long类型，需要先查询关系表获取sceneId字符串）
        // 先查询关系表，找到对应的sceneId
        List<SysSceneAlarmRelDO> sysSceneAlarmRelList = sysSceneAlarmMapper.selectSysSceneAlarmRelById(updateDTO.getSceneId());
        if (CollectionUtils.isEmpty(sysSceneAlarmRelList)) {
            return 0;
        }
        
        String sceneId = sysSceneAlarmRelList.get(0).getSceneId();
        SysSceneConfigDO existConfig = sysSceneAlarmMapper.selectSceneConfigBySceneId(sceneId);
        if (existConfig == null) {
            return 0;
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();

        // 1. 校验场景名称（如果修改了场景名称）
        if (updateDTO.getSceneName() != null && !updateDTO.getSceneName().equals(existConfig.getSceneName())) {
            String validateError = validateSceneName(updateDTO.getSceneName(), sceneId);
            if (validateError != null) {
                throw new CustomException(validateError, 400);
            }
        }

        // 2. 更新场景配置表
        if (updateDTO.getSceneName() != null) {
            existConfig.setSceneName(updateDTO.getSceneName());
        }
        if (updateDTO.getAlgorithmCode() != null) {
            existConfig.setAlgorithmCode(updateDTO.getAlgorithmCode());
        }
        setUpdateInfoForConfig(existConfig);
        existConfig.setTenantId(user.getTenantId());
        existConfig.setIndustryCode(user.getIndustryCode());
        sysSceneAlarmMapper.updateSceneConfig(existConfig);

        // 3. 逻辑删除该场景下的所有告警类型关系
        sysSceneAlarmMapper.deleteSysSceneAlarmRelBySceneId(sceneId, user.getTenantId(), user.getIndustryCode());

        // 3. 如果传入了新的告警类型列表，重新插入
        if (!CollectionUtils.isEmpty(updateDTO.getAlarmTypeList())) {
            List<SysSceneAlarmRelDO> insertList = new ArrayList<>();
            for (AlarmTypeItem item : updateDTO.getAlarmTypeList()) {
                // 验证告警类型编码不能为空
                String alarmType = item.getAlarmType();
                if (alarmType == null || alarmType.isEmpty()) {
                    log.warn("告警类型编码为空，跳过该记录，sceneId={}", sceneId);
                    continue;
                }
                
                SysSceneAlarmRelDO relDO = new SysSceneAlarmRelDO();
                relDO.setSceneId(sceneId); // 使用原有的场景ID
                relDO.setAlarmType(alarmType); // 设置告警类型编码
                relDO.setAlarmTypeName(item.getAlarmTypeName());
                relDO.setAlgorithmCode(updateDTO.getAlgorithmCode() != null ? updateDTO.getAlgorithmCode() : existConfig.getAlgorithmCode());
                relDO.setCreator(user.getUserName());
                relDO.setTenantId(user.getTenantId());
                relDO.setIndustryCode(user.getIndustryCode());
                relDO.setAppVerCode(user.getAppVerCode());
                relDO.setDeleteFlag(0); // 确保设置删除标志
                insertList.add(relDO);
                
                log.debug("准备插入场景告警关系: sceneId={}, alarmType={}, alarmTypeName={}", 
                        sceneId, alarmType, item.getAlarmTypeName());
            }

            if (!CollectionUtils.isEmpty(insertList)) {
                int insertCount = sysSceneAlarmMapper.insertSysSceneAlarmRelBatch(insertList);
                log.info("成功插入场景告警关系记录: sceneId={}, 插入数量={}", sceneId, insertCount);
            } else {
                log.warn("告警类型列表为空或全部跳过，sceneId={}", sceneId);
            }
        }

        // 4. 同步到sys_alarm_type表（source = '3'）
        // 先物理删除该场景下的所有告警类型
        sysSceneAlarmMapper.deleteAlarmTypeByTypeValue(sceneId, "3", user.getTenantId(), user.getIndustryCode());

        // 然后插入新的告警类型
        if (!CollectionUtils.isEmpty(updateDTO.getAlarmTypeList())) {
            syncAlarmTypeToSysAlarmType(sceneId, updateDTO.getSceneName(), user);
        }

        return 1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysSceneAlarmById(Long id) {
        // 查询关系表获取sceneId
        List<SysSceneAlarmRelDO> relDOList = sysSceneAlarmMapper.selectSysSceneAlarmRelById(String.valueOf(id));
        if (CollectionUtils.isEmpty(relDOList)) {
            return 0;
        }
        
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String sceneId = relDOList.get(0).getSceneId();
        
        // 逻辑删除关系表记录
        int result = sysSceneAlarmMapper.deleteSysSceneAlarmRelById(id);
        
        // 检查该场景是否还有其他未删除的关系记录
        SysSceneAlarmQueryDTO queryDTO = new SysSceneAlarmQueryDTO();
        queryDTO.setSceneId(sceneId);
        queryDTO.setTenantId(user.getTenantId());
        queryDTO.setIndustryCode(user.getIndustryCode());
        List<SysSceneAlarmDO> remainingList = sysSceneAlarmMapper.selectSysSceneAlarmList(queryDTO);
        
        // 如果该场景没有其他关系记录了，删除场景配置和sys_alarm_type表中的记录
        if (CollectionUtils.isEmpty(remainingList)) {
            // 删除场景配置
            sysSceneAlarmMapper.deleteSceneConfigBySceneId(sceneId, user.getTenantId(), user.getIndustryCode());
            
            // 删除sys_alarm_type表中source=3的记录
            sysSceneAlarmMapper.deleteAlarmTypeByTypeValue(sceneId, "3", user.getTenantId(), user.getIndustryCode());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysSceneAlarmBySceneId(String sceneId) {
        if (sceneId == null || sceneId.isEmpty()) {
            return 0;
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();

        // 1. 逻辑删除该场景下的所有告警类型关系
        int result = sysSceneAlarmMapper.deleteSysSceneAlarmRelBySceneId(sceneId, user.getTenantId(), user.getIndustryCode());

        // 2. 删除场景配置
        sysSceneAlarmMapper.deleteSceneConfigBySceneId(sceneId, user.getTenantId(), user.getIndustryCode());

        // 3. 删除sys_alarm_type表中source=3的记录
        sysSceneAlarmMapper.deleteAlarmTypeByTypeValue(sceneId, "3", user.getTenantId(), user.getIndustryCode());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysSceneAlarmByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();
        Set<String> sceneIdsToDelete = new HashSet<>();

        // 收集需要删除的场景ID
        for (Long id : ids) {
            List<SysSceneAlarmRelDO> relDOList = sysSceneAlarmMapper.selectSysSceneAlarmRelById(String.valueOf(id));
            if (!CollectionUtils.isEmpty(relDOList)) {
                sceneIdsToDelete.add(relDOList.get(0).getSceneId());
            }
        }

        // 批量逻辑删除关系表记录
        int result = sysSceneAlarmMapper.deleteSysSceneAlarmRelByIds(ids);

        // 检查每个场景是否还有其他未删除的关系记录
        for (String sceneId : sceneIdsToDelete) {
            SysSceneAlarmQueryDTO queryDTO = new SysSceneAlarmQueryDTO();
            queryDTO.setSceneId(sceneId);
            queryDTO.setTenantId(user.getTenantId());
            queryDTO.setIndustryCode(user.getIndustryCode());
            List<SysSceneAlarmDO> remainingList = sysSceneAlarmMapper.selectSysSceneAlarmList(queryDTO);
            
            // 如果该场景没有其他关系记录了，删除场景配置和sys_alarm_type表中的记录
            if (CollectionUtils.isEmpty(remainingList)) {
                // 删除场景配置
                sysSceneAlarmMapper.deleteSceneConfigBySceneId(sceneId, user.getTenantId(), user.getIndustryCode());
                
                // 删除sys_alarm_type表中source=3的记录
                sysSceneAlarmMapper.deleteAlarmTypeByTypeValue(sceneId, "3", user.getTenantId(), user.getIndustryCode());
            }
        }

        return result;
    }

    @Override
    public List<AlarmTypeVO> selectAlarmTypesByName(AlarmTypeQueryDTO queryDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        return sysSceneAlarmMapper.selectAlarmTypesByName(
                queryDTO.getAlarmTypeName(),
                user.getTenantId(),
                user.getIndustryCode(),
                queryDTO.getAlgorithmCode()
        );
    }

    @Override
    public List<SceneVO> selectScenesByName(SceneQueryDTO queryDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        return sysSceneAlarmMapper.selectScenesByName(
                queryDTO.getSceneName(),
                user.getTenantId(),
                user.getIndustryCode(),
                queryDTO.getAlgorithmCode()
        );
    }

    /**
     * 设置创建信息（场景配置表）
     */
    public void setCreateInfoForConfig(SysSceneConfigDO entity) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        entity.setCreator(user.getUserName());
        entity.setCreateTime(new Date());
        entity.setTenantId(user.getTenantId());
        entity.setAppVerCode(user.getAppVerCode());
        entity.setIndustryCode(user.getIndustryCode());
        entity.setDeleteFlag(0);
    }

    /**
     * 设置更新信息（场景配置表）
     */
    public void setUpdateInfoForConfig(SysSceneConfigDO entity) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        entity.setModifier(user.getUserName());
        entity.setUpdateTime(new Date());
    }

    /**
     * 设置创建信息（关系表）
     */
    public void setCreateInfoForRel(SysSceneAlarmRelDO entity) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        entity.setCreator(user.getUserName());
        entity.setCreateTime(new Date());
        entity.setTenantId(user.getTenantId());
        entity.setAppVerCode(user.getAppVerCode());
        entity.setIndustryCode(user.getIndustryCode());
        entity.setDeleteFlag(0);
    }

    /**
     * 设置更新信息（关系表）
     */
    public void setUpdateInfoForRel(SysSceneAlarmRelDO entity) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        entity.setModifier(user.getUserName());
        entity.setUpdateTime(new Date());
    }

    @Override
    public List<ModelAlgorithmVO> getModelAlgorithms() {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            
            // 直接查库
            List<String> algorithmCodeList = sysSceneAlarmMapper.getLargeModelAlgorithms();
            
            // 如果字典中没有配置，返回空列表
            if (CollectionUtils.isEmpty(algorithmCodeList)) {
                log.info("字典参数model_config_list为空，返回空列表");
                return new ArrayList<>();
            }
            
            // 查询vm_order_algorithms表，获取匹配的大模型算法列表
            List<ModelAlgorithmVO> modelAlgorithmList = sysSceneAlarmMapper.selectModelAlgorithms(
                    user.getTenantId(),
                    user.getIndustryCode(),
                    algorithmCodeList
            );
            
            return modelAlgorithmList != null ? modelAlgorithmList : new ArrayList<>();
        } catch (Exception e) {
            log.error("查询大模型算法列表异常", e);
            return new ArrayList<>();
        }
    }

    @Override
    @DataScope(tenantColumn = "config.tenant_id", industryCode = "config.industry_code")
    public List<SceneTreeVO> treeByAlgoCode(SceneAlarmQueryDTO queryDTO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        queryDTO.setTenantId(user.getTenantId());
        queryDTO.setIndustryCode(user.getIndustryCode());
        String algorithmCode = queryDTO.getAlgorithmCode();

        // 直接使用 MyBatis collection 构建树状结构，无需内存拼接
        List<SceneTreeVO> treeList = sysSceneAlarmMapper.selectSceneTreeByAlgorithmCode(queryDTO);

        //查询高级参数json串
        String modelConfigJson = sysSceneAlarmMapper.selectModelConfigFromAlgorithmInfo(algorithmCode);

        // 遍历树状结构中每个场景的子节点（告警类型），将typeValue与字典配置进行匹配
        if (!CollectionUtils.isEmpty(treeList) ) {
            // 遍历每个场景节点
            for (SceneTreeVO sceneNode : treeList) {
                if (!CollectionUtils.isEmpty(sceneNode.getChildren())) {
                    List<AlarmTypeItem> alarmTypeList = new ArrayList<>();
                    // 遍历场景下的所有告警类型子节点
                    for (AlarmTypeVO alarmType : sceneNode.getChildren()) {
                        AlarmTypeItem alarmTypeItem = new AlarmTypeItem();
                        alarmTypeItem.setAlarmType(alarmType.getTypeValue());
                        alarmTypeItem.setAlarmTypeName(alarmType.getTypeName());
                        alarmTypeList.add(alarmTypeItem);
                    }
                    sceneNode.setSupportTripThread(this.checkSupportTripThread(alarmTypeList, modelConfigJson));
                }
            }
        }

        log.debug("查询场景告警树状结构: algorithmCode={}, 返回数量={}", algorithmCode, treeList != null ? treeList.size() : 0);
        return treeList != null ? treeList : new ArrayList<>();
    }

    @Override
    public Boolean checkSceneInAlgorithmConfig(String sceneId) {
        if (sceneId == null || sceneId.isEmpty()) {
            return false;
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();
        int count = sysSceneAlarmMapper.countAlgorithmConfigBySceneId(
                sceneId,
                user.getTenantId(),
                user.getIndustryCode()
        );

        return count > 0;
    }

    @Override
    public SceneInAlgorithmConfigVO checkSceneInAlgorithmConfigBySceneId(SceneInAlgorithmConfigQueryDTO queryDTO) {
        SceneInAlgorithmConfigVO vo = new SceneInAlgorithmConfigVO();
        
        if (queryDTO == null || queryDTO.getSceneId() == null || queryDTO.getSceneId().isEmpty()) {
            vo.setSceneId(queryDTO != null ? queryDTO.getSceneId() : null);
            vo.setInAlgorithmConfig(false);
            return vo;
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();
        int count = sysSceneAlarmMapper.countSceneInAlgorithmConfig(
                queryDTO.getSceneId(),
                user.getTenantId(),
                user.getIndustryCode()
        );

        vo.setSceneId(queryDTO.getSceneId());
        vo.setInAlgorithmConfig(count > 0);
        return vo;
    }

    @Override
    public String validateSceneName(String sceneName, String sceneId) {
        if (sceneName == null || sceneName.isEmpty()) {
            return null; // 场景名称为空时不校验
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();

        // 1. 检查场景名称是否已存在（排除当前sceneId）
        int sceneNameCount = sysSceneAlarmMapper.countSceneNameExists(
                sceneName,
                sceneId,
                user.getTenantId(),
                user.getIndustryCode()
        );

        if (sceneNameCount > 0) {
            return "您输入的场景名称与已有告警类型名称重复，场景名称与告警类型名称需保持唯一，请修改冲突的场景名称。";
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateSceneAlarmFromModelConfig(String algorithmCode) {
        if (algorithmCode == null) {
            log.warn("参数不能为空：algorithmCode={}", algorithmCode);
            return 0;
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        // 1. 检查 sys_scene_config 表中是否有有效数据
        int existingCount = sysSceneAlarmMapper.countSceneConfigExists(tenantId, industryCode, algorithmCode);
        if (existingCount > 0) {
            log.info("sys_scene_config 表中已存在有效数据，tenantId={}, industryCode={}, algorithmCode={}", 
                    tenantId, industryCode, algorithmCode);
            return 0;
        }

        // 2. 从 vm_algorithm_info_t 表中查询 model_config 字段
        String modelConfigJson = sysSceneAlarmMapper.selectModelConfigFromAlgorithmInfo(algorithmCode);
        if (modelConfigJson == null || modelConfigJson.isEmpty()) {
            log.warn("vm_algorithm_info_t 表中未找到 model_config，algorithmCode={}", algorithmCode);
            return 0;
        }

        // 3. 解析 model_config JSON
        List<Map<String, Object>> modelConfig;
        try {
            modelConfig = JSON.parseObject(modelConfigJson, List.class);
        } catch (Exception e) {
            log.error("解析 model_config JSON 失败，algorithmCode={}", algorithmCode, e);
            return 0;
        }

        if (CollectionUtils.isEmpty(modelConfig)) {
            log.warn("model_config 为空，algorithmCode={}", algorithmCode);
            return 0;
        }

        // 4. 解析告警编码和名称
        Map<String, String> alarmCodeToNameMap = getAlarmCodeToNameMap(modelConfig);
        if (alarmCodeToNameMap.isEmpty()) {
            log.warn("未从 model_config 中解析到告警编码，algorithmCode={}", algorithmCode);
            return 0;
        }

        // 5. 查询现有场景告警关系，找出缺少的告警编码
        // 注意：这里需要查询 sys_scene_config 和 sys_scene_alarm_rel 的关联数据
        // 由于没有现成的方法，我们通过查询场景配置来间接判断
        SysSceneAlarmQueryDTO queryDTO = new SysSceneAlarmQueryDTO();
        queryDTO.setAlgorithmCode(algorithmCode);
        queryDTO.setTenantId(tenantId);
        queryDTO.setIndustryCode(industryCode);
        List<SysSceneAlarmDO> existingData = sysSceneAlarmMapper.selectSysSceneAlarmList(queryDTO);
        
        Set<String> existingAlarmTypes = existingData.stream()
                .map(SysSceneAlarmDO::getAlarmType)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 6. 找出需要插入的告警编码
        Set<String> toInsert = new HashSet<>(alarmCodeToNameMap.keySet());
        toInsert.removeAll(existingAlarmTypes);

        if (toInsert.isEmpty()) {
            log.info("所有告警编码已存在，无需插入，algorithmCode={}", algorithmCode);
            return 0;
        }

        // 7. 插入新的场景告警数据
        Date now = new Date();
        int insertCount = 0;

        for (String alarmTypeCode : toInsert) {
            String alarmTypeName = alarmCodeToNameMap.get(alarmTypeCode);
            if (alarmTypeName == null || alarmTypeName.isEmpty()) {
                alarmTypeName = alarmTypeCode; // 如果找不到名称，使用编码作为名称
            }

            String sceneId = getUUID();

            // 插入场景配置
            SysSceneConfigDO sceneConfig = new SysSceneConfigDO();
            sceneConfig.setSceneId(sceneId);
            sceneConfig.setSceneName(alarmTypeName);
            sceneConfig.setTypeValue(alarmTypeCode);
            sceneConfig.setAlgorithmCode(algorithmCode);
            sceneConfig.setCreator(user.getUserName());
            sceneConfig.setCreateTime(now);
            sceneConfig.setDeleteFlag(0);
            sceneConfig.setTenantId(tenantId);
            sceneConfig.setIndustryCode(industryCode);
            sysSceneAlarmMapper.insertSceneConfig(sceneConfig);

            // 插入场景告警关系
            SysSceneAlarmRelDO sceneAlarmRel = new SysSceneAlarmRelDO();
            sceneAlarmRel.setSceneId(sceneId);
            sceneAlarmRel.setAlarmType(alarmTypeCode);
            sceneAlarmRel.setAlarmTypeName(alarmTypeName);
            sceneAlarmRel.setAlgorithmCode(algorithmCode);
            sceneAlarmRel.setCreator(user.getUserName());
            sceneAlarmRel.setCreateTime(now);
            sceneAlarmRel.setDeleteFlag(0);
            sceneAlarmRel.setTenantId(tenantId);
            sceneAlarmRel.setIndustryCode(industryCode);
            sysSceneAlarmMapper.insertSysSceneAlarmRel(sceneAlarmRel);

            insertCount++;
        }

        log.info("成功生成场景告警配置，algorithmCode={}, tenantId={}, industryCode={}, 插入数量={}", 
                algorithmCode, tenantId, industryCode, insertCount);
        return insertCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateSceneAlarmFromModelConfigWithParams(String tenantId, String industryCode, String algorithmCode) {
        if (algorithmCode == null || tenantId == null || industryCode == null) {
            log.warn("参数不能为空：algorithmCode={}, tenantId={}, industryCode={}", algorithmCode, tenantId, industryCode);
            return 0;
        }

        // 1. 检查 sys_scene_config 表中是否有有效数据
        int existingCount = sysSceneAlarmMapper.countSceneConfigExists(tenantId, industryCode, algorithmCode);
        if (existingCount > 0) {
            log.info("sys_scene_config 表中已存在有效数据，tenantId={}, industryCode={}, algorithmCode={}", 
                    tenantId, industryCode, algorithmCode);
            return 0;
        }

        // 2. 从 vm_algorithm_info_t 表中查询 model_config 字段
        String modelConfigJson = sysSceneAlarmMapper.selectModelConfigFromAlgorithmInfo(algorithmCode);
        if (modelConfigJson == null || modelConfigJson.isEmpty()) {
            log.warn("vm_algorithm_info_t 表中未找到 model_config，algorithmCode={}", algorithmCode);
            return 0;
        }

        // 3. 解析 model_config JSON
        List<Map<String, Object>> modelConfig;
        try {
            modelConfig = JSON.parseObject(modelConfigJson, List.class);
        } catch (Exception e) {
            log.error("解析 model_config JSON 失败，algorithmCode={}", algorithmCode, e);
            return 0;
        }

        if (CollectionUtils.isEmpty(modelConfig)) {
            log.warn("model_config 为空，algorithmCode={}", algorithmCode);
            return 0;
        }

        // 4. 解析告警编码和名称
        Map<String, String> alarmCodeToNameMap = getAlarmCodeToNameMap(modelConfig);
        if (alarmCodeToNameMap.isEmpty()) {
            log.warn("未从 model_config 中解析到告警编码，algorithmCode={}", algorithmCode);
            return 0;
        }

        // 5. 查询现有场景告警关系，找出缺少的告警编码
        SysSceneAlarmQueryDTO queryDTO = new SysSceneAlarmQueryDTO();
        queryDTO.setAlgorithmCode(algorithmCode);
        queryDTO.setTenantId(tenantId);
        queryDTO.setIndustryCode(industryCode);
        List<SysSceneAlarmDO> existingData = sysSceneAlarmMapper.selectSysSceneAlarmList(queryDTO);
        
        // 获取已存在的告警类型编码
        Set<String> existingAlarmTypes = new HashSet<>();
        if (!CollectionUtils.isEmpty(existingData)) {
            existingAlarmTypes = existingData.stream()
                    .map(SysSceneAlarmDO::getAlarmType)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        // 6. 找出需要插入的告警编码（排除已存在的）
        Set<String> toInsert = new HashSet<>(alarmCodeToNameMap.keySet());
        toInsert.removeAll(existingAlarmTypes);

        if (toInsert.isEmpty()) {
            log.info("所有告警编码已存在，无需插入，algorithmCode={}, tenantId={}, industryCode={}", 
                    algorithmCode, tenantId, industryCode);
            return 0;
        }
        
        log.info("需要插入的告警编码数量：{}，已存在的告警编码数量：{}，algorithmCode={}, tenantId={}, industryCode={}", 
                toInsert.size(), existingAlarmTypes.size(), algorithmCode, tenantId, industryCode);

        // 7. 批量插入新的场景告警数据
        String creator = "system"; // 系统自动生成，使用system作为创建人

        // 准备批量插入的数据列表
        List<SysSceneConfigDO> sceneConfigList = new ArrayList<>();
        List<SysSceneAlarmRelDO> sceneAlarmRelList = new ArrayList<>();

        for (String alarmTypeCode : toInsert) {
            String alarmTypeName = alarmCodeToNameMap.get(alarmTypeCode);
            if (alarmTypeName == null || alarmTypeName.isEmpty()) {
                alarmTypeName = alarmTypeCode; // 如果找不到名称，使用编码作为名称
            }

            String sceneId = getUUID();

            // 准备场景配置数据
            SysSceneConfigDO sceneConfig = new SysSceneConfigDO();
            sceneConfig.setSceneId(sceneId);
            sceneConfig.setSceneName(alarmTypeName);
            sceneConfig.setTypeValue(alarmTypeCode);
            sceneConfig.setAlgorithmCode(algorithmCode);
            sceneConfig.setCreator(creator);
            sceneConfig.setDeleteFlag(0);
            sceneConfig.setTenantId(tenantId);
            sceneConfig.setIndustryCode(industryCode);
            sceneConfigList.add(sceneConfig);

            // 准备场景告警关系数据
            SysSceneAlarmRelDO sceneAlarmRel = new SysSceneAlarmRelDO();
            sceneAlarmRel.setSceneId(sceneId);
            sceneAlarmRel.setAlarmType(alarmTypeCode);
            sceneAlarmRel.setAlarmTypeName(alarmTypeName);
            sceneAlarmRel.setAlgorithmCode(algorithmCode);
            sceneAlarmRel.setCreator(creator);
            sceneAlarmRel.setDeleteFlag(0);
            sceneAlarmRel.setTenantId(tenantId);
            sceneAlarmRel.setIndustryCode(industryCode);
            sceneAlarmRelList.add(sceneAlarmRel);
        }

        // 批量插入
        int insertCount = 0;
        if (CollectionUtil.isNotEmpty(sceneConfigList)) {
            sysSceneAlarmMapper.insertSceneConfigBatch(sceneConfigList);
            insertCount += sceneConfigList.size();
        }
        if (CollectionUtil.isNotEmpty(sceneAlarmRelList)) {
            sysSceneAlarmMapper.insertSysSceneAlarmRelBatch(sceneAlarmRelList);
        }

        log.info("成功生成场景告警配置，algorithmCode={}, tenantId={}, industryCode={}, 插入数量={}",
                algorithmCode, tenantId, industryCode, insertCount);
        return insertCount;
    }

    /**
     * 获取告警编码到名称的映射
     *
     * @param modelConfig modelConfig 数据
     * @return 告警编码到名称的映射
     */
    private Map<String, String> getAlarmCodeToNameMap(List<Map<String, Object>> modelConfig) {
        Map<String, String> codeToNameMap = new HashMap<>();

        // 如果找到了目标 code，解析 dictList
        for (Map<String, Object> config : modelConfig) {
            Object codeObj = config.get("code");
            String code = codeObj != null ? codeObj.toString() : "";
            if (!code.toLowerCase().contains("modetype")) {
                continue;
            }

            Object dictListObj = config.get(FIELD_DICT_LIST);
            List<Map<String, Object>> dictList = null;
            if (dictListObj instanceof List) {
                dictList = (List<Map<String, Object>>) dictListObj;
            }
            if (CollectionUtils.isEmpty(dictList)) {
                continue;
            }

            for (Map<String, Object> dict : dictList) {
                Object itemCodeObj = dict.get(FIELD_ITEM_CODE);
                Object itemNameObj = dict.get("itemName");
                String itemCode = itemCodeObj != null ? itemCodeObj.toString() : null;
                String itemName = itemNameObj != null ? itemNameObj.toString() : null;
                if (itemCode != null && !itemCode.isEmpty() && itemName != null && !itemName.isEmpty()) {
                    codeToNameMap.put(itemCode, itemName);
                }
            }

            // 找到目标 code 后，解析完就退出
            break;
        }

//            log.debug("使用字典类型：{}，解析到告警编码数量：{}", targetCode, codeToNameMap.size());

        return codeToNameMap;
    }

    /**
     * 从modelConfigJson中提取指定code的dictList中的itemCode列表
     *
     * @param modelConfigJson 模型配置JSON字符串
     * @param targetCode 目标code值
     * @return 匹配的itemCode列表，如果没有找到则返回null
     */
    private List<String> extractItemCodesByCode(String modelConfigJson, String targetCode) {
        if (StringUtils.isEmpty(modelConfigJson)) {
            return null;
        }

        List<Map<String, Object>> modelConfig = new ArrayList<>();
        try {
            modelConfig = JSON.parseObject(modelConfigJson, List.class);
        } catch (Exception e) {
            log.error("解析 model_config JSON 失败，targetCode={}", targetCode, e);
            return null;
        }
        // 第一阶段：extraValue 子高参优先
        List<String> extraResult = extractItemCodesFromExtraValue(modelConfig, targetCode);
        if (extraResult != null) {
            return extraResult;
        }
        // 第二阶段：回退顶层
        return extractItemCodesFromTopLevel(modelConfig, targetCode);
    }

    private List<String> extractItemCodesFromExtraValue(List<Map<String, Object>> modelConfig, String targetCode) {
        for (Map<String, Object> config : modelConfig) {
            Object extraObj = config.get("extraValue");
            if (!(extraObj instanceof List)) {
                continue;
            }
            List<Map<String, Object>> extraValue = (List<Map<String, Object>>) extraObj;
            if (CollectionUtils.isEmpty(extraValue)) {
                continue;
            }
            List<Map<String, Object>> scoped = extraValue.size() > 50 ? extraValue.subList(0, 50) : extraValue;
            if (extraValue.size() > 50) {
                log.warn("extraValue 子高参规模超过50，仅取前50解析，targetCode={}", targetCode);
            }
            for (Map<String, Object> sub : scoped) {
                Object codeObj = sub.get("code");
                String code = codeObj != null ? codeObj.toString() : "";
                if (!code.equals(targetCode)) {
                    continue;
                }
                List<String> itemCodes = extractItemCodesFromDictList(sub);
                if (itemCodes != null) {
                    log.info("extractItemCodesByCode 命中 extraValue，targetCode={}", targetCode);
                    return itemCodes;
                }
            }
        }
        return null;
    }

    private List<String> extractItemCodesFromTopLevel(List<Map<String, Object>> modelConfig, String targetCode) {
        for (Map<String, Object> config : modelConfig) {
            Object codeObj = config.get("code");
            String code = codeObj != null ? codeObj.toString() : "";
            if (!code.equals(targetCode)) {
                continue;
            }
            List<String> itemCodes = extractItemCodesFromDictList(config);
            if (itemCodes != null) {
                log.info("extractItemCodesByCode 命中顶层，targetCode={}", targetCode);
                return itemCodes;
            }
        }
        return null;
    }

    private List<String> extractItemCodesFromDictList(Map<String, Object> element) {
        Object dictListObj = element.get(FIELD_DICT_LIST);
        if (!(dictListObj instanceof List)) {
            return null;
        }
        List<Map<String, Object>> dictList = (List<Map<String, Object>>) dictListObj;
        if (CollectionUtils.isEmpty(dictList)) {
            return null;
        }
        return dictList.stream()
                .map(dict -> {
                    Object itemCodeObj = dict.get(FIELD_ITEM_CODE);
                    return itemCodeObj != null ? itemCodeObj.toString() : "";
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 计算alarmTypeList中有多少元素在itemCodes列表中
     *
     * @param alarmTypeList 告警类型列表
     * @param itemCodes 待匹配的itemCode列表
     * @return 匹配的数量
     */
    private int countMatches(List<AlarmTypeItem> alarmTypeList, List<String> itemCodes) {
        if (CollectionUtils.isEmpty(alarmTypeList) || CollectionUtils.isEmpty(itemCodes)) {
            return 0;
        }

        int matchCount = 0;
        for (AlarmTypeItem alarmTypeItem : alarmTypeList) {
            if (itemCodes.contains(alarmTypeItem.getAlarmType())) {
                matchCount++;
            }
        }
        return matchCount;
    }

    /**
     * 检查场景下子类型，是否是支持绊线与不支持绊线的子类型混用
     * 如果混用，则抛出异常提示哪些算法支持绘制绊线/箭头
     *
     * @param alarmTypeList 告警类型列表
     * @param modelConfigJson 模型配置JSON字符串
     * @throws CustomException 当检测到混用时抛出，包含支持绊线的算法名称提示
     */
    public void checkTripThread(List<AlarmTypeItem> alarmTypeList, String modelConfigJson) {
        if (CollectionUtils.isEmpty(alarmTypeList) || StringUtils.isEmpty(modelConfigJson)) {
            return;
        }

        List<String> itemCodes = extractItemCodesByCode(modelConfigJson, DICT_CV_MODE_TRIP_THREAD_TYPE);
        if (itemCodes == null || itemCodes.isEmpty()) {
            return;
        }

        // 计算匹配数量
        int matchCount = countMatches(alarmTypeList, itemCodes);

        // 部分在（有的匹配，有的不匹配）说明是混用
        if (matchCount > 0 && matchCount < alarmTypeList.size()) {
            // 提取支持绘制绊线的子类型名称
            List<String> supportTripThreadNames = new ArrayList<>();
            for (AlarmTypeItem alarmTypeItem : alarmTypeList) {
                if (itemCodes.contains(alarmTypeItem.getAlarmType())) {
                    // 从modelConfigJson中查找对应的itemName
                    String itemName = extractItemNameByCode(modelConfigJson, DICT_CV_MODE_TRIP_THREAD_TYPE, alarmTypeItem.getAlarmType());
                    if (itemName != null && !itemName.isEmpty()) {
                        supportTripThreadNames.add(itemName);
                    }
                }
            }

            // 构建提示信息
            if (!supportTripThreadNames.isEmpty()) {
                String namesStr = String.join("、", supportTripThreadNames);
                String errorMsg = String.format("【%s】算法支持绘制绊线/箭头，请勿与不支持该绘制功能的算法混合配置", namesStr);
                throw new CustomException(errorMsg, 400);
            }
        }
    }

    /**
     * 从modelConfigJson中提取指定code和itemCode对应的itemName
     *
     * @param modelConfigJson 模型配置JSON字符串
     * @param targetCode 目标code值
     * @param targetItemCode 目标itemCode值
     * @return 匹配的itemName，如果没有找到则返回null
     */
    private String extractItemNameByCode(String modelConfigJson, String targetCode, String targetItemCode) {
        if (StringUtils.isEmpty(modelConfigJson)) {
            return null;
        }

        List<Map<String, Object>> modelConfig = new ArrayList<>();
        try {
            modelConfig = JSON.parseObject(modelConfigJson, List.class);
        } catch (Exception e) {
            log.error("解析 model_config JSON 失败，targetCode={}, targetItemCode={}", targetCode, targetItemCode, e);
            return null;
        }
        // 第一阶段：extraValue 子高参优先
        String extraResult = extractItemNameFromExtraValue(modelConfig, targetCode, targetItemCode);
        if (extraResult != null) {
            return extraResult;
        }
        // 第二阶段：回退顶层
        return extractItemNameFromTopLevel(modelConfig, targetCode, targetItemCode);
    }

    private String extractItemNameFromExtraValue(List<Map<String, Object>> modelConfig, String targetCode, String targetItemCode) {
        for (Map<String, Object> config : modelConfig) {
            Object extraObj = config.get("extraValue");
            if (!(extraObj instanceof List)) {
                continue;
            }
            List<Map<String, Object>> extraValue = (List<Map<String, Object>>) extraObj;
            if (CollectionUtils.isEmpty(extraValue)) {
                continue;
            }
            List<Map<String, Object>> scoped = extraValue.size() > 50 ? extraValue.subList(0, 50) : extraValue;
            if (extraValue.size() > 50) {
                log.warn("extraValue 子高参规模超过50，仅取前50解析，targetCode={}, targetItemCode={}", targetCode, targetItemCode);
            }
            for (Map<String, Object> sub : scoped) {
                Object codeObj = sub.get("code");
                String code = codeObj != null ? codeObj.toString() : "";
                if (!code.equals(targetCode)) {
                    continue;
                }
                String itemName = extractItemNameFromDictList(sub, targetItemCode);
                if (itemName != null) {
                    log.info("extractItemNameByCode 命中 extraValue，targetCode={}, targetItemCode={}", targetCode, targetItemCode);
                    return itemName;
                }
            }
        }
        return null;
    }

    private String extractItemNameFromTopLevel(List<Map<String, Object>> modelConfig, String targetCode, String targetItemCode) {
        for (Map<String, Object> config : modelConfig) {
            Object codeObj = config.get("code");
            String code = codeObj != null ? codeObj.toString() : "";
            if (!code.equals(targetCode)) {
                continue;
            }
            String itemName = extractItemNameFromDictList(config, targetItemCode);
            if (itemName != null) {
                log.info("extractItemNameByCode 命中顶层，targetCode={}, targetItemCode={}", targetCode, targetItemCode);
                return itemName;
            }
        }
        return null;
    }

    private String extractItemNameFromDictList(Map<String, Object> element, String targetItemCode) {
        Object dictListObj = element.get(FIELD_DICT_LIST);
        if (!(dictListObj instanceof List)) {
            return null;
        }
        List<Map<String, Object>> dictList = (List<Map<String, Object>>) dictListObj;
        if (CollectionUtils.isEmpty(dictList)) {
            return null;
        }
        for (Map<String, Object> dict : dictList) {
            Object itemCodeObj = dict.get(FIELD_ITEM_CODE);
            String itemCode = itemCodeObj != null ? itemCodeObj.toString() : "";
            if (targetItemCode.equals(itemCode)) {
                Object itemNameObj = dict.get("itemName");
                return itemNameObj != null ? itemNameObj.toString() : null;
            }
        }
        return null;
    }

    /**
     * 检查场景下子类型是否都支持绊线绘制
     *
     * @param alarmTypeList 告警类型列表
     * @param modelConfigJson 模型配置JSON字符串
     * @return true表示全部支持，false表示不是全部支持
     */
    public boolean checkSupportTripThread(List<AlarmTypeItem> alarmTypeList, String modelConfigJson) {
        boolean flag = false;
        List<String> itemCodes = extractItemCodesByCode(modelConfigJson, DICT_CV_MODE_TRIP_THREAD_TYPE);
        
        if (itemCodes != null) {
            int matchCount = countMatches(alarmTypeList, itemCodes);
            if (matchCount == alarmTypeList.size()) {
                flag = true;
            }
        }
        return flag;
    }
}


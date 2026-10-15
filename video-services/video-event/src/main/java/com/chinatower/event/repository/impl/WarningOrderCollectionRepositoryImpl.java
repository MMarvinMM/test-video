package com.chinatower.event.repository.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dataobject.WarningOrderCollectionDO;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.event.mapper.WarningOrderCollectionMapper;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 告警信息收藏表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
@Service
@Slf4j
public class WarningOrderCollectionRepositoryImpl extends ServiceImpl<WarningOrderCollectionMapper, WarningOrderCollectionDO> implements IWarningOrderCollectionRepository {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;
    @Autowired
    private WarningOrderCollectionMapper warningOrderCollectionMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private ISysAlarmTagsRepository sysAlarmTagsRepository;

    @Autowired
    private ISysService sysService;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IWarningOrderRepository iWarningOrderRepository;

    @Autowired
    private IAlarmLableRelRepository alarmLableRelRepository;

    /**
     * 我的收藏-查询列表
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="t.device_code",tenantColumn="t.tenant_id",
            industryCode="t.industry_code", alarmSourceColumn="t.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10,11,12",areaPro = "t.province_code"
            ,areaCity = "t.city_code",areaCode = "t.county_code",processIdColumn = "t.process_id")
    public List<WarningOrderListVO> qryWarningOrderCollection(AlarmOrderDTO dto) {

        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        dto.setTenantId(loginUser.getUser().getTenantId());
        long start = System.currentTimeMillis();
        String loginUserId = sysService.getLoginUserId();
        dto.setCollectionCreator(loginUserId);
        dto.setSysUser(loginUser.getUser());
        //处理传入的组织条件
        warningOrderRepository.handleOrgIdList(dto);
        //跨库获取页面查询设备信息关联数据
        List<AlarmOrderByDeviceDTO> devList = this.getAlarmOrderByDeviceDTOS(dto);
        if (CollectionUtils.isEmpty(devList) &&
                (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
                        || !StringUtils.isEmpty(dto.getModelId()) || !StringUtils.isEmpty(dto.getDeviceName()) || !StringUtils.isEmpty(dto.getIotDeviceCodeOrName()))) {
            return new ArrayList<>();
        }
        dto.setDevList(devList);
        //告警类型标签
        if (!CollectionUtils.isEmpty(dto.getTagIdList())) {
            List<AlarmTagsTypeRelDTO> alarmTagsTypeRelDTOList = sysAlarmTagsRepository.getAlarmTypeByIds(dto.getTagIdList(), loginUser.getUser().getTenantId());
            if (!CollectionUtils.isEmpty(alarmTagsTypeRelDTOList)) {
                List<String> warningTypeIdList = new ArrayList<>();
                alarmTagsTypeRelDTOList.forEach(alarmTagsTypeRelDTO -> {
                    if (!StringUtils.isEmpty(alarmTagsTypeRelDTO.getTypeValue())) {
                        warningTypeIdList.add(alarmTagsTypeRelDTO.getTypeValue());
                    }
                });
                if (!CollectionUtils.isEmpty(dto.getWarningTypeIdList())) {
                    warningTypeIdList.addAll(dto.getWarningTypeIdList());
                }
                dto.setWarningTypeIdList(warningTypeIdList);
            } else {
                return new ArrayList<>();
            }
        }

        //告警标签
        List<AlarmLableRelDTO> alarmLableRelDTOList = new ArrayList<>();
        List<AlarmLableRelDTO> alarmLableAllList = alarmLableRelRepository.getAlarmLableByIds(new ArrayList<>(),"");
        alarmLableRelDTOList = alarmLableRelRepository.getAlarmLableByIds(dto.getLableIdList(),dto.getOrderId());
        if (!CollectionUtils.isEmpty(alarmLableRelDTOList)) {
            List<String> warningOrderIds = new ArrayList<>();
            alarmLableRelDTOList.forEach(item -> {
                if (!StringUtils.isEmpty(item.getWaringOrderId())) {
                    warningOrderIds.add(item.getWaringOrderId());
                }
            });
            if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                dto.setWarningOrderIds(warningOrderIds);
            }
        }
        else if(!CollectionUtils.isEmpty(dto.getLableIdList())) {
            return new ArrayList<>();
        }

        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态
        if(!CollectionUtil.isEmpty(dto.getOrderStatusList()) && dto.getOrderStatusList().contains(EnumUtil.FlowNode.END.getCode())|| (StringUtil.isNotEmpty(dto.getOrderStatus())&&EnumUtil.FlowNode.END.getCode().contains(dto.getOrderStatus()) )){
            List<String> orderStatusList = new ArrayList<String>();
            if((StringUtil.isNotEmpty(dto.getOrderStatus()))&&(dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))){
                orderStatusList.add(EnumUtil.FlowNode.END.getCode());
            }else{
                orderStatusList=dto.getOrderStatusList();
            }
            orderStatusList.add(EnumUtil.FlowNode.MISINFORMATION.getCode());
            orderStatusList.add(EnumUtil.FlowNode.DUPLICATION.getCode());
            dto.setOrderStatusList(orderStatusList);
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<WarningOrderListVO> resultList = new ArrayList<>();
        // 事件状态条件包含已完结时需要增加重复告警完结和假警完结的状态，OrderStatus由6变为空
        if((StringUtil.isNotEmpty(dto.getOrderStatus()))&&(dto.getOrderStatus().contains(EnumUtil.FlowNode.END.getCode()))){
            dto.setOrderStatus("");
        }
        resultList = warningOrderCollectionMapper.qryWarningOrderCollection(dto);
        //告警结果集
        List<Map> warningOrderList = new ArrayList<>();
        //领导批示 告警数据月份结果集
        List<String> warningOrderYMList = new ArrayList<>();
        //领导批示 告警数据月份数据结果集
        Map<String, List<String>> map = new HashMap<>();
        for (WarningOrderListVO orderListVO : resultList) {
            String orderId = String.valueOf(orderListVO.getOrderId());
            String warningOrderYM = orderId.substring(0, 6);
            if (CollectionUtils.isEmpty(map.get(warningOrderYM))) {
                List<String> list = new ArrayList<>();
                list.add(orderId);
                map.put(warningOrderYM, list);
                warningOrderYMList.add(warningOrderYM);
            }
            else {
                List<String> list = map.get(warningOrderYM);
                list.add(orderId);
                map.put(warningOrderYM, list);
            }
        }
        for (String warningOrderYM : warningOrderYMList) {
            String tableName = "";
            try {
                // 获取表名
                tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderYM;
                String processTableName = TableConstant.WARN_ORDER_PROCESS_TEBLE + warningOrderYM;
                Map reqMap = new HashMap();
                reqMap.put(Constants.PARAM_TABLE_NAME, tableName);
                reqMap.put(Constants.PARAM_PROCESS_TABLE_NAME, processTableName);
                reqMap.put(Constants.PARAM_WARNING_ORDER_ID_LIST, map.get(warningOrderYM));
                List<Map> resList = warningOrderMapper.getWarningOrderList(reqMap);
                if (!CollectionUtils.isEmpty(resList)) {
                    warningOrderList.addAll(resList);
                }
            }
            catch (Exception e) {
                log.error("我的收藏查询告警表:{},异常：", tableName, e);
            }
        }
        for (WarningOrderListVO vo : resultList) {
            //告警标签回显
            List<AlarmLableRelAPIDTO> alarmLableRelDTOS = new ArrayList<>();
            List<AlarmLableRelDTO> alarmLableRelAPIDTOs= alarmLableAllList.stream().filter(item -> vo.getOrderId().equals(item.getWaringOrderId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(alarmLableRelAPIDTOs)){
                alarmLableRelAPIDTOs.forEach(itemRe ->{
                    AlarmLableRelAPIDTO relAPIDTO = new AlarmLableRelAPIDTO();
                    BeanUtils.copyProperties(itemRe,relAPIDTO);
                    alarmLableRelDTOS.add(relAPIDTO);
                });
            }
            vo.setAlarmLableRelDTOList(alarmLableRelDTOS);
            //从工单表获取透传字段
            warningOrderList.forEach(item -> {
                if (vo.getOrderId().equals(item.get(Constants.RES_WARNING_ORDER_ID))) {
                    vo.setExtraField(String.valueOf(item.get(Constants.WARNING_ORDER_EXTRA_FIELD)));
                    vo.setDeleteFlag(Integer.parseInt(item.get(Constants.PARAM_DELETE_FLAG_FIELD).toString()));
                    vo.setLinkId(String.valueOf(item.get(Constants.PARAM_RES_LINK_ID)));
                }
            });
        }
        warningOrderRepository.resultHandle(resultList, dto, dto.getCollectionCreator(),"");
        log.info("查询我的收藏-查询列表耗时：{},入参：{}", (System.currentTimeMillis() - start), JSONObject.toJSONString(dto));
        return resultList;
    }

    private List<AlarmOrderByDeviceDTO> getAlarmOrderByDeviceDTOS(AlarmOrderDTO dto) {
        List<AlarmOrderByDeviceDTO> devList;
        if(!StringUtils.isEmpty(dto.getIotDeviceCodeOrName())){
            devList = deviceRepository.getAlarmOrderByIotDevice(dto);
        } else {
            devList = deviceRepository.queryAlarmOrderByDevice(dto);
        }
        if(!CollectionUtils.isEmpty(devList)){
            dto.setDevList(devList);
        }
        return devList;
    }

    /**
     * 我的收藏-取消收藏
     *
     * @param req
     */
    @Override
    public boolean delWarningOrderCollection(WarningOrderCollectionDO req) throws Exception {

        QueryWrapper<WarningOrderCollectionDO> wrapper = new QueryWrapper();
        wrapper.eq("warning_order_id", req.getWarningOrderId());
        wrapper.eq("collection_creator", req.getCollectionCreator());
        List<WarningOrderCollectionDO> list = warningOrderCollectionMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new Exception(Constants.WARNING_ORDER_COLLECTION);
        }
        List<Integer> paramList = new ArrayList<>();
        for (WarningOrderCollectionDO warningOrderCollection : list) {
            paramList.add(warningOrderCollection.getCollectionId());
        }
        return this.removeByIds(paramList);
    }

    /**
     * 我的收藏-新增收藏
     *
     * @param req
     */
    @Override
    public int addWarningOrderCollection(WarningOrderCollectionDO req) throws Exception {

        QueryWrapper<WarningOrderCollectionDO> wrapper = new QueryWrapper();
        wrapper.eq("warning_order_id", req.getWarningOrderId());
        wrapper.eq("collection_creator", req.getCollectionCreator());
        List<WarningOrderCollectionDO> resList = warningOrderCollectionMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(resList)) {
            List<Integer> paramList = new ArrayList<>();
            for (WarningOrderCollectionDO warningOrderCollection : resList) {
                paramList.add(warningOrderCollection.getCollectionId());
            }
            this.removeByIds(paramList);
        }
        // 告警查询vm_warning_order_t_yyyymm表
        String tableName = TableUtil.getTableName(DateUtils.string2Date(req.getAlarmTime()), TableConstant.WARN_ORDER_TEBLE);
        AlarmOrderDTO dto = new AlarmOrderDTO();
        dto.setTableName(tableName);
        dto.setOrderId(req.getWarningOrderId());
        List<WarningOrderCollectionDO> warningOrderCollectionList = warningOrderCollectionMapper.getWarningOrderByParam(dto);
        WarningOrderCollectionDO warningOrderCollection = new WarningOrderCollectionDO();
        if (CollectionUtil.isEmpty(warningOrderCollectionList)) {
            // 运营研判 串行流程
            dto.setTableName(TableConstant.WARN_ALARM_OPERATE);
            warningOrderCollection = warningOrderCollectionMapper.getAlarmOperateOrderByParam(dto);
            if (warningOrderCollection == null) {
                dto.setTableName(TableConstant.WARN_ALARM_INTERCEPT);
                warningOrderCollection = warningOrderCollectionMapper.getWarningInterceptByParam(dto);
                if (warningOrderCollection == null){
                    throw new Exception(Constants.WARNING_ORDER_MSG);
                }
            }
        }else{
            warningOrderCollection = warningOrderCollectionList.get(0);
        }
        warningOrderCollection.setCollectionCreateTime(new Date());
        warningOrderCollection.setCollectionCreator(req.getCollectionCreator());
        warningOrderCollection.setDeleteFlag(Constants.ZERO);
        return warningOrderCollectionMapper.insert(warningOrderCollection);
    }

    /**
     * 我的收藏-大屏展示
     *
     * @param dto
     * @return
     */
    @EventObsEncrypt
    @Override
    public List<Map> qryWarningOrderCollectionForBigScreen(BigScreenOrderListDTO dto) {
        List<Map> resultList = new ArrayList<>();
        //告警标签
        List<AlarmLableRelDTO> alarmLableRelDTOList = new ArrayList<>();
        PageHelper.clearPage();
        List<AlarmLableRelDTO> alarmLableAllList = alarmLableRelRepository.getAlarmLableByIds(new ArrayList<>(),"");
        alarmLableRelDTOList = alarmLableRelRepository.getAlarmLableByIds(dto.getLableIdList(),dto.getWarningOrderId());
        if (!CollectionUtils.isEmpty(alarmLableRelDTOList)) {
            List<String> warningOrderIds = new ArrayList<>();
            alarmLableRelDTOList.forEach(item -> {
                if (!StringUtils.isEmpty(item.getWaringOrderId())) {
                    warningOrderIds.add(item.getWaringOrderId());
                }
            });
            if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                dto.setWarningOrderIds(warningOrderIds);
            }
        }
        else if(!CollectionUtils.isEmpty(dto.getLableIdList())) {
            return resultList;
        }
        long start = System.currentTimeMillis();

        PageMethod.startPage(dto.getPageNum(), dto.getPageSize());
        resultList = warningOrderCollectionMapper.qryWarningOrderCollectionForBigScreen(dto);
        //根据用户id和角色id查询流程环节信息
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        // 获取当前用户角色
        Long[] roleIds = sysUser.getRoleIds();
        List<String> roleIdList = new ArrayList<>();
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleIdList.add(String.valueOf(roleId));
            }
        }
        String flowLinkIds = flowLinkService.getFlowLinkAttrByUser(sysUser, roleIdList);
        String userId = sysService.getLoginUserId();
        boolean admin =(OrderConstants.IS_TENANT_ADMIN.equals(sysUser.getIsTenantAdmin()));
        List<String> tableList = new ArrayList<>();
        List<String> orderIdList = new ArrayList<>();
        for (Map map : resultList) {
            String orderId = (String)map.get(Constants.PARAM_ORDER_ID);
            //告警标签回显
            List<AlarmLableRelAPIDTO> alarmLableRelDTOS = new ArrayList<>();
            List<AlarmLableRelDTO> alarmLableRelAPIDTOs= alarmLableAllList.stream().filter(item -> orderId.equals(item.getWaringOrderId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(alarmLableRelAPIDTOs)){
                alarmLableRelAPIDTOs.forEach(itemRe ->{
                    AlarmLableRelAPIDTO relAPIDTO = new AlarmLableRelAPIDTO();
                    BeanUtils.copyProperties(itemRe,relAPIDTO);
                    alarmLableRelDTOS.add(relAPIDTO);
                });
            }
            map.put(Constants.ALARM_LABLE_LIST,alarmLableRelDTOS);
            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
            orderIdList.add(orderId);
            if(!tableList.contains(alarmTableName)){
                tableList.add(alarmTableName);
            }
        }
        List<WarningOrderListVO> listAll = new ArrayList<>();
        for (String tableName:tableList) {
            AlarmOrderDTO alarmOrderVO = new AlarmOrderDTO();
            alarmOrderVO.setWarningOrderIds(orderIdList);
            alarmOrderVO.setTableName(tableName);
            List<WarningOrderListVO> queryResult = warningOrderMapper.queryAlarmOrderByOrderList(alarmOrderVO);
            listAll.addAll(queryResult);
        }
        for (WarningOrderListVO vo:listAll) {
            int ifEdit = iWarningOrderRepository.ifEditHandle(admin, userId, vo,flowLinkIds);
            for (Map map :resultList) {
                if(vo.getOrderId().equals(String.valueOf(map.get(Constants.PARAM_ORDER_ID)))){
                    map.put("ifEdit",ifEdit);
                }
            }
        }
        warningOrderRepository.resultMapHandle(resultList);
        log.info("查询我的收藏-大屏数据查询耗时：{},入参：{}", (System.currentTimeMillis() - start), JSONObject.toJSONString(dto));
        return resultList;
    }


    @Override
    public int updateWarningOrderCollectionForBigScreen(SendAlarmOrderDTO req) {
        return warningOrderCollectionMapper.updateByPrimaryKeySelective(req);
    }
}

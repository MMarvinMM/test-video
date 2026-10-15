package com.chinatower.event.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.AlarmStrategy;
import com.chinatower.event.core.alarm.util.GisUtil;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.dto.ReceiveAlarmVOReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.mongodb.OperateMongoMapper;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.IReceiveRepository;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.service.IAlarmService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.VideoQueryAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 告警相关服务实现类
 * @Author guyuefei
 * @Date 2022/7/11
 * @Version release_v1.5
 **/
@Service("alarmService")
@Slf4j
public class AlarmServiceImpl implements IAlarmService {

    /** 引入所有告警策略处理类 **/
    @Autowired
    private List<AlarmStrategy> alarmStrategyList;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private VideoQueryAPI videoQueryAPI;

    @Autowired
    private GisUtil gisUtil;

    @Autowired
    private ISysService sysService;
    @Autowired
    private IReceiveRepository receiveRepository;

    @Override
    public Result receiveAlarm(ReceiveAlarmVOReq receiveAlarmVOReq) {
        log.info("告警接收入参：{}", JSON.toJSONString(receiveAlarmVOReq));
        /**
         * 根据传入参数条件判断告警类型，执行策略处理执行器
         */
        String alarmType = receiveAlarmVOReq.getData().getAlarmType();
        Assert.notNull(alarmType,"告警类型不能为空！");
        String localNode="";
        //获取当前节点
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                localNode = sysConfigInfo.getConfigValue();
            }
        }
        // 卡口告警没有受理节点，暂时放开(告警来源是卡口告警“5” 或者 是摄像机告警来源的卡口告警类型)
        if((receiveAlarmVOReq.getData().getAccessNode() !=null && localNode.equals(receiveAlarmVOReq.getData().getAcceptNode()))
                || Constants.FIVE.equals(receiveAlarmVOReq.getData().getAlarmSource())
                || StringUtils.isEmpty(receiveAlarmVOReq.getData().getAccessNode())){
            log.info("工单成功上报节点无误");
        }else{
            return Result.fail("受理节点与当前节点不一致或没有接入节点");
        }
        receiveAlarmVOReq.getData().setLocalNode(localNode);
        try {
            for(AlarmStrategy alarmStrategy : alarmStrategyList){
                if(alarmStrategy.jdugeAlarmType(receiveAlarmVOReq.getData())){
                    log.info("告警接收data入参：{}", JSON.toJSONString(receiveAlarmVOReq));
                    ReceiveAlarmDTO receiveAlarmDTO = new ReceiveAlarmDTO();
                    ReceiveAlarmVO receiveAlarmVO = montageUrl(receiveAlarmVOReq.getData().getAccessNode(),receiveAlarmVOReq.getData());
                    receiveAlarmDTO.setReceiveAlarmVO(receiveAlarmVO);
                    receiveAlarmDTO.setReceiveIotAlarmVOList(receiveAlarmVOReq.getReceiveIotAlarmVOList());

                    alarmStrategy.receiveAlarm(receiveAlarmDTO);
                    return Result.success("接收告警成功");
                }
            }
        } catch (Exception e) {
            log.error("接收告警失败，参数：{},异常原因：{}",receiveAlarmVOReq,e);
        }
        return Result.success("没有相应的告警类型处理策略");
    }

    @Override
    public Result syncAlarm(ReceiveAlarmVO req) {
        log.info("syncAlarm-运营研判告警同步入参:{}",JSON.toJSONString(req));
        operateMongoMapper.insertLocalOperate(req,req.getAlarmTime());
        return Result.success("同步告警成功");
    }

    @Override
    public Result receiveRegionAlarm(List<ReceiveRegionAlarmDto> reqList) {
        log.info("卫片执法上报告警入参:{}",JSON.toJSONString(reqList));
        //根据租户id查询租户名称
        List<String> list = reqList.stream().map(ReceiveRegionAlarmDto::getTenantId).collect(Collectors.toList());
        List<SysTenantInfo> tenantInfoList = sysService.queryTenantList(list);
        if(!CollectionUtils.isEmpty(tenantInfoList)){
            for (SysTenantInfo tenantInfo:tenantInfoList) {
                for (ReceiveRegionAlarmDto alarmDto:reqList) {
                    if(tenantInfo.getTenantId().equals(alarmDto.getTenantId())){
                        alarmDto.setTenantName(tenantInfo.getTenantName());
                    }
                }
            }
        }
        //批量异步上报卫星执法告警
        ThreadParallelUtil.run(reqList, o->{
            saveReceiveRegionAlarm(o);
            return null;
        }, ThreadParallelUtil.ConfigFactory.WARNING_CODE);
        return Result.success("告警上报成功");
    }

    public String getFormatTime(String time){
        OffsetDateTime dt = OffsetDateTime.parse(time);
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void saveReceiveRegionAlarm(ReceiveRegionAlarmDto req){
        log.info("卫片执法上报告警入参-saveReceiveRegionAlarm:{}",JSON.toJSONString(req));
        try{
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            //告警实体
            ReceiveAlarmVO vo = new ReceiveAlarmVO();
            BeanUtil.copyProperties(req,vo);
            //卫片透传字段实体
            RegionAlarmExtraField regionAlarmExtraField = new RegionAlarmExtraField();
            BeanUtil.copyProperties(req,regionAlarmExtraField);
           if(!StringUtils.isEmpty(regionAlarmExtraField.getDetectionFlag())){
                DecimalFormat df = new DecimalFormat("0");
                Double b = Double.parseDouble(regionAlarmExtraField.getDetectionFlag());
                regionAlarmExtraField.setDetectionFlag(df.format(b));
            }
             /*if(!StringUtils.isEmpty(regionAlarmExtraField.getPreChronology())){
                regionAlarmExtraField.setPreChronology(getFormatTime(regionAlarmExtraField.getPreChronology()));
            }
            if(!StringUtils.isEmpty(regionAlarmExtraField.getPostChronology())){
                regionAlarmExtraField.setPostChronology(getFormatTime(regionAlarmExtraField.getPostChronology()));
            }
            if(!StringUtils.isEmpty(regionAlarmExtraField.getImageTime())){
                regionAlarmExtraField.setImageTime(getFormatTime(regionAlarmExtraField.getImageTime()));
            }*/
            String alarmBody="";
            if(req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_GDWP)){
                //耕地卫片执法
                alarmBody = req.getPreType()+Constants.PARAM_CHANGE+req.getPostType();
            }else if(req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_KCWP)){
                //矿产卫片执法
                alarmBody = "矿产卫片执法";
            }else if(req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_FNHWP) || req.getWarningTypeId().equals(Constants.WARNING_TYPE_ID_FLHWP)){
                //非农化卫片执法&非粮化卫片执法
                alarmBody = req.getChangeType();
            }
            vo.setAlarmBody(alarmBody);
            vo.setLongitude(req.getSpotCenterLongitude());
            vo.setLatitude(req.getSpotCenterLatitude());
            vo.setImportanceLevel(Constants.ONE);
            vo.setWarningSource(req.getAlarmSource());
            vo.setWarningOrderId(PKUtils.getWarnOrderId(vo.getTenantId(), vo.getAlarmTime()));
            vo.setFlowNodeTime(DateUtils.date2Str(new Date(),"yyyy-MM-dd HH:mm:ss"));
            vo.setAlarmType(req.getWarningTypeId());
            //设置来源于卫片，后续根据这个字段匹配人时不过滤数据权限
            vo.setRegionAlarmFlag(Constants.ONE);
            gisUtil.getAddressFromAmap(vo,null);
            //BeanUtil.copyProperties(vo,regionAlarmExtraField);
            regionAlarmExtraField.setProvinceName(vo.getProvinceName());
            regionAlarmExtraField.setCityName(vo.getCityName());
            regionAlarmExtraField.setCountyName(vo.getCountyName());
            regionAlarmExtraField.setTownName(vo.getTownName());
            vo.setExtraField(JSON.toJSONString(regionAlarmExtraField));
            // 根据经纬度获取网格信息
            if (!StringUtils.isEmpty(vo.getLatitude()) && !StringUtils.isEmpty(vo.getLongitude())) {
                try {
                    GridByLngLatParam param = new GridByLngLatParam();
                    param.setLatitude(vo.getLatitude());
                    param.setLongitude(vo.getLongitude());
                    List<BaseIndustryEntity> tenantList = new ArrayList<>();
                    BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                    baseIndustryEntity.setIndustryCode(vo.getIndustryCode());
                    baseIndustryEntity.setTenantId(vo.getTenantId());
                    tenantList.add(baseIndustryEntity);
                    param.setTenantList(tenantList);
                    log.info("setAlarmGridInfo-getMessageByLngLatNew-req:{}", JSON.toJSONString(param));
                    Result<List<GridByLngLat>> result = gridAPI.getMessageByLngLatNew(param);

                    log.info(MessageFormat.format(MessageConstants.GRID_MESSAGE, result));
                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                        List<GridByLngLat> gridList = result.getData();
                        if (!CollectionUtils.isEmpty(gridList)) {
                            GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(vo.getTenantId(), grid.getTenantId()) && Objects.equals(vo.getIndustryCode(), grid.getIndustryCode()))
                                    .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                            vo.setGridId(gridInfo.getGridId());
                            vo.setGridName(gridInfo.getGridName());
                            vo.setGridUserList(gridInfo.getUserList());
                        }
                    }
                } catch (Exception e) {
                    log.error(MessageConstants.GRID_ERROR_MESSAGE, e);
                }
            }
            //匹配告警流程相关
            alarms.add(vo);
            // 创建流程
            List<Map> recordList = warningOrderRepository.createRecord(alarms);
            // 回填当前环节相关字段
            warningOrderRepository.setWarningLinkInfo(alarms, recordList);
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(alarms);
            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(vo.getAlarmTime()), TableConstant.WARN_ORDER_TEBLE));

            receiveAlarmVODB.setList(alarms);
            warningOrderMapper.insertWarningOrders(receiveAlarmVODB);
            //发送消息推送
            receiveRepository.batchPushMessage(receiveAlarmVODB.getList());
        }catch (Exception e){
            log.error("卫片执法上报告警异常:{}",e);
        }

    }

    /**
     * 对工单图片进行拼接（非中心节点&受理节点与当前节点不一致得单子进行操作，其余保持原有路径）
     * @param accessNode
     * @param receiveAlarmVO
     * @return
     */
    public ReceiveAlarmVO montageUrl(String accessNode,ReceiveAlarmVO receiveAlarmVO){
        if(StringUtils.isEmpty(accessNode)){
            log.info("接入节点为空-入参:{}",JSON.toJSONString(receiveAlarmVO));
            return receiveAlarmVO;
        }
        //中心节点文件地址无需拼接属地编码
        if("10000000001".equals(accessNode)){
            return receiveAlarmVO;
        }
        //纯属化不拼接节点编码
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey("dependence_manage");
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        log.info("获取当前环境字段返回:{}",JSON.toJSONString(result));
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null && Constants.ZERO.equals(sysConfigInfo.getConfigValue())) {
                return receiveAlarmVO;
            }
        }

        String localNode=receiveAlarmVO.getLocalNode();
        //接入节点和受理节点同节点无需拼接属地编码(限制去除)
        /*if(accessNode.equals(localNode)){
            return receiveAlarmVO;
        }*/
        receiveAlarmVO.setFileImgUrlIcon(getUrl(accessNode,receiveAlarmVO.getFileImgUrlIcon()));
        receiveAlarmVO.setFileImgUrlSrc(getUrl(accessNode,receiveAlarmVO.getFileImgUrlSrc()));
        receiveAlarmVO.setAlarmVideoUrl(getUrl(accessNode,receiveAlarmVO.getAlarmVideoUrl()));
        receiveAlarmVO.setAlarmPictureUrl(getUrl(accessNode,receiveAlarmVO.getAlarmPictureUrl()));
        receiveAlarmVO.setOriginalPictureUrl(getUrl(accessNode,receiveAlarmVO.getOriginalPictureUrl()));
        return receiveAlarmVO;
    }

    public String getUrl(String accessNode,String url){
        if(StringUtils.isEmpty(url)){
            return url;
        }
        String[] urls = url.split(Constants.COMMA);
        String resUrl="";
        for (String realUrl:urls) {
            if(realUrl.contains("http") || realUrl.contains("https")){
                //将属地编码拼接在域名之后
                int thirdSlashIndex = realUrl.indexOf('/', realUrl.indexOf('/', realUrl.indexOf('/') + 1) + 1);
                // 拼接字符串，在第三个斜杠之后
                StringBuilder result = new StringBuilder(realUrl.substring(0, thirdSlashIndex + 1));
                result.append(accessNode+"/");
                result.append(realUrl.substring(thirdSlashIndex + 1));
                realUrl = result.toString();
            }else{
                realUrl = "/"+accessNode+realUrl;
            }
            if(StringUtils.isEmpty(resUrl)){
                resUrl = realUrl;
            }else{
                resUrl +=Constants.COMMA+realUrl;
            }
        }
        return resUrl;
    }
}

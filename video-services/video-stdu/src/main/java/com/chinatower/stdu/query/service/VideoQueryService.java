package com.chinatower.stdu.query.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.CommonService;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.stdu.common.entity.ResultEntity;
import com.chinatower.stdu.query.dao.VideoQueryDAO;
import com.chinatower.stdu.query.entity.HabitVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.PlayerGetVideoRealtimeUrlData;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 视频查询服务类
 * Created by ldl on 2022/1/27.
 */
@Service
@Slf4j
public class VideoQueryService{

    @Autowired
    VideoQueryDAO videoQueryDAO;
    @Resource
    private NetConnectUtils connectUtils;
    @Resource
    private ICasCadeInfoService iCasCadeQueryService;
    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private CommonService commonService;


    public ResultEntity getVideoRealtimeUrls(List list) {
        List resultData = new ArrayList();
        try{
            String platformCode = "";
            SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
            if(!ObjectUtils.isEmpty(config)) {
                platformCode = config.getConfigValue();
            }
            final String fromCode = platformCode;

            //根据to-code对入参分组 分组后格式：[{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
            List<Map> threadList = commonService.groupByNode(list, "#item.get('deviceCode')", iCasCadeQueryService::getCasCadeMapByDeviceCodeBatch);

            //多线程处理
            List<Object> data = ThreadParallelUtil.run(threadList, it -> {
                log.info("getVideoRealtimeUrls线程处理数据量:{}", it.size());
                return sendGetVideoRealtimeUrls(it, fromCode);
            }, ThreadParallelUtil.ConfigFactory.GET_VIDEO_REALTIME_URLS);

            //融合结果
            if(!CollectionUtils.isEmpty(data)){
                for (Object datum : data) {
                    if (datum instanceof List) {
                        // 情况1: data 是 List<List<T>>，则展开每个子 List
                        resultData.addAll((List<?>) datum);
                    } else {
                        // 情况2: data 是 List<T>，直接把每个元素加入 resultData
                        resultData.add(datum);
                    }
                }
            }
            log.info("getVideoRealtimeUrls.resultData="+JSONObject.toJSON(resultData));
        } catch (Exception e) {
            log.error(VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE , e);
            return ResultEntity.fail(VideoConstants.FAIL_CODE,VideoConstants.ERROR_PRE + VideoConstants.GETVIDEOREALTIMEURLS_ERRO);
        }
        return ResultEntity.success(resultData);
    }

    private Object sendGetVideoRealtimeUrls(Map map,String fromCode){
        try {
            NetConnectUtils.RequestParamter<List> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody((List)map.get(VideoConstants.VALUE));
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_VIDEO_REALTIME_URLS);
            paramter.setPath(path);

            String toCode = (String)map.get(VideoConstants.KEY);
            // 调用统一网关接口
            log.info(toCode + VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE + VideoConstants.REQUEST_LOG + JSONObject.toJSON(paramter));
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(fromCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(toCode + VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE + VideoConstants.RESPONSE_LOG + JSONObject.toJSON(cr));
            return cr.getData();
        } catch (Exception e) {
            log.error(VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE , e);
            throw new RuntimeException(e);
        }
    }

    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(Set<PlayerGetVideoRealtimeUrlData> reqList) {
        log.info("getVideoRealtimeUrlsForComponent入参" + JSON.toJSONString(reqList));
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String userId = currentUser.getUser().getUserId();
        List<String> habitKeyList = new ArrayList<>();
        for (PlayerGetVideoRealtimeUrlData data : reqList) {
            habitKeyList.add(this.getHabitKey(userId,data.getDeviceCode(),data.getChannelCode()));
        }

        if(!CollectionUtils.isEmpty(habitKeyList)){
            List<HabitVO> userHabitList = videoQueryDAO.getUserHabitList(habitKeyList);
            Map<String,String> habitVOMap = new HashMap<>();
            for (HabitVO habitVO : userHabitList) {
                habitVOMap.put(habitVO.getHabitKey(),habitVO.getHabitValue());
            }

            int netType = this.getNetType();
            int streamType = Constants.ONE_NUM;
            int protocolType = this.getProtocolType();

            List<Map> list = new ArrayList<>();
            for (PlayerGetVideoRealtimeUrlData deviceInfo : reqList) {
                String value = habitVOMap.get(this.getHabitKey(userId, deviceInfo.getDeviceCode(), deviceInfo.getChannelCode()));
                if(!StringUtils.isEmpty(value)){
                    String[] split = value.split(Constants.COMMA);
                    try{
                        streamType = Integer.parseInt(split[Constants.ZERO_NUM]);
                        protocolType = Integer.parseInt(split[Constants.ONE_NUM]);
                    }catch (Exception e){
                        log.error("视频记忆功能参数错误：" + value);
                    }
                }
                //构造返回参数用到
                deviceInfo.setStreamType(streamType);
                deviceInfo.setProtocolType(protocolType);

                Map map = new HashMap();
                map.put(Constants.DEVICECODE, deviceInfo.getDeviceCode());
                map.put(DeviceConstants.CHANNELCODE, deviceInfo.getChannelCode());
                map.put(DeviceConstants.STREAM_TYPE, streamType);
                map.put(Constants.STREAM_UUID, IdUtils.fastSimpleUUID());
                map.put(Constants.PROTOCOL_TYPE, protocolType);
                map.put(Constants.NET_TYPE,netType);
                list.add(map);
            }

            log.info("getVideoRealtimeUrls入参" + JSON.toJSONString(list));
            ResultEntity res = this.getVideoRealtimeUrls(list);
            log.info("getVideoRealtimeUrls出参" + JSON.toJSONString(res));
            if(Constants.SUCCESS_CODE.equals(res.getReturnCode())){
                Object data = res.getData();
                List jsonArray = (List) data;
                for (PlayerGetVideoRealtimeUrlData d : reqList) {
                    d.setStreamSwitchFlag(Constants.ONE_NUM);
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = (JSONObject) o;
                        if(d.getDeviceCode().equals(jsonObject.get(Constants.DEVICECODE))
                                && d.getChannelCode().equals(jsonObject.get(VideoConstants.CHANNELCODE)) ){
                            d.setStreamUrl((String)jsonObject.get(Constants.STREAM_URL));
                            d.setStreamUuid((String)jsonObject.get(Constants.STREAM_UUID));
                            d.setReturnMsg((String)jsonObject.get(Constants.RETURN_MSG));
                        }
                    }
                }
                return Result.data(reqList);
            }else{
                return Result.fail(res.getReturnMsg() + res.getErrorMsg());
            }
        }
        return Result.data(null);
    }

    private String getHabitKey(String userId,String deviceCode,String channelCode ){
        return userId +
                VideoConstants.UNDERLINE_STR + "real" +
                VideoConstants.UNDERLINE_STR + deviceCode +
                VideoConstants.UNDERLINE_STR + channelCode;
    }

    private int getNetType(){
        int netType = Constants.TWO_NUM;
        // 网域类型字段
        AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.NETTYPE_PARAM);
        if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if(!ObjectUtils.isEmpty(sysConfig)){
                netType = Integer.parseInt(sysConfig.getConfigValue());
            }
        }
        return netType;
    }

    private int getProtocolType(){
        int protocolType = Constants.ZERO_NUM;
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType("99");
        if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
            List<SysDictDataResponse> data = sysResult.getData();
            if(!CollectionUtils.isEmpty(data)){
                for (int i = 0; i < data.size(); i++) {
                    int max = Integer.parseInt(data.get(i).getDictValue());
                    if (max > protocolType) {
                        protocolType = max;
                    }
                }
            }else {
                protocolType = Constants.FIVE_NUM;
            }
        }else{
            protocolType = Constants.FIVE_NUM;
        }
        return protocolType;
    }

    public Map<String,String> getCasCadeMapByDeviceCodeBatch(List<String> list){
        if(CollectionUtils.isEmpty(list)){
            return new HashMap<>();
        }
        Map<String,String> map = new HashMap<>();
        //获取目标节点编码
        List<CasCadeVO> result = videoQueryDAO.getCasCadeByDeviceCodeBatch(list);
        for (CasCadeVO vo : result) {
            map.put(vo.getDeviceCode(),vo.getCascadeToCode());
        }
        return map;
    }

    /**
     *
     * @param params
     * @param itemExpression
     * @param batchQuery
     * @return [{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
     */
    public static List<Map> groupByNode(List params, String itemExpression, Function<List<String>, Result<Map<String,String>>> batchQuery){
        List<Map> list = new ArrayList<>();
        List targetList = new ArrayList();
        for(Object p:params){
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable(VideoConstants.ITEM,p);
            Expression expression = parser.parseExpression(itemExpression);
            String target = expression.getValue(context).toString();

            targetList.add(target);
        }

        //数据库批量查询，返回设备code和node的map
        Result<Map<String,String>> rm = batchQuery.apply(targetList);
        Map<String,String> targetNodeMap = new HashMap<>();
        if(rm.getCode() == ResultCode.SUCCESS.getCode()){
            targetNodeMap = rm.getData();
        }

        Map<String,List> result = new HashMap<>();
        for(Object p:params){
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable(VideoConstants.ITEM,p);
            Expression expression = parser.parseExpression(itemExpression);
            String key = expression.getValue(context).toString();

            String node = targetNodeMap.get(key);
            if(!result.containsKey(node)) {
                result.put(node, new ArrayList());
            }
            result.get(node).add(p);
        }
        for (Map.Entry<String, List> r : result.entrySet()) {
            Map map = new HashMap();
            map.put(VideoConstants.KEY,r.getKey());
            map.put(VideoConstants.VALUE,r.getValue());
            list.add(map);
        }
        return list;
    }

    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsCustomSet(Set<PlayerGetVideoRealtimeUrlData> reqList){
        log.info("getVideoRealtimeUrlsCustomSet入参:" + JSON.toJSONString(reqList));
        int netType = 0;
        int streamType = 0;
        int protocolType = 0;

        int netTypeDef = this.getNetType();
        int protocolTypeDef = this.getProtocolType();
        List<Map<String, Object>> playRequestList = new ArrayList<>();
        // 存储最终要返回的 Data 对象集合（每个设备-通道一个）
        Set<PlayerGetVideoRealtimeUrlData> resultDataSet = new HashSet<>();
        // 没有channelCode的设备，用于批量查库
        List<String> deviceCodesWithoutChannel = reqList.stream().filter(device -> StringUtils.isEmpty(device.getChannelCode())).map(PlayerGetVideoRealtimeUrlData::getDeviceCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<String, List<ChannelInfoVO>> deviceChannelMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(deviceCodesWithoutChannel)) {
            DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
            deviceInfoVO.setDeviceCodeList(deviceCodesWithoutChannel);
            List<ChannelInfoVO> allChannels = getChannelInfoList(deviceInfoVO);
            deviceChannelMap = allChannels.stream().collect(Collectors.groupingBy(ChannelInfoVO::getParentDeviceCode, Collectors.toList()));
        }
        for (PlayerGetVideoRealtimeUrlData deviceInfo : reqList) {
            List<ChannelInfoVO> effectiveChannels = new ArrayList<>();
            if (StringUtils.isEmpty(deviceInfo.getChannelCode())) {
                // 没有 channelCode从数据库查出的通道列表
                List<ChannelInfoVO> channelsFromDB = deviceChannelMap.get(deviceInfo.getDeviceCode());
                if (!CollectionUtils.isEmpty(channelsFromDB)) {
                    effectiveChannels.addAll(channelsFromDB);
                } else {
                    // 没有通道信息，跳过
                    continue;
                }
            } else {
                // 已有 channelCode
                ChannelInfoVO singleChannel = new ChannelInfoVO();
                singleChannel.setParentDeviceCode(deviceInfo.getDeviceCode());
                singleChannel.setChannelCode(deviceInfo.getChannelCode());
                effectiveChannels.add(singleChannel);
            }

            for (ChannelInfoVO channel : effectiveChannels) {
                PlayerGetVideoRealtimeUrlData resultData = new PlayerGetVideoRealtimeUrlData();
                resultData.setDeviceCode(deviceInfo.getDeviceCode());
                resultData.setChannelCode(channel.getChannelCode());
                resultData.setStreamSwitchFlag(Constants.ONE_NUM);
                // streamType
                resultData.setStreamType(deviceInfo.getStreamType() == 0 ? Constants.ONE_NUM : deviceInfo.getStreamType());
                // protocolType
                resultData.setProtocolType(deviceInfo.getProtocolType() == 0 ? protocolTypeDef : deviceInfo.getProtocolType());
                // netType
                resultData.setNetType(StringUtils.isEmpty(deviceInfo.getNetType()) ? String.valueOf(netTypeDef) : deviceInfo.getNetType());
                // UUID
                String streamUuid = IdUtils.fastSimpleUUID();
                resultData.setStreamUuid(streamUuid);
                // 添加到结果集
                resultDataSet.add(resultData);
                // 构建播放请求参数
                Map<String, Object> map = new HashMap<>();
                if(org.apache.commons.lang3.StringUtils.isEmpty(deviceInfo.getNetType())){
                    netType = netTypeDef;
                }else{
                    netType = Integer.parseInt(deviceInfo.getNetType());
                }
                if(deviceInfo.getStreamType() == 0){
                    streamType = com.chinatower.platform.constants.Constants.ONE_NUM;
                }else{
                    streamType = deviceInfo.getStreamType();
                }
                if(deviceInfo.getProtocolType() == 0){
                    protocolType = protocolTypeDef;
                }else{
                    protocolType = deviceInfo.getProtocolType();
                }
                map.put(Constants.DEVICECODE, deviceInfo.getDeviceCode());
                map.put(DeviceConstants.CHANNELCODE, channel.getChannelCode());
                map.put(DeviceConstants.STREAM_TYPE, streamType);
                map.put(Constants.STREAM_UUID, IdUtils.fastSimpleUUID());
                map.put(Constants.PROTOCOL_TYPE, protocolType);
                map.put(Constants.NET_TYPE,String.valueOf(netType));
                if(StringUtils.isNotEmpty(deviceInfo.getPlugId())){
                    map.put("plugId",deviceInfo.getPlugId());
                }
                playRequestList.add(map);
            }

        }
        ResultEntity res = this.getVideoRealtimeUrls(playRequestList);
        // 2. 无论 res 是否成功，都尝试处理其 data
        List<?> jsonArray = null;
        if (res != null && res.getData() instanceof List) {
            jsonArray = (List<?>) res.getData();
        } else {
            // 即使失败或 data 无效，也不返回 fail，继续处理
            log.warn("Failed to get valid data from getVideoRealtimeUrls, returnCode: {}, returnMsg: {}, errorMsg: {}",
                    res != null ? res.getReturnCode() : "null",
                    res != null ? res.getReturnMsg() : "null",
                    res != null ? res.getErrorMsg() : "null");
        }

// 3. 遍历返回结果，只处理有效的 JSONObject
        if (jsonArray != null) {
            for (Object o : jsonArray) {
                if (!(o instanceof JSONObject)) {
                    continue;
                }
                JSONObject jsonObject = (JSONObject) o;
                String deviceCode = (String) jsonObject.get(Constants.DEVICECODE);
                String channelCode = (String) jsonObject.get(VideoConstants.CHANNELCODE);
                String streamUrl = (String) jsonObject.get(Constants.STREAM_URL);
                streamUrl = streamUrl != null ? streamUrl : "";
                String returnMsg = (String) jsonObject.get(Constants.RETURN_MSG);

                // 确保 deviceCode 和 channelCode 存在才匹配
                if (deviceCode == null || channelCode == null) {
                    continue;
                }

                // 在 resultDataSet 中查找匹配项并回填
                for (PlayerGetVideoRealtimeUrlData d : resultDataSet) {
                    if (deviceCode.equals(d.getDeviceCode()) &&
                            channelCode.equals(d.getChannelCode())) {
                        d.setStreamUrl(streamUrl); // 可能为 null，表示该设备失败
                        d.setReturnMsg(returnMsg); // 可记录失败原因
                        break;
                    }
                }
            }
        }

        for (PlayerGetVideoRealtimeUrlData data : resultDataSet) {
            if (data.getStreamUrl() == null) {
                data.setStreamUrl("");  // 强制转为空字符串
            }
            if (data.getReturnMsg() == null) {
                data.setReturnMsg("fail");  // 可选：统一消息也处理
            }
        }
// 4. 始终返回 resultDataSet，包含成功和失败的设备（失败的 streamUrl 为 null）
        return Result.data(resultDataSet);
    }

    private List<ChannelInfoVO> getChannelInfoList(DeviceInfoVO deviceInfoVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        deviceInfoVO.setTenantId(user.getTenantId());
        return videoQueryDAO.listChannelInfoForCustDev(deviceInfoVO);
    }


}

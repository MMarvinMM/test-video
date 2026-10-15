package com.chinatower.video.modules.casCade.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.GroupByNodeVO;
import com.chinatower.video.api.entity.VideoConstants;
import com.chinatower.video.modules.casCade.mapper.CasCadeQueryMapper;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.enums.DeviceTableNameEnum;
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

import java.util.*;
import java.util.function.Function;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

@Service
@Slf4j
public class CasCadeQueryServiceImpl implements ICasCadeQueryService {
    @Autowired
    private CasCadeQueryMapper casCadeQueryMapper;
    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public CasCadeVO getCasCadeByDeviceCode(String deviceCode) {
        return getCasCadeByDeviceInfo(deviceCode, com.chinatower.common.core.constant.Constants.DEVICE_TYPE_CAMERA);
    }

    @Override
    public CasCadeVO getCasCadeByDeviceInfo(String deviceCode, Integer deviceType) {
        CasCadeVO casCadeVO = new CasCadeVO();
        String tableName = DeviceTableNameEnum.getTableName(deviceType);
        //获取目标节点编码
        String cascadeToCode = casCadeQueryMapper.queryDeviceLocationCodeByCode(deviceCode, tableName);

        casCadeVO.setCascadeToCode(cascadeToCode);
        //获取来源节点编码,即本节点
        casCadeVO.setPlatformCode(this.getPlatformCode());
        return casCadeVO;
    }

    @Override
    public String getCurrentPlatformCode() {
        return getPlatformCode();
    }

    @Override
    public List<CasCadeVO> getCasCadeByDeviceCodeBatch(List<String> list) {
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        //获取来源节点编码,即本节点
        String platformCode = this.getPlatformCode();
        //获取目标节点编码
        List<CasCadeVO> result = casCadeQueryMapper.getCasCadeByDeviceCodeBatch(list);
        for (CasCadeVO cadeByDeviceCodeBatch : result) {
            cadeByDeviceCodeBatch.setPlatformCode(platformCode);
        }
        return result;
    }

    @Override
    public CasCadeVO getCasCadeByTaskId(String taskId) {
        CasCadeVO casCadeVO = new CasCadeVO();
        //获取目标节点编码
        String cascadeToCode = casCadeQueryMapper.getCasCadeByTaskId(taskId);
        casCadeVO.setCascadeToCode(cascadeToCode);
        //获取来源节点编码,即本节点
        casCadeVO.setPlatformCode(this.getPlatformCode());
        return casCadeVO;
    }

    @Override
    public List<CasCadeVO> getCasCadeByChannelCodeBatch(List<String> list) {
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        //获取来源节点编码,即本节点
        String platformCode = this.getPlatformCode();
        //获取目标节点编码
        List<CasCadeVO> result = casCadeQueryMapper.getCasCadeByChannelCodeBatch(list);
        for (CasCadeVO cadeByDeviceCodeBatch : result) {
            cadeByDeviceCodeBatch.setPlatformCode(platformCode);
        }
        return result;
    }

    @Override
    public Map<String,String> getCasCadeMapByDeviceCodeBatch(List<String> list){
        if(CollectionUtils.isEmpty(list)){
            return new HashMap<>();
        }
        Map<String,String> map = new HashMap<>();
        //获取目标节点编码
        List<CasCadeVO> result = casCadeQueryMapper.getCasCadeByDeviceCodeBatch(list);
        for (CasCadeVO vo : result) {
            map.put(vo.getDeviceCode(),vo.getCascadeToCode());
        }
        return map;
    }

    @Override
    public Map<String,String> getCasCadeMapByChannelCodeBatch(List<String> list){
        if(CollectionUtils.isEmpty(list)){
            return new HashMap<>();
        }
        Map<String,String> map = new HashMap<>();
        //获取目标节点编码
        List<CasCadeVO> result = casCadeQueryMapper.getCasCadeByChannelCodeBatch(list);
        for (CasCadeVO vo : result) {
            map.put(vo.getChannelCode(),vo.getCascadeToCode());
        }
        return map;
    }


    private String getPlatformCode(){
        String platformCode = "";
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
        if(!ObjectUtils.isEmpty(config)) {
            platformCode = config.getConfigValue();
        }else{
            //获取来源节点编码,即本节点
            AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.PLATFORM_CODE);
            if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)){
                    platformCode = sysConfig.getConfigValue();
                }
            }
        }
        return platformCode;
    }

    /**
     * @return [{"key": "node1","value":["code1","code2"]},{"key": "node2","value":["code3","code4"]}]
     */
    public List<Map> groupByNode(GroupByNodeVO groupByNodeVO){
        List params = groupByNodeVO.getParams();
        String itemExpression = groupByNodeVO.getItemExpression();
        Function<List<String>,Map<String,String>> batchQuery = groupByNodeVO.getBatchQuery();
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
        Map<String,String> targetNodeMap = batchQuery.apply(targetList);

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
}

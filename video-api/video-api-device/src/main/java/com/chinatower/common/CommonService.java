package com.chinatower.common;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * video-video公共服务类
 * Created by zhujun on 2022/1/14.
 */
@Service
@Slf4j
public class CommonService {
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

}

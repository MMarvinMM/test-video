package com.chinatower.platform.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.dto.SysOperLogDTO;
import com.chinatower.platform.entity.InterfaceOpeLog;
import com.chinatower.platform.entity.SysOperLogEntity;
import com.chinatower.platform.entity.admin.SysInterfaceInfo;
import com.chinatower.platform.mapper.SysInterfaceConfInfoMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.mongo.OperLogMongoMapper;
import com.chinatower.platform.service.ISysOperLogService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.resp.SysUserResponse;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 操作日志 服务层处理
 *
 * @author chinatower
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysOperLogServiceImpl implements ISysOperLogService {

    @Resource
    private OperLogMongoMapper operLogMapper;

    @Autowired
    private SysInterfaceConfInfoMapper sysInterfaceConfInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String TOWER_INTERFACE_KEYS = "tower_interface_log:";

    public static final String LOG_MATH_OPEN = "log_math_open";

    private static Map<String,String> concurrentLogOpen = new ConcurrentHashMap<>();

    private static Map<String,SysInterfaceInfo> concurrentUri = new ConcurrentHashMap<>();

    private static Map<String,SysInterfaceInfo> concurrentMatchUri = new ConcurrentHashMap<>();

    @Override
    public Boolean insertOperlog(SysOperLogEntity operLog) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.nonNull(loginUser) && Objects.nonNull(loginUser.getUser())) {
            log.info("获取到当前登录用户：{}", loginUser.getUser().getUserName());
            operLog.setDeptName(loginUser.getUser().getDept().getDeptName());
            operLog.setOperName(loginUser.getUser().getUserName());
            operLog.setUserType(loginUser.getUser().getUserType());
        } else {
            log.info("tower-log insertOperlog 获取不到到当前登录用户");
        }
        operLog.setOperTime(new Date());
        operLog.setOperMonth(DateUtil.format(new Date(),"yyyyMM"));
        operLog.setTraceId(IdUtil.simpleUUID());
        try {
            operLogMapper.insert(operLog, MongoDBType.PRIMARY);
            return true;
        }
        catch (Exception e) {
            log.error("数据插入失败",e.getMessage());
        }
        return false;
    }

    @Override
    public Boolean handleLog(InterfaceOpeLog operLog) {
        //api请求，不登录，从请求头获取客户编码
        String customerCode = operLog.getCustomerCode();
        if(!StringUtils.isEmpty(customerCode)){
            SysUserResponse sysUserIn = new SysUserResponse();
            sysUserIn.setTenantId(customerCode);
            try{
                List<SysUserResponse> apiUserInfo = sysInterfaceConfInfoMapper.getApiUserInfo(sysUserIn);
                if(!CollectionUtils.isEmpty(apiUserInfo)){
                    SysUserResponse sysUserOut = apiUserInfo.get(Constants.ZERO_NUM);
                    operLog.setOperName(sysUserOut.getUserName());
                    operLog.setUserType(sysUserOut.getUserType());
                    operLog.setTenantId(sysUserOut.getTenantId());
                    operLog.setTenantName(sysUserOut.getTenantName());
                }
            }catch (Exception e){
                log.error("获取api用户信息失败",e);
            }
        }

        String logMathOpen = concurrentLogOpen.get(LOG_MATH_OPEN);
        if (StringUtils.isEmpty(logMathOpen) || !logMathOpen.equals(com.chinatower.common.core.constant.Constants.ONE)) {
            return false;
        }
        String uri = operLog.getOperUrl();
        // 内存获取配置，匹配当前uri

        SysInterfaceInfo sysInterfaceInfo = concurrentUri.get(TOWER_INTERFACE_KEYS + uri);;
        log.info("根据key:{},获取的缓存数据:{}", TOWER_INTERFACE_KEYS + uri, sysInterfaceInfo);

        LinkedHashMap<String,String> pathVariables = new LinkedHashMap<>();
        if (Objects.isNull(sysInterfaceInfo)) {
            sysInterfaceInfo = getSysInterfaceInfo(uri, sysInterfaceInfo, pathVariables);
        }
        if (Objects.isNull(sysInterfaceInfo)) {
            return false;
        }
        extractedMethod(operLog,  sysInterfaceInfo, pathVariables);

        return true;
    }

    @Override
    public Page<SysOperLogEntity> selectOperLogList(SysOperLogDTO operLog) {
        //获取分页参数
        Page page = new Page(operLog.getPageNum(), operLog.getPageSize());
        //获取排序参数
        SortEntity sortEntity = operLog.getSortEntity();
        //获取查询参数
        QueryEntity query = operLog.getQueryEntity();
        page.setSortEntity(sortEntity);
        Page<SysOperLogEntity> pageInfo = operLogMapper.findPage(query, page, null, MongoDBType.PRIMARY);
        return pageInfo;
    }


    private SysInterfaceInfo getSysInterfaceInfo(String uri, SysInterfaceInfo sysInterfaceInfo, LinkedHashMap<String, String> pathMap) {
        // 替换掉{}
        String regex = "\\{.*?\\}";
        List<String> pathParams = new ArrayList<>();
        for (Map.Entry<String, SysInterfaceInfo> info : concurrentMatchUri.entrySet()) {
            String tempStr = info.getKey().replace(TOWER_INTERFACE_KEYS, "").replaceAll(regex, "([^/]+)");
            if (uri.matches("^"+tempStr+"$")) {
                log.info("uri match key :{}, key:{}", uri, info.getKey());
                sysInterfaceInfo = info.getValue();
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(info.getKey());
                while (matcher.find()) {
                    log.info("matcher.group():{}",matcher.group());
                    pathParams.add(matcher.group().replace("{", "").replace("}",""));
                }


                Pattern patternValue = Pattern.compile(tempStr);
                Matcher matcherValue = patternValue.matcher(uri);
                if (matcherValue.find()) {
                    log.info("matcher.group(1):{}",matcherValue.group(1));
                    for (int i = 0; i< pathParams.size(); i++) {
                        pathMap.put(pathParams.get(i), matcherValue.group(i+1));
                    }
                }
                break;
            }
        }
        return sysInterfaceInfo;
    }

    private void extractedMethod(InterfaceOpeLog commonLog, SysInterfaceInfo sysInterfaceInfo,Map<String, String> pathVariables) {

        commonLog.setOperTime(new Date());
        commonLog.setTitle(sysInterfaceInfo.getUrlDesc());
        commonLog.setOperateTypeDesc(sysInterfaceInfo.getOperateTypeDesc());

        if (Objects.nonNull(sysInterfaceInfo.getParamsExplain())){
            // 解析参数，组织文字描述
            StringBuilder operateDesc = new StringBuilder();
            JSONArray paramsExplain = sysInterfaceInfo.getParamsExplain();
            log.info("paramsExplain:{}", paramsExplain);
            if (commonLog.getRequestMethod().toUpperCase().equals("GET")) {
                extractedGet(commonLog, operateDesc, paramsExplain, pathVariables);
            } else {
                extractedOther(commonLog, operateDesc, paramsExplain, pathVariables);
            }
        }
        log.info("commonLog:{}", commonLog);
        SysOperLogEntity sysLog = new SysOperLogEntity();
        BeanUtils.copyProperties(commonLog, sysLog);
        sysLog.setOperTime(new Date());
        sysLog.setOperMonth(DateUtil.format(new Date(),"yyyyMM"));
        sysLog.setTraceId(IdUtil.simpleUUID());
        try {
            operLogMapper.insert(sysLog, MongoDBType.PRIMARY);
            log.info("log_sysOper表数据插入成功");
        }
        catch (Exception e) {
            log.error("数据插入失败",e.getMessage());
        }
    }

    private void extractedGet(InterfaceOpeLog commonLog, StringBuilder operateDesc,
                              JSONArray jsonArray, Map<String, String> pathVariables) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotEmpty(commonLog.getOperParam())) {
            map = parse(commonLog.getOperParam());
        }
        // 将 JSON 数组转换为 Java List 集合
        List<Object> list = JSON.parseArray(jsonArray.toJSONString());
        // 遍历集合并打印每个元素
        for (Object obj : list) {
            // 判断当前元素是否为 JSONArray 类型
            // 判断当前元素是否为 JSONObject 类型
            if (obj instanceof JSONObject) {
                JSONObject innerJson = (JSONObject) obj;
                // 参数名称
                String paramName = innerJson.get("name").toString();
                // 参数描述
                String paramDesc = innerJson.get("desc").toString();
                // 参数值
                String paramValue = "";
                if (Objects.nonNull(pathVariables) && StringUtils.isNotEmpty(pathVariables.get(paramName))) {
                    paramValue = pathVariables.get(paramName);
                } else {
                    paramValue = map.get(paramName);
                }
                if (StringUtils.isEmpty(paramValue)) {
                    continue;
                }
                // 参数值是否展示
                Integer isShow = 1;
                Object objShow = innerJson.get("isShow");
                if (Objects.nonNull(objShow)) {
                    isShow = Integer.parseInt(objShow.toString());
                }

                // 参数值的翻译
                String valueDesc = "";
                Object valueObj = innerJson.get("value");
                if (Objects.nonNull(valueObj)) {
                    Map<String, String> valueMap = JSONObject.parseObject(valueObj.toString(), Map.class);
                    valueDesc = valueMap.get(paramValue);
                }
                if (StringUtils.isNotEmpty(valueDesc)) {
                    String appendStr = paramDesc+valueDesc+paramValue+";";
                    if (isShow.intValue() == Constants.ZERO_NUM) {
                        appendStr = paramDesc+valueDesc+";";
                    }
                    operateDesc.append(appendStr);
                } else {
                    String appendStr = paramDesc+paramValue+";";
                    if (isShow.intValue() == Constants.ZERO_NUM) {
                        appendStr = paramDesc+";";
                    }
                    operateDesc.append(appendStr);
                }

            }
        }
        commonLog.setOperateDesc(operateDesc.toString());
    }

    private void extractedOther(InterfaceOpeLog commonLog, StringBuilder operateDesc,
                                JSONArray jsonArray, Map<String, String> pathVariables) {
        Configuration configuration = Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build();
        DocumentContext documentContext = JsonPath.parse(commonLog.getOperParam(), configuration);

        // 将 JSON 数组转换为 Java List 集合
        List<Object> list = JSON.parseArray(jsonArray.toJSONString());
        // 遍历集合并打印每个元素
        for (Object obj : list) {
            // 判断当前元素是否为 JSONArray 类型
            // 判断当前元素是否为 JSONObject 类型
            if (obj instanceof JSONObject) {
                JSONObject json = (JSONObject) obj;
                // 参数名称
                String paramName = json.get("name").toString();
                String type = json.get("type").toString();
                // 参数值
                List<Object> paramValues = new ArrayList<>();
                // 参数值是否展示
                Integer isShow = 1;
                Object objShow = json.get("isShow");
                if (Objects.nonNull(objShow)) {
                    isShow = Integer.parseInt(objShow.toString());
                }
                // 参数描述
                String paramDesc = json.get("desc").toString();
                // 配置的value含义
                Map<String, String> valueMap = new HashMap<>();
                Object valueObj = json.get("value");
                if (Objects.nonNull(valueObj)) {
                    valueMap = JSONObject.parseObject(valueObj.toString(), Map.class);
                }
                // type为1代表多个，数组中获取
                if (Constants.ONE.equals(type)) {
                    paramValues = documentContext.read("$."+paramName, List.class);
                    for (Object paramValueObj : paramValues) {
                        String paramValue = paramValueObj.toString();
                        // 参数值的翻译
                        String valueDesc = valueMap.get(paramValue);
                        if (StringUtils.isNotEmpty(valueDesc)) {
                            String appendStr = paramDesc+valueDesc+paramValue+";";
                            if (isShow.intValue() == Constants.ZERO_NUM) {
                                appendStr = paramDesc+valueDesc+";";
                            }
                            operateDesc.append(appendStr);
                        } else {
                            String appendStr = paramDesc+paramValue+";";
                            if (isShow.intValue() == Constants.ZERO_NUM) {
                                appendStr = paramDesc+";";
                            }
                            operateDesc.append(appendStr);
                        }
                    }
                } else {
                    String paramValue = "";
                    if (Objects.nonNull(pathVariables) && StringUtils.isNotEmpty(pathVariables.get(paramName))) {
                        paramValue = pathVariables.get(paramName);
                    } else if  (Objects.nonNull(documentContext.read("$."+paramName))) {
                        paramValue = documentContext.read("$."+paramName).toString();
                    }
                    if (StringUtils.isEmpty(paramValue)) {
                        continue;
                    }
                    // 参数值的翻译
                    String valueDesc = valueMap.get(paramValue);
                    if (StringUtils.isNotEmpty(valueDesc)) {
                        String appendStr = paramDesc+valueDesc+paramValue+";";
                        if (isShow.intValue() == Constants.ZERO_NUM) {
                            appendStr = paramDesc+valueDesc+";";
                        }
                        operateDesc.append(appendStr);
                    } else {
                        String appendStr = paramDesc+paramValue+";";
                        if (isShow.intValue() == Constants.ZERO_NUM) {
                            appendStr = paramDesc+";";
                        }
                        operateDesc.append(appendStr);
                    }
                }
            }
        }
        commonLog.setOperateDesc(operateDesc.toString());
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void memoryLoad(){
        log.info("url 配置加载到内存中");
        String logMathOpen = RedisUtil.get(LOG_MATH_OPEN, RedisDBType.PRIMARY);
        if (StringUtils.isNotEmpty(logMathOpen) && logMathOpen.equals(com.chinatower.common.core.constant.Constants.ONE)){
            concurrentLogOpen.put(LOG_MATH_OPEN, logMathOpen);
            // 修改为从mysql中查询
            LambdaQueryWrapper<SysInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysInterfaceInfo::getDeleteFlag, Constants.ZERO_NUM);
            List<SysInterfaceInfo> all = sysInterfaceConfInfoMapper.selectList(queryWrapper);
            for(SysInterfaceInfo info : all) {
                concurrentUri.put(TOWER_INTERFACE_KEYS+info.getUrl(), info);
                if (info.getUrl().contains("{")) {
                    concurrentMatchUri.put(TOWER_INTERFACE_KEYS+info.getUrl(), info);
                }
            }
        }

    }

    public static Map<String, String> parse(String paramStr) {
        Map<String, String> paramMap = new HashMap<>();
        String[] params = paramStr.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length != 2) {
                continue;
            }
            paramMap.put(pair[0], pair[1]);
        }

        return paramMap;
    }

}

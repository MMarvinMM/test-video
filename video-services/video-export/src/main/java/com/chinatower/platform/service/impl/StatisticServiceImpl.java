package com.chinatower.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.StatisticMapper;
import com.chinatower.platform.mapper.SysUserMapper;
import com.chinatower.platform.mapper.WorkbenchMapper;
import com.chinatower.platform.service.StatisticService;
import com.chinatower.platform.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    @Resource
    private StatisticMapper statisticDAO;
    @Resource
    private CommonUtils commonUtils;
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 通道算法任务排行
     */
    @DataScope(dbType = EbikeDataBaseConstant.MASTER,tenantColumn="dataTable.tenant_id",industryCode="dataTable.industry_code",deviceColumn = "dataTable.device_code",acceptColumn="od.accept_node")
    public List<Map<String,String>> getAllAlgorithmTaskRank(AnalysisVO analysisVO){
        //处理传入的组织条件
        commonUtils.handleOrgIdDeviceList(analysisVO,null);
        if(CollectionUtils.isEmpty(analysisVO.getDeviceCodeList()) && !CollectionUtils.isEmpty(analysisVO.getOrgIdList())){
            return new ArrayList<>();
        }
        List<Map<String,String>> deviceStatisticsVOList  = statisticDAO.getAllAlgorithmTaskRank(analysisVO);
        log.info("getAllAlgorithmTaskRank result:{} ", JSON.toJSONString(deviceStatisticsVOList));
        return deviceStatisticsVOList;
    }

    /**
     * 用户登录时长及次数排行
     * @param customVO
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.MASTER,tenantColumn="dataTable.tenant_id", deptColumn = "dataTable.dept_id")
    public List<Map> getLoginDurationRankAndNum(CustomVO customVO) {
        List<Map> loginDurationMap = statisticDAO.getLoginDurationRankAndNum(customVO);
        SysUser users = new SysUser();
        List<SysUser> userList = sysUserMapper.selectUser(users);
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(loginDurationMap)) {
            for (Map map : loginDurationMap) {
                //登录名，租户名为空赋值
                if ("".equals(map.get("tenantName"))){
                    for (SysUser user : userList) {
                        if (map.get("userId").equals(user.getUserId())){
                            map.put("tenantName",user.getTenantName());
                            map.put("loginUserName",user.getUserName());
                        }
                    }
                }
                if (map.get(Constants.ONLINE_TIME) == null || "".equals(map.get(Constants.ONLINE_TIME))){
                    map.put(Constants.LOGIN_DURATION, "");
                }else {
                    long seconds = Long.parseLong(map.get(Constants.ONLINE_TIME).toString());
                    long days = TimeUnit.SECONDS.toDays(seconds);
                    long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);
                    long minutes = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
                    if (days == 0) {
                        if (hours == 0) {
                            map.put(Constants.LOGIN_DURATION, minutes + "分");
                        } else {
                            map.put(Constants.LOGIN_DURATION, hours + "时" + minutes + "分");
                        }
                    } else {
                        map.put(Constants.LOGIN_DURATION, days + "天" + hours + "时" + minutes + "分");
                    }
                }

            }
        }
        return loginDurationMap;
    }
}

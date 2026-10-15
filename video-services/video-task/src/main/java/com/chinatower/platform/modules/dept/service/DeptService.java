package com.chinatower.platform.modules.dept.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.modules.dept.dao.DeptDAO;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户定时
 */
@Component
@Slf4j
public class DeptService {
    @Resource
    private DeptDAO deptDAO;

    /**
     * 同步更新组织管理区域定时任务
     */
    @XxlJob("dealDeptStreetHandler")
    @DS("base")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> dealDeptStreetHandler(String param) {
        log.info("同步更新组织管理区域定时任务");
        XxlJobLogger.log("同步更新组织管理区域定时任务");
        //组织设备表vm_organ_device_rel_t表和别的基本信息表关联，查出租户id,行业编码，版本编码，组织id，省市区，乡镇编码，去重
        List<SysDeptStreetRelVO> deptStreetList = deptDAO.getOrganDeviceRelAll();
        if(!CollectionUtils.isEmpty(deptStreetList)){
            int deptStreetRelAllNum = deptDAO.deleteDeptStreetRelAll();
            XxlJobLogger.log("删除的组织管理区域个数"+ deptStreetRelAllNum);
            XxlJobLogger.log("deptStreetList为"+ JSONObject.toJSONString(deptStreetList));
            List<List<SysDeptStreetRelVO>> partitionList = Lists.partition(deptStreetList, 5000);
            partitionList.forEach(item ->
                    deptDAO.insertBatchDeptStreetRelTenantAll(item)
            );
        }
        return ReturnT.SUCCESS;
    }

}

package com.chinatower.platform.deal;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.mapper.AuthBaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantForDBTypeDeal {
    @Autowired(required = false)
    public AuthBaseMapper authBaseMapper;

//    @DS("#dbType")
//    public List<String> getDevideCodesDB(String appVerCode, String tenantId, String deptIdStr, String dbType) {
//        return authBaseMapper.getDevideCodes(appVerCode,tenantId,deptIdStr);
//    }

//    @DS("#dbType")
//    public List<String> getPersonCodesDB(String appVerCode, String tenantId, String personDeptIdStr, String dbType) {
//        return authBaseMapper.getPersonCodes(appVerCode,tenantId,personDeptIdStr);
//    }

//    @DS("#dbType")
//    public List<String> getSensorCodesDB(String appVerCode, String tenantId, String deptIdStr, String dbType) {
//        return authBaseMapper.getSensorCodes(appVerCode,tenantId,deptIdStr);
//    }

//    @DS("#dbType")
//    public List<String> getSatelliteCodesDB(String appVerCode, String tenantId, String deptIdStr, String dbType) {
//        return authBaseMapper.getSatelliteCodes(appVerCode,tenantId,deptIdStr);
//    }
}

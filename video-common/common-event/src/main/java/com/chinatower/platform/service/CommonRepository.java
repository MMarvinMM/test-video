package com.chinatower.platform.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 16:27
 */
public interface CommonRepository {


    /**
     * 调用华苏接口返回又权限用户
     * @param userIdList
     * @param tenantId
     * @param deviceCode
     * @param industryCode
     * @param appVerCode
     * @param alarmSource
     * @param cityId
     * @param countyId
     * @param provinceId
     * @return
     */
    Result<List<SysUser>> getUserByDeptAndDeviceScope(List<String> userIdList, String tenantId,
                                                      String deviceCode, String industryCode,
                                                      String appVerCode, String alarmSource,
                                                      String cityId, String countyId, String provinceId,String alarmTypeId);


}

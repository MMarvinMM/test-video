package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.PushThirdDto;
import com.chinatower.framework.common_service.response.ProcessResult;
import com.chinatower.platform.entity.admin.SysUser;

import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: ICommonRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 21:19
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface ICommonRepository {

    /**
     * 第三方推送，异步调用
     * @param
     * @return
     */
//    Result pushThird(Map map);

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
    Result<List<SysUser>> getUserByDeptAndDeviceScope(List<String> userIdList,String tenantId,
                                                      String deviceCode,String industryCode,
                                                      String appVerCode,String alarmSource,
                                                      String cityId,String countyId,String provinceId,String alarmTypeId);


    /**
     * 告警上报推送第三方告警
     * @param dto
     * @return
     */
    public ProcessResult pushThird(PushThirdDto dto);

    //public void pushThirdOrder(String originalJson,List<Map> alarmList);
}

package com.chinatower.airline.mapper;

import com.chinatower.airline.dto.request.AirPwdRes;
import com.chinatower.airline.dto.request.RoadsNameMilepostReq;
import com.chinatower.device.entity.dto.AirPwdReq;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.platform.annotation.DataScope;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2025/4/15 11:13
 */
@Mapper
public interface AirConfigMapper {

    /**
     * 查询
     * @param airPwdReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    AirPwdRes getUavPwdConfig(AirPwdReq airPwdReq);

    /**
     * 查询
     * @param airPwdReq
     * @return
     */
    String getUavAdminName(AirPwdReq airPwdReq);

    /**
     * 查询无人机编码
     * @param deviceCode
     * @return
     */
    String getUavCodeByDeviceCode(String deviceCode);

    /**
     * 获取经纬度对应的桩号
     * @param roadsNameMilepostReq
     * @return
     */
    String queryMilepostByLocation(RoadsNameMilepostReq roadsNameMilepostReq);

    /**
     * 获取无人机型号
     * @param uavCodeList
     * @return
     */
    List<Map<String, String>> queryModelCodeByUavCode(List<String> uavCodeList);

    UavDeviceDTO queryUavCameraInfoByModelCode(String uavModelCode);
}

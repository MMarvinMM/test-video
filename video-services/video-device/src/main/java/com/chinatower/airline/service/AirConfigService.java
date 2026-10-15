package com.chinatower.airline.service;

import com.chinatower.airline.dto.request.AIrCarReq;
import com.chinatower.airline.dto.request.RoadsNameMilepostReq;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AirPwdReq;
import com.chinatower.device.entity.vo.FlightRadiusVo;
import com.chinatower.device.entity.vo.UavDetailVO;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/5/20 9:43
 */
public interface AirConfigService {

    /**
     *
     * @param airPwdReq
     * @return
     */
    Result getUavPwdConfig(AirPwdReq airPwdReq);

    /**
     *
     * @param airPwdReq
     * @return
     */
    Result checkUavPwd(AirPwdReq airPwdReq);

    /**
     *
     * @param deviceCode
     * @return
     */
    boolean checkUavCache(String deviceCode);


    /**
     *
     * @param roadsNameMilepostReqList
     * @return
     */
    Result queryRoadsNameMilepost(List<RoadsNameMilepostReq> roadsNameMilepostReqList);

    /**
     *
     * @param uavCodeList
     * @return
     */
    Result<List<FlightRadiusVo>> queryFlightRadiusByUavCode(List<String> uavCodeList);


    Result<UavDetailVO> queryUavCameraInfoByModelCode(AIrCarReq aIrCarReq);
}

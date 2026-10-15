package com.chinatower.event.mapper;

import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dto.AppOrderUpdateDto;
import com.chinatower.event.vo.ParkDeviceInfoReq;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.entity.vo.WarningOrderListVO;

@Mapper
public interface AlarmOrderAppMapper {

    @DS(EbikeDataBaseConstant.PLATFORM)
    int getOrderListCount(@Param("param") AppOrderListByUserDTO param);

    /**
     * 获取订单列表
     *
     * @param param
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<WarningOrderListVO> getOrderList(@Param("param") AppOrderListByUserDTO param);

    /**
     * 更新订单告警类型信息
     *
     * @param dto
     * @return
     */
    int updateOrderAlarmType(AppOrderUpdateDto dto);

    /**
     * 功能描述 根据租户code和摄像机获取人流摄像机信息
     * @author yhb
     * @date 2022/4/8
     * [tenantCode]
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="b.device_code",tenantColumn="b.tenant_id",
            industryCode="b.industry_code", alarmSourceColumn="b.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "b.province_code"
            ,areaCity = "b.city_code",areaCode = "b.county_code")
    List<ReceiveAlarmVO> getOrderAlarmByTenantCode(ParkDeviceInfoReq req);

}

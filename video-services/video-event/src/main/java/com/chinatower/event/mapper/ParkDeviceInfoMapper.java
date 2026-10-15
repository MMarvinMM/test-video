package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.vo.ParkDeviceInfoReq;
import com.chinatower.event.vo.ParkDeviceInfoVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface ParkDeviceInfoMapper {


    List<ParkDeviceInfoVo> selectVmParkDeviceInfoList(ParkDeviceInfoReq req);

    Integer selectVmParkDeviceInfoListCount(ParkDeviceInfoReq req);

    int insertVmParkDeviceInfo(ParkDeviceInfoVo vmParkDeviceInfo);

    List<ParkDeviceInfoVo> selectVmParkDeviceInfoCount(ParkDeviceInfoReq req);

    int updateVmParkDeviceInfo(ParkDeviceInfoVo vmParkDeviceInfo);
    int updateVmParkDeviceInfoRe(ParkDeviceInfoVo vmParkDeviceInfo);
}

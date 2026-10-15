package com.chinatower.event.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 物联设备表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@DS(EbikeDataBaseConstant.BASE)
public interface IotDeviceMapper {

    /**
     * 条件查询雷达设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<IotDeviceVO> queryRadarListPage(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 条件查询物联设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<IotDeviceVO> queryIotDeviceList(@Param("param") IotDeviceVO deviceInfoVO);



}

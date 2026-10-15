package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.VmDeviceIotRelDO;
import com.chinatower.device.dto.IotDeviceConfigVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lzt
 */
public interface IotDeviceConfigMapper extends BaseMapper<VmDeviceIotRelDO> {


    /**
     *  获取物联设备置列表
     * @param iotDeviceConfigVO
     * @return
     */
    List<IotDeviceConfigVO> queryIotDeviceConfigList(@Param("param") IotDeviceConfigVO iotDeviceConfigVO);

    /**
     * 查询设备订购有效数量
     * @param iotDeviceConfigVO
     * @return
     */
    int getOrderIotDeviceNum(@Param("param") IotDeviceConfigVO iotDeviceConfigVO);

    /**
     * 查询物联设备配置有效数量
     * @param iotDeviceConfigVO
     * @return
     */
    int getIotDeviceConfigNum(@Param("param") IotDeviceConfigVO iotDeviceConfigVO);

    /**
     * 更新物联设备配置
     * @param iotDeviceConfigVO
     * @return
     */
    void updateIotDeviceConfig(@Param("param") IotDeviceConfigVO iotDeviceConfigVO);

    IotDeviceConfigVO getIotDeviceConfig(@Param("param") IotDeviceConfigVO iotDeviceConfigVO);

    /**
     * 查询是否是二次绑定
     * @param iotDeviceConfigVO
     * @return
     */
    int getSecondIotDeviceConfig(@Param("param") IotDeviceConfigVO iotDeviceConfigVO);

}

package com.chinatower.platform.consumer.dao;

import com.chinatower.outdata.api.entity.DeviceAccessNodeDTO;
import com.chinatower.outdata.api.entity.DeviceAccessNodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 喇叭管理
 */
@Mapper
public interface UavDeviceDAO {

    /**
     * 查询摄像机接入节点信息
     * @param dto 请求参数
     * @return 摄像机接入节点信息
     */
    List<DeviceAccessNodeVO> queryDeviceAccessNodeInfoList(@Param("param")DeviceAccessNodeDTO dto);
}

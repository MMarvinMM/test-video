package com.chinatower.sync.consumer.dao;

import com.chinatower.device.entity.dto.VmHornTaskConfDO;
import com.chinatower.device.entity.dto.VmHornTaskDiffNodeRelDO;
import com.chinatower.sync.consumer.item.HornTaskStatusReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HornTaskConfDAO {

    void updateHornTaskStatus(@Param("list") List<VmHornTaskConfDO> li);

    void updateHornTaskRelStatus(@Param("list") List<HornTaskStatusReq> li);

    //通过广播平台的taskId查关联表
    List<VmHornTaskDiffNodeRelDO> selectVmHornTaskDiffNodeRelByHornTaskId(@Param("hornTaskIds") List<HornTaskStatusReq> hornTaskIds);
}

package com.chinatower.device.mapper;

import com.chinatower.device.entity.vo.DeviceDiyTreeResultVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeVO;

import java.util.List;

public interface DeviceDiyTreeMapper {
    void saveDiy(DeviceDiyTreeVO deviceInfoVO);
    void saveDiyNode(DeviceDiyTreeVO deviceInfoVO);
    void updateDiy(DeviceDiyTreeVO deviceInfoVO);
    void delDiy(DeviceDiyTreeVO deviceInfoVO);
    void delDiyNode(DeviceDiyTreeVO deviceInfoVO);
    List<DeviceDiyTreeVO> getList(DeviceDiyTreeVO deviceInfoVO);
    List<DeviceDiyTreeResultVO> getOriginalDiyTree(DeviceDiyTreeVO deviceInfoVO);
    DeviceDiyTreeVO getOne(DeviceDiyTreeVO deviceInfoVO);
    List<DeviceDiyTreeResultVO> getDiyTree(DeviceDiyTreeResultVO deviceInfoVO);
    List<DeviceDiyTreeResultVO> getAllDiyTree(DeviceDiyTreeResultVO deviceInfoVO);
    List<DeviceDiyTreeResultVO> getAlias (List<DeviceDiyTreeResultVO> list,String tenantId,String industryCode);
}

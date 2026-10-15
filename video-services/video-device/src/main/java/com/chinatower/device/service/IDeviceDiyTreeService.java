package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.DeviceDiyTreeRequestVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeResultVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeVO;

import java.util.List;

public interface IDeviceDiyTreeService {
    Result save(DeviceDiyTreeVO deviceInfoVO);
    Result update(DeviceDiyTreeVO deviceInfoVO);
    Result updateStatus(DeviceDiyTreeVO deviceInfoVO);
    Result delete(DeviceDiyTreeRequestVO deviceDiyTreeRequestVO);
    Result<List<DeviceDiyTreeVO>> getList(DeviceDiyTreeVO deviceInfoVO);
    Result<List<DeviceDiyTreeResultVO>> getOriginalDiyTree(DeviceDiyTreeVO deviceInfoVO);
    Result<List<DeviceDiyTreeResultVO>> getDiyTree(DeviceDiyTreeResultVO deviceInfoVO);
}

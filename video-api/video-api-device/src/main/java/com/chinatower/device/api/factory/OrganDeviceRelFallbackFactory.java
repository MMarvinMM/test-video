package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IOrganDeviceRelService;
import com.chinatower.platform.entity.device.DeviceOrgRelByTenantIdDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName OrganDeviceRelFallbackFactory
 * @Deacription 设备组织关系服务降级处理
 * @Author chenmf
 * @Date 2022/2/11 14:51
 * @Version 1.0
 **/
@Component
public class OrganDeviceRelFallbackFactory implements FallbackFactory<IOrganDeviceRelService> {
    
    
    @Override
    public IOrganDeviceRelService create(Throwable throwable) {
        
        return new IOrganDeviceRelService() {
    
    
            @Override
            public Result batchDelDeviceRela(DelDeviceRelaDTO delDeviceRelaDTO) {
                return Result.fail("批量删除设备组织关系失败:" + throwable.getMessage());
            }
    
            @Override
            public Result batchSaveDeviceRela(List<OrganDeviceRelVO> organDeviceRelList) {
                return Result.fail("批量保存设备组织关系失败:" + throwable.getMessage());
            }
    
            @Override
            public Result<List<String>> getDeptNoDeviceCode(DelDeviceRelaDTO delDeviceRelaDTO) {
                return Result.fail("查询组织下没有设备的列表失败:" + throwable.getMessage());
            }
    
            @Override
            public Result<List<String>> updateDeptStreetRel(DeviceOrgRelByTenantIdDTO deviceOrgRelByTenantIdDTO) {
                return Result.fail("更新组织行政区域表失败:" + throwable.getMessage());
            }
        };
    }
}

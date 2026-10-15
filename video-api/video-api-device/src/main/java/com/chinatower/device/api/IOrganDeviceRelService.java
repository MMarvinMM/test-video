package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.device.DeviceOrgRelByTenantIdDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.device.api.factory.OrganDeviceRelFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "organDeviceRelService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = OrganDeviceRelFallbackFactory.class)
public interface IOrganDeviceRelService {

    @PostMapping("/video/device/batchDelDeviceRela")
    Result batchDelDeviceRela(@RequestBody DelDeviceRelaDTO delDeviceRelaDTO);

    @PostMapping("/video/device/batchSaveDeviceRela")
    Result batchSaveDeviceRela(@RequestBody List<OrganDeviceRelVO> organDeviceRelList);

    /**
     * 查询组织下没有设备的列表
     * @apiNote 查询组织下没有设备的列表
     * @apiVersion 3.25
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * data [部门id]
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694676176276,
     *     "data":[
     *         "122035807-01",
     *         "122035807-02"
     *     ]
     * }
     */
    @PostMapping("/video/device/getDeptNoDeviceCode")
    Result<List<String>> getDeptNoDeviceCode(@RequestBody DelDeviceRelaDTO delDeviceRelaDTO);

    /**
     * 更新组织行政区域表（先删后增）
     * @apiNote 更新组织行政区域表（先删后增）
     * @apiVersion 3.26
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"批量保存设备组织关系成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/device/updateDeptStreetRel")
    Result<List<String>> updateDeptStreetRel(@RequestBody DeviceOrgRelByTenantIdDTO deviceOrgRelByTenantIdDTO);
}

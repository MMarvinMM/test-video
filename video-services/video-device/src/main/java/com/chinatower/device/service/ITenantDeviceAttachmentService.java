package com.chinatower.device.service;

import java.util.List;
import java.util.Map;

public interface ITenantDeviceAttachmentService {

    /**
     * 查询批量设备编码文件信息
     * @param deviceCodeList
     * @param deviceType
     * @return
     */
    Map<String, List<String>> getFileUrlByDeviceCodeList(String tenantId, String industryCode, List<String> deviceCodeList, int deviceType);
}

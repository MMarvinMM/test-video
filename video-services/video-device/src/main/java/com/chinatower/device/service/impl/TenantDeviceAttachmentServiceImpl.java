package com.chinatower.device.service.impl;

import com.chinatower.device.dto.DeviceAttachmentFileUrlVO;
import com.chinatower.device.mapper.TenantDeviceAttachmentMapper;
import com.chinatower.device.service.ITenantDeviceAttachmentService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TenantDeviceAttachmentServiceImpl implements ITenantDeviceAttachmentService {

    @Resource
    private TenantDeviceAttachmentMapper tenantDeviceAttachmentMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 根据设备编号查询上传的附件信息
     * @param deviceCodeList
     * @param deviceType
     * @return
     */
    @Override
    public Map<String, List<String>> getFileUrlByDeviceCodeList(String tenantId, String industryCode, List<String> deviceCodeList, int deviceType) {
        Map<String, List<String>> fileUrlMap = new HashMap<>(deviceCodeList.size());
        //查询上传的附件信息
        List<DeviceAttachmentFileUrlVO> tenantDeviceAttachmentVOList = tenantDeviceAttachmentMapper.selectFileUrlByDeviceCodeList(tenantId, industryCode,
                deviceCodeList, deviceType);
        if (CollectionUtils.isEmpty(tenantDeviceAttachmentVOList)) {
            return null;
        }
        try {
            List<String> fileUrlList = Lists.newArrayList();
            for (DeviceAttachmentFileUrlVO tenantDeviceAttachmentVO : tenantDeviceAttachmentVOList) {
                fileUrlList.addAll(tenantDeviceAttachmentVO.getFileUrlList());
            }
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
            // 处理Obs文件URL加密返回
            for (DeviceAttachmentFileUrlVO tenantDeviceAttachmentVO : tenantDeviceAttachmentVOList) {
                if (!CollectionUtils.isEmpty(tenantDeviceAttachmentVO.getFileUrlList())) {
                    for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                        if (tenantDeviceAttachmentVO.getFileUrlList().contains(entry.getKey())) {
                            if (fileUrlMap.containsKey(tenantDeviceAttachmentVO.getDeviceCode())) {
                                fileUrlMap.get(tenantDeviceAttachmentVO.getDeviceCode()).add(entry.getValue());
                            }
                            else {
                                List<String> encryptUrlList = Lists.newArrayList();
                                encryptUrlList.add(entry.getValue());
                                fileUrlMap.put(tenantDeviceAttachmentVO.getDeviceCode(), encryptUrlList);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("TenantDeviceAttachmentServiceImpl getFileUrlByDeviceCode error, e:", e);
        }
        return fileUrlMap;
    }
}

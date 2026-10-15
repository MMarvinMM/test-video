package com.chinatower.platform.consumer.service;


import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.DeviceAccessNodeDTO;
import com.chinatower.outdata.api.entity.DeviceAccessNodeVO;
import com.chinatower.platform.consumer.dao.UavDeviceDAO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class UavDeviceService {

    @Resource
    private UavDeviceDAO uavDeviceDAO;

    /**
     * 查询摄像机接入节点信息
     * @param dto
     * @return
     */
    public Result<List<DeviceAccessNodeVO>> queryDeviceAccessNodeInfoList(DeviceAccessNodeDTO dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getDeviceCodeList())) {
            throw new CustomException("必填参数不能为空");
        }
        if (dto.getDeviceCodeList().size() > 500) {
            throw new CustomException("参数设备列表最多传500个");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        dto.setTenantId(loginUser.getUser().getTenantId());
        dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        return Result.data(uavDeviceDAO.queryDeviceAccessNodeInfoList(dto));
    }
}

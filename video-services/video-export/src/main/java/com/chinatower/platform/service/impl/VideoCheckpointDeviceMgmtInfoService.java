package com.chinatower.platform.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import com.chinatower.platform.mapper.CheckpointDeviceInfoExportMapper;
import com.chinatower.platform.service.IVideoCheckpointDeviceMgmtInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author：zl
 * @Description：卡口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__卡口管理
 */
@Service
@Slf4j
public class VideoCheckpointDeviceMgmtInfoService extends BaseController implements IVideoCheckpointDeviceMgmtInfoService {

    @Autowired(required = false)
    private CheckpointDeviceInfoExportMapper checkpointDeviceInfoMapper;


    /**
     * 功能描述：导出卡口设备数据  增加industryCode appVerCode
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @return List<CheckpointDeviceExportVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    @DataScope(tenantColumn = "m.tenant_id", acceptColumn="t.accept_node", industryCode = "m.industry_code",deviceColumn = "d.device_code",alarmSourceVal = "4")
    public List<CheckpointDeviceExportVO> queryCheckpointDeviceExportList(CheckpointDeviceInfoVO checkpointDeviceInfoVO) {
        log.info("导出卡口设备数据---请求接口报文------" + JSONObject.toJSONString(checkpointDeviceInfoVO));
        List<CheckpointDeviceExportVO> checkpointDeviceExportVOS = new ArrayList<>();
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            checkpointDeviceInfoVO.setTenantId(user.getTenantId());
            checkpointDeviceInfoVO.setIndustryCode(user.getIndustryCode());
            checkpointDeviceExportVOS = checkpointDeviceInfoMapper.queryCheckpointDeviceExportList(checkpointDeviceInfoVO);
        } catch (DataAccessException e) {
            log.error("导出卡口租户设备信息异常：",e);
            throw new CustomException("导出卡口租户设备信息异常");
        }
        return checkpointDeviceExportVOS;
    }

}

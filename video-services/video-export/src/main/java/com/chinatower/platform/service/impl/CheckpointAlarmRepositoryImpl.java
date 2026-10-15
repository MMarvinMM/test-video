package com.chinatower.platform.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.common.core.vo.CheckpointAlarmExportVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.mapper.CheckpointAlarmExportMapper;
import com.chinatower.platform.service.ICheckpointAlarmRepository;
import com.chinatower.platform.utils.JNConstants;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.chinatower.common.core.constant.Constants.SYS_DICT_KEY;

/**
 * @Author：zl
 * @Description：出入口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__出入口管理
 */
@Slf4j
@Service
public class CheckpointAlarmRepositoryImpl implements ICheckpointAlarmRepository {

    @Autowired
    private CheckpointAlarmExportMapper checkpointAlarmMapper;


    /**
     * 功能描述：导出车辆进出记录
     * @Author：zl
     * @param dto
     * @return List<CheckpointAlarmDTO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code",deviceColumn = "a.device_code",alarmSourceVal = "4")
    public List<CheckpointAlarmExportVO> exportCheckpointAlarmList(CheckpointAlarmDTO dto) {
//        dto.setDeviceCodes(getCheckpointDeviceList(dto));
        List<SysDictData> sysDictDataList = RedisUtil.getList(SYS_DICT_KEY+ JNConstants.SYS_VEHICLE_TYPES, SysDictData.class, RedisDBType.PRIMARY);
        if(CollUtil.isEmpty(sysDictDataList)){
            sysDictDataList = checkpointAlarmMapper.getVehicleTypeDict(JNConstants.SYS_VEHICLE_TYPES);
            RedisUtil.set(SYS_DICT_KEY+JNConstants.SYS_VEHICLE_TYPES, sysDictDataList, RedisDBType.PRIMARY);
        }
        dto.setSysDictDataList(sysDictDataList);
        log.info("导出车辆进出记录---请求接口报文------" + JSONObject.toJSONString(dto));
        return checkpointAlarmMapper.exportCheckpointAlarmList(dto);
    }

}

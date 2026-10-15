package com.chinatower.event.dto.ThreadDto;

import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.vo.InterceptVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InterceptOrderResultDto {
    private List<DeviceInfoVO> deviceInfoList;
    private List<AlarmLableRelDTO> alarmLableAllList;
    private List<Map> alarmTypeList;
    private List<Map> alarmIconList;
    private List<SysDictDataResponse> importanceLevelEnum;
    private List<SysDictDataResponse> dictData;
    private List<SysDictDataResponse> suppressDictData;
    private List<SysDictDataResponse> warningSourceEnum;
    private InterceptVO orderInfoVO;
}

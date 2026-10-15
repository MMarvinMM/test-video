package com.chinatower.event.dto.ThreadDto;

import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.entity.dto.OrderLeaderNumDTO;
import com.chinatower.event.entity.vo.HandleRecordVO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WarningOrderResultDto {

    private AlarmOrderDTO alarmOrderListReq;
    private String userId;
    private String flowLinkIds;
    private List<OrderLeaderNumDTO> auditNum;
    private List<Map> alramTypeList;
    private List<SysDictDataResponse> emergencyLevelEnum;
    private List<SysDictDataResponse> orderStatusEnum;
    private List<SysDictDataResponse> noticeResourceEnum;
    private List<SysDictDataResponse> warningSourceEnum;
    private List<SysDictDataResponse> importanceLevelEnum;
    private List<WarningOrderListVO> warningOrderCollectionList;
    private List<HandleRecordVO> handleRecordList;
    private List<Map<String, String>> orgInfoList;
    private WarningOrderListVO vo;
}

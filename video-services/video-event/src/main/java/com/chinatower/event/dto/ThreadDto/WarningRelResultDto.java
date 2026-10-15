package com.chinatower.event.dto.ThreadDto;

import com.chinatower.event.entity.dto.WarningOrderRelDTO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.WarningOrderInfoVO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WarningRelResultDto {

    private WarningOrderRelDTO dto;
    private List<WarningOrderInfoVO> warningOrderInfoVOList;
    private List<WarningOrderListVO> warningOrderCollectionList;
    private List<SysDictDataResponse> importanceLevelEnum;
    private List<SysDictDataResponse> modelIdEnum;
    private List<SysDictDataResponse> iotModelIdEnum;
    private List<SysDictDataResponse> msgTypeEnum;
    private List<SysDictDataResponse> noticeResourceEnum;
    private List<SysDictDataResponse> emergencyLevelEnum;
    private List<SysDictDataResponse> orderStatusEnum;
    private List<Map> alramTypeList;
    private List<SysDictDataResponse> warningSourceEnum;
    private List<DeviceSiteVO> deviceSiteVOList;
    private OrderInfoVO vo;
}

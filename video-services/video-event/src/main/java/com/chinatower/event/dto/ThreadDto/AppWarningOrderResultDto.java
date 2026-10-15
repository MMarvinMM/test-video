package com.chinatower.event.dto.ThreadDto;

import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.entity.dto.OrderLeaderNumDTO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppWarningOrderResultDto {

    private AppOrderListByUserDTO dto;
    private String flowLinkIds;
    private List<OrderLeaderNumDTO> auditNum;
    private List<Map> alramTypeList;
    private List<SysDictDataResponse> importanceLevelEnum;
    private List<SysDictDataResponse> orderStatusEnum;
    private WarningOrderListVO map;
    private List<UserInfoDto> userList;
}

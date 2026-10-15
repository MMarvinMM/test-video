package com.chinatower.event.dto.ThreadDto;


import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.AppOrderListByUserDTO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.platform.entity.admin.SysUser;
import lombok.Data;

import java.util.List;

@Data
public class AppWarningAlarmOrderDto {
    private AppOrderListByUserDTO dto;
    private TableDataInfo tableDataInfo;
    private int allCount;
    private List<WarningOrderListVO> resultList;
    private AlarmOrderDTO alarmOrderDTO;
}

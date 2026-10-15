package com.chinatower.platform.service;

import com.chinatower.event.entity.dto.AlarmOrderInfoExportDTO;
import com.chinatower.event.entity.dto.ExportOrderInfoReq;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/4 10:36
 */
public interface ExportWarningInfoService {

    AlarmOrderInfoExportDTO getExportWarningDto(ExportOrderInfoReq req);
}

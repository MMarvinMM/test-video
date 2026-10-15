package com.chinatower.emergency.service;

import com.chinatower.common.entity.IResultCode;
import com.chinatower.emergency.dto.Digital;
import com.chinatower.emergency.dto.DigitalQueryVO;
import com.chinatower.emergency.dto.DigitalStage;
import com.chinatower.emergency.dto.DigitalTask;

import java.util.List;

public interface DigitalService {

    void save(Digital digital);

    void update(Digital digital);

    Digital findById(Digital digital);

    List<Digital> list(DigitalQueryVO digital);

    void deleteBatch(List<String> ids);

    void digitalImportEmergency(Digital digital);

    List<DigitalStage> findStage(DigitalQueryVO digital);

    List<DigitalTask> findTasksByStage(DigitalQueryVO digital);

    String checkDigitalImportEmergency(Digital digital);
}

package com.chinatower.platform.mapper;

import com.chinatower.bdm.api.entity.InspectionConfParam;
import com.chinatower.bdm.api.entity.InspectionTaskListVO;
import com.chinatower.bdm.api.entity.InspectionTaskParam;
import com.chinatower.platform.entity.InspectionConfVO;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface BdmInspectionExportMapper {

    List<InspectionTaskListVO> getTaskList(InspectionTaskParam vo);

    List<InspectionConfVO> getConfList(InspectionConfParam inspectionConfParam);

}

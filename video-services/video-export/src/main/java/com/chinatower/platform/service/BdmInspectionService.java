package com.chinatower.platform.service;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.mapper.BdmInspectionExportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.chinatower.platform.entity.InspectionConfVO;

@Service
@Slf4j
public class BdmInspectionService {
    @Resource
    private BdmInspectionExportMapper bdmInspectionDAO;


    /**
     * 功能描述：巡查历史列表数据getTaskList
     * @Author：zl
     * @param inspectionTaskParam
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<InspectionTaskListVO> getTaskList(InspectionTaskParam inspectionTaskParam) {
        return bdmInspectionDAO.getTaskList(inspectionTaskParam);
    }

    /**
     * 功能描述：巡查计划列表数据getConfList
     * @Author：zhaozj
     * @param inspectionConfParam
     * @return List<InspectionConfListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<InspectionConfVO> getConfList(InspectionConfParam inspectionConfParam) {
        return bdmInspectionDAO.getConfList(inspectionConfParam);
    }

}
